package io.chenylnti.logindemo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.chenylnti.logindemo.pojo.Admin;
import io.chenylnti.logindemo.pojo.RespBean;
import io.chenylnti.logindemo.service.AdminDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.session.HttpSessionEventPublisher;


import javax.sql.DataSource;
import java.io.PrintWriter;

//前后端分离返回json写法
@EnableWebSecurity
@Configuration
public class SecurityConfig {
    @Autowired
    AdminDetailsServiceImpl userDetailsService;
    @Autowired
    DataSource dataSource;
    @Autowired
    CustomFilterInvocationSecurityMetadataSource customFilterInvocationSecurityMetadataSource;
    @Autowired
    CustomUrlDecisionManager customUrlDecisionManager;
    @Bean//密码加密用
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean//spring security可以及时清理session，但可能无法及时清理会话信息表
    HttpSessionEventPublisher httpSessionEventPublisher(){
        return new HttpSessionEventPublisher();
    }

    @Bean//自定义账户验证
    MyAuthenticationProvider myAuthenticationProvider(){
        MyAuthenticationProvider provider = new MyAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }
    @Bean//把自己写的加进去
    AuthenticationManager authenticationManager(){
        return new ProviderManager(myAuthenticationProvider());
    }

    @Bean//持久化令牌，防止异地登录加强rememberMe的安全性
    JdbcTokenRepositoryImpl jdbcTokenRepository(){
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        //jdbcTokenRepository.setCreateTableOnStartup(true);//自动建表，表是固定的
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }
    @Bean//角色继承，比如管理员自动拥有用户权限
    RoleHierarchy roleHierarchy(){
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_lv3 > ROLE_lv2");//用>号来描述
        return roleHierarchy;
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.authorizeRequests()//开启认证授权,静态权限控制
//                //.anyRequest().authenticated()//所有请求都需要认证
//                .antMatchers("/").permitAll()//页面访问权限
//                .antMatchers("/admin").fullyAuthenticated()//二次校验，用用户名密码的方式才能访问，rememberMe的方式不行
//                .antMatchers("/level1/**").hasRole("ROLE_lv1")
//                .antMatchers("/level2/**").hasRole("ROLE_lv2")
//                .antMatchers("/level3/**").hasRole("ROLE_lv3")
//                .antMatchers("/vf").permitAll();
        http.authorizeRequests()//动态权限管理
                .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                    @Override
                    public <O extends FilterSecurityInterceptor> O postProcess(O object) {
                        object.setAccessDecisionManager(customUrlDecisionManager);
                        object.setSecurityMetadataSource(customFilterInvocationSecurityMetadataSource);
                        return object;
                    }
                });
        http.formLogin()//表单配置
        .loginPage("/views/login.html")//登录页和接口都是login.html
        .loginProcessingUrl("/doLogin")//登录接口设置，如果没设就是上面那个
        .successHandler((request,response,authentication)->{    //代替successForwardUrl和defaultSuccessUrl,成功回调
            response.setContentType("application/json;charset=utf-8");
            PrintWriter out= response.getWriter();
            Admin admin = (Admin) authentication.getPrincipal();
            out.write(new ObjectMapper().writeValueAsString(RespBean.ok("登录成功",admin)));
            out.flush();
            out.close();
        })
        .failureHandler((request,response,exception)->{//登录失败回调
            response.setContentType("application/json;charset=utf-8");
            PrintWriter out= response.getWriter();
            RespBean respBean = RespBean.error("登录失败！");
            //判断登录失败的原因
            if (exception instanceof LockedException){
                respBean.setMsg("账户被锁定");
            }else if (exception instanceof CredentialsExpiredException){
                respBean.setMsg("密码过期");
            }else if (exception instanceof AccountExpiredException){
                respBean.setMsg("账户过期");
            }else if (exception instanceof DisabledException){
                respBean.setMsg("账户被禁用");
            }else if (exception instanceof BadCredentialsException){
                respBean.setMsg("用户名或密码错误");
            }else{
                respBean.setMsg("未知错误");
                System.out.println(exception.getMessage());
            }
            out.write(new ObjectMapper().writeValueAsString(respBean));
            out.flush();
            out.close();
        })
        .permitAll();//放行和登录有关的请求
        http.csrf().disable()//不关掉的话postman测试会有问题
        .exceptionHandling()
        .authenticationEntryPoint((request,response,exception)->{//用户未经身份验证就来，在用户想访问要登录后才能访问到的页面时，应该先给个提示要他登录，然后在跳到登录页面
            response.setContentType("application/json;charset=utf-8");
            PrintWriter  out = response.getWriter();
            out.write(new ObjectMapper().writeValueAsString(RespBean.error("尚未登录，请登录")));
            out.flush();
            out.close();
        })
        ;
        http
        .rememberMe()
        .tokenRepository(jdbcTokenRepository())//开启持久化令牌
        ;
        http.logout()
                //.logoutUrl("/logout")//get方式退出登录
        .logoutSuccessHandler((request,response,authentication)->{
            response.setContentType("application/json;charset=utf-8");
            PrintWriter out = response.getWriter();
            out.write(new ObjectMapper().writeValueAsString(RespBean.ok("注销成功")));
            out.flush();
            out.close();
        })
        ;
        http.sessionManagement()
                .maximumSessions(1);//session最大并发数，实现一个账号只能在一个地方用，要放在最后面并且需要重写用户类的hashcode方法
        return http.build();
    }
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        //忽略路径
        return (web) -> web.ignoring()
                .antMatchers("/css/**","/js/**","/img/**","/fonts/**","/login.html","/views/login.html","/verifyCode");
    }
}

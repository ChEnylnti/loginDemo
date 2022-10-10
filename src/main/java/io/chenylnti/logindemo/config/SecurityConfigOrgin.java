package io.chenylnti.logindemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


public class SecurityConfigOrgin {
    //@Bean//spring security可以及时清理session，但可能无法及时清理会话信息表
    HttpSessionEventPublisher httpSessionEventPublisher(){
        return new HttpSessionEventPublisher();
    }
    //@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()//开启认证授权
                .antMatchers("/").permitAll()//页面访问权限
                .antMatchers("/level1/**").hasRole("vip1")
                .antMatchers("/level2/**").hasRole("vip2")
                .antMatchers("/level3/**").hasRole("vip3");
        http.formLogin()//表单配置
        .loginPage("/views/login.html")//登录页和接口都是login.html
        .loginProcessingUrl("/doLogin")//登录接口设置，如果没设就是上面那个
        //.successForwardUrl("/s")//登录成功之后地址,缺点不能记录原始url地址一定会转到这个页面，属于服务端跳转
        .defaultSuccessUrl("/s")//属于重定向，会记录原始url，推荐
        .permitAll();//放行和登录有关的请求
        http.csrf().disable();
        http.rememberMe();
        http.logout()
                //.logoutUrl("/logout")//get方式退出登录
        .logoutRequestMatcher(new AntPathRequestMatcher("/logout","POST"))//POST方式退出登录
        .logoutSuccessUrl("/login.html")//注销成功去哪里
        ;
        http.sessionManagement()
                .maximumSessions(1);//session最大并发数，实现一个账号只能在一个地方用，要放在最后面并且需要重写用户类的hashcode方法
        return http.build();
    }
    //@Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        //忽略路径
        return (web) -> web.ignoring().antMatchers();
    }
}

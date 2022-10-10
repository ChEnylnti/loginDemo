package io.chenylnti.logindemo.config;

import io.chenylnti.logindemo.pojo.Menu;
import io.chenylnti.logindemo.pojo.Role;
import io.chenylnti.logindemo.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.util.Collection;
import java.util.List;

//根据用户传来的请求地址，分析请求需要的角色
@Component
public class CustomFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {
    @Autowired
    MenuService menuService;
    AntPathMatcher antPathMatcher=new AntPathMatcher();//spring自带的匹配器
    @Override//每次请求都会调用这个方法
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        String requestUrl = ((FilterInvocation) object).getRequestUrl();//获取请求的地址
        System.out.println(requestUrl);
        List<Menu> allMenusWithRole = menuService.getAllMenusWithRole();//获取所有匹配规则
        for (Menu menu : allMenusWithRole) {
            if (antPathMatcher.match(menu.getPattern(),requestUrl)){
                List<Role> roles = menu.getRoles();
                String[] rolesStr=new String[roles.size()];
                for (int i = 0; i < roles.size(); i++) {
                    rolesStr[i]=roles.get(i).getName();
                }
                return SecurityConfig.createList(rolesStr);//返回请求路径需要的角色
            }
        }

        return SecurityConfig.createList("ROLE_login");//如果上述都匹配不上就返回默认，之后需要处理这个特殊的标记
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}

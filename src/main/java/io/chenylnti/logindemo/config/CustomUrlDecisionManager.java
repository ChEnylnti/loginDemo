package io.chenylnti.logindemo.config;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.FilterInvocation;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.logging.Filter;

@Component
public class CustomUrlDecisionManager implements AccessDecisionManager {
    @Override//authentication获得当前认证用户，object获得路径，configAttributes是filter的返回值也就是需要的角色
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {
//        if (((FilterInvocation) object).getRequestUrl().equals("/verifyCode")){
//            return;
//        }
        for (ConfigAttribute attribute : configAttributes) {//遍历需要的角色
            String needRole = attribute.getAttribute();
            if ("ROLE_login".equals(needRole)){//没匹配上,可以设计成登录之后就能访问的
                if (authentication instanceof AnonymousAuthenticationToken){//如果是未登录用户则抛出异常
                    throw new AccessDeniedException("非法请求！");
                }else {
                    return;//不是匿名用户
                }
            }
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();//获取带你用户的权限
            for (GrantedAuthority authority : authorities) {
                if (authority.getAuthority().equals(needRole)){
                    return;
                }
            }

        }
        throw new AccessDeniedException("权限不足");
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}

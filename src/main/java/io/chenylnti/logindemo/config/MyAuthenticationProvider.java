package io.chenylnti.logindemo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.chenylnti.logindemo.pojo.RespBean;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.Response;
import java.io.IOException;
import java.io.PrintWriter;

//通过自定义authenticationProvider的方式来做验证码
public class MyAuthenticationProvider extends DaoAuthenticationProvider {
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        String code = request.getParameter("code");//用户输入的验证码
        String vf = (String) request.getSession().getAttribute("vf");
        if (code==null||vf==null||!code.equals(vf)){
            response.setContentType("application/json;charset=utf-8");
            try {
                PrintWriter out = response.getWriter();
                out.write(new ObjectMapper().writeValueAsString(RespBean.error("验证码错误")));
            } catch (IOException e) {
                e.printStackTrace();
            }

            throw new AuthenticationServiceException("验证码错误");
        }
        //下面是校验密码所以要在校验密码之前校验验证码
        super.additionalAuthenticationChecks(userDetails, authentication);
    }
}

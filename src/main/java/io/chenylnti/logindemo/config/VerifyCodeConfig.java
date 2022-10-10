package io.chenylnti.logindemo.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration//验证码
public class VerifyCodeConfig {
    @Bean
    Producer verifyCode(){
        Properties properties = new Properties();
        properties.setProperty("kaptcha.image.width","150");//设置验证码宽度
        properties.setProperty("kaptcha.image.height","50");//设置验证码高度
        properties.setProperty("kaptcha.textproducer.char.string","0123456789");//设置验证码包含的字符
        properties.setProperty("kaptcha.textproducer.char.length","4");//设置验证码包含的字符长度
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        defaultKaptcha.setConfig(new Config(properties));
        return defaultKaptcha;
    }
}











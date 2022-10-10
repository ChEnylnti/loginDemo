package io.chenylnti.logindemo.controller;

import com.google.code.kaptcha.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;

@RestController
public class TestController {
    @Autowired
    Producer producer;
    @GetMapping("/hello")
    public String hello(){
        return "Hello";
    }
    @GetMapping("/admin")//假设这是个敏感资源
    public String admin(){
        return "admin";
    }
    @GetMapping("/lv1/1")
    public String lv1(){
        return "lv1";
    }
    @GetMapping("/lv2/1")
    public String lv2(){
        return "lv2";
    }
    @GetMapping("/lv3/1")
    public String lv3(){
        return "lv3";
    }
    @GetMapping("/vf")//获取验证码端口
    public void getVerifyCode(HttpServletResponse response, HttpSession session) throws IOException {
        response.setContentType("image/jpeg");
        String text=producer.createText();
        System.out.println(text);
        session.setAttribute("vf",text);
        BufferedImage bi = producer.createImage(text);
        try (ServletOutputStream out=response.getOutputStream()){
            ImageIO.write(bi,"jpg",out);
        }
    }
}

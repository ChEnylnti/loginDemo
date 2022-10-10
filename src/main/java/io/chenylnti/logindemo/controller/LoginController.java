package io.chenylnti.logindemo.controller;

import com.google.code.kaptcha.Producer;
import io.chenylnti.logindemo.pojo.Admin;
import io.chenylnti.logindemo.pojo.RespBean;
import io.chenylnti.logindemo.service.AdminDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

@RestController
public class LoginController {
    @Autowired
    Producer producer;
    @Autowired
    AdminDetailsServiceImpl adminDetailsService;
    @GetMapping("/verifyCode")//获取验证码端口
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
    @PostMapping("/addAdmin")
    public RespBean addAdmin(@RequestBody Admin admin){
        System.out.println("admin = " + admin.toString());
        try {
            adminDetailsService.addAdmin(admin);
            return RespBean.ok("添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return RespBean.error("添加失败");
        }
    }
    @DeleteMapping("/delete/{id}")
    public RespBean deleteAdminById(@PathVariable Integer id){
        if (adminDetailsService.deleteAdminById(id)==1){
            return RespBean.ok("删除成功");
        }
        return RespBean.error("删除失败");
    }
    @GetMapping("/getAll")
    public List<Admin> getAll(){
        return adminDetailsService.getAllAdmins();
    }
    @PutMapping("/update")
    public RespBean updateAdmin(@RequestBody Admin admin){
        if(adminDetailsService.updateAdminById(admin)==1){
            return RespBean.ok("更新成功");
        }
        return RespBean.error("更新失败");
    }
}

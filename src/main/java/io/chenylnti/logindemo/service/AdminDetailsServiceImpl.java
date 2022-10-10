package io.chenylnti.logindemo.service;

import io.chenylnti.logindemo.mapper.AdminLoginMapper;
import io.chenylnti.logindemo.pojo.Admin;
import io.chenylnti.logindemo.pojo.RespBean;
import io.chenylnti.logindemo.pojo.Role;
import io.chenylnti.logindemo.service.utils.AdminUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


@Service("userDetailsService")
public class AdminDetailsServiceImpl implements UserDetailsService {
    @Autowired
    AdminLoginMapper adminLoginMapper;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //通过用户名在数据库中查询
        Admin admin = adminLoginMapper.adminLogin(username);
        System.out.println(admin.getAdmin_account()+admin.getAdmin_password());
        //如果admin为空说明用户名错误，抛出一个异常
        if (admin==null){
            throw new UsernameNotFoundException("用户名不存在");
        }
        //获取账号权限
        admin.setRoles(adminLoginMapper.getAdminRoleById(admin.getId()));
        //查询数据库返回的user对象，密码认证交给security做，密码加密
        return admin;
    }
    public Integer deleteAdminById(Integer id){
        return adminLoginMapper.deleteByPrimaryKey(id);
    }
    @Transactional
    public Integer addAdmin(Admin admin){
        System.out.println("admin = " + admin.toString());
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encode = bCryptPasswordEncoder.encode(admin.getPassword());
        System.out.println(encode);
        admin.setAdmin_password(encode);
        adminLoginMapper.insertSelective(admin);
        System.out.println("admin = " + admin.toString());
        Integer result=0;
        for (Role role : admin.getRoles()) {
            result=adminLoginMapper.afterInsert(admin.getId(),role.getId());
        }
        return result;
    }
    public List<Admin> getAllAdmins(){
        System.out.println("AdminUtils.getCurrentAdmin() = " + AdminUtils.getCurrentAdmin().toString());
        return adminLoginMapper.getAdminByIdOrderById(AdminUtils.getCurrentAdmin().getId());
    }
    public Integer updateAdminById(Admin admin){
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encode = bCryptPasswordEncoder.encode(admin.getPassword());
        System.out.println(encode);
        admin.setAdmin_password(encode);
        return adminLoginMapper.updateByPrimaryKeySelective(admin);
    }
}

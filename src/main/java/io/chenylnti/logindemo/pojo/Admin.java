package io.chenylnti.logindemo.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class Admin implements UserDetails {
    private Integer id;
    private String admin_account;
    private String admin_password;
    private String profile;
    private Boolean isAccountNonExpired;
    private Boolean isAccountNonLocked;
    private Boolean isCredentialsNonExpired;
    private Boolean isEnabled;
    private List<Role> roles;//一个用户有多个角色

    @Override
    public String toString() {
        return "Admin{" +
                "id=" + id +
                ", admin_account='" + admin_account + '\'' +
                ", admin_password='" + admin_password + '\'' +
                ", profile='" + profile + '\'' +
                ", isAccountNonExpired=" + isAccountNonExpired +
                ", isAccountNonLocked=" + isAccountNonLocked +
                ", isCredentialsNonExpired=" + isCredentialsNonExpired +
                ", isEnabled=" + isEnabled +
                ", roles=" + roles +
                '}';
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAdmin_account() {
        return admin_account;
    }

    public void setAdmin_account(String admin_account) {
        this.admin_account = admin_account;
    }

    public String getAdmin_password() {
        return admin_password;
    }

    public void setAdmin_password(String admin_password) {
        this.admin_password = admin_password;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    @Override
    @JsonIgnore//不用返回这个到前端
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities=new ArrayList<>(roles.size());
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return admin_password;
    }

    @Override
    public String getUsername() {
        return admin_account;
    }

    @Override//是否过期
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override//被锁定
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override//凭证已过期
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override//可用
    public boolean isEnabled() {
        return true;
    }
}

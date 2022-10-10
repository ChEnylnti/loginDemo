package io.chenylnti.logindemo.service;

import io.chenylnti.logindemo.mapper.MenuMapper;
import io.chenylnti.logindemo.pojo.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuService {
    @Autowired
    MenuMapper menuMapper;


    public List<Menu> getAllMenusWithRole(){
        return menuMapper.getAllMenusWithRole();
    }
}

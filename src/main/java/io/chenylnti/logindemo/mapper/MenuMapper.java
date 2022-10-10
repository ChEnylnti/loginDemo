package io.chenylnti.logindemo.mapper;

import io.chenylnti.logindemo.pojo.Menu;
import io.chenylnti.logindemo.pojo.MenuRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface MenuMapper {
    int deleteByPrimaryKey(Integer id);
    int insertSelective(MenuRole record);//一对一插入
    int insertRecord(@Param("rid") Integer rid,@Param("mid") Integer[] mids);//一对多插入
    MenuRole selectByPrimaryKey(Integer id);
    List<Menu> getAllMenusWithRole();
    int updateByPrimaryKey(Integer id);
    int deleteByRid(Integer rid);
}

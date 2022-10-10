package io.chenylnti.logindemo.mapper;

import io.chenylnti.logindemo.pojo.Admin;
import io.chenylnti.logindemo.pojo.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface AdminLoginMapper {
    Admin adminLogin(@Param("admin_account") String admin_account);
    List<Admin> getAdminByIdOrderById(@Param("id") Integer id);
    Admin selectByPrimaryKey(@Param("id") Integer id);
    List<Role> getAdminRoleById(Integer id);
    int deleteByPrimaryKey(Integer id);
    int insert(Admin record);
    int afterInsert(@Param("d_id") Integer d_id,@Param("r_id") Integer r_id);
    int insertSelective(Admin record);
    int updateByPrimaryKeySelective(Admin record);

}

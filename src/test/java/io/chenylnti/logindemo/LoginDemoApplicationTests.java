package io.chenylnti.logindemo;

import io.chenylnti.logindemo.service.utils.AdminUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Calendar;

@SpringBootTest
class LoginDemoApplicationTests {

    @Autowired
    DataSource dataSource;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Test
    void contextLoads() throws SQLException {
        Calendar calendar = Calendar.getInstance();
        System.out.println("calendar = " + calendar);
    }

}

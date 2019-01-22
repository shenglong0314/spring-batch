package com.example.demo.task;

import com.example.demo.Bean.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int i) throws SQLException {
        User u = new User();

        u.setId(rs.getInt("id"));
        u.setFirstName(rs.getString("first_name"));
        u.setLastName(rs.getString("last_name"));


        return u;
    }
}

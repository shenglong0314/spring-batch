package com.example.demo.task;

import com.example.demo.Bean.User;
import com.example.demo.dao.UserRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;


public class UserItemProcessor implements ItemProcessor<User,User> {
    @Autowired
    private UserRepository userRepository;
    @Override
    public User process(User item) throws Exception {
        System.out.println("======================="+Thread.currentThread().getName());
        return item;
    }
}

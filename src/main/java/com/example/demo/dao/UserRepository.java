package com.example.demo.dao;

import com.example.demo.Bean.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Integer> {
}

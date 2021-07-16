package com.project.mega.triplus.service;

import com.project.mega.triplus.entity.User;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


import java.util.List;
import java.util.UUID;

@Getter
public class UserUser extends org.springframework.security.core.userdetails.User {
    private final User user;
    public UserUser(User user){
        super(user.getEmail(), user.getPassword() == null || user.getPassword().isEmpty() ? UUID.randomUUID().toString() : user.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
        this.user = user;
    }

}

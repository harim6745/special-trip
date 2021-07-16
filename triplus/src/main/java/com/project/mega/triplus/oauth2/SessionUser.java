package com.project.mega.triplus.oauth2;

import com.project.mega.triplus.entity.User;
import com.project.mega.triplus.service.UserUser;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public class SessionUser extends UserUser implements Serializable, OAuth2User {
    private String nickName;
    private String email;
    private final Map<String, Object> map = new HashMap<>();


    public SessionUser(User user) {
        super(user);
        this.nickName = user.getNickName();
        this.email = user.getEmail();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return map;
    }

    @Override
    public String getName() {
        return nickName;
    }
}

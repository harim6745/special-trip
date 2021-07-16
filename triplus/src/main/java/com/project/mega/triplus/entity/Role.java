package com.project.mega.triplus.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Getter
@RequiredArgsConstructor
public enum Role implements GrantedAuthority {
    USER("ROLE_USER", "일반 사용자"),  ADMIN("ROLE_ADMIN", "관리자");

    @Override
    public String getAuthority() {
        return this.name();
    }

    private final String key;
    private final String title;
}

package com.expenseiq.security;

import com.expenseiq.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@AllArgsConstructor
public class SecurityUser implements UserDetails {

    private Long id;
    private String email;
    private String password;
    private String name;
    private Collection<? extends GrantedAuthority> authorities;
    private boolean enabled;

    public static SecurityUser build(User user) {
        Collection<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );

        return new SecurityUser(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getName(),
                authorities,
                user.getEnabled()
        );
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}

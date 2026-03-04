package levelup42.trivia.infraestructure.security;

import levelup42.trivia.infraestructure.adapter.out.persistence.entity.PlayerEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final PlayerEntity playerEntity;

    public CustomUserDetails(PlayerEntity playerEntity) {
        this.playerEntity = playerEntity;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + playerEntity.getRole().name()));
    }

    @Override
    public String getPassword() {
        return playerEntity.getPassword();
    }

    @Override
    public String getUsername() {
        return playerEntity.getMail();
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
        return true;
    }
}

package example.customauthenticationprincipal;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public record ApplicationUser(
        String firstName,
        String lastName,
        String email,
        Set<String> access // This is a set of IDs that the user has access to, this could be anything.
) implements UserDetails {
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // We might need to replace with actual authorities at some point
    }

    @Override
    public String getPassword() {
        // No password is used for OAuth, so return null or empty
        return "";
    }

    @Override
    public String getUsername() {
        // Use email as the username for authentication
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // or implement your logic
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // or implement your logic
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // or implement your logic
    }

    @Override
    public boolean isEnabled() {
        return true; // or implement your logic
    }

}
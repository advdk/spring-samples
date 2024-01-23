package example.customauthenticationprincipal;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class CustomAuthenticationToken implements Authentication {

    private final ApplicationUser applicationUser;
    private boolean authenticated = true;

    public CustomAuthenticationToken(ApplicationUser applicationUser) {
        this.applicationUser = applicationUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return applicationUser.getAuthorities();
    }

    @Override
    public Object getCredentials() {
        return null; // Credentials are not needed after authentication
    }

    @Override
    public Object getDetails() {
        return applicationUser;
    }

    @Override
    public Object getPrincipal() {
        return applicationUser;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return applicationUser.getUsername();
    }
}
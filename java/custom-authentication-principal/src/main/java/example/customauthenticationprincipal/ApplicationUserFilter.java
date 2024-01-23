package example.customauthenticationprincipal;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Principal;
import java.util.concurrent.TimeUnit;

/**
 * This filter is responsible for authorizing the users of the application.
 * based on JWT (JSON Web Token) tokens. It intercepts incoming HTTP requests,
 * validates JWT tokens, and stores the user information in a cache for faster
 * access.
 * The user information is then available in the controllers through the request.
 */
@Component
public class ApplicationUserFilter extends OncePerRequestFilter {
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationUserFilter.class);
    private final UserRepository userRepository;
    private static final long CACHE_EXPIRY_MINUTES = 5;
    private final Cache<String, ApplicationUser> userCache;

    public ApplicationUserFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
        userCache = Caffeine.newBuilder()
          .expireAfterWrite(CACHE_EXPIRY_MINUTES, TimeUnit.MINUTES)
          .maximumSize(1000)
          .build();
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {

           String uniqueIdentifier = null;
            Principal principal = request.getUserPrincipal();

            if (principal instanceof JwtAuthenticationToken jwt) {
                uniqueIdentifier = jwt.getToken().getClaimAsString("iss") + "_" + jwt.getToken().getClaimAsString("sub"); // issuer + subject. This is unique for each user. We use this as a key for the cache.
            }

            if (uniqueIdentifier == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            String jwtToken = request.getHeader("Authorization");
            if (jwtToken == null || !jwtToken.startsWith("Bearer ")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            ApplicationUser currentUserFromToken = userCache.get(uniqueIdentifier, key -> userRepository.getCurrentUserFromToken(jwtToken));

            if (currentUserFromToken != null) {
                CustomAuthenticationToken customAuthentication = new CustomAuthenticationToken(currentUserFromToken);
                SecurityContextHolder.getContext().setAuthentication(customAuthentication);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }


        } catch (Exception e) {
            LOG.error("Error while getting current user from token", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // Continue
        filterChain.doFilter(request, response);
    }
}

package example.customauthenticationprincipal;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.io.IOException;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class ApplicationApplicationUserFilterTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ApplicationUserFilter applicationUserFilter;

    @BeforeEach
    public void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testFilterWithValidJWT() throws Exception {
        String jwtToken = "Bearer valid.token.here";
        var testUser = getTestApplicationUser("1");

        when(userRepository.getCurrentUserFromToken(anyString())).thenReturn(testUser);
        MockHttpServletResponse response = simulateRequestWithJwtToken(jwtToken);

        // Verify that the SecurityContext is updated
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertInstanceOf(CustomAuthenticationToken.class, SecurityContextHolder.getContext().getAuthentication());
        assertEquals(testUser, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }


    @Test
    public void testFilterWithInvalidJWT() throws Exception {
        String invalidJwtToken = "Bearer invalid.token.here";

        // Assuming the behavior of userRepository is to return null for an invalid token
        when(userRepository.getCurrentUserFromToken(anyString())).thenReturn(null);
        MockHttpServletResponse response = simulateRequestWithJwtToken(invalidJwtToken);

        // Verify that the response status is set to 401 Unauthorized
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());

        // Verify that the SecurityContext is not updated with a valid authentication
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        // Reset SecurityContext after test
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testSeparateSecurityContextsForDifferentRequests() throws Exception {

        var testUser1 = getTestApplicationUser("1");
        var testUser2 = getTestApplicationUser("2");

        // Simulate first request
        when(userRepository.getCurrentUserFromToken(anyString())).thenReturn(testUser1);
        simulateRequestWithJwtToken("token1");
        ApplicationUser user1 = (ApplicationUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        assertEquals(testUser1.getUsername(), user1.getUsername());
        SecurityContextHolder.clearContext();

        // Simulate second request
        when(userRepository.getCurrentUserFromToken(anyString())).thenReturn(testUser2);
        simulateRequestWithJwtToken("token2");
        ApplicationUser user2 = (ApplicationUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        assertEquals(testUser2.getUsername(), user2.getUsername());

        // Verify that the two users are different
        assertNotEquals(user1.getUsername(), user2.getUsername());

        // Clear context after test
        SecurityContextHolder.clearContext();
    }


    private MockHttpServletResponse simulateRequestWithJwtToken(String tokenValue) throws ServletException, IOException {
        Jwt jwt = Jwt.withTokenValue(tokenValue)
                .header("alg", "none")
                .claim("iss", "issuer" + tokenValue)
                .claim("sub", "subject" + tokenValue)
                .build();
        JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(jwt);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setUserPrincipal(jwtAuthenticationToken);
        request.setMethod("POST");
        request.setRequestURI("/api/test");
        request.addHeader("Authorization", "Bearer " + tokenValue);

        MockHttpServletResponse response = new MockHttpServletResponse();
        applicationUserFilter.doFilter(request, response, (req, res) -> {});

        // Reset the mock for the next request
        reset(userRepository);

        return response;
    }


    private ApplicationUser getTestApplicationUser(String uniqueIdentifier) {
        return new ApplicationUser(
                "firstName " + uniqueIdentifier,
                "lastName " + uniqueIdentifier,
                uniqueIdentifier + "@testmail.test",
                Set.of("0","1")
        );
    }


}

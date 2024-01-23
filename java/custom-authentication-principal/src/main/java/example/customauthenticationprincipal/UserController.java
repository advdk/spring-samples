package example.customauthenticationprincipal;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
public class UserController {
    @GetMapping("/me")
    public ApplicationUser getUsers(@AuthenticationPrincipal ApplicationUser user) {
        return user;
    }

    @GetMapping("/me/access")
    public Set<String> getAccess(@AuthenticationPrincipal ApplicationUser user) {
        return user.access();
    }

}

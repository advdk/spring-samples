package example.customauthenticationprincipal;

import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public class UserRepository {

    public ApplicationUser getCurrentUserFromToken(String token){
        // This is where you would get the user from the token
        // For this example, we will just return a test user
        return new ApplicationUser(
                "firstName ",
                "lastName ",
                "test@testmail.test",
                Set.of("1", "2", "3")
        );
    }
}

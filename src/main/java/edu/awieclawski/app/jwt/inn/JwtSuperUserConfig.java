package edu.awieclawski.app.jwt.inn;

import edu.awieclawski.app.jwt.dto.JwtUserDTO;
import edu.awieclawski.app.model.UserRole;
import edu.awieclawski.app.util.OptionalExtended;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Configuration
class JwtSuperUserConfig implements CommandLineRunner {

    @Value("${super-user.login}")
    private String login;

    @Value("${super-user.pass}")
    private String password;

    @Autowired
    private JwtUserDetailsService userDataService;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("Super User create procedure started");
        UserDetails userDetails = null;
        try {
            userDetails = userDataService.loadUserByUsername(login);
        } catch (UsernameNotFoundException ignored) {
        }
        OptionalExtended.ofNullable(userDetails).ifPresentOrElse(
                user -> System.out.println("Super User already exists. Failed!"),
                () -> {
                    JwtUserDTO user = JwtUserDTO.builder()
                            .login(login)
                            .password(password)
                            .name("Superior User")
                            .roleId(UserRole.SUPER.getRoleId())
                            .build();
                    userDataService.saveEncodedUser(user);
                    System.out.println("Super User created. OK");
                });
    }

}

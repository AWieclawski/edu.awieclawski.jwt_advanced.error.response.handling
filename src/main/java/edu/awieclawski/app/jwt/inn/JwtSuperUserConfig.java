package edu.awieclawski.app.jwt.inn;

import edu.awieclawski.app.dao.RoleRepository;
import edu.awieclawski.app.jwt.dto.JwtUserDTO;
import edu.awieclawski.app.model.RoleEntity;
import edu.awieclawski.app.model.UserRole;
import edu.awieclawski.app.util.OptionalExtended;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

@Configuration
class JwtSuperUserConfig implements CommandLineRunner {

    @Value("${super-user.login}")
    private String login;

    @Value("${super-user.pass}")
    private String password;

    @Value("${super-user.name}")
    private String name;

    @Autowired
    private JwtUserDetailsService userDataService;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("Roles create procedure started");
        Arrays.stream(UserRole.values()).forEach(role -> {
            var roleToSave = createRoleIfNotFound(role.getRoleId());
            roleRepository.save(roleToSave);
        });
        System.out.println("Super User create procedure started");
        UserDetails userDetails = null;
        try {
            userDetails = userDataService.loadUserByUsername(login);
        } catch (UsernameNotFoundException ignored) {
        }
        OptionalExtended.ofNullable(userDetails).ifPresentOrElse(
                user -> System.out.println("Super User already exists. Failed!"),
                () -> {
                    JwtUserDTO jwtUserDTO = JwtUserDTO.builder()
                            .login(login)
                            .password(password)
                            .name(name)
                            .roles(Collections.singletonList(UserRole.SUPER.getRoleId()))
                            .build();
                    var saved = userDataService.saveEncodedUser(jwtUserDTO);
                    System.out.println("Super User created. " + saved);
                });
    }

    @Transactional
    public RoleEntity createRoleIfNotFound(Integer roleId) {
        AtomicReference<RoleEntity> roleEntity = new AtomicReference<>();
        UserRole userRole = UserRole.getUserRoleById(roleId);
        OptionalExtended.ofNullable(userRole).ifPresentOrElse(role -> {
                    roleEntity.set(roleRepository.findByUserRole(role));
                    if (roleEntity.get() == null) {
                        roleEntity.set(new RoleEntity(role));
                        roleRepository.save(roleEntity.get());
                        System.out.println("Role created. " + roleEntity.get());
                    }
                }
                , () -> System.out.println("Role create Failed for: " + name));
        return roleEntity.get();
    }

}

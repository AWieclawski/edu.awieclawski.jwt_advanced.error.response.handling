package edu.awieclawski.app.jwt.inn;


import edu.awieclawski.app.dto.UserResponseDTO;
import edu.awieclawski.app.jwt.dto.JwtUserDTO;
import edu.awieclawski.app.mapper.UserMapper;
import edu.awieclawski.app.model.UserEntity;
import edu.awieclawski.app.model.UserRole;
import edu.awieclawski.app.service.UserDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service(value = JwtUserDetailsService.BEAN_NAME)
@RequiredArgsConstructor
@DependsOn({UserDataService.BEAN_NAME, JwtPasswordEncoder.BEAN_NAME})
class JwtUserDetailsService implements UserDetailsService {

    static final String BEAN_NAME = "edu.awieclawski.app.jwt.inn.JwtUserDetailsService";

    private final UserDataService userService;
    private final JwtPasswordEncoder passwordEncoder;

    UserResponseDTO saveEncodedUser(JwtUserDTO jwtUserDTO) {
        jwtUserDTO.setPassword(passwordEncoder.getEncoder().encode(jwtUserDTO.getPassword()));
        return userService.saveUser(jwtUserDTO);
    }

    UserResponseDTO updateEncodedUser(JwtUserDTO jwtUserDTO) {
        UserEntity user = userService.getUserByLogin(jwtUserDTO.getLogin());
        if (user == null) {
            throw new UsernameNotFoundException("User to update not found with login: " + jwtUserDTO.getLogin());
        }
        if (jwtUserDTO.getPassword() != null) {
            jwtUserDTO.setPassword(passwordEncoder.getEncoder().encode(jwtUserDTO.getPassword()));
        }
        UserMapper.updateEntity(jwtUserDTO, user);
        return UserMapper.toDto(userService.updateUser(user));
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        UserEntity user = userService.getUserByLogin(login);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with login: " + login);
        }
        String roleNameById = UserRole.SUPER.getRoleById(user.getRoleId());
        List<SimpleGrantedAuthority> roles = Arrays.asList(new SimpleGrantedAuthority(roleNameById));

        return new org.springframework.security.core.userdetails.User(
                user.getLogin(),
                user.getPassword(),
                roles);
    }

}

package edu.awieclawski.app.jwt.inn;


import edu.awieclawski.app.dto.UserResponseDTO;
import edu.awieclawski.app.jwt.dto.JwtUserDTO;
import edu.awieclawski.app.mapper.UserMapper;
import edu.awieclawski.app.model.UserEntity;
import edu.awieclawski.app.service.UserDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Slf4j
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
        if (jwtUserDTO.getPassword() != null) {
            jwtUserDTO.setPassword(passwordEncoder.getEncoder().encode(jwtUserDTO.getPassword()));
        }
        return UserMapper.toDto(userService.updateUser(jwtUserDTO));
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        UserEntity user = userService.getUserByLogin(login);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with login: " + login);
        }
        var roles = user.getRoles().stream()
                .map(it -> new SimpleGrantedAuthority(it.getUserRole().getRole()))
                .collect(Collectors.toSet());
        log.debug("Got {} roles for user: {}", roles, login);
        return new org.springframework.security.core.userdetails.User(
                user.getLogin(),
                user.getPassword(),
                roles);
    }

}

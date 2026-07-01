package edu.awieclawski.app.service;

import edu.awieclawski.app.dao.RoleRepository;
import edu.awieclawski.app.dao.UserRepository;
import edu.awieclawski.app.dto.UserResponseDTO;
import edu.awieclawski.app.exception.SaveEntityException;
import edu.awieclawski.app.jwt.dto.JwtUserDTO;
import edu.awieclawski.app.mapper.UserMapper;
import edu.awieclawski.app.model.RoleEntity;
import edu.awieclawski.app.model.UserEntity;
import edu.awieclawski.app.model.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component(value = UserDataService.BEAN_NAME)
@RequiredArgsConstructor
public class UserDataService {

    public static final String BEAN_NAME = "edu.awieclawski.app.service.UserDataService";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserResponseDTO saveUser(JwtUserDTO jwtUserDTO) {
        try {
            UserEntity userEntity = UserMapper.toEntity(jwtUserDTO);
            assignRolesToUser(userEntity, jwtUserDTO);
            userEntity = userRepository.save(userEntity);
            log.debug("User {} saved", userEntity);
            return UserMapper.toDto(userEntity);
        } catch (Exception e) {
            log.error("User save failed! {}", e.getMessage());
        }
        String login = jwtUserDTO != null ? jwtUserDTO.getLogin() : null;
        throw new SaveEntityException("User [" + login + "] could not be saved");
    }

    public UserEntity getUserByLogin(String username) {
        return userRepository.findByLogin(username);
    }

    public UserEntity updateUser(JwtUserDTO jwtUserDTO) {
        UserEntity userEntity = userRepository.findByLogin(jwtUserDTO.getLogin());
        if (userEntity == null) {
            throw new UsernameNotFoundException("User to update not found with login: " + jwtUserDTO.getLogin());
        }
        UserMapper.updateEntity(jwtUserDTO, userEntity);
        assignRolesToUser(userEntity, jwtUserDTO);
        userEntity = userRepository.save(userEntity);
        log.info("User [{}|{}] updated", userEntity.getLogin(), userEntity.getRoles());
        return userEntity;
    }

    private void assignRolesToUser(UserEntity userEntity, JwtUserDTO jwtUserDTO) {
        List<RoleEntity> roles;
        if (jwtUserDTO.getRoles() == null || jwtUserDTO.getRoles().isEmpty()) {
            roles = Collections.singletonList(roleRepository.findByUserRole(UserRole.BASIC));
        } else {
            roles = roleRepository.findAllById(jwtUserDTO.getRoles());
        }
        userEntity.setRoles(new HashSet<>(roles));
    }
}

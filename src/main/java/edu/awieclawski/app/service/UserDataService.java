package edu.awieclawski.app.service;

import edu.awieclawski.app.dao.UserRepository;
import edu.awieclawski.app.dto.UserResponseDTO;
import edu.awieclawski.app.exception.SaveEntityException;
import edu.awieclawski.app.jwt.dto.JwtUserDTO;
import edu.awieclawski.app.mapper.UserMapper;
import edu.awieclawski.app.model.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component(value = UserDataService.BEAN_NAME)
@RequiredArgsConstructor
public class UserDataService {

    public static final String BEAN_NAME = "edu.awieclawski.app.service.UserDataService";

    private final UserRepository userRepository;

    public UserResponseDTO saveUser(JwtUserDTO user) {
        try {
            UserEntity userEntity = UserMapper.toEntity(user);
            UserEntity savedEntity = userRepository.save(userEntity);
            return UserMapper.toDto(savedEntity);
        } catch (Exception e) {
            log.error("User save failed! {}", e.getMessage());
        }
        String login = user != null ? user.getLogin() : null;
        throw new SaveEntityException("User [" + login + "] could not be saved");
    }

    public UserEntity getUserByLogin(String username) {
        return userRepository.findByLogin(username);
    }

    public UserEntity updateUser(UserEntity user) {
        user = userRepository.save(user);
        log.info("User [{}|{}] updated", user.getLogin(), user.getRoleId());
        return user;
    }
}

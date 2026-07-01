package edu.awieclawski.app.mapper;

import edu.awieclawski.app.dto.UserResponseDTO;
import edu.awieclawski.app.jwt.dto.JwtUserDTO;
import edu.awieclawski.app.model.RoleEntity;
import edu.awieclawski.app.model.UserEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static UserEntity toEntity(JwtUserDTO jwtUserDTO) {
        return UserEntity.builder()
                .login(jwtUserDTO.getLogin())
                .password(jwtUserDTO.getPassword())
                .name(jwtUserDTO.getName())
                .email(jwtUserDTO.getEmail())
                .build();
    }

    public static void updateEntity(JwtUserDTO jwtUserDTO, UserEntity userEntity) {
        userEntity.setPassword(jwtUserDTO.getPassword());
        userEntity.setName(jwtUserDTO.getName());
        userEntity.setEmail(jwtUserDTO.getEmail());
    }

    public static UserResponseDTO toDto(UserEntity userEntity) {
        return UserResponseDTO.builder()
                .login(userEntity.getLogin())
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .roles(userEntity.getRoles().stream().map(RoleEntity::getId).collect(Collectors.toSet()))
                .build();
    }

}

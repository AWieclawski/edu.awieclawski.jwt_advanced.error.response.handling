package edu.awieclawski.app.mapper;

import edu.awieclawski.app.dto.UserResponseDTO;
import edu.awieclawski.app.jwt.dto.JwtUserDTO;
import edu.awieclawski.app.model.UserEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static UserEntity toEntity (JwtUserDTO jwtUserDTO) {
        return UserEntity.builder()
                .login(jwtUserDTO.getLogin())
                .password(jwtUserDTO.getPassword())
                .name(jwtUserDTO.getName())
                .email(jwtUserDTO.getEmail())
                .role(jwtUserDTO.getRole() != null ? jwtUserDTO.getRole() : "ROLE_USER")
                .build();
    }

    public static UserResponseDTO toDto(UserEntity userEntity) {
        return UserResponseDTO.builder()
                .login(userEntity.getLogin())
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .role(userEntity.getRole())
                .build();
    }

}

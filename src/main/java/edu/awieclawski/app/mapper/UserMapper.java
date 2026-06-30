package edu.awieclawski.app.mapper;

import edu.awieclawski.app.dto.UserResponseDTO;
import edu.awieclawski.app.jwt.dto.JwtUserDTO;
import edu.awieclawski.app.model.UserEntity;
import edu.awieclawski.app.model.UserRole;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static UserEntity toEntity(JwtUserDTO jwtUserDTO) {
        return UserEntity.builder()
                .login(jwtUserDTO.getLogin())
                .password(jwtUserDTO.getPassword())
                .name(jwtUserDTO.getName())
                .email(jwtUserDTO.getEmail())
                .roleId(jwtUserDTO.getRoleId() != null
                        ? jwtUserDTO.getRoleId()
                        : UserRole.BASIC.getRoleId())
                .build();
    }

    public static void updateEntity(JwtUserDTO jwtUserDTO, UserEntity userEntity) {
        userEntity.setPassword(jwtUserDTO.getPassword());
        userEntity.setName(jwtUserDTO.getName());
        userEntity.setEmail(jwtUserDTO.getEmail());
        userEntity.setRoleId(jwtUserDTO.getRoleId());
    }

    public static UserResponseDTO toDto(UserEntity userEntity) {
        return UserResponseDTO.builder()
                .login(userEntity.getLogin())
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .roleId(userEntity.getRoleId())
                .build();
    }

}

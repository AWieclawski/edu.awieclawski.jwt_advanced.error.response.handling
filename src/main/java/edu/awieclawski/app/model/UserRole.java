package edu.awieclawski.app.model;

import lombok.Getter;

import java.util.Arrays;

public enum UserRole {
    BASIC("ROLE_USER", "USER", 1),
    MANAGE("ROLE_MANAGE", "MANAGE", 2),
    SUPER("ROLE_ADMIN", "ADMIN", 3);

    @Getter
    private final String role;
    @Getter
    private final String roleName;
    @Getter
    private final Integer roleId;

    UserRole(String role, String roleName, Integer roleId) {
        this.role = role;
        this.roleName = roleName;
        this.roleId = roleId;
    }

    public String getRoleById(Integer roleId) {
        return Arrays.stream(values())
                .filter(role -> role.roleId.equals(roleId))
                .findFirst()
                .map(el -> el.role)
                .orElse(null);

    }
}

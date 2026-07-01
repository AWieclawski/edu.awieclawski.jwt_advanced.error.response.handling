package edu.awieclawski.app.model;

import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;

@ToString
public enum UserRole {
    BASIC("USER", 1),
    MANAGER("MANAGER", 9),
    SUPER("ADMIN", 99);

    @Getter
    private final String role;
    @Getter
    private final String roleName;
    @Getter
    private final Integer roleId;

    UserRole(String roleName, Integer roleId) {
        this.role = "ROLE_" + roleName;
        this.roleName = roleName;
        this.roleId = roleId;
    }

    public static String getRoleById(Integer roleId) {
        return Arrays.stream(values())
                .filter(role -> role.roleId.equals(roleId))
                .findFirst()
                .map(el -> el.role)
                .orElse(null);

    }

    public static UserRole getUserRoleById(Integer roleId) {
        return Arrays.stream(values())
                .filter(role -> role.roleId.equals(roleId))
                .findFirst()
                .orElse(null);

    }

    public static String getRoleNameById(Integer roleId) {
        return getUserRoleById(roleId) != null
                ? getUserRoleById(roleId).getRoleName()
                : null;

    }
}

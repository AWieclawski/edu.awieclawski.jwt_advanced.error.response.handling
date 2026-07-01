package edu.awieclawski.app.model;

import lombok.*;

import javax.persistence.*;

@Setter
@Getter
@NoArgsConstructor (access = AccessLevel.PRIVATE)
@ToString
@Entity
@Table(name = "roles")
public class RoleEntity {

    @Id
    private Integer id;

    @Column(name = "ROLE_NAME")
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    public RoleEntity(UserRole userRole) {
        this.userRole = userRole;
        this.id = this.userRole.getRoleId();
    }
}

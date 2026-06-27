package edu.awieclawski.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor //(access = AccessLevel.PRIVATE)
@ToString(exclude = {"password"})
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "LOGIN", unique = true, nullable = false)
    private String login;

    @Column
    private String email;

    @Column
    private String name;

    @Column
    private String role;

    @Column(unique = true, nullable = false)
    @JsonIgnore
    private String password;

    @Column(name = "CREATED_AT")
    private Timestamp createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = new Timestamp(System.currentTimeMillis());
        }
    }
}

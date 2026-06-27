package edu.awieclawski.app.model;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor (access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false, of = {"message", "errorCode", "userLogin", "url"})
@Entity
@Table(name = "error_messages")
public class ErrorMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String message;

    @Column(name = "CREATED_AT")
    private Timestamp createdAt;

    @Column(name = "ERROR_ID")
    private UUID errorId;

    @Column(name = "TOKEN_SHORT")
    private String tokenShort;

    @Column(name = "USER_LOGIN")
    private String userLogin;

    @Column(name = "STATUS_CODE")
    private Integer errorCode;

    @Column
    private String url;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = new Timestamp(System.currentTimeMillis());
        }
        if (errorId == null) {
            errorId = UUID.randomUUID();
        }
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (errorCode!= null ? errorCode.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (tokenShort != null ? tokenShort.hashCode() : 0);
        return result;
    }
}

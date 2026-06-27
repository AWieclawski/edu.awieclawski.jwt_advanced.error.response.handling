package edu.awieclawski.app.cache;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class AuthorisedUserDto {
    private String userLogin;
    private Date createdAt;
    private Date tokenValidTo;
    private String tokenShort;

}

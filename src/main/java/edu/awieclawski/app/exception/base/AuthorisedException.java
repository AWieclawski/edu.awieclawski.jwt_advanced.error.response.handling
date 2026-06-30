package edu.awieclawski.app.exception.base;

import lombok.Getter;

import java.util.UUID;

public abstract class AuthorisedException extends RuntimeException {

    @Getter
    protected final UUID errorId = UUID.randomUUID();

    public AuthorisedException(String message) {
        super(message);
    }

}

package edu.awieclawski.app.exception.base;

import lombok.Getter;

import java.util.UUID;

public abstract class BaseRestApiException extends RuntimeException {

    @Getter
    protected UUID errorId;

    public BaseRestApiException(String message) {
        super(message);
    }

}

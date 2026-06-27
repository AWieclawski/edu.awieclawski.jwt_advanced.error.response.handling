package edu.awieclawski.app.exception;

import edu.awieclawski.app.exception.base.BaseRestApiException;

public class EntityNotFoundException extends BaseRestApiException {

    public EntityNotFoundException(String message) {
        super(message);
    }
}


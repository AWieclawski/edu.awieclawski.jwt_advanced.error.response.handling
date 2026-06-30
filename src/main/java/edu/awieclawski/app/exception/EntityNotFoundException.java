package edu.awieclawski.app.exception;

import edu.awieclawski.app.exception.base.AuthorisedException;

public class EntityNotFoundException extends AuthorisedException {

    public EntityNotFoundException(String message) {
        super(message);
    }
}


package edu.awieclawski.app.exception;

import edu.awieclawski.app.exception.base.AuthorisedException;

public class UpdateEntityException extends AuthorisedException {

    public UpdateEntityException(String message) {
        super(message);
    }
}


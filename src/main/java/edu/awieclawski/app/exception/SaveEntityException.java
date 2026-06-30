package edu.awieclawski.app.exception;

import edu.awieclawski.app.exception.base.AuthorisedException;

public class SaveEntityException extends AuthorisedException {

    public SaveEntityException(String message) {
        super(message);
    }
}


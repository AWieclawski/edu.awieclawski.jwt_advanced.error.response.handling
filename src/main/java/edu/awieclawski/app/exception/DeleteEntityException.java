package edu.awieclawski.app.exception;

import edu.awieclawski.app.exception.base.AuthorisedException;

public class DeleteEntityException extends AuthorisedException {

    public DeleteEntityException(String message) {
        super(message);
    }
}


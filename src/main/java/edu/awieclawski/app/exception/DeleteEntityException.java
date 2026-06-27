package edu.awieclawski.app.exception;

import edu.awieclawski.app.exception.base.BaseRestApiException;

public class DeleteEntityException extends BaseRestApiException {

    public DeleteEntityException(String message) {
        super(message);
    }
}


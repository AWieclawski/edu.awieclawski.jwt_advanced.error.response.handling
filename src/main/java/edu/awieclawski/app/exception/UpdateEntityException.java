package edu.awieclawski.app.exception;

import edu.awieclawski.app.exception.base.BaseRestApiException;

public class UpdateEntityException extends BaseRestApiException {

    public UpdateEntityException(String message) {
        super(message);
    }
}


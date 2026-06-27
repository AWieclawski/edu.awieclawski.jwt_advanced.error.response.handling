package edu.awieclawski.app.exception;

import edu.awieclawski.app.exception.base.BaseRestApiException;

public class SaveEntityException extends BaseRestApiException {

    public SaveEntityException(String message) {
        super(message);
    }
}


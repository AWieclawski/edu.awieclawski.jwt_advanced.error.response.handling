package edu.awieclawski.app.exception;

import edu.awieclawski.app.exception.base.BaseRestApiException;

public class RequestHeaderValueException extends BaseRestApiException {

    public RequestHeaderValueException(String message) {
        super(message);
    }
}

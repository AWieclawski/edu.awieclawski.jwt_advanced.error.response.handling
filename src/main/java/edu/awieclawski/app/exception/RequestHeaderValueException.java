package edu.awieclawski.app.exception;

import edu.awieclawski.app.exception.base.AuthorisedException;

public class RequestHeaderValueException extends AuthorisedException {

    public RequestHeaderValueException(String message) {
        super(message);
    }
}

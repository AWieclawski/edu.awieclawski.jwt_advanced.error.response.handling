package edu.awieclawski.app.exception;

import edu.awieclawski.app.exception.base.AuthorisedException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorIdSolver {

    public static UUID solve(Exception exception) {
        UUID uuid = UUID.randomUUID();
        if (exception instanceof AuthorisedException && ((AuthorisedException) exception).getErrorId() != null) {
            uuid = ((AuthorisedException) exception).getErrorId();
        }
        return uuid;
    }
}

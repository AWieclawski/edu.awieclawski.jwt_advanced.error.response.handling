package edu.awieclawski.app.jwt.inn;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component(JwtPasswordEncoder.BEAN_NAME)
class JwtPasswordEncoder {

    static final String BEAN_NAME = "edu.awieclawski.app.jwt.inn.JwtPasswordEncoder";

    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }
}

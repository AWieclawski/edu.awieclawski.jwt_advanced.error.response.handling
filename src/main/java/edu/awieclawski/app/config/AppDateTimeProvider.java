package edu.awieclawski.app.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.TimeZone;

@Slf4j
@Component(value = AppDateTimeProvider.BEAN_NAME)
public class AppDateTimeProvider {

    public static final String BEAN_NAME = "edu.awieclawski.app.config.AppDateTimeProvider";

    @Value("${application.timezone}")
    private String applicationTimeZone;

    private TimeZone timeZone;

    @PostConstruct
    public void executeAfterMain() {
        timeZone = TimeZone.getTimeZone(applicationTimeZone);
        TimeZone.setDefault(TimeZone.getTimeZone(applicationTimeZone));
        log.debug("Initialized TimeZone: [{}/{}]", TimeZone.getDefault(), applicationTimeZone);
    }

    public Timestamp getCurrentTimestamp() {
        return Timestamp.valueOf(getCurrentLocalDateTime());
    }

    public Date getCurrentDate() {
        return java.sql.Timestamp.valueOf(getCurrentLocalDateTime());
    }

    public LocalDateTime getCurrentLocalDateTime() {
        return LocalDateTime.now(timeZone.toZoneId());
    }

    public LocalDate getCurrentLocalDate() {
        return LocalDate.now(timeZone.toZoneId());
    }

}

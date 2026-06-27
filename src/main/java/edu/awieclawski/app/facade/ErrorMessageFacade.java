package edu.awieclawski.app.facade;

import edu.awieclawski.app.cache.AuthorisedUserDto;
import edu.awieclawski.app.cache.UserCacheService;
import edu.awieclawski.app.config.AppDateTimeProvider;
import edu.awieclawski.app.dto.ErrorResponseDto;
import edu.awieclawski.app.exception.ErrorIdSolver;
import edu.awieclawski.app.mapper.ErrorMessageMapper;
import edu.awieclawski.app.model.ErrorMessageEntity;
import edu.awieclawski.app.service.ErrorMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.WebRequest;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static edu.awieclawski.app.util.DateTimeUtils.timestampToStringMulti;

@Slf4j
@Service(ErrorMessageFacade.BEAN_NAME)
@RequiredArgsConstructor
@DependsOn({ErrorMessageService.BEAN_NAME, UserCacheService.BEAN_NAME, AppDateTimeProvider.BEAN_NAME})
public class ErrorMessageFacade {

    public static final String BEAN_NAME = "edu.awieclawski.app.facade.ErrorMessageFacade";

    private final AppDateTimeProvider appDateTimeProvider;
    private final UserCacheService userCacheService;
    private final ErrorMessageService errorMessageService;

    public ErrorResponseDto createMessage(String message, HttpStatus httpStatus, WebRequest webRequest, Exception ex) {
        if (webRequest == null) {
            return notAuthenticatedUserError(message, httpStatus, ex);
        }
        return authenticatedUserError(message, httpStatus, webRequest, ex);
    }

    private ErrorResponseDto notAuthenticatedUserError(String message, HttpStatus httpStatus, Exception ex) {
        Timestamp createdAt = appDateTimeProvider.getCurrentTimestamp();
        ErrorResponseDto errorResponseDto = ErrorMessageMapper.getErrorResponseDto(httpStatus, message, ex, createdAt);
        log.warn("Error id [{}] short mapped: {} : {} | {} [{}]", errorResponseDto.getErrorId(), ex.getClass().getSimpleName(),
                httpStatus, message, createdAt);
        return errorResponseDto;
    }


    private ErrorResponseDto authenticatedUserError(String message, HttpStatus httpStatus, WebRequest webRequest, Exception ex) {
        AuthorisedUserDto authorisedUserDto = null;
        String userLogin = webRequest.getRemoteUser();
        String url = webRequest.getDescription(false);
        Principal userPrincipal = webRequest.getUserPrincipal();
        if (userLogin != null) {
            authorisedUserDto = userCacheService.getByUserLogin(userLogin);
        }
        Timestamp currentTimestamp = appDateTimeProvider.getCurrentTimestamp();
        UUID uuid = ErrorIdSolver.solve(ex);
        ErrorResponseDto errorResponseDto = ErrorResponseDto.builder()
                .message(message)
                .errorCode(httpStatus.value())
                .tokenShort(authorisedUserDto != null ? authorisedUserDto.getTokenShort() : null)
                .userLogin(userLogin)
                .createdAt(timestampToStringMulti(currentTimestamp))
                .url(url)
                .errorId(userLogin != null ? uuid : null)
                .build();

        if (userPrincipal instanceof AbstractAuthenticationToken && ((AbstractAuthenticationToken) userPrincipal).isAuthenticated()) {
            Timestamp endDate = appDateTimeProvider.getCurrentTimestamp();
            Timestamp startDate = new Timestamp(endDate.getTime() - (60 * 1000)); // one minute before
            List<ErrorMessageEntity> userErrors = errorMessageService.getByUserLoginAndTimeRange(userLogin, startDate, endDate);
            final ErrorResponseDto finalErrorResponseDto = errorResponseDto;
            List<ErrorMessageEntity> matched = userErrors.stream()
                    .filter(Objects::nonNull)
                    .filter(error -> error.hashCode() == finalErrorResponseDto.hashCode())
                    .collect(Collectors.toList());
            if (matched.isEmpty()) {
                // async process do not stop creation
                errorMessageService.insertErrorMessageAsync(errorResponseDto);
            } else {
                log.debug("Found matched errors qty: {}", matched.size());
                errorResponseDto = ErrorMessageMapper.toDto(matched.get(matched.size() - 1));
            }
        }

        log.warn("Error Response: {} | {} | {} | {} | {} - Error id [{}]",
                errorResponseDto.getUserLogin(), errorResponseDto.getTokenShort(), errorResponseDto.getMessage(),
                errorResponseDto.getErrorCode(), errorResponseDto.getUrl(), errorResponseDto.getErrorId());
        return errorResponseDto;
    }


}

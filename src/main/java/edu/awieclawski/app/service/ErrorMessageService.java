package edu.awieclawski.app.service;

import edu.awieclawski.app.dao.ErrorMessageRepository;
import edu.awieclawski.app.dto.ErrorResponseDto;
import edu.awieclawski.app.mapper.ErrorMessageMapper;
import edu.awieclawski.app.model.ErrorMessageEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service(ErrorMessageService.BEAN_NAME)
@RequiredArgsConstructor
public class ErrorMessageService {

    public final static String BEAN_NAME = "edu.awieclawski.app.service.ErrorMessageService";

    private final ErrorMessageRepository errorMessageRepository;

    public List<ErrorMessageEntity> getByUserLoginAndTimeRange(String userLogin, Timestamp startDate, Timestamp endDate) {
        return errorMessageRepository.findByCreatedAtBetween(userLogin, startDate, endDate);
    }

    public List<ErrorResponseDto> getAllDtoErrors() {
        return errorMessageRepository.findAll()
                .stream().map(ErrorMessageMapper::toDto)
                .collect(Collectors.toList());
    }

    // Mark method as async (runs in a background thread)
    @Async
    public CompletableFuture<Void> insertErrorMessageAsync(final ErrorResponseDto errorResponseDto) {
        return CompletableFuture.runAsync(() -> {
            log.debug("Starting inserting of Error Entity");
            try {
                ErrorMessageEntity errorMessageEntity = ErrorMessageMapper.toEntity(errorResponseDto);
                Thread processThread = getErrorMessageInsertThread(errorMessageEntity);
                processThread.start();
            } catch (Exception e) {
                log.error("Insert Error Message Error {}", e.getMessage(), e);
            }
        });
    }

    private Thread getErrorMessageInsertThread(ErrorMessageEntity errorMessageEntity) {
        return new Thread(() -> {
            ErrorMessageEntity saved = errorMessageRepository.save(errorMessageEntity);
            log.debug("Error Message saved with id [{}]", saved.getId());
        });
    }
}

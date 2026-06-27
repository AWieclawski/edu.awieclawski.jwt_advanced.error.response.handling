package edu.awieclawski.app.cache;

import edu.awieclawski.app.config.AppDateTimeProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service(UserCacheService.BEAN_NAME)
@RequiredArgsConstructor
public class UserCacheService {

    public final static String BEAN_NAME = "edu.awieclawski.app.cache.UserCacheService";

    private final UserCacheRepository userCacheRepository;
    private final AppDateTimeProvider appDateTimeProvider;

    private volatile boolean isCacheUpdated;

    @PostConstruct
    public void init() {
        isCacheUpdated = false;
    }

    @Scheduled(fixedRate = 60 * 1000, initialDelay = 30 * 1000)
    public void clearCache() {
        if (!isCacheUpdated) {
            log.debug("Starting scheduled user cache verification");
            verifyUserCache();
        }
    }

    public AuthorisedUserDto getByUserLogin(String login) {
        return userCacheRepository.findByUserLogin(login);
    }

    // Mark method as async (runs in a background thread)
    @Async
    public CompletableFuture<Void> insertUserCacheAsync(final AuthorisedUserDto authorisedUserDto) {
        return CompletableFuture.runAsync(() -> {
            try {
                Thread processThread = getUserCacheInsertThread(authorisedUserDto);
                processThread.start();
            } catch (Exception e) {
                log.error("Insert User Cache Error {}", e.getMessage(), e);
            }
            if (!isCacheUpdated) {
                verifyUserCache();
            }
        });
    }

    private void verifyUserCache() {
        try {
            Thread processThread = getUserCacheUpdateThread();
            processThread.start();
        } catch (Exception e) {
            log.error("Verify User Cache Error {}", e.getMessage(), e);
        }
    }

    private Thread getUserCacheInsertThread(AuthorisedUserDto authorisedUserDto) {
        return new Thread(() -> {
            log.debug("Starting inserting of user cache");
            final Date dateNow = appDateTimeProvider.getCurrentDate();
            AuthorisedUserDto byUserLogin = userCacheRepository.findByUserLogin(authorisedUserDto.getUserLogin());
            if (byUserLogin == null
                    || byUserLogin.getTokenValidTo().before(dateNow)
                    || (byUserLogin.getTokenValidTo().after(dateNow)
                    && !byUserLogin.getTokenShort().equals(authorisedUserDto.getTokenShort()))
            ) {
                userCacheRepository.insertUserData(authorisedUserDto);
            }
        });
    }

    private Thread getUserCacheUpdateThread() {
        return new Thread(() -> {
            isCacheUpdated = true;
            final Date dateNow = appDateTimeProvider.getCurrentDate();
            Map<String, AuthorisedUserDto> allMap = userCacheRepository.findAll();
            log.debug("Starting verifying of user cache size: {}", allMap.size());
            allMap.forEach((k, v) -> {
                        if (v.getTokenValidTo().before(dateNow)) {
                            allMap.remove(k);
                        }
                    }
            );
            isCacheUpdated = false;
        });
    }
}

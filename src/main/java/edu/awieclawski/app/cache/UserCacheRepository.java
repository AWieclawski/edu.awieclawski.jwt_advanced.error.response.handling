package edu.awieclawski.app.cache;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository(UserCacheRepository.BEAN_NAME)
@RequiredArgsConstructor
@DependsOn(DataBusCache.BEAN_NAME)
class UserCacheRepository {

    public static final String BEAN_NAME = "eedu.awieclawski.app.cache.UserCacheRepository";

    private final DataBusCache dataBusCache;

    public AuthorisedUserDto findByUserLogin(String userLogin) {
        return dataBusCache.getAuthorisedUsersMap().get(userLogin);
    }

    public AuthorisedUserDto insertUserData(AuthorisedUserDto authorisedUserDto) {
        dataBusCache.getAuthorisedUsersMap().put(authorisedUserDto.getUserLogin(), authorisedUserDto);
        return dataBusCache.getAuthorisedUsersMap().get(authorisedUserDto.getUserLogin());
    }

    public Map<String, AuthorisedUserDto> findAll() {
        return dataBusCache.getAuthorisedUsersMap();
    }
}

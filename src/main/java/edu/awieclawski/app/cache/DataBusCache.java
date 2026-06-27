package edu.awieclawski.app.cache;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component(DataBusCache.BEAN_NAME)
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
class DataBusCache {
    public static final String BEAN_NAME = "edu.awieclawski.app.cache.DataBusCache";

    private volatile Map<String, AuthorisedUserDto> authorisedUsersMap;

    Map<String, AuthorisedUserDto> getAuthorisedUsersMap() {
        if (this.authorisedUsersMap == null) {
            this.authorisedUsersMap = new ConcurrentHashMap<>();
        }
        return this.authorisedUsersMap;
    }

}

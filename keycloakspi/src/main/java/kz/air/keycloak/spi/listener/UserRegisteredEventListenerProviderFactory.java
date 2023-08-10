package kz.air.keycloak.spi.listener;

import org.keycloak.Config;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class UserRegisteredEventListenerProviderFactory  implements EventListenerProviderFactory {

    @Override
    public UserRegisteredListenerProvider create(KeycloakSession session){
        return new UserRegisteredListenerProvider(session);
    }

    @Override
    public void init(Config.Scope config) {

    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
    }

    @Override
    public void close() {
    }

    @Override
    public String getId() {
        return "UserRegistered";
    }

}

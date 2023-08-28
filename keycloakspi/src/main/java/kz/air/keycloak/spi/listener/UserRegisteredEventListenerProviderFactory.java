package kz.air.keycloak.spi.listener;

import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class UserRegisteredEventListenerProviderFactory  implements EventListenerProviderFactory {
    private static final Logger log = Logger.getLogger(UserRegisteredEventListenerProviderFactory.class);
    private String externalService;
    @Override
    public UserRegisteredListenerProvider create(KeycloakSession session){
        return new UserRegisteredListenerProvider(session, externalService );
    }

    @Override
    public void init(Config.Scope config) {
        this.externalService = config.get("external-service");
        log.info("Using %s as external service ".formatted(externalService));
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
    }

    @Override
    public void close() {
    }

    @Override
    public String getId() {
        return "user-registered";
    }

}

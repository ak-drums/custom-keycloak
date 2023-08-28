package kz.air.keycloak.spi.listener;

import kz.air.keycloak.spi.transaction.RegisteredUserSyncTransaction;
import org.jboss.logging.Logger;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;


public class UserRegisteredListenerProvider implements EventListenerProvider {
    private final KeycloakSession session;
    private static final Logger log = Logger.getLogger(UserRegisteredListenerProvider.class);

    private final String externalService;

    public UserRegisteredListenerProvider(KeycloakSession session, String externalService) {
        this.session = session;
        this.externalService = externalService;
    }

    @Override
    public void onEvent(Event event) {
        if (event.getType().equals(EventType.REGISTER)) {
            session.getTransactionManager().enlistPrepare(new RegisteredUserSyncTransaction(session, event, externalService));
        }
    }

    @Override
    public void onEvent(AdminEvent var1, boolean var2) {}

    @Override
    public void close() {

    }
}

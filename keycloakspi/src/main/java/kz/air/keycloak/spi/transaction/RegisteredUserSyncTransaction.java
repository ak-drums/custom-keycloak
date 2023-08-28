package kz.air.keycloak.spi.transaction;

import org.apache.http.entity.StringEntity;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.events.Event;
import org.keycloak.models.AbstractKeycloakTransaction;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import java.util.List;
import java.util.Map;

public class RegisteredUserSyncTransaction extends AbstractKeycloakTransaction {

    private final KeycloakSession keycloakSession;
    private final Event event;

    private final String externalService;

    public RegisteredUserSyncTransaction(KeycloakSession session, Event event, String externalService) {
        this.keycloakSession = session;
        this.event = event;
        this.externalService = externalService;
    }
    @Override
    protected void commitImpl() {
        try {
            RealmModel realmModel = keycloakSession.realms().getRealm(event.getRealmId());
            UserModel userModel = keycloakSession.users().getUserById(realmModel, event.getUserId());

            Map<String, List<String>> attributes = userModel.getAttributes();

            logger.info(String.format("Realm %s Received User Register Event with User Attributes %s", realmModel.getName(),
                    attributes));

            logger.info("About to send data to external service");

            StringEntity stringEntity = new StringEntity("Hello world"); //Body
            SimpleHttp.Response response = SimpleHttp.doPost(externalService, keycloakSession).entity(stringEntity).asResponse();
            logger.info(String.format("Received response from external API %s", response.asJson()));
        } catch (Exception e) {
            logger.error(String.format("Could not send data to external service. Localized error message %s", e.getLocalizedMessage()));
            throw new RuntimeException("Could not send data to external service !");
        }
    }

    @Override
    protected void rollbackImpl() {
        //
    }
}

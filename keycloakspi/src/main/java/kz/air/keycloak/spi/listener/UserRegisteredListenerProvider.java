package kz.air.keycloak.spi.listener;

import org.apache.http.entity.StringEntity;
import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.InternalServerErrorException;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import java.util.List;
import java.util.Map;


public class UserRegisteredListenerProvider implements EventListenerProvider {
    private final KeycloakSession session;
    private static final Logger log = Logger.getLogger(UserRegisteredListenerProvider.class);


    public UserRegisteredListenerProvider(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public void onEvent(Event event) {

        if (event.getType().equals(EventType.REGISTER)) {
            try {
                RealmModel realmModel = session.realms().getRealm(event.getRealmId());
                UserModel userModel = session.users().getUserById(realmModel, event.getUserId());

                Map<String, List<String>> attributes = userModel.getAttributes();

                log.info(String.format("Realm %s Received User Register Event with details %s And User Attributes %s", realmModel.getName(),
                        event.getDetails(),
                        attributes));
                log.info(String.format("Phone search %s", userModel.getFirstAttribute("phone_number")));
                StringEntity stringEntity = new StringEntity("Hello world"); //Body
                SimpleHttp.Response response = SimpleHttp.doPost("http://192.168.0.154:8090/test", session).entity(stringEntity).asResponse();
                log.info(String.format("Received response from external API %s", response.asJson()));
            } catch (Exception e) {
                log.info(String.format("Caught an exception. Read stack trace below error message %s", e.getLocalizedMessage()));
                e.printStackTrace();
                throw new InternalServerErrorException("Could not send data to external service !");
            }

        }
    }

    @Override
    public void onEvent(AdminEvent var1, boolean var2) {}

    @Override
    public void close() {

    }
}

package kz.air.keycloak.spi.requiredaction;

import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.authentication.RequiredActionFactory;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class VerifyPhoneNumberRequiredActionFactory implements RequiredActionFactory {
    private static Logger logger = Logger.getLogger(VerifyPhoneNumberRequiredActionFactory.class);
    public final RequiredActionProvider singleton = new VerifyPhoneNumberAction();
    @Override
    public String getDisplayText() {
        return "Verify phone number";
    }

    @Override
    public RequiredActionProvider create(KeycloakSession session) {
        return singleton;
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
        return VerifyPhoneNumberAction.PROVIDER_ID;

    }
}

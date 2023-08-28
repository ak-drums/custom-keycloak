package kz.air.keycloak.spi.authenticator;

import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.authentication.ConfigurableAuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.Arrays;
import java.util.List;

public class AttributeUsernamePasswordAuthenticatorFactory implements AuthenticatorFactory, ConfigurableAuthenticatorFactory {
    private static final Logger log = Logger.getLogger(AttributeUsernamePasswordAuthenticatorFactory.class);

    public static final String PROVIDER_ID = "add-name";
    private AttributeUsernamePasswordAuthenticator SINGLETON;

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getDisplayType() {
        return "Attribute Username Password Authenticator";
    }

    @Override
    public String getReferenceCategory() {
        return null;
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return Arrays.asList(AuthenticationExecutionModel.Requirement.REQUIRED).toArray(new AuthenticationExecutionModel.Requirement[0]);
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    @Override
    public String getHelpText() {
        return null;
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return null;
    }

    @Override
    public void init(Config.Scope scope) {
        String additionalUsername = scope.get("username", "phone_number");
        this.SINGLETON = new AttributeUsernamePasswordAuthenticator(additionalUsername);
        log.info("Using %s ".formatted(additionalUsername));
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {

    }

    @Override
    public void close() {

    }

    @Override
    public Authenticator create(KeycloakSession session) {
        return SINGLETON;
    }
}
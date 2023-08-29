package kz.air.keycloak.spi.resource;

import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;

public class CustomResourceProvider implements RealmResourceProvider {
    private final KeycloakSession session;

    public CustomResourceProvider(KeycloakSession keycloakSession) {
        this.session = keycloakSession;
    }

    @Override
    public Object getResource() {
        return new CustomResource(session);
    }

    @Override
    public void close() {
    }

}

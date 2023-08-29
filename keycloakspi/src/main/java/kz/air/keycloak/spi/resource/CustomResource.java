package kz.air.keycloak.spi.resource;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager;


public class CustomResource {
    private static final org.jboss.logging.Logger log = Logger.getLogger(CustomResource.class);

    private final KeycloakSession session;
    //private final AuthenticationManager.AuthResult authResult;

    public CustomResource(KeycloakSession keycloakSession) {
        this.session = keycloakSession;
        //his.authResult = new AppAuthManager.BearerTokenAuthenticator(session).authenticate();
    }
    @GET
    @Path("{param}/test")
    @Produces(MediaType.APPLICATION_JSON)
    public String getRegistrationLink(final @PathParam("param") String param) {
        log.debug(String.format("Received get request with param %s", param));
        return "Hello world";
    }

/*    private void authenticate() {
        if (authResult == null) {
            throw new NotAuthorizedException("Bearer");
        } else if (authResult.getToken().getRealmAccess() == null || !authResult.getToken().getRealmAccess().isUserInRole("SUPER_ADMIN")) {
            throw new ForbiddenException("Does not have SUPER_ADMIN role ");
        }
    }*/


}

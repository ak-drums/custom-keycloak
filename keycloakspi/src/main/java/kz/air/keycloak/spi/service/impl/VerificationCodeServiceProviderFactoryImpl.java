package kz.air.keycloak.spi.service.impl;

import kz.air.keycloak.spi.service.VerificationCodeService;
import kz.air.keycloak.spi.service.VerificationCodeServiceProviderFactory;
import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class VerificationCodeServiceProviderFactoryImpl implements VerificationCodeServiceProviderFactory {
    private int codeLength;
    private int codeTtlInSeconds;
    private int maxAttempts;
    private static Logger logger = Logger.getLogger(VerificationCodeServiceProviderFactoryImpl.class);

    @Override
    public VerificationCodeService create(KeycloakSession session) {
        return new PhoneVerificationService(session, codeLength, codeTtlInSeconds, maxAttempts);
    }

    @Override
    public void init(Config.Scope config) {
        this.codeLength = config.getInt("code-length", 4);
        this.codeTtlInSeconds = config.getInt("ttl", 60);
        this.maxAttempts = config.getInt("max-attempts", 3);
        logger.info("Code length = %s, codeTtlInSeconds = %s, maxAttempts = %s".formatted(codeLength, codeTtlInSeconds, maxAttempts));
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return "sms-code-service";
    }
}

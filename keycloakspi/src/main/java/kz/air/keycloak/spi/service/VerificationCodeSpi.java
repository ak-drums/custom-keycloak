package kz.air.keycloak.spi.service;

import org.keycloak.provider.Provider;
import org.keycloak.provider.ProviderFactory;
import org.keycloak.provider.Spi;

public class VerificationCodeSpi implements Spi {
    @Override
    public boolean isInternal() {
        return true;
    }

    @Override
    public String getName() {
        return "ver-code";
    }

    @Override
    public Class<? extends Provider> getProviderClass() {
        return VerificationCodeService.class;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Class<? extends ProviderFactory> getProviderFactoryClass() {
        return VerificationCodeServiceProviderFactory.class;
    }
}

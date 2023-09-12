package kz.air.keycloak.spi.service;


import kz.air.keycloak.spi.jpa.VerificationCode;
import kz.air.keycloak.spi.models.VerificationCodeState;
import org.keycloak.models.UserModel;
import org.keycloak.provider.Provider;

import java.util.Optional;

public interface VerificationCodeService extends Provider {

    VerificationCodeState sendVerificationCode(UserModel userModel);

    boolean verifyCode(UserModel userModel, String code, VerificationCode verificationCode);

    boolean requiresVerification(UserModel userModel);

    Optional<VerificationCode> findMostRecent(String phone, String realmId);

    int getCodeLength();

    int getCodeTtlInSeconds();

    int getMaxAttempts();

    VerificationCodeState getCurrentState(UserModel userModel);
}

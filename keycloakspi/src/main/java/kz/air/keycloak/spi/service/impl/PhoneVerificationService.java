package kz.air.keycloak.spi.service.impl;

import jakarta.persistence.EntityManager;
import kz.air.keycloak.spi.jpa.VerificationCode;
import kz.air.keycloak.spi.models.VerificationCodeState;
import kz.air.keycloak.spi.service.VerificationCodeService;
import org.jboss.logging.Logger;
import org.keycloak.common.util.SecretGenerator;
import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import java.time.LocalDateTime;
import java.util.Optional;

public class PhoneVerificationService implements VerificationCodeService {

    public final int codeLength;
    public final int codeTtlInSeconds;
    public final int maxAttempts;

    private final KeycloakSession session;
    private final EntityManager entityManager;
    private final RealmModel realmModel;

    public PhoneVerificationService(KeycloakSession session,
                                    int codeLength,
                                    int codeTtlInSeconds,
                                    int maxAttempts) {
        this.session = session;
        this.entityManager = session.getProvider(JpaConnectionProvider.class).getEntityManager();
        this.codeLength = codeLength;
        this.codeTtlInSeconds = codeTtlInSeconds;
        this.maxAttempts = maxAttempts;
        this.realmModel = session.getContext().getRealm();
    }
    private static final Logger log = Logger.getLogger(PhoneVerificationService.class);

    @Override
    public VerificationCodeState sendVerificationCode(UserModel userModel) {
        String phone = userModel.getFirstAttribute("phone_number");
        VerificationCodeState codeState = getCurrentState(userModel);

        if(codeState.stateIs(VerificationCodeState.State.no_verification_code) || codeState.stateIs(VerificationCodeState.State.expired_code)) {
            VerificationCode verificationCode = create(phone, realmModel.getId());
            //Send SMS here
            log.info("Successfully sent verification code %s".formatted(verificationCode.toString()));
            return new VerificationCodeState(verificationCode, VerificationCodeState.State.active_verification_code);
        } else {
            log.info("Can not send verification code to user %s. State is %s".formatted(userModel.getUsername(), codeState.getState()));
            return codeState;
        }
    }

    @Override
    public boolean verifyCode(UserModel userModel, String code, VerificationCode verificationCode) {
        if(verificationCode.getCode().equals(code)) {
            log.info("User %s successfully verified phone number %s ".formatted(userModel.getUsername(), verificationCode.getIdentifier()));
            userModel.setSingleAttribute("phone_number_verified", "true");
            return true;
        }

        verificationCode.setAttempts(verificationCode.getAttempts() + 1);
        entityManager.persist(verificationCode);
        return false;
    }


    public VerificationCodeState getCurrentState(UserModel userModel) {
        String phone = userModel.getFirstAttribute("phone_number");

        if(!requiresVerification(userModel)) {
            return VerificationCodeState.verified();
        }

        Optional<VerificationCode> optionalVerificationCode = findMostRecent(phone, realmModel.getId());

        if(optionalVerificationCode.isEmpty()) {
            return new VerificationCodeState(null, VerificationCodeState.State.no_verification_code);
        }

        VerificationCode verificationCode = optionalVerificationCode.get();
        if(LocalDateTime.now().isAfter(verificationCode.getExpiresAt())) {
            return new VerificationCodeState(verificationCode, VerificationCodeState.State.expired_code);
        }

        if(verificationCode.getAttempts().compareTo(maxAttempts) >= 0) {
            return new VerificationCodeState(verificationCode, VerificationCodeState.State.exceeded_max_attempts);
        }

        return new VerificationCodeState(verificationCode, VerificationCodeState.State.active_verification_code);
    }

    @Override
    public boolean requiresVerification(UserModel userModel) {
        return userModel.getAttributeStream("phone_number_verified")
                .findAny()
                .map(v -> !Boolean.parseBoolean(v))
                .orElse(Boolean.TRUE);
    }

    @Override
    public Optional<VerificationCode> findMostRecent(String phone, String realmId) {
        return entityManager.createNamedQuery("VerificationCode.findMostRecent", VerificationCode.class)
                    .setParameter("identifier", phone)
                    .setParameter("realmId", realmId)
                    .getResultStream()
                    .findAny();
    }

    public VerificationCode create(String phone, String realmId) {
        String code = SecretGenerator.getInstance().randomString(codeLength, SecretGenerator.DIGITS); // Used to generate secret code
        LocalDateTime expDate = LocalDateTime.now().plusSeconds(codeTtlInSeconds);

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setCode(code);
        verificationCode.setIdentifier(phone);
        verificationCode.setExpiresAt(expDate);
        verificationCode.setCode(code);
        verificationCode.setRealmId(realmId);
        verificationCode.setCreatedAt(LocalDateTime.now());
        verificationCode.setAttempts(0);

        entityManager.persist(verificationCode);
        return verificationCode;
    }

    @Override
    public void close() {
    }

    public int getCodeLength() {
        return codeLength;
    }

    public int getCodeTtlInSeconds() {
        return codeTtlInSeconds;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }
}

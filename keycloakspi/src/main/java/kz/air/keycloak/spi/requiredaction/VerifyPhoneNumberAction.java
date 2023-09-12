package kz.air.keycloak.spi.requiredaction;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import kz.air.keycloak.spi.jpa.VerificationCode;
import kz.air.keycloak.spi.models.VerificationCodeState;
import kz.air.keycloak.spi.service.VerificationCodeService;
import org.jboss.logging.Logger;
import org.keycloak.authentication.InitiatedActionSupport;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.function.Consumer;

import static kz.air.keycloak.spi.models.VerificationCodeState.State.*;

public class VerifyPhoneNumberAction implements RequiredActionProvider {

    public VerifyPhoneNumberAction() {
    }
    private static Logger logger = Logger.getLogger(VerifyPhoneNumberAction.class);
    public static final String PROVIDER_ID = "mobile-number-ra";

    private static final String MOBILE_NUMBER_FIELD = "mobile_number";

    @Override
    public InitiatedActionSupport initiatedActionSupport() {
        return InitiatedActionSupport.SUPPORTED;
    }

    @Override
    public void evaluateTriggers(RequiredActionContext context) {
        // you would implement something like the following, if this required action should be "self registering" at the user
         if (getPhoneVerificationService(context.getSession()).requiresVerification(context.getUser())) {
         	context.getUser().addRequiredAction(PROVIDER_ID);
        }
    }

    @Override
    public void requiredActionChallenge(RequiredActionContext context) {
        UserModel userModel = context.getUser();
        VerificationCodeService verificationCodeService = getPhoneVerificationService(context.getSession());
        VerificationCodeState codeState = verificationCodeService.getCurrentState(userModel);
        updateContext(context, codeState, null);
    }

    @Override
    public void processAction(RequiredActionContext context) {
        VerificationCodeService verificationCodeService = getPhoneVerificationService(context.getSession());

        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();

        if(formData.getFirst("resendSms") != null) {
            sendSms(verificationCodeService, context);
        } else {
            verifyCode(verificationCodeService, context, formData);
        }
    }

    @Override
    public void close() {
    }

    private void sendSms(VerificationCodeService verificationCodeService, RequiredActionContext context) {
        UserModel user = context.getUser();
        logger.info("About to send verification code to %s from requiredActionChallenge method".formatted(user.getUsername()));
        VerificationCodeState verificationCodeState = verificationCodeService.sendVerificationCode(context.getUser());
        updateContext(context, verificationCodeState, null);
    }

    private void verifyCode(VerificationCodeService verificationCodeService, RequiredActionContext context, MultivaluedMap<String, String> formData) {
        UserModel user = context.getUser();
        VerificationCodeState verificationCodeState = verificationCodeService.getCurrentState(user);
        if(verificationCodeState.getState() == VerificationCodeState.State.active_verification_code) {
            VerificationCode verificationCode = verificationCodeState.getVerificationCode();
            String code = formData.getFirst(MOBILE_NUMBER_FIELD);
            boolean verified = verificationCodeService.verifyCode(user, code, verificationCode);
            if(verified) {
                updateContext(context, VerificationCodeState.verified(verificationCode), null);
            } else if(verificationCode.getAttempts() >= verificationCodeService.getMaxAttempts()){
                updateContext(context, VerificationCodeState.exceeded(verificationCode), form -> form.addError(new FormMessage(MOBILE_NUMBER_FIELD, "invalid_code_exceeded_max_attempts")));
            } else {
                updateContext(context, verificationCodeState, form -> form.addError(new FormMessage(MOBILE_NUMBER_FIELD, "invalid_code")));
            }
        } else {
            updateContext(context, verificationCodeState, null);
        }
    }

    private void updateContext(RequiredActionContext context, VerificationCodeState codeState, Consumer<LoginFormsProvider> formConsumer) {
        logger.info("Verify Phone Action state %s".formatted(codeState.getVerificationCode()));
        context.form().setAttribute("exceededMaxAttempts", false);
        switch (codeState.getState()) {
            case verified:
                context.success();
                return;
            case no_verification_code:
            case expired_code:
                context.form().setAttribute("expInSeconds", 0);
                context.challenge(createForm(context, formConsumer));
                return;
            case exceeded_max_attempts:
                context.form().setAttribute("expInSeconds", ChronoUnit.SECONDS.between(LocalDateTime.now(),codeState.getVerificationCode().getExpiresAt()));
                context.form().setAttribute("exceededMaxAttempts", true);
                context.challenge(createForm(context, formConsumer != null ?
                        formConsumer.andThen(v -> v.addError(new FormMessage(MOBILE_NUMBER_FIELD,"exceeded_max_attempts"))) :
                        form -> form.addError(new FormMessage(MOBILE_NUMBER_FIELD, "invalid_code_exceeded_max_attempts"))));
                return;
            case active_verification_code:
                context.form().setAttribute("expInSeconds", ChronoUnit.SECONDS.between(LocalDateTime.now(), codeState.getVerificationCode().getExpiresAt()));
                context.challenge(createForm(context, formConsumer));
                return;
            default:
                logger.error("Could not process state %s".formatted(codeState.getVerificationCode()));
                context.failure();
        }
    }
    private Response createForm(RequiredActionContext context, Consumer<LoginFormsProvider> formConsumer) {
        LoginFormsProvider form = context.form();
        form.setAttribute("username", context.getUser().getUsername());

        String mobileNumber = context.getUser().getFirstAttribute(MOBILE_NUMBER_FIELD);
        form.setAttribute(MOBILE_NUMBER_FIELD, mobileNumber == null ? "" : mobileNumber);

        if (formConsumer != null) {
            formConsumer.accept(form);
        }

        return form.createForm("update-mobile-number.ftl");
    }

    public VerificationCodeService getPhoneVerificationService(KeycloakSession session) {
        return session.getProvider(VerificationCodeService.class);
    }
}

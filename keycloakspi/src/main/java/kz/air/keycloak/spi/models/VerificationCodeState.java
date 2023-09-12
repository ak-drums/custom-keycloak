package kz.air.keycloak.spi.models;

import kz.air.keycloak.spi.jpa.VerificationCode;

public class VerificationCodeState {
    private final VerificationCode verificationCode;
    private final State state;

    public VerificationCodeState(VerificationCode verificationCode, State state) {
        this.verificationCode = verificationCode;
        this.state = state;
    }

    public static VerificationCodeState verified(VerificationCode verificationCode) {
        return new VerificationCodeState(verificationCode, State.verified);
    }

    public static VerificationCodeState verified() {
        return new VerificationCodeState(null, State.verified);
    }

    public static VerificationCodeState exceeded(VerificationCode verificationCode) {
        return new VerificationCodeState(verificationCode, State.exceeded_max_attempts);
    }

    public enum State {
        verified,
        no_verification_code,
        expired_code,
        exceeded_max_attempts,
        active_verification_code
    }

    public VerificationCode getVerificationCode() {
        return verificationCode;
    }

    public State getState() {
        return state;
    }

    public boolean stateIs(State state) {
        return this.state == state;
    }
}

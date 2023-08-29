package kz.air.keycloak.spi.validator;

import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.provider.ConfiguredProvider;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.userprofile.UserProfileAttributeValidationContext;
import org.keycloak.validate.*;
import org.keycloak.validate.validators.ValidatorConfigValidator;

import java.util.*;

public class UniqueAttributeValidator extends AbstractStringValidator implements ConfiguredProvider {
    public static final String ID = "unique-attribute";

    private static final Logger log = Logger.getLogger(UniqueAttributeValidator.class);
    public static final String errorMessage = "UserWithAttributeAlreadyExists";
    private static final List<ProviderConfigProperty> configProperties = new ArrayList<>();
    private static final String attributeName = "attribute_name";

    static {
        ProviderConfigProperty property;
        property = new ProviderConfigProperty();
        property.setName(attributeName);
        property.setLabel("Attribute name");
        property.setHelpText("Attribute name");
        property.setType(ProviderConfigProperty.STRING_TYPE);
        configProperties.add(property);
    }
    @Override
    public String getId() {
        return ID;
    }

    @Override
    protected void doValidate(String value, String inputHint, ValidationContext context, ValidatorConfig config) {
        String attributeNameValue = config.getString(attributeName);

        KeycloakSession session = context.getSession();
        RealmModel realm = session.getContext().getRealm();

        session.users().searchForUserByUserAttributeStream(realm, attributeNameValue, value)
                .findAny()
                .ifPresent(userModel -> {
                    UserModel user = UserProfileAttributeValidationContext.from(context).getAttributeContext().getUser();
                    if (user == null || !Objects.equals(user.getId(), userModel.getId())) {
                            log.info(String.format("User %s also has a phone number %s", userModel.getUsername(), value));
                            context.addError(new ValidationError(ID, inputHint, errorMessage)
                                    .setStatusCode(Response.Status.CONFLICT));
                    }
                });
    }

    @Override
    public String getHelpText() {
        return "Checks if phone exists in db";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return configProperties;
    }

    @Override
    public ValidationResult validateConfig(KeycloakSession session, ValidatorConfig config) {
        Set<ValidationError> errors = new LinkedHashSet<>();
        if (config == null || config == ValidatorConfig.EMPTY) {
            errors.add(new ValidationError(ID, attributeName, ValidatorConfigValidator.MESSAGE_CONFIG_MISSING_VALUE));
        } else if (!config.containsKey(attributeName)) {
            errors.add(new ValidationError(ID, attributeName, ValidatorConfigValidator.MESSAGE_CONFIG_MISSING_VALUE));
        }
        return new ValidationResult(errors);
    }
}

package uk.gov.di.services;

import com.nimbusds.oauth2.sdk.id.Subject;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthenticationResultType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ConfirmSignUpRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ConfirmSignUpResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpResponse;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class CognitoService implements AuthenticationService {

    private static final Logger LOG = LoggerFactory.getLogger(CognitoService.class);

    private CognitoIdentityProviderClient cognitoClient;
    private final String clientId = "3pf8i39bspmlkmd9pqo1s626oe";
    private final String userPoolId = "eu-west-2_JVIkRJSaV";

    public CognitoService(CognitoIdentityProviderClient cognitoClient) {
        this.cognitoClient = cognitoClient;
    }

    @Override
    public UserInfo getInfoForEmail(String email) {
        UserInfo userInfo = new UserInfo(new Subject());
        userInfo.setEmailAddress(email);
        return userInfo;
    }

    @Override
    public boolean isEmailVerificationRequired() {
        return true;
    }

    @Override
    public boolean userExists(String email) {
        try {

            cognitoClient.adminGetUser(
                    AdminGetUserRequest.builder().userPoolId(userPoolId).username(email).build());

        } catch (CognitoIdentityProviderException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean signUp(String email, String password) {
        List<AttributeType> attributes = new ArrayList<>();
        attributes.add(AttributeType.builder().name("email").value(email).build());

        SignUpRequest request =
                SignUpRequest.builder()
                        .clientId(clientId)
                        .username(email)
                        .password(password)
                        .userAttributes(attributes)
                        .build();

        SignUpResponse signUpResponse = cognitoClient.signUp(request);
        LOG.info("signUpResponse: ", signUpResponse.toString());
        return signUpResponse.userConfirmed();
    }

    @Override
    public boolean verifyAccessCode(String username, String code) {
        ConfirmSignUpRequest confirmSignUpRequest =
                ConfirmSignUpRequest.builder()
                        .clientId(clientId)
                        .username(username)
                        .confirmationCode(code)
                        .build();

        try {
            ConfirmSignUpResponse confirmSignUpResponse =
                    cognitoClient.confirmSignUp(confirmSignUpRequest);
            LOG.info("confirmSignUpResult: ", confirmSignUpResponse.toString());
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return false;
        }
        return true;
    }

    @Override
    public boolean login(String email, String password) {
        LinkedHashMap<String, String> authParams = new LinkedHashMap<>();
        authParams.put("USERNAME", email);
        authParams.put("PASSWORD", password);

        AdminInitiateAuthRequest authRequest =
                AdminInitiateAuthRequest.builder()
                        .authFlow(AuthFlowType.ADMIN_USER_PASSWORD_AUTH)
                        .userPoolId(userPoolId)
                        .clientId(clientId)
                        .authParameters(authParams)
                        .build();
        try {
            AdminInitiateAuthResponse authResult = cognitoClient.adminInitiateAuth(authRequest);
            AuthenticationResultType authenticationResult = authResult.authenticationResult();
            LOG.info("authResult: " + authResult);
            LOG.info("authenticationResult: " + authenticationResult.toString());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return false;
        }
        return true;
    }
}

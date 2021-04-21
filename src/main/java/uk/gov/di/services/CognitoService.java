package uk.gov.di.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthenticationResultType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpResponse;

import java.util.LinkedHashMap;

public class CognitoService {

    private static final Logger LOG = LoggerFactory.getLogger(CognitoService.class);

    private CognitoIdentityProviderClient cognitoClient;
    //ClientId of the userpool in Cognito
    private final String clientId = "3pf8i39bspmlkmd9pqo1s626oe";
    private final String userPoolId = "eu-west-2_JVIkRJSaV";

    public CognitoService(CognitoIdentityProviderClient cognitoClient) {
        this.cognitoClient = cognitoClient;
    }

    public SignUpResponse signUp(String email, String password) {
        //https://docs.aws.amazon.com/cognito-user-identity-pools/latest/APIReference/API_SignUp.html
        SignUpRequest request = SignUpRequest.builder()
                .clientId(clientId)
                .username(email)
                .password(password)
                .build();
        SignUpResponse signUpResponse = cognitoClient.signUp(request);
        return signUpResponse;
        //Assuming we don't need to confirm sign up with confirmation code
    }

    public AuthenticationResultType login(String email, String password) {
        LinkedHashMap<String, String> authParams = new LinkedHashMap<>();
        authParams.put("USERNAME", email);
        authParams.put("PASSWORD", password);

        AdminInitiateAuthRequest authRequest = AdminInitiateAuthRequest.builder()
                .authFlow(AuthFlowType.ADMIN_USER_PASSWORD_AUTH)
                .userPoolId(userPoolId)
                .clientId(clientId)
                .authParameters(authParams)
                .build();

        AdminInitiateAuthResponse authResult = cognitoClient.adminInitiateAuth(authRequest);
        AuthenticationResultType authenticationResult = authResult.authenticationResult();

        LOG.info("authResult: " + authResult.toString());

        return authenticationResult;
    }
}

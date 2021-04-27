package uk.gov.di.services;

import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.nimbusds.srp6.SRP6ClientCredentials;
import com.nimbusds.srp6.SRP6ClientSession;
import com.nimbusds.srp6.SRP6CryptoParams;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ChallengeNameType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.RespondToAuthChallengeRequest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CognitoSrpService extends CognitoService {

    public CognitoSrpService(CognitoIdentityProviderClient cognitoClient) {
        super(cognitoClient);
    }

    @Override
    public boolean signUp(String email, String password) {
        return super.signUp(email, password);
    }

    public String performSRPAuthentication(String username, String password) {
        String authresult = null;

        InitiateAuthRequest initiateAuthRequest = initiateUserSrpAuthRequest(username);
        try {
            InitiateAuthResponse initiateAuthResponse = cognitoClient.initiateAuth(initiateAuthRequest);
            if (ChallengeNameType.PASSWORD_VERIFIER.toString().equals(initiateAuthResponse.challengeName())) {
                RespondToAuthChallengeRequest challengeRequest = userSrpAuthRequest(initiateAuthResult, password,
                        initiateAuthRequest.authParameters().get("SECRET_HASH"));
                RespondToAuthChallengeResult result = cognitoIdentityProvider.respondToAuthChallenge(challengeRequest);
                //System.out.println(result);
                System.out.println(CognitoJWTParser.getPayload(result.getAuthenticationResult().getIdToken()));
                authresult = result.getAuthenticationResult().getIdToken();
            }
        } catch (final Exception ex) {
            System.out.println("Exception" + ex);

        }
        return authresult;
    }

    private InitiateAuthRequest initiateUserSrpAuthRequest(String username, String password) {
        SRP6CryptoParams config = SRP6CryptoParams.getInstance();
        SRP6ClientSession srp6ClientSession = new SRP6ClientSession();
        srp6ClientSession.step1(username, password);
        SRP6ClientCredentials cred = srp6ClientSession.step2(config, new BigInteger(srpStep1Response.salt(), 16), new BigInteger(srpStep1Response.B(),16));
        Map<String, String> authParams = new HashMap<>();
        authParams.put("USERNAME", username);
        authParams.put("SRP_A", );

        InitiateAuthRequest initiateAuthRequest = InitiateAuthRequest.builder()
                .authFlow(AuthFlowType.USER_SRP_AUTH)
                .clientId(this.clientId)
                .authParameters(authParams)
                .build();

        return initiateAuthRequest;
    }

    private String calculateSecretHash(String userPoolClientId, String userPoolClientSecret, String userName) {
        final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

        SecretKeySpec signingKey = new SecretKeySpec(
                userPoolClientSecret.getBytes(StandardCharsets.UTF_8),
                HMAC_SHA256_ALGORITHM);
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
            mac.init(signingKey);
            mac.update(userName.getBytes(StandardCharsets.UTF_8));
            byte[] rawHmac = mac.doFinal(userPoolClientId.getBytes(StandardCharsets.UTF_8));
            return java.util.Base64.getEncoder().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException("Error while calculating ");
        }
    }
}

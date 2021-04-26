package uk.gov.di.services;

import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.nimbusds.srp6.BigIntegerUtils;
import com.nimbusds.srp6.SRP6ClientCredentials;
import com.nimbusds.srp6.SRP6ClientEvidenceContext;
import com.nimbusds.srp6.SRP6ClientSession;
import com.nimbusds.srp6.SRP6CryptoParams;
import com.nimbusds.srp6.SRP6Routines;
import com.nimbusds.srp6.URoutineContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ChallengeNameType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ChallengeResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.RespondToAuthChallengeRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.RespondToAuthChallengeResponse;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.SimpleTimeZone;

import static org.apache.commons.codec.digest.MessageDigestAlgorithms.SHA_256;

public class CognitoSrpService extends CognitoService {

    private static final Logger LOG = LoggerFactory.getLogger(CognitoSrpService.class);

    public CognitoSrpService(CognitoIdentityProviderClient cognitoClient) {
        super(cognitoClient);
    }

    @Override
    public boolean login(String email, String password) {
        return performSRPAuthentication(email, password);
    }

    @Override
    public boolean signUp(String email, String password) {
        return super.signUp(email, password);
    }

    public boolean performSRPAuthentication(String username, String password) {
        SRP6CryptoParams config = SRP6CryptoParams.getInstance(3072, "SHA-256");

        SRP6Routines srp6Routines = new SRP6Routines();
        // Generate client private and public values
        var a = srp6Routines.generatePrivateValue(config.N, new SecureRandom());
        config.getMessageDigestInstance().reset();

        var A = srp6Routines.computePublicClientValue(config.N, config.g, a);

        InitiateAuthRequest initiateAuthRequest = initiateUserSrpAuthRequest(username, A);

        try {
            InitiateAuthResponse initiateAuthResponse = cognitoClient.initiateAuth(initiateAuthRequest);
            LOG.info("performSRPAuthentication:initiateAuthResponse: {}", initiateAuthResponse.toString());

            if (ChallengeNameType.PASSWORD_VERIFIER.toString().equals(initiateAuthResponse.challengeName().name())) {

                RespondToAuthChallengeRequest challengeRequest = userSrpAuthRequest(config, initiateAuthResponse, password, a, A);
                RespondToAuthChallengeResponse result = cognitoClient.respondToAuthChallenge(challengeRequest);

                LOG.info("respondToAuthChallengeResult: {}", result.toString());

                LOG.info("Cognito ID Token = {}", result.authenticationResult().idToken());
                return true;
            }
        } catch (final Exception ex) {
            LOG.error("Error authenticating to Cognito", ex);
        }
        return false;
    }

    private RespondToAuthChallengeRequest userSrpAuthRequest(SRP6CryptoParams config,
                                                             InitiateAuthResponse initiateAuthResponse,
                                                             String password,
                                                             BigInteger a,
                                                             BigInteger A) {
        var B = new BigInteger(initiateAuthResponse.challengeParameters().get("SRP_B"), 16);
        var salt = new BigInteger(initiateAuthResponse.challengeParameters().get("SALT"), 16);
        SRP6Routines srp6Routines = new SRP6Routines();
        MessageDigest digest = config.getMessageDigestInstance();

        var x = srp6Routines.computeX(digest ,
                BigIntegerUtils.bigIntegerToBytes(salt),
                password.getBytes(Charset.forName("UTF-8")));
        digest.reset();

        // Compute the session key
        var k = srp6Routines.computeK(digest, config.N, config.g);
		digest.reset();

        var u = srp6Routines.computeU(digest, config.N, A, B);
        digest.reset();

        var S = srp6Routines.computeSessionKey(config.N, config.g, k, x, u, a, B);

        var M1 = srp6Routines.computeClientEvidence(digest, A, B, S);
        digest.reset();

        SimpleDateFormat formatTimestamp = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy", Locale.UK);
        formatTimestamp.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));

        Map<String, String> challengeResponses = new HashMap<>();
        challengeResponses.put("PASSWORD_CLAIM_SECRET_BLOCK", initiateAuthResponse.challengeParameters().get("SECRET_BLOCK"));
        challengeResponses.put("PASSWORD_CLAIM_SIGNATURE", new String(Base64.getEncoder().encode(M1.toString(16).getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8));
        challengeResponses.put("TIMESTAMP", formatTimestamp.format(new Date()));
        challengeResponses.put("USERNAME", initiateAuthResponse.challengeParameters().get("USER_ID_FOR_SRP"));

        return RespondToAuthChallengeRequest.builder()
                .clientId(this.clientId)
                .challengeName(ChallengeNameType.PASSWORD_VERIFIER)
                .challengeResponses(challengeResponses)
                .build();
    }
    private InitiateAuthRequest initiateUserSrpAuthRequest(String username, BigInteger A) {
        Map<String, String> authParams = new HashMap<>();
        authParams.put("USERNAME", username);
        authParams.put("SRP_A", A.toString(16));

        return InitiateAuthRequest.builder()
                .authFlow(AuthFlowType.USER_SRP_AUTH)
                .clientId(this.clientId)
                .authParameters(authParams)
                .build();
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

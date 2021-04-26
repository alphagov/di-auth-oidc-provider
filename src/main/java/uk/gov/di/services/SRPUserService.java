package uk.gov.di.services;

import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import com.nimbusds.srp6.SRP6ClientCredentials;
import com.nimbusds.srp6.SRP6ClientSession;
import com.nimbusds.srp6.SRP6CryptoParams;
import com.nimbusds.srp6.SRP6Exception;
import com.nimbusds.srp6.SRP6ServerSession;
import com.nimbusds.srp6.SRP6VerifierGenerator;
import uk.gov.di.entity.SRP6Credentials;
import uk.gov.di.entity.SRPStep1Response;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class SRPUserService implements AuthenticationService{

    private SRP6ServerSession srp6ServerSession;
    private Map<String, SRP6Credentials> credentialsMap = new HashMap<>();

    public SRPUserService() {
        SRP6CryptoParams config = SRP6CryptoParams.getInstance();
        this.srp6ServerSession = new SRP6ServerSession(config);
    }

    @Override
    public boolean userExists(String email) {
        return credentialsMap.containsKey(email);
    }

    @Override
    public boolean signUp(String email, String password) {
        SRP6CryptoParams config = SRP6CryptoParams.getInstance();
        SRP6VerifierGenerator gen = new SRP6VerifierGenerator(config);
        BigInteger salt = new BigInteger(gen.generateRandomSalt());
        BigInteger verifier = gen.generateVerifier(salt, email, password);
        credentialsMap.put(email, new SRP6Credentials(salt.toString(16), verifier.toString(16)));
        return true;
    }

    @Override
    public boolean verifyAccessCode(String username, String code) {
        return false;
    }

    @Override
    public boolean login(String email, String password) {
        SRPStep1Response srpStep1Response = step1(email);
        var M2 = step2(srpStep1Response);
        return false;
    }

    @Override
    public boolean isEmailVerificationRequired() {
        return false;
    }

    @Override
    public UserInfo getInfoForEmail(String email) {
        return null;
    }

    private SRPStep1Response step1(String email) {
        var credentials = credentialsMap.get(email);
        BigInteger salt = new BigInteger(credentials.salt(), 16);
        BigInteger verifier = new BigInteger(credentials.verifier(), 16);

        BigInteger b = srp6ServerSession.step1(email, salt, verifier);
        return new SRPStep1Response(credentials.salt(), b.toString(16));
    }

    private BigInteger step2(SRPStep1Response srpStep1Response) {

        SRP6CryptoParams config = SRP6CryptoParams.getInstance();
        SRP6ClientCredentials cred = null;

        SRP6ClientSession srp6ClientSession = new SRP6ClientSession();

        try {
            cred = srp6ClientSession.step2(config, new BigInteger(srpStep1Response.salt(), 16), new BigInteger(srpStep1Response.B(),16));

        } catch (SRP6Exception e) {
            // Invalid server 'B'
        }

       return serverStep2(cred.A, cred.M1);
    }

    private BigInteger serverStep2(final BigInteger A, final BigInteger M1) {
        try {
            return srp6ServerSession.step2(A, M1);
        } catch (SRP6Exception e) {
            // User authentication failed
            throw new RuntimeException(e);
        }
    }

}

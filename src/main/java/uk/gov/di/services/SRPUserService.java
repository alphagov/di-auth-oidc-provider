package uk.gov.di.services;

import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import com.nimbusds.srp6.SRP6CryptoParams;
import com.nimbusds.srp6.SRP6ServerSession;
import org.apache.commons.codec.binary.Hex;
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
        return false;
    }

    @Override
    public boolean verifyAccessCode(String username, String code) {
        return false;
    }

    @Override
    public boolean login(String email, String password) {
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

    public SRPStep1Response step1(String email) {
        var credentials = credentialsMap.get(email);
        BigInteger salt = new BigInteger(credentials.salt(), 16);
        BigInteger verifier = new BigInteger(credentials.verifier(), 16);

        BigInteger b = srp6ServerSession.step1(email, salt, verifier);
        return new SRPStep1Response(credentials.salt(), b.toString(16));
    }
}

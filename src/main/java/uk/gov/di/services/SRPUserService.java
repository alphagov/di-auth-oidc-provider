package uk.gov.di.services;

import com.nimbusds.oauth2.sdk.id.Subject;
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

    private Map<String, SRP6Credentials> credentialsMap = new HashMap<>();
    private SRP6CryptoParams config = SRP6CryptoParams.getInstance();

    @Override
    public boolean userExists(String email) {
        return credentialsMap.containsKey(email);
    }

    @Override
    public boolean signUp(String email, String password) {
        SRP6CryptoParams config = SRP6CryptoParams.getInstance();

        SRP6VerifierGenerator gen = new SRP6VerifierGenerator(config);
        BigInteger salt = new BigInteger(BigInteger.ONE.signum(), gen.generateRandomSalt());

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
        if (userExists(email)) {
            SRP6ClientSession srp6ClientSession = new SRP6ClientSession();
            SRP6ServerSession srp6ServerSession = new SRP6ServerSession(config);

            try {
                srp6ClientSession.step1(email, password);
                SRPStep1Response srpStep1Response = serverStep1(srp6ServerSession, email);
                SRP6ClientCredentials credentials = clientStep2(srp6ClientSession, srpStep1Response);
                BigInteger M2 = serverStep2(srp6ServerSession, credentials.A, credentials.M1);
                return clientStep3(srp6ClientSession, M2);
            } catch (SRP6Exception e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean isEmailVerificationRequired() {
        return false;
    }

    @Override
    public UserInfo getInfoForEmail(String email) {
        UserInfo userInfo = new UserInfo(new Subject());
        userInfo.setEmailAddress(email);
        return userInfo;
    }

    private SRPStep1Response serverStep1(SRP6ServerSession srp6ServerSession, String email) {
        var credentials = credentialsMap.get(email);
        BigInteger salt = new BigInteger(credentials.salt(), 16);
        BigInteger verifier = new BigInteger(credentials.verifier(), 16);

        BigInteger b = srp6ServerSession.step1(email, salt, verifier);
        return new SRPStep1Response(credentials.salt(), b.toString(16));
    }

    private SRP6ClientCredentials clientStep2(SRP6ClientSession srp6ClientSession, SRPStep1Response srpStep1Response) throws SRP6Exception {

        SRP6CryptoParams config = SRP6CryptoParams.getInstance();

        return srp6ClientSession.step2(config, new BigInteger(srpStep1Response.salt(), 16), new BigInteger(srpStep1Response.B(),16));
    }

    private BigInteger serverStep2(SRP6ServerSession srp6ServerSession, final BigInteger A, final BigInteger M1) throws SRP6Exception {
        return srp6ServerSession.step2(A, M1);
    }

    private boolean clientStep3(SRP6ClientSession srp6ClientSession, final BigInteger M2) {
        try {
            srp6ClientSession.step3(M2);
            return true;
        } catch (SRP6Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}

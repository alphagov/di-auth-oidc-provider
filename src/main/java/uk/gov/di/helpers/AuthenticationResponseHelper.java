package uk.gov.di.helpers;

import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.AuthorizationRequest;
import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.openid.connect.sdk.AuthenticationErrorResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationSuccessResponse;

public class AuthenticationResponseHelper {

    public static AuthenticationResponse generateSuccessfulAuthResponse(
            AuthorizationRequest authRequest) {
        return new AuthenticationSuccessResponse(
                authRequest.getRedirectionURI(),
                new AuthorizationCode(),
                null,
                null,
                authRequest.getState(),
                null,
                null);
    }

    public static AuthenticationErrorResponse generateErrorAuthnResponse(
            AuthorizationRequest authRequest, ErrorObject errorObject) {
        return new AuthenticationErrorResponse(
                authRequest.getRedirectionURI(),
                errorObject,
                authRequest.getState(),
                authRequest.getResponseMode());
    }
}

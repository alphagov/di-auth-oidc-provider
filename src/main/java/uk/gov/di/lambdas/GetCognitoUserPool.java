package example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserResponse;

import java.util.Map;

public class GetCognitoUserPool implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>{
    private final String userPoolId = "eu-west-2_JVIkRJSaV";

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context)
    {
        LambdaLogger logger = context.getLogger();
        CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder()
                .region(Region.EU_WEST_2)
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();
        Map<String, String> inputParams = request.getQueryStringParameters();
        logger.log("Input params: " + inputParams.toString());
        logger.log("Input params name: " + inputParams.get("name"));
        logger.log("-----------------------------------------------");

        AdminGetUserResponse getUserResponse = cognitoClient.adminGetUser(
                AdminGetUserRequest.builder().userPoolId(userPoolId).username(inputParams.get("name")).build());

        APIGatewayProxyResponseEvent apiGatewayProxyResponseEvent = new APIGatewayProxyResponseEvent();
        apiGatewayProxyResponseEvent.setStatusCode(200);
        apiGatewayProxyResponseEvent.setBody(getUserResponse.toString());
        return apiGatewayProxyResponseEvent;
    }
}
}
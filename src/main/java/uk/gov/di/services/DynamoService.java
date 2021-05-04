package uk.gov.di.services;

import com.nimbusds.oauth2.sdk.id.Subject;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DynamoService {

    private DynamoDbClient dynamoDbClient;

    public DynamoService(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    public UserInfo getUserInfo(String email) {

        HashMap<String, AttributeValue> keyToGet = new HashMap<String,AttributeValue>();

        keyToGet.put("user_id", AttributeValue.builder()
                .s(email).build());

        GetItemRequest request = GetItemRequest.builder()
                .key(keyToGet)
                .tableName("user_info")
                .build();

        try {
            Map<String,AttributeValue> returnedItem = dynamoDbClient.getItem(request).item();
            UserInfo userInfo = new UserInfo(new Subject());
            userInfo.setEmailAddress(email);
            if (!(returnedItem == null || returnedItem.isEmpty())) {
                Set<String> keys = returnedItem.keySet();
                System.out.println("Amazon DynamoDB table attributes: \n");

                for (String key1 : keys) {
                    System.out.format("%s: %s\n", key1, returnedItem.get(key1).toString());
                }

                userInfo.setFamilyName(returnedItem.get("family_name").s());
                userInfo.setGivenName(returnedItem.get("given_name").s());
            }
            return userInfo;
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}

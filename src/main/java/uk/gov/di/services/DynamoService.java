package uk.gov.di.services;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.AttributeEncryptor;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.DynamoDBEncryptor;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.DirectKmsMaterialProvider;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
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

    private AmazonDynamoDB dynamoDbClient;
    private final String cmkArn = "arn:aws:kms:eu-west-2:761723964695:key/3a57678d-41a3-4421-b1dc-f89f93ec5fd5";
    private final String region = "eu-west-2";
    private DynamoDBMapper mapper;

    public DynamoService(AmazonDynamoDB dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;

        final AWSKMS kms = AWSKMSClientBuilder.standard().withRegion(region).build();
        final DirectKmsMaterialProvider cmp = new DirectKmsMaterialProvider(kms, cmkArn);

        final DynamoDBEncryptor encryptor = DynamoDBEncryptor.getInstance(cmp);
        DynamoDBMapperConfig mapperConfig = DynamoDBMapperConfig.builder().withSaveBehavior(DynamoDBMapperConfig.SaveBehavior.PUT).build();
        mapper = new DynamoDBMapper(dynamoDbClient, mapperConfig, new AttributeEncryptor(encryptor));
    }

    public UserInfo getUserInfo(String email) {
        uk.gov.di.dto.UserInfo dbUserInfo = mapper.load(uk.gov.di.dto.UserInfo.class, email);
        UserInfo userInfo = new UserInfo(new Subject());
        userInfo.setEmailAddress(email);
        if (dbUserInfo != null){
            userInfo.setFamilyName(dbUserInfo.getFamilyName());
            userInfo.setGivenName(dbUserInfo.getGivenName());
        }
        return userInfo;
    }

    public void writeStubData() {
        uk.gov.di.dto.UserInfo dbUserInfo = new uk.gov.di.dto.UserInfo();
        dbUserInfo.setUserId("joe.bloggs@digital.cabinet-office.gov.uk");
        dbUserInfo.setFamilyName("Bloggs");
        dbUserInfo.setGivenName("Joe");
        mapper.save(dbUserInfo);
    }
}

package uk.gov.di.services;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.AttributeEncryptor;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.DynamoDBEncryptor;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.EncryptionContext;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.EncryptionFlags;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.DirectKmsMaterialProvider;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.nimbusds.oauth2.sdk.id.Subject;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;

import java.security.GeneralSecurityException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DynamoService {

    private static final String CMK_ARN = "arn:aws:kms:eu-west-2:761723964695:key/3a57678d-41a3-4421-b1dc-f89f93ec5fd5";
    private static final String REGION = "eu-west-2";

    private static final String TABLE_NAME = "user_info";
    private static final String PARTITION_KEY_NAME = "user_id";
    private static final String FAMILY_NAME = "family_name";
    private static final String GIVEN_NAME = "given_name";
    private static final String AMAZON_GIBBERISH = "*amzn-ddb-map-desc*";
    private static final String AMAZON_GIBBERISH2 = "*amzn-ddb-map-sig*";

    private static final EnumSet<EncryptionFlags> SIGN_ONLY = EnumSet.of(EncryptionFlags.SIGN);
    private static final EnumSet<EncryptionFlags> ENCRYPT_AND_SIGN = EnumSet.of(EncryptionFlags.ENCRYPT, EncryptionFlags.SIGN);

    private final DynamoDBEncryptor encryptor;
    private final AmazonDynamoDB dynamoDbClient;

    private final EncryptionContext encryptionContext = new EncryptionContext.Builder()
            .withTableName(TABLE_NAME)
            .withHashKeyName(PARTITION_KEY_NAME)
            .build();

    public DynamoService(AmazonDynamoDB dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;

        final AWSKMS kms = AWSKMSClientBuilder.standard().withRegion(REGION).build();
        final DirectKmsMaterialProvider cmp = new DirectKmsMaterialProvider(kms, CMK_ARN);

        encryptor = DynamoDBEncryptor.getInstance(cmp);
    }

    public UserInfo getUserInfo(String email) {
        HashMap<String, AttributeValue> keyToGet = new HashMap<>();
        keyToGet.put("user_id", new AttributeValue().withS(email));
        GetItemRequest request = new GetItemRequest(TABLE_NAME ,keyToGet);

        UserInfo userInfo = new UserInfo(new Subject());
        userInfo.setEmailAddress(email);
        Map<String, AttributeValue> record = dynamoDbClient.getItem(request).getItem();

        if (!(record == null || record.isEmpty())) {
            try {
                Map<String, AttributeValue> decryptedRecord = encryptor.decryptRecord(record, actionFlags(record), encryptionContext);
                userInfo.setFamilyName(decryptedRecord.get(FAMILY_NAME).getS());
                userInfo.setGivenName(decryptedRecord.get(GIVEN_NAME).getS());
            } catch (GeneralSecurityException e) {
                throw new RuntimeException(e);
            }
        }

        return userInfo;
    }

    public void writeStubData(String userId, String familyName, String givenName) {
        final Map<String, AttributeValue> record = new HashMap<>();

        record.put(PARTITION_KEY_NAME, new AttributeValue().withS(userId));
        record.put(FAMILY_NAME, new AttributeValue().withS(familyName));
        record.put(GIVEN_NAME, new AttributeValue().withS(givenName));

        final Map<String, Set<EncryptionFlags>> actions = actionFlags(record);

        final Map<String, AttributeValue> encrypted_record;
        try {
            encrypted_record = encryptor.encryptRecord(record, actions, encryptionContext);
            dynamoDbClient.putItem(TABLE_NAME, encrypted_record);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, Set<EncryptionFlags>> actionFlags(Map<String, AttributeValue> record) {
        final Map<String, Set<EncryptionFlags>> actions = new HashMap<>();

        for (final String attributeName : record.keySet()) {
            switch (attributeName) {
                case PARTITION_KEY_NAME: // fall through to the next case
                    // Partition and sort keys must not be encrypted, but should be signed
                    actions.put(attributeName, SIGN_ONLY);
                    break;
                case AMAZON_GIBBERISH:
                case AMAZON_GIBBERISH2:
                        // Do nothing
                    break;
                default:
                    // Encrypt and sign all other attributes
                    actions.put(attributeName, ENCRYPT_AND_SIGN);
                    break;
            }
        }

        return actions;
    }
}

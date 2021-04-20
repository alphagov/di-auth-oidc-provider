package uk.gov.di.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES;

public class VcapServices {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Service(Credentials credentials) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Credentials(String host, int port, String name, String username, String password) {}

    public static Optional<Credentials> readPostgresConfiguration(String vcapServices) {
        try {
            var services = new ObjectMapper()
                .configure(FAIL_ON_MISSING_CREATOR_PROPERTIES, true)
                .readValue(
                    vcapServices,
                    new TypeReference<Map<String, List<Service>>>() {}
                );

            if (services == null || !services.containsKey("postgres")) {
                return Optional.empty();
            }

            return services.get("postgres").stream().findFirst().map(Service::credentials);

        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }
}

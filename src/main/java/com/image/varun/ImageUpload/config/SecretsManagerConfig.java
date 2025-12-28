package com.image.varun.ImageUpload.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

@Configuration
@ConditionalOnProperty(name = "spring.profiles.active", havingValue = "test")
public class SecretsManagerConfig {

    private static final String SECRET_NAME = "image-share/DB";
    private static final String REGION = "ap-south-1";

    @Bean
    public DatabaseCredentials databaseCredentials() {
        return getSecret();
    }

    private DatabaseCredentials getSecret() {
        Region region = Region.of(REGION);

        // Create a Secrets Manager client
        SecretsManagerClient client = SecretsManagerClient.builder()
                .region(region)
                .build();

        GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
                .secretId(SECRET_NAME)
                .build();

        GetSecretValueResponse getSecretValueResponse;

        try {
            getSecretValueResponse = client.getSecretValue(getSecretValueRequest);
        } catch (Exception e) {
            // For a list of exceptions thrown, see
            // https://docs.aws.amazon.com/secretsmanager/latest/apireference/API_GetSecretValue.html
            throw new RuntimeException("Failed to retrieve secret from AWS Secrets Manager", e);
        }

        String secret = getSecretValueResponse.secretString();

        // Parse the JSON secret
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(secret);
            
            DatabaseCredentials credentials = new DatabaseCredentials();
            credentials.setUsername(jsonNode.get("username").asText());
            credentials.setPassword(jsonNode.get("password").asText());
            
            // Check if URL is in the secret, otherwise use default
            if (jsonNode.has("url")) {
                credentials.setUrl(jsonNode.get("url").asText());
            }
            
            return credentials;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse database credentials from secret", e);
        }
    }

    public static class DatabaseCredentials {
        private String username;
        private String password;
        private String url;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}

package com.ridelink.payment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.ridelink.payment.repository")
public class MongoConfig {

    private static final String DEFAULT_URI = "mongodb://localhost:27017/ridelink_fare_payment_db";

    @Value("${spring.data.mongodb.uri:" + DEFAULT_URI + "}")
    private String mongoUri;

    @Bean
    @Primary
    public MongoDatabaseFactory mongoDatabaseFactory() {
        String uriToUse = (mongoUri != null && !mongoUri.isBlank()) ? mongoUri : DEFAULT_URI;
        return new SimpleMongoClientDatabaseFactory(uriToUse);
    }

    @Bean
    @Primary
    public MongoTemplate mongoTemplate(MongoDatabaseFactory mongoDatabaseFactory) {
        return new MongoTemplate(mongoDatabaseFactory);
    }
}

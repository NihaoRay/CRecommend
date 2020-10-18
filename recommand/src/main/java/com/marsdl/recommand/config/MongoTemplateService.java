package com.marsdl.recommand.config;

import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClients;
import com.mongodb.connection.ClusterConnectionMode;
import com.mongodb.connection.ClusterType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.util.Collections;

@Configuration
public class MongoTemplateService {

    @Value("${mongodb.service.dbname}")
    private String dbname;
    @Value("${mongodb.service.host}")
    private String host;
    @Value("${mongodb.service.port}")
    private int port;

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoDatabaseFactory(), mappingMongoConverter(mongoDatabaseFactory()));
    }

    @Bean
    public MongoDatabaseFactory mongoDatabaseFactory() {
        MongoClientSettings setting = MongoClientSettings.builder()
//                .credential(credential)
                .applyToClusterSettings(builder ->
                        builder.hosts(Collections.singletonList(new ServerAddress(host, port)))
                                .mode(ClusterConnectionMode.SINGLE)
                                .requiredClusterType(ClusterType.STANDALONE)
                ).build();

        return new SimpleMongoClientDatabaseFactory(MongoClients.create(setting), dbname);
    }

    @Bean
    public MappingMongoConverter mappingMongoConverter(MongoDatabaseFactory factory) {
        MappingMongoConverter converter = new MappingMongoConverter(new DefaultDbRefResolver(factory), new MongoMappingContext());
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return converter;
    }
}

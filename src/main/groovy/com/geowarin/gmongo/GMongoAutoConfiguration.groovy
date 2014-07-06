package com.geowarin.gmongo

import com.gmongo.GMongo
import com.mongodb.DB
import com.mongodb.Mongo
import com.mongodb.MongoURI
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import javax.annotation.PreDestroy

/**
 *
 * Date: 18/05/2014
 * Time: 22:03
 * @author Geoffroy Warin (http://geowarin.github.io)
 */
@Configuration
@ConditionalOnClass([Mongo, GMongo])
@EnableConfigurationProperties(GMongoProperties)
class GMongoAutoConfiguration {

    @Autowired
    GMongoProperties properties

    private GMongo gMongo;

    @PreDestroy
    public void close() {
        gMongo?.close();
    }

    @Bean
    GMongo GMongo() {
        gMongo = new GMongo(new MongoURI(properties.uri));
    }

    @Bean
    DB db(GMongo gMongo) {
        gMongo.getDB(new MongoURI(properties.uri).getDatabase())
    }
}

package com.geowarin.model

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.data.mongodb.core.CollectionOptions
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.index.GeospatialIndex
import org.springframework.stereotype.Component

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 *
 * Date: 24/05/2014
 * Time: 19:11
 * @author Geoffroy Warin (http://geowarin.github.io)
 */
@Component
class DbInitializer implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    MongoTemplate mongoTemplate

    @Autowired
    LunchRepository lunchRepository

    @Override
    void onApplicationEvent(ContextRefreshedEvent event) {
        mongoTemplate.dropCollection('lunch')
        CollectionOptions options = new CollectionOptions(10, 10, true);
        mongoTemplate.createCollection('lunch', options);

        long now = LocalDateTime.now().plusSeconds(10).toEpochSecond(ZoneOffset.UTC)
        println "Now + 10 secs : $now"
        lunchRepository.save(new Lunch(time: now))
    }
}

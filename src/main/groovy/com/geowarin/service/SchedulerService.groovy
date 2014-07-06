package com.geowarin.service

import com.geowarin.model.Lunch
import com.geowarin.model.LunchRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 *
 * Date: 06/07/2014
 * Time: 03:42
 * @author Geoffroy Warin (http://geowarin.github.io)
 */
@Service
class SchedulerService {

    @Autowired
    LunchRepository repository

    Random random = new Random()

//    @Scheduled(fixedDelay = 1000L)
//    void scheduled() {
//
//        if (random.nextBoolean()) {
//            long now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
//            Lunch lunch = repository.findAll()[0]
//            lunch.time = now
//            repository.save(lunch)
//            println 'updated lunch'
//        }
//    }

    @Scheduled(fixedDelay = 2000L)
    void createLunch() {
        long now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        repository.save(new Lunch(time: now))
        println 'new lunch'
    }

}

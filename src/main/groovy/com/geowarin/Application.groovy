package com.geowarin

import com.geowarin.gmongo.GMongoAutoConfiguration.TailableCursorFactory
import com.geowarin.model.Lunch
import com.geowarin.service.LunchService
import com.geowarin.stream.EventStream
import com.mongodb.DBCursor
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.async.DeferredResult

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static com.geowarin.stream.EventStream.withEventStream

@Configuration
@ComponentScan
@EnableAutoConfiguration
@RestController
@EnableScheduling
class Application {

    static void main(String[] args) {
        SpringApplication.run Application, args
    }

    @Autowired
    TailableCursorFactory tails

    @Autowired
    LunchService lunchService

    @RequestMapping('events')
    DeferredResult events(HttpServletRequest request, HttpServletResponse response) {

        return withEventStream(request, response) { EventStream stream ->

            def randomLunch = lunchService.getRandomLunch()
            println "Will query updates for lunch ${randomLunch.name} - id = ${randomLunch.id}"

            DBCursor cursor = tails.getTail(['o._id': new ObjectId(randomLunch.id), ns: 'stream.lunch', op: 'u'])
            while (cursor.hasNext()) {
                def update = cursor.next()
                Lunch lunch = update.o.findAll { !it.key.startsWith('_') }
                stream.write("Lunch updated : ${lunch}")
            }
        }
    }
}

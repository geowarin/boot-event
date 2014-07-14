package com.geowarin

import com.geowarin.model.Lunch
import com.geowarin.service.LunchService
import com.geowarin.stream.EventStream
import com.geowarin.utils.DateUtils
import com.mongodb.Bytes
import com.mongodb.DBCollection
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
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
import java.util.concurrent.Callable

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

    class LunchUpdateReader {
        private LunchUpdateFinder lunchFinder
        private EventStream stream

        LunchUpdateReader(EventStream stream, LunchUpdateFinder lunchFinder) {
            this.stream = stream
            this.lunchFinder = lunchFinder
        }

        void run() {
            while (true) {
                Lunch lunch = lunchFinder.call()
                stream.write("Lunch updated : ${lunch}")
            }
        }
    }

    class LunchUpdateFinder implements Callable {
        private cursor

        LunchUpdateFinder(def lunchId, DBCollection opLog) {

            def now = DateUtils.currentBSONTimeStamp()
            cursor = opLog.find(ts: ['$gt': now], 'o._id': new ObjectId(lunchId), ns: 'stream.lunch', op: 'u')
                    .addOption(Bytes.QUERYOPTION_TAILABLE)
                    .addOption(Bytes.QUERYOPTION_AWAITDATA)
        }

        Lunch call() throws Exception {
            def update = cursor.next()
            def lunchObj = update.o
            return new Lunch(time: lunchObj.time, name: lunchObj.name)
        }
    }

    @Autowired
    @Qualifier('oplog')
    DBCollection opLogs

    @Autowired
    LunchService lunchService

    @RequestMapping('events')
    DeferredResult event2(HttpServletRequest request, HttpServletResponse response) {

        return withEventStream(request, response) { EventStream stream ->

            def randomLunch = lunchService.getRandomLunch()
            println "Will query updates for lunch ${randomLunch.name} - id = ${randomLunch.id}"
            new LunchUpdateReader(stream, new LunchUpdateFinder(randomLunch.id, opLogs)).run()
        }
    }


}

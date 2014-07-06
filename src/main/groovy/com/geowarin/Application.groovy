package com.geowarin

import com.geowarin.model.Lunch
import com.geowarin.service.LunchService
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

@Configuration
@ComponentScan
@EnableAutoConfiguration
@RestController
@EnableScheduling
class Application {

    static void main(String[] args) {
        SpringApplication.run Application, args
    }

    class LunchUpdateReader implements Runnable {
        private final PrintWriter writer
        private LunchUpdateFinder lunchFinder

        LunchUpdateReader(PrintWriter writer, LunchUpdateFinder lunchFinder) {
            this.lunchFinder = lunchFinder
            this.writer = writer
        }

        void run() {
            while (true) {
                Lunch lunch = lunchFinder.call()
                writer.println('retry: 10000')
                writer.println('event: truc')
//                writer.println("id: ${lunch.time}")
//                writer.println("data: new lunch !! at ${lunch.time}\n")
                writer.println("data: Lunch updated : ${lunch}\n")
                writer.flush()
            }
        }
    }

    class LunchUpdateFinder implements Callable {
        private cursor

        LunchUpdateFinder(def lunchId, DBCollection opLog) {

            def now = DateUtils.currentBSONTimeStamp()
            cursor = opLog.find(ts: ['$gt': now], 'o._id' : new ObjectId(lunchId), ns: 'stream.lunch', op: 'u')
                    .addOption(Bytes.QUERYOPTION_TAILABLE)
                    .addOption(Bytes.QUERYOPTION_AWAITDATA)
        }

        Lunch call() throws Exception {
            while (true) {
                def update = cursor.next()

                def lunchObj = update.o
                println lunchObj
                return new Lunch(time: lunchObj.time, name: lunchObj.name)
            }
        }
    }

    @Autowired
    @Qualifier('oplog')
    DBCollection opLogs

    @Autowired
    LunchService lunchService

    @RequestMapping('events')
    DeferredResult<String> event(HttpServletRequest request, HttpServletResponse response) {

        DeferredResult<String> result = new DeferredResult<String>()

        response.addHeader('Content-Type', 'text/event-stream')
        response.addHeader('Content-Control', 'no-cache')
        response.addHeader('Access-Control-Allow-Origin', '*')
        response.characterEncoding = 'UTF-8'

        String lastEventId = request.getHeader('Last-Event-ID')
        Number eventId = lastEventId ? lastEventId.toLong() : 0

        def randomLunch = lunchService.getRandomLunch()
        println "Will query updates for lunch ${randomLunch.name} - id = ${randomLunch.id}"

        new LunchUpdateReader(response.writer, new LunchUpdateFinder(randomLunch.id, opLogs)).run()

        result.onTimeout {
            println 'Timeout'
        }

        return result
    }
}

package com.geowarin

import com.geowarin.model.Lunch
import com.geowarin.model.LunchRepository
import com.mongodb.BasicDBObject
import com.mongodb.Bytes
import com.mongodb.DB
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
import java.time.LocalDateTime
import java.time.ZoneOffset
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

//    @RequestMapping('mongostream')
//    void stream() {
//
//
//        DBObject query =
//                lastReceivedEventId.isPresent()
//        ? BasicDBObjectBuilder.start("_id",
//                BasicDBObjectBuilder
//                .start("$gte", lookFrom(lastReceivedEventId.get())).get())
//                .get()
//        : null;
//
//        DBObject sortBy = BasicDBObjectBuilder.start("$natural", 1).get();
//
//        DBCollection collection = null;// must be a capped collection
//        DBCursor cursor = collection
//                .find(query)
//                .sort(sortBy)
//                .addOption(Bytes.QUERYOPTION_TAILABLE)
//                .addOption(Bytes.QUERYOPTION_AWAITDATA);
//    }

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
                writer.println("id: ${lunch.time}")
                writer.println("data: new lunch !! at ${lunch.time}\n")
                writer.flush()
            }
        }
    }

    class LunchUpdateFinder implements Callable<Lunch> {
        private final LunchRepository repository
        private cursor

        LunchUpdateFinder(LunchRepository repository, DB db) {
            this.repository = repository
            def now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
            cursor = db.lunch.find(time: new BasicDBObject('$gt', now))
                    .addOption(Bytes.QUERYOPTION_TAILABLE)
                    .addOption(Bytes.QUERYOPTION_AWAITDATA)
        }

        Lunch call() throws Exception {
            while (true) {
                def lunch = cursor.next()
                return new Lunch(id: lunch.id, time: lunch.time)
            }
        }
    }


    @Autowired
    LunchRepository repository

    @Autowired
    DB db


    @RequestMapping('events')
    DeferredResult<String> event(HttpServletRequest request, HttpServletResponse response) {

        DeferredResult<String> result = new DeferredResult<String>()

        response.addHeader('Content-Type', 'text/event-stream')
        response.addHeader('Content-Control', 'no-cache')
        response.addHeader('Access-Control-Allow-Origin', '*')
        response.characterEncoding = 'UTF-8'

        String lastEventId = request.getHeader('Last-Event-ID')
        Number eventId = lastEventId ? lastEventId.toLong() : 0


        new LunchUpdateReader(response.writer, new LunchUpdateFinder(repository, db)).run()

        result.onTimeout {
            println 'Timeout'
        }

        return result
    }
}

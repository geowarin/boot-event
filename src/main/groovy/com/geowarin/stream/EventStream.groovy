package com.geowarin.stream

import org.springframework.web.context.request.async.DeferredResult

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 *
 * Date: 14/07/2014
 * Time: 19:23
 * @author Geoffroy Warin (http://geowarin.github.io)
 */
class EventStream {
    private final HttpServletResponse response
    private final DeferredResult result
    private final PrintWriter writer
    private Number eventId
    private static final CLIENT_RETRY_TIMEOUT = 2000;

    EventStream(HttpServletResponse response, Number eventId) {
        this.eventId = eventId
        this.response = response
        result = new DeferredResult()
        writer = response.writer
    }

    void write(Object data) {
        writer.println("retry: ${CLIENT_RETRY_TIMEOUT}")
//        writer.println('event: truc')
//                writer.println("id: ${lunch.time}")
        writer.println("data: ${data}\n")
        writer.flush()
    }

    static DeferredResult withEventStream(HttpServletRequest request, HttpServletResponse response, Closure<DeferredResult> block) {
        DeferredResult<String> result = new DeferredResult<String>()

        response.addHeader('Content-Type', 'text/event-stream')
        response.addHeader('Content-Control', 'no-cache')
        response.addHeader('Access-Control-Allow-Origin', '*')
        response.characterEncoding = 'UTF-8'

        String lastEventId = request.getHeader('Last-Event-ID')
        Number eventId = lastEventId ? lastEventId.toLong() : 0

        def stream = new EventStream(response, eventId)
        block.call(stream)

        result.onTimeout {
            println 'Timeout'
        }

        return result
    }
}

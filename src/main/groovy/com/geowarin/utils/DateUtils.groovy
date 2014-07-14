package com.geowarin.utils

import org.bson.types.BSONTimestamp

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 *
 * Date: 06/07/2014
 * Time: 23:14
 * @author Geoffroy Warin (http://geowarin.github.io)
 */
class DateUtils {
    static long timestamp() {
        Instant.now().getEpochSecond()
    }

    static BSONTimestamp currentBSONTimeStamp() {
        new BSONTimestamp(timestamp().toInteger(), 0)
    }
}

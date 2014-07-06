package com.geowarin.model

import org.springframework.data.mongodb.repository.MongoRepository

/**
 *
 * Date: 06/07/2014
 * Time: 03:56
 * @author Geoffroy Warin (http://geowarin.github.io)
 */
interface LunchRepository extends MongoRepository<Lunch, String> {

    List<Lunch> findByTimeGreaterThan(long time)
}

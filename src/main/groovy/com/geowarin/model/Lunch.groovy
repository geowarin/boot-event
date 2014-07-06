package com.geowarin.model

import groovy.transform.Canonical
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

/**
 *
 * Date: 21/05/2014
 * Time: 23:19
 * @author Geoffroy Warin (http://geowarin.github.io)
 */
@Canonical
class Lunch {
    @Id
    String id
    String name
    long time
}

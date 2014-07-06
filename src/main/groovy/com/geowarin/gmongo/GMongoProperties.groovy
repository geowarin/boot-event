package com.geowarin.gmongo

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 *
 * Date: 18/05/2014
 * Time: 22:00
 * @author Geoffroy Warin (http://geowarin.github.io)
 */
@ConfigurationProperties(prefix = 'spring.data.gmongo')
class GMongoProperties {
    String uri = 'mongodb://localhost/test';
}

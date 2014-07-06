package com.geowarin.service

import org.junit.Test

/**
 *
 * Date: 06/07/2014
 * Time: 23:51
 * @author Geoffroy Warin (http://geowarin.github.io)
 */
class SchedulerServiceTest {

    @Test
    void testShuffleName() {
        String name = 'name'
        for (int i = 0; i < 4; i++) {
            name = SchedulerService.shuffleName(name)
            println name
        }
    }
}

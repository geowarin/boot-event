package com.geowarin.service

import com.geowarin.model.LunchRepository
import com.geowarin.utils.DateUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

/**
 *
 * Date: 06/07/2014
 * Time: 03:42
 * @author Geoffroy Warin (http://geowarin.github.io)
 */
@Service
class SchedulerService {

    @Autowired
    LunchRepository repository

    @Autowired
    LunchService service

    Random random = new Random()

    @Scheduled(fixedDelay = 1000L)
    void scheduled() {

        def lunchToUpdate = service.getRandomLunch()
        def lunchName = lunchToUpdate.name

        switch (random.nextInt(3)) {
            case 0:
                lunchToUpdate.name = shuffleName(lunchName)
                println "Changed name from $lunchName to ${lunchToUpdate.name}"
                break
            case 1:
                lunchToUpdate.time = DateUtils.timestamp()
                println "Updated $lunchName time"
                break
            case 3:
                println 'Nothing'
                break
        }

        repository.save(lunchToUpdate)
    }

    static String shuffleName(String name) {
        def chars = name.toList()
        Collections.shuffle(chars)
        return chars.join()
    }

}

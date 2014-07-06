package com.geowarin.service

import com.geowarin.model.Lunch
import com.geowarin.model.LunchRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 *
 * Date: 06/07/2014
 * Time: 23:30
 * @author Geoffroy Warin (http://geowarin.github.io)
 */
@Service
class LunchService {
    @Autowired
    LunchRepository repository

    Lunch getRandomLunch() {
        def lunches = repository.findAll()
        Collections.shuffle(lunches)
        return lunches[0]
    }
}

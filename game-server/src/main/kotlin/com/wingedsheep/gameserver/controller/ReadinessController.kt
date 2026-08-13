package com.wingedsheep.gameserver.controller

import com.wingedsheep.gameserver.coverage.CardReadinessService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/** Developer-facing release-readiness evidence, intentionally not a coverage percentage. */
@RestController
@RequestMapping("/api/readiness")
class ReadinessController(private val cardReadinessService: CardReadinessService) {
    @GetMapping
    fun readiness(): CardReadinessService.ReadinessDTO = cardReadinessService.readiness()
}

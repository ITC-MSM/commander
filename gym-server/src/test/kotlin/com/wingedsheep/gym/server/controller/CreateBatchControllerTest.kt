package com.wingedsheep.gym.server.controller

import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.gym.service.MultiEnvService
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class CreateBatchControllerTest : FunSpec({
    test("empty create batch maps to an empty response") {
        val controller = CreateBatchController(MultiEnvService(CardRegistry()))

        controller.createBatch(emptyList()) shouldBe emptyList()
    }
})

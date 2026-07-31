package com.wingedsheep.ai.engine.evaluation

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class EvalWeightsTest : StringSpec({

    "bundled default reproduces the compiled fallback" {
        EvalWeights.resolve("default") shouldBe EvaluationWeights.DEFAULT
    }

    "bundled profiles are selectable without recompiling" {
        EvalWeights.resolve("blind") shouldBe EvaluationWeights.BLIND
        EvalWeights.ids shouldBe setOf("default", "blind")
    }

    "unknown profile safely uses the compiled fallback" {
        EvalWeights.resolve("does-not-exist") shouldBe EvaluationWeights.DEFAULT
    }

    "resource shape decodes profile ids to vectors" {
        EvalWeights.decode(
            """{"candidate":{"life":2.0,"boardPresence":3.0,"cardAdvantage":4.0,"threatAssessment":5.0,"tempo":6.0}}"""
        )["candidate"] shouldBe EvaluationWeights(2.0, 3.0, 4.0, 5.0, 6.0)
    }

    "malformed tuning artifact is ignored" {
        EvalWeights.decodeOrEmpty("""{"candidate":""") shouldBe emptyMap()
    }
})

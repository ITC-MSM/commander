package com.wingedsheep.gameserver.coverage

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe

class CardReadinessServiceTest : FunSpec({
    val readiness = CardReadinessService().readiness()

    test("manifest is versioned and marks catalog coverage as distinct from readiness") {
        readiness.schemaVersion shouldBe 1
        readiness.generatedFrom.commit.length shouldBeGreaterThan 7
        readiness.semantics.catalogCoverage.contains("Separate") shouldBe true
    }

    test("empty evidence ledger never promotes existing definitions to verified") {
        readiness.counts.getValue("IMPLEMENTED_UNVERIFIED") shouldBeGreaterThan 0
        readiness.counts.getValue("IMPLEMENTED_VERIFIED") shouldBe 0
        readiness.semantics.releaseEligible.shouldBeFalse()
    }

    test("every primary generated status is present") {
        listOf(
            "IMPLEMENTED_UNVERIFIED",
            "IMPLEMENTED_VERIFIED",
            "BLOCKED_FEATURE",
            "NOT_PLANNED",
            "UNMATCHED_TRIAGE",
        ).forEach { readiness.counts.containsKey(it) shouldBe true }
    }
})

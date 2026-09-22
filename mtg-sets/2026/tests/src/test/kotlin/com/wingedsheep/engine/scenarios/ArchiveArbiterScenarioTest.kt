package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe

class ArchiveArbiterScenarioTest : ScenarioTestBase() {
    private fun castArbiter(mode: Int) = scenario().withPlayers()
        .withCardInHand(1, "Archive Arbiter")
        .withCardOnBattlefield(2, "Sol Ring")
        .withCardOnBattlefield(2, "Grizzly Bears")
        .withLandsOnBattlefield(1, "Plains", 6)
        .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
        .also { game ->
            game.castSpell(1, "Archive Arbiter").error shouldBe null
            game.resolveStack()
            val decision = game.state.pendingDecision as ChooseOptionDecision
            game.submitDecision(OptionChosenResponse(decision.id, mode)).error shouldBe null
        }

    init {
        test("enter mode one destroys a noncreature, nonland permanent and cannot target a creature") {
            val game = castArbiter(0)
            val targeting = game.state.pendingDecision as ChooseTargetsDecision
            targeting.legalTargets[0]!! shouldContain game.findPermanent("Sol Ring")!!
            targeting.legalTargets[0]!! shouldNotContain game.findPermanent("Grizzly Bears")!!
            game.selectTargets(listOf(game.findPermanent("Sol Ring")!!)).error shouldBe null
            game.resolveStack()
            game.isInGraveyard(2, "Sol Ring") shouldBe true
        }

        test("enter mode two gains four life") {
            val game = castArbiter(1)
            game.resolveStack()
            game.getLifeTotal(1) shouldBe 24
            game.isOnBattlefield("Sol Ring") shouldBe true
        }
    }
}

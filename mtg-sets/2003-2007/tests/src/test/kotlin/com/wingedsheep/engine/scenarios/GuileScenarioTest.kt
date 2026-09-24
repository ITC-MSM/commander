package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SpellCounteredEvent
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Guile (LRW #69) — a spell you counter is exiled instead, and you may cast it right away without
 * paying its mana cost.
 */
class GuileScenarioTest : ScenarioTestBase() {
    init {
        fun base() = scenario().withPlayers("P1", "P2")
            .withCardOnBattlefield(1, "Guile")
            .withCardInHand(1, "Counterspell")
            .withLandsOnBattlefield(1, "Island", 2)
            .withCardInHand(2, "Grizzly Bears")
            .withLandsOnBattlefield(2, "Forest", 2)
            .withActivePlayer(2).inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)

        test("the countered spell is exiled and you may cast it for free") {
            val game = base().build()
            game.castSpell(2, "Grizzly Bears").error shouldBe null
            game.passPriority().error shouldBe null
            game.castSpellTargetingStackSpell(1, "Counterspell", "Grizzly Bears").error shouldBe null
            val results = game.resolveStack()
            game.getPendingDecision().shouldBeInstanceOf<YesNoDecision>()
            game.answerYesNo(true).error shouldBe null
            game.resolveStack()
            val bears = game.findPermanent("Grizzly Bears")!!
            game.state.projectedState.getController(bears) shouldBe game.player1Id
            results.flatMap { it.events }.filterIsInstance<SpellCounteredEvent>().size shouldBe 0
        }

        test("declining leaves the card in exile for good") {
            val game = base().build()
            game.castSpell(2, "Grizzly Bears").error shouldBe null
            game.passPriority().error shouldBe null
            game.castSpellTargetingStackSpell(1, "Counterspell", "Grizzly Bears").error shouldBe null
            game.resolveStack()
            game.answerYesNo(false).error shouldBe null
            game.isInExile(2, "Grizzly Bears") shouldBe true
            game.isInGraveyard(2, "Grizzly Bears") shouldBe false
            game.isOnBattlefield("Grizzly Bears") shouldBe false
        }

        test("a declined counter-unless-pay offers the free cast before the counterspell finishes") {
            val game = scenario().withPlayers("P1", "P2")
                .withCardOnBattlefield(1, "Guile")
                .withCardInHand(1, "Disrupt")
                .withLandsOnBattlefield(1, "Island", 1)
                .withCardInLibrary(1, "Island").withCardInLibrary(1, "Island").withCardInLibrary(1, "Island")
                .withCardInHand(2, "Divination")
                .withLandsOnBattlefield(2, "Island", 4)
                .withActivePlayer(2).inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()
            game.castSpell(2, "Divination").error shouldBe null
            game.passPriority().error shouldBe null
            game.castSpellTargetingStackSpell(1, "Disrupt", "Divination").error shouldBe null
            game.resolveStack()
            // Divination's controller is asked whether to pay {1}; declining counters it.
            game.getPendingDecision()!!.playerId shouldBe game.player2Id
            game.answerYesNo(false).error shouldBe null
            // Guile's offer comes now — before Disrupt's "Draw a card".
            val offer = game.getPendingDecision().shouldBeInstanceOf<YesNoDecision>()
            offer.playerId shouldBe game.player1Id
            game.handSize(1) shouldBe 0
            game.answerYesNo(true).error shouldBe null
            game.handSize(1) shouldBe 1
            game.resolveStack()
            game.handSize(1) shouldBe 3
            game.isInGraveyard(2, "Divination") shouldBe true
        }

        test("an opponent's counter is not replaced") {
            val game = scenario().withPlayers("P1", "P2")
                .withCardOnBattlefield(1, "Guile")
                .withCardInHand(1, "Grizzly Bears")
                .withLandsOnBattlefield(1, "Forest", 2)
                .withCardInHand(2, "Counterspell")
                .withLandsOnBattlefield(2, "Island", 2)
                .withActivePlayer(1).inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()
            game.castSpell(1, "Grizzly Bears").error shouldBe null
            game.passPriority().error shouldBe null
            game.castSpellTargetingStackSpell(2, "Counterspell", "Grizzly Bears").error shouldBe null
            game.resolveStack()
            game.isInGraveyard(1, "Grizzly Bears") shouldBe true
        }

        test("a spell that can't be countered resolves normally") {
            val game = scenario().withPlayers("P1", "P2")
                .withCardOnBattlefield(1, "Guile")
                .withCardInHand(1, "Counterspell")
                .withLandsOnBattlefield(1, "Island", 2)
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withCardInHand(2, "Abrupt Decay")
                .withLandsOnBattlefield(2, "Swamp", 1)
                .withLandsOnBattlefield(2, "Forest", 1)
                .withActivePlayer(2).inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()
            game.castSpell(2, "Abrupt Decay", game.findPermanent("Grizzly Bears")!!).error shouldBe null
            game.passPriority().error shouldBe null
            game.castSpellTargetingStackSpell(1, "Counterspell", "Abrupt Decay").error shouldBe null
            game.resolveStack()
            game.hasPendingDecision() shouldBe false
            game.isInGraveyard(2, "Abrupt Decay") shouldBe true
            game.isInGraveyard(1, "Grizzly Bears") shouldBe true
        }
    }
}

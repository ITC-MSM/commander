package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

/**
 * Cairn Wanderer (LRW #105) — has each listed keyword a creature card in any graveyard has,
 * including every landwalk and every protection quality.
 */
class CairnWandererScenarioTest : ScenarioTestBase() {
    init {
        fun base() = scenario().withPlayers("P1", "P2")
            .withCardOnBattlefield(1, "Cairn Wanderer")
            .withActivePlayer(1).inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)

        test("empty graveyards grant nothing") {
            val game = base().build()
            val wanderer = game.findPermanent("Cairn Wanderer")!!
            game.state.projectedState.hasKeyword(wanderer, Keyword.FLYING) shouldBe false
            game.state.projectedState.hasKeyword(wanderer, Keyword.VIGILANCE) shouldBe false
        }

        test("a creature card in an opponent's graveyard lends its listed keywords") {
            val game = base().withCardInGraveyard(2, "Serra Angel").build()
            val wanderer = game.findPermanent("Cairn Wanderer")!!
            game.state.projectedState.hasKeyword(wanderer, Keyword.FLYING) shouldBe true
            game.state.projectedState.hasKeyword(wanderer, Keyword.VIGILANCE) shouldBe true
        }

        test("protection is gained with its quality, and landwalk in its kind") {
            val game = base().withCardInGraveyard(1, "White Knight")
                .withCardInGraveyard(2, "Bog Wraith").build()
            val wanderer = game.findPermanent("Cairn Wanderer")!!
            val projected = game.state.projectedState
            projected.hasKeyword(wanderer, "PROTECTION_FROM_BLACK") shouldBe true
            projected.hasKeyword(wanderer, Keyword.FIRST_STRIKE) shouldBe true
            projected.hasKeyword(wanderer, Keyword.SWAMPWALK) shouldBe true
        }

        test("the keywords go when the card leaves the graveyard") {
            val game = base().withCardInGraveyard(2, "Serra Angel")
                .withCardInHand(1, "Cremate")
                .withLandsOnBattlefield(1, "Swamp", 1)
                .withCardInLibrary(1, "Swamp").build()
            game.castSpellTargetingGraveyardCard(1, "Cremate", 2, "Serra Angel").error shouldBe null
            game.resolveStack()
            val wanderer = game.findPermanent("Cairn Wanderer")!!
            game.state.projectedState.hasKeyword(wanderer, Keyword.FLYING) shouldBe false
        }

        test("a noncreature card with a listed keyword grants nothing") {
            val game = base().withCardInGraveyard(2, "Lightning Bolt").build()
            val wanderer = game.findPermanent("Cairn Wanderer")!!
            game.state.projectedState.hasKeyword(wanderer, Keyword.HASTE) shouldBe false
        }
    }
}

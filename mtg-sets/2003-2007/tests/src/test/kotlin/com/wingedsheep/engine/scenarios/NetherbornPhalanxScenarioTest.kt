package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import io.kotest.matchers.shouldBe

/**
 * Netherborn Phalanx — "each opponent loses 1 life for each creature they control".
 *
 * The interesting claim is that the amount is read **per opponent**, from that opponent's own
 * battlefield, rather than once from a table-wide or controller-relative count.
 */
class NetherbornPhalanxScenarioTest : ScenarioTestBase() {
    init {
        test("each opponent loses life equal to their own creature count") {
            val game = scenario().withPlayers("P1", "P2")
                .withCardInHand(1, "Netherborn Phalanx")
                .withLandsOnBattlefield(1, "Swamp", 6)
                .withCardOnBattlefield(2, "Grizzly Bears")
                .withCardOnBattlefield(2, "Hill Giant")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            game.castSpell(1, "Netherborn Phalanx").error shouldBe null
            game.resolveStack()

            game.getLifeTotal(2) shouldBe 18
            game.getLifeTotal(1) shouldBe 20
        }

        test("the controller's own creatures are not counted") {
            // P1 fields three creatures (the Phalanx plus two others), P2 exactly one. A global or
            // controller-relative count would drain P2 for three or four; the printed card drains
            // for one.
            val game = scenario().withPlayers("P1", "P2")
                .withCardInHand(1, "Netherborn Phalanx")
                .withLandsOnBattlefield(1, "Swamp", 6)
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withCardOnBattlefield(1, "Hill Giant")
                .withCardOnBattlefield(2, "Grizzly Bears")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            game.castSpell(1, "Netherborn Phalanx").error shouldBe null
            game.resolveStack()

            game.getLifeTotal(2) shouldBe 19
            game.getLifeTotal(1) shouldBe 20
        }

        test("an opponent with no creatures loses no life") {
            val game = scenario().withPlayers("P1", "P2")
                .withCardInHand(1, "Netherborn Phalanx")
                .withLandsOnBattlefield(1, "Swamp", 6)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            game.castSpell(1, "Netherborn Phalanx").error shouldBe null
            game.resolveStack()

            game.getLifeTotal(2) shouldBe 20
            game.isOnBattlefield("Netherborn Phalanx") shouldBe true
        }

        test("transmute searches for a card with mana value six") {
            val game = scenario().withPlayers("P1", "P2")
                .withCardInHand(1, "Netherborn Phalanx")
                .withCardInLibrary(1, "Hill Giant")
                .withCardInLibrary(1, "Netherborn Phalanx")
                .withLandsOnBattlefield(1, "Swamp", 3)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            val source = game.findCardsInHand(1, "Netherborn Phalanx").single()
            val ability = cardRegistry.getCard("Netherborn Phalanx")!!
                .activatedAbilities.single { it.activateFromZone == Zone.HAND }
            game.execute(ActivateAbility(game.player1Id, source, ability.id)).error shouldBe null
            game.isInGraveyard(1, "Netherborn Phalanx") shouldBe true
            game.resolveStack()

            // Only the six-drop matches; Hill Giant's mana value is four.
            val match = game.findCardsInLibrary(1, "Netherborn Phalanx").single()
            (game.state.pendingDecision as SelectCardsDecision).options shouldBe listOf(match)
            game.selectCards(listOf(match)).error shouldBe null
            game.resolveStack()
            game.isInHand(1, "Netherborn Phalanx") shouldBe true
        }
    }
}

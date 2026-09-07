package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import io.kotest.matchers.shouldBe

/**
 * Clutch of the Undercity — "Return target permanent to its owner's hand. Its controller loses
 * 3 life."
 *
 * The life loss is read *after* the bounce has already moved the permanent, so "its controller"
 * has to fall through to the permanent's last-known controller rather than to Clutch's own
 * controller. Both directions are covered below: bouncing the opponent's creature must not cost
 * the caster life, and bouncing your own must.
 */
class ClutchOfTheUndercityScenarioTest : ScenarioTestBase() {
    init {
        test("the bounced permanent's controller loses the life, not the caster") {
            val game = scenario().withPlayers("P1", "P2")
                .withCardInHand(1, "Clutch of the Undercity")
                .withLandsOnBattlefield(1, "Island", 3)
                .withLandsOnBattlefield(1, "Swamp", 1)
                .withCardOnBattlefield(2, "Grizzly Bears")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            val bear = game.findPermanent("Grizzly Bears")!!
            game.castSpell(1, "Clutch of the Undercity", bear).error shouldBe null
            game.resolveStack()

            game.isInHand(2, "Grizzly Bears") shouldBe true
            game.getLifeTotal(2) shouldBe 17
            game.getLifeTotal(1) shouldBe 20
        }

        test("bouncing your own permanent costs you the life") {
            val game = scenario().withPlayers("P1", "P2")
                .withCardInHand(1, "Clutch of the Undercity")
                .withLandsOnBattlefield(1, "Island", 3)
                .withLandsOnBattlefield(1, "Swamp", 1)
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            val bear = game.findPermanent("Grizzly Bears")!!
            game.castSpell(1, "Clutch of the Undercity", bear).error shouldBe null
            game.resolveStack()

            game.isInHand(1, "Grizzly Bears") shouldBe true
            game.getLifeTotal(1) shouldBe 17
            game.getLifeTotal(2) shouldBe 20
        }

        test("any permanent is a legal target, lands included") {
            val game = scenario().withPlayers("P1", "P2")
                .withCardInHand(1, "Clutch of the Undercity")
                .withLandsOnBattlefield(1, "Island", 3)
                .withLandsOnBattlefield(1, "Swamp", 1)
                .withLandsOnBattlefield(2, "Forest", 1)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            val forest = game.findPermanent("Forest")!!
            game.castSpell(1, "Clutch of the Undercity", forest).error shouldBe null
            game.resolveStack()

            game.isInHand(2, "Forest") shouldBe true
            game.getLifeTotal(2) shouldBe 17
        }

        test("transmute searches by Clutch's own mana value") {
            val game = scenario().withPlayers("P1", "P2")
                .withCardInHand(1, "Clutch of the Undercity")
                .withCardInLibrary(1, "Frogmite")
                .withCardInLibrary(1, "Island")
                .withLandsOnBattlefield(1, "Island", 2)
                .withLandsOnBattlefield(1, "Swamp", 2)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            val source = game.findCardsInHand(1, "Clutch of the Undercity").single()
            val ability = cardRegistry.getCard("Clutch of the Undercity")!!
                .activatedAbilities.single { it.activateFromZone == Zone.HAND }
            game.execute(ActivateAbility(game.player1Id, source, ability.id)).error shouldBe null
            game.isInGraveyard(1, "Clutch of the Undercity") shouldBe true
            game.resolveStack()

            // Clutch's own mana value is 4, so the Island (0) is filtered out and Frogmite (4) is the
            // only match — affinity does not reduce mana value.
            val match = game.findCardsInLibrary(1, "Frogmite").single()
            (game.state.pendingDecision as SelectCardsDecision).options shouldBe listOf(match)
            game.selectCards(listOf(match)).error shouldBe null
            game.resolveStack()
            game.isInHand(1, "Frogmite") shouldBe true
        }
    }
}

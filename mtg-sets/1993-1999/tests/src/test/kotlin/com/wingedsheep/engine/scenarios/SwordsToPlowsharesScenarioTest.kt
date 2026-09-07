package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Swords to Plowshares (LEA #40).
 *
 * "{W} Instant — Exile target creature. Its controller gains life equal to its power."
 *
 * The second case is the one that pins the implementation down: an Unholy Strength on the
 * targeted creature makes its *projected* power (4) differ from its printed power (2), so the
 * assertion fails if the life gain ever reads base characteristics — which is exactly what
 * happens if the two effects are re-sequenced into the printed exile-then-gain order.
 */
class SwordsToPlowsharesScenarioTest : ScenarioTestBase() {

    init {
        test("Swords to Plowshares exiles the creature and its controller gains life equal to its power") {
            val game = scenario()
                .withPlayers("Caster", "Defender")
                .withCardInHand(1, "Swords to Plowshares")
                .withLandsOnBattlefield(1, "Plains", 1)
                .withCardOnBattlefield(2, "Grizzly Bears")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val bears = game.findPermanent("Grizzly Bears")!!

            val cast = game.castSpell(1, "Swords to Plowshares", targetId = bears)
            withClue("Casting Swords to Plowshares at Grizzly Bears should succeed: ${cast.error}") {
                cast.error shouldBe null
            }
            if (game.hasPendingDecision()) game.submitManaSourcesAutoPay()
            game.resolveStack()

            withClue("Grizzly Bears (the targeted creature) should be exiled") {
                game.isOnBattlefield("Grizzly Bears") shouldBe false
            }
            withClue("The creature's controller (player 2) should gain 2 life (Grizzly Bears' power)") {
                game.getLifeTotal(2) shouldBe 22
            }
            withClue("The caster (player 1) should not gain life") {
                game.getLifeTotal(1) shouldBe 20
            }
        }

        test("the life gained is the creature's projected power, not its printed power") {
            val game = scenario()
                .withPlayers("Caster", "Defender")
                .withCardInHand(1, "Swords to Plowshares")
                .withLandsOnBattlefield(1, "Plains", 1)
                .withCardOnBattlefield(2, "Grizzly Bears")
                .withCardAttachedTo(2, "Unholy Strength", "Grizzly Bears")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val bears = game.findPermanent("Grizzly Bears")!!

            val cast = game.castSpell(1, "Swords to Plowshares", targetId = bears)
            withClue("Casting Swords to Plowshares at the enchanted Grizzly Bears should succeed: ${cast.error}") {
                cast.error shouldBe null
            }
            if (game.hasPendingDecision()) game.submitManaSourcesAutoPay()
            game.resolveStack()

            withClue("The enchanted Grizzly Bears should be exiled") {
                game.isOnBattlefield("Grizzly Bears") shouldBe false
            }
            withClue("Player 2 should gain 4 life — Grizzly Bears' 2 power plus Unholy Strength's +2, not its printed 2") {
                game.getLifeTotal(2) shouldBe 24
            }
        }
    }
}

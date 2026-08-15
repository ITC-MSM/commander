package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Ran and Shaw exercises the `removeLegendary` clause on `CreateTokenCopyOfSourceEffect`
 * ("create a token that's a copy of Ran and Shaw, except it's not legendary") gated by an
 * intervening-if: "if you cast them and there are three or more Dragon and/or Lesson cards in your
 * graveyard" (`Conditions.WasCast` + `CardsInGraveyardMatchingAtLeast`).
 */
class RanAndShawScenarioTest : ScenarioTestBase() {

    init {
        test("cast with 3+ Dragon/Lesson cards in graveyard makes a non-legendary copy") {
            val game = scenario()
                .withPlayers("Alice", "Bob")
                .withCardInHand(1, "Ran and Shaw")
                .withLandsOnBattlefield(1, "Mountain", 5)
                .withCardInGraveyard(1, "Firebending Lesson")
                .withCardInGraveyard(1, "Earthbending Lesson")
                .withCardInGraveyard(1, "Airbending Lesson")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val cast = game.castSpell(1, "Ran and Shaw")
            withClue("Casting should succeed: ${cast.error}") { cast.error shouldBe null }
            if (game.hasPendingDecision()) game.submitManaSourcesAutoPay()
            game.resolveStack()

            val copies = game.findPermanents("Ran and Shaw")
                .mapNotNull { game.state.getEntity(it)?.get<CardComponent>() }
            withClue("The original plus a non-legendary token coexist (legend rule spares the token)") {
                copies.size shouldBe 2
                copies.count { it.typeLine.isLegendary } shouldBe 1
            }
            val token = copies.first { !it.typeLine.isLegendary }
            withClue("Token is a faithful copy: a Dragon creature, just not legendary") {
                token.typeLine.isCreature shouldBe true
                token.typeLine.hasSubtype(Subtype("Dragon")) shouldBe true
            }
        }

        test("cast with fewer than 3 Dragon/Lesson cards makes no copy") {
            val game = scenario()
                .withPlayers("Alice", "Bob")
                .withCardInHand(1, "Ran and Shaw")
                .withLandsOnBattlefield(1, "Mountain", 5)
                .withCardInGraveyard(1, "Firebending Lesson")
                .withCardInGraveyard(1, "Earthbending Lesson")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val cast = game.castSpell(1, "Ran and Shaw")
            withClue("Casting should succeed: ${cast.error}") { cast.error shouldBe null }
            if (game.hasPendingDecision()) game.submitManaSourcesAutoPay()
            game.resolveStack()

            withClue("Intervening-if fails (only 2 Lesson cards) — no token, just Ran and Shaw itself") {
                game.findPermanents("Ran and Shaw").size shouldBe 1
            }
        }
    }
}

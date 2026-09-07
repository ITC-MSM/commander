package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Shred Memory — "Exile up to four target cards from a single graveyard."
 *
 * Two claims worth proving: the "single graveyard" constraint really rejects a mixed set of
 * targets, and "up to four" tolerates fewer than four (including zero).
 */
class ShredMemoryScenarioTest : ScenarioTestBase() {
    init {
        fun graveyardTargets(game: TestGame, ownerNumber: Int, names: List<String>): List<ChosenTarget> {
            val ownerId = if (ownerNumber == 1) game.player1Id else game.player2Id
            return names.map { name ->
                ChosenTarget.Card(
                    game.findCardsInGraveyard(ownerNumber, name).first(),
                    ownerId,
                    Zone.GRAVEYARD
                )
            }
        }

        test("exiles four cards from one opponent's graveyard") {
            val game = scenario().withPlayers("P1", "P2")
                .withCardInHand(1, "Shred Memory")
                .withLandsOnBattlefield(1, "Swamp", 2)
                .withCardInGraveyard(2, "Grizzly Bears")
                .withCardInGraveyard(2, "Hill Giant")
                .withCardInGraveyard(2, "Island")
                .withCardInGraveyard(2, "Forest")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            val spell = game.findCardsInHand(1, "Shred Memory").single()
            val targets = graveyardTargets(
                game, 2, listOf("Grizzly Bears", "Hill Giant", "Island", "Forest")
            )
            game.execute(CastSpell(game.player1Id, spell, targets)).error shouldBe null
            game.resolveStack()

            game.graveyardSize(2) shouldBe 0
            game.isInExile(2, "Grizzly Bears") shouldBe true
            game.isInExile(2, "Hill Giant") shouldBe true
            game.isInExile(2, "Island") shouldBe true
            game.isInExile(2, "Forest") shouldBe true
        }

        test("targets may not be spread across two graveyards") {
            val game = scenario().withPlayers("P1", "P2")
                .withCardInHand(1, "Shred Memory")
                .withLandsOnBattlefield(1, "Swamp", 2)
                .withCardInGraveyard(1, "Grizzly Bears")
                .withCardInGraveyard(2, "Hill Giant")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            val spell = game.findCardsInHand(1, "Shred Memory").single()
            val targets = graveyardTargets(game, 1, listOf("Grizzly Bears")) +
                graveyardTargets(game, 2, listOf("Hill Giant"))
            game.execute(CastSpell(game.player1Id, spell, targets)).error shouldNotBe null

            game.isInGraveyard(1, "Grizzly Bears") shouldBe true
            game.isInGraveyard(2, "Hill Giant") shouldBe true
        }

        test("fewer than four targets is legal") {
            val game = scenario().withPlayers("P1", "P2")
                .withCardInHand(1, "Shred Memory")
                .withLandsOnBattlefield(1, "Swamp", 2)
                .withCardInGraveyard(2, "Grizzly Bears")
                .withCardInGraveyard(2, "Hill Giant")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            val spell = game.findCardsInHand(1, "Shred Memory").single()
            game.execute(
                CastSpell(game.player1Id, spell, graveyardTargets(game, 2, listOf("Hill Giant")))
            ).error shouldBe null
            game.resolveStack()

            game.isInExile(2, "Hill Giant") shouldBe true
            game.isInGraveyard(2, "Grizzly Bears") shouldBe true
        }

        test("zero targets still resolves") {
            val game = scenario().withPlayers("P1", "P2")
                .withCardInHand(1, "Shred Memory")
                .withLandsOnBattlefield(1, "Swamp", 2)
                .withCardInGraveyard(2, "Grizzly Bears")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            val spell = game.findCardsInHand(1, "Shred Memory").single()
            game.execute(CastSpell(game.player1Id, spell, emptyList())).error shouldBe null
            game.resolveStack()

            game.isInGraveyard(2, "Grizzly Bears") shouldBe true
            game.isInGraveyard(1, "Shred Memory") shouldBe true
        }
    }
}

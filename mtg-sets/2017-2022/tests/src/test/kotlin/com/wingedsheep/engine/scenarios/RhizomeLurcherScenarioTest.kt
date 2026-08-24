package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario coverage for Rhizome Lurcher (GRN #196).
 *
 * {2}{B}{G} Creature — Fungus Zombie 2/2
 * "Undergrowth — This creature enters with a number of +1/+1 counters on it equal to the number
 *  of creature cards in your graveyard."
 *
 * Undergrowth's first appearance in the corpus. The count is read as the permanent *enters*
 * (a replacement effect, CR 614.1c), so it is fixed at entry rather than re-read afterwards, and
 * only creature cards in *your* graveyard count — an opponent's graveyard and your non-creature
 * cards are both invisible to it.
 */
class RhizomeLurcherScenarioTest : ScenarioTestBase() {

    private val projector = StateProjector()

    init {
        context("Rhizome Lurcher") {

            fun board(
                yourCreatureCards: Int,
                yourNoncreatureCards: Int = 0,
                opponentCreatureCards: Int = 0,
            ) = run {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Rhizome Lurcher")
                    .withLandsOnBattlefield(1, "Swamp", 2)
                    .withLandsOnBattlefield(1, "Forest", 2)
                repeat(yourCreatureCards) { builder = builder.withCardInGraveyard(1, "Centaur Courser") }
                repeat(yourNoncreatureCards) { builder = builder.withCardInGraveyard(1, "Shock") }
                repeat(opponentCreatureCards) { builder = builder.withCardInGraveyard(2, "Centaur Courser") }
                builder
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()
            }

            test("enters with one +1/+1 counter per creature card in your graveyard") {
                val game = board(yourCreatureCards = 3)

                game.castSpell(1, "Rhizome Lurcher").error shouldBe null
                game.resolveStack()

                val lurcher = game.findPermanent("Rhizome Lurcher")!!
                val projected = projector.project(game.state)
                withClue("2/2 base plus three counters") {
                    projected.getPower(lurcher) shouldBe 5
                    projected.getToughness(lurcher) shouldBe 5
                }
            }

            test("only creature cards in YOUR graveyard count") {
                val game = board(
                    yourCreatureCards = 1,
                    yourNoncreatureCards = 4,
                    opponentCreatureCards = 4,
                )

                game.castSpell(1, "Rhizome Lurcher").error shouldBe null
                game.resolveStack()

                val lurcher = game.findPermanent("Rhizome Lurcher")!!
                withClue("one counter — the instants and the opponent's creatures are invisible") {
                    projector.project(game.state).getPower(lurcher) shouldBe 3
                }
            }

            test("an empty graveyard leaves it a plain 2/2") {
                val game = board(yourCreatureCards = 0)

                game.castSpell(1, "Rhizome Lurcher").error shouldBe null
                game.resolveStack()

                val lurcher = game.findPermanent("Rhizome Lurcher")!!
                val projected = projector.project(game.state)
                projected.getPower(lurcher) shouldBe 2
                projected.getToughness(lurcher) shouldBe 2
            }
        }
    }
}

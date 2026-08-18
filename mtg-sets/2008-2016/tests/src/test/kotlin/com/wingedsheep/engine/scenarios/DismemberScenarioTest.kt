package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Dismember (NPH #57) — {1}{B/P}{B/P} Instant.
 *
 *   Target creature gets -5/-5 until end of turn.
 *
 * `{B/P}` is Phyrexian black mana, parsed by `ManaCost.parse` like Skrelv, Defector Mite's and
 * Namor, the Sub-Mariner's activated-ability costs — no card-specific wiring needed. The effect
 * itself composes the plain `Effects.ModifyStats(-5, -5, target)` used by Last Gasp.
 */
class DismemberScenarioTest : ScenarioTestBase() {

    init {
        context("Dismember") {

            test("kills a small creature by reducing its toughness to 0 or less") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Dismember")
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withCardOnBattlefield(2, "Hill Giant") // 3/3
                    .withCardInLibrary(1, "Swamp")
                    .withCardInLibrary(2, "Swamp")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val giant = game.findPermanent("Hill Giant")!!

                game.castSpell(1, "Dismember", giant).error shouldBe null
                game.resolveStack()

                withClue("-5/-5 kills a 3/3 (0 or less toughness, CR 704.5g)") {
                    game.isOnBattlefield("Hill Giant") shouldBe false
                    game.isInGraveyard(2, "Hill Giant") shouldBe true
                }
            }

            test("a high-toughness creature survives at reduced stats") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Dismember")
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withCardOnBattlefield(2, "Wall of Stone") // 0/8, Defender
                    .withCardInLibrary(1, "Swamp")
                    .withCardInLibrary(2, "Swamp")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val wall = game.findPermanent("Wall of Stone")!!

                game.castSpell(1, "Dismember", wall).error shouldBe null
                game.resolveStack()

                withClue("a 0/8 survives -5/-5 as a -5/3 (still alive)") {
                    game.isOnBattlefield("Wall of Stone") shouldBe true
                    game.state.projectedState.getPower(wall) shouldBe -5
                    game.state.projectedState.getToughness(wall) shouldBe 3
                }
            }
        }
    }
}

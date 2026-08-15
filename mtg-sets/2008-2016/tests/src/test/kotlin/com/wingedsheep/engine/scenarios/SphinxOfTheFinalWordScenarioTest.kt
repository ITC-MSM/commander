package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Sphinx of the Final Word (OGW #63, reprinted FDN #747).
 *
 * "This spell can't be countered. Flying. Hexproof.
 *  Instant and sorcery spells you control can't be countered."
 *
 * The interesting half is the static ability's *scope*: `GrantCantBeCountered` is filtered with
 * `youControl()`, so it protects only the Sphinx controller's instants and sorceries — an
 * opponent's instant is still counterable while the Sphinx is on the battlefield.
 */
class SphinxOfTheFinalWordScenarioTest : ScenarioTestBase() {

    init {
        context("Sphinx of the Final Word") {

            test("your instant can't be countered while the Sphinx is on the battlefield") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Sphinx of the Final Word")
                    .withCardInHand(1, "Shock")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withCardInHand(2, "Cancel")
                    .withLandsOnBattlefield(2, "Island", 3)
                    .withLifeTotal(2, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                // Player 1 (Sphinx controller, active player) casts Shock at the opponent.
                game.castSpellTargetingPlayer(1, "Shock", 2).error shouldBe null
                game.passPriority()

                // Player 2 responds with Cancel — legal to cast, but it can't counter Shock.
                val cancel = game.castSpellTargetingStackSpell(2, "Cancel", "Shock")
                withClue("Cancel itself is legal to cast — it just won't counter: ${cancel.error}") {
                    cancel.error shouldBe null
                }
                game.resolveStack()

                withClue("Shock resolved despite Cancel — 2 damage dealt") {
                    game.getLifeTotal(2) shouldBe 18
                }
                withClue("Both spells left the stack") {
                    game.isInGraveyard(1, "Shock") shouldBe true
                    game.isInGraveyard(2, "Cancel") shouldBe true
                }
            }

            test("does not protect an opponent's instant") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Sphinx of the Final Word")
                    .withCardInHand(1, "Cancel")
                    .withLandsOnBattlefield(1, "Island", 3)
                    .withCardInHand(2, "Shock")
                    .withLandsOnBattlefield(2, "Mountain", 2)
                    .withLifeTotal(1, 20)
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                // Player 2 (the opponent, active player) casts Shock at the Sphinx's controller.
                game.castSpellTargetingPlayer(2, "Shock", 1).error shouldBe null
                game.passPriority()

                // Player 1 counters it — the Sphinx's `youControl()` scope does NOT protect the
                // opponent's Shock, so Cancel resolves and counters it.
                game.castSpellTargetingStackSpell(1, "Cancel", "Shock").error shouldBe null
                game.resolveStack()

                withClue("The opponent's Shock is not covered by the Sphinx's `youControl()` scope") {
                    game.getLifeTotal(1) shouldBe 20
                    game.isInGraveyard(2, "Shock") shouldBe true
                }
            }
        }
    }
}

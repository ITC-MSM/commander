package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Malicious Eclipse (LCI #111) — {1}{B}{B} Sorcery, Uncommon.
 *
 * "All creatures get -2/-2 until end of turn. If a creature an opponent controls would die this
 *  turn, exile it instead."
 *
 * The death→exile clause is a floating, turn-scoped `RedirectZoneChange` scoped to creatures an
 * opponent controls (Forgotten Cellar idiom) — so a dying opponent creature is exiled while your own
 * still goes to the graveyard, and the redirect lasts the whole turn (not just the -2/-2's kills).
 */
class MaliciousEclipseScenarioTest : ScenarioTestBase() {

    init {
        context("Malicious Eclipse") {

            test("your creature goes to the graveyard, an opponent's is exiled") {
                val game = scenario()
                    .withPlayers("You", "Opponent")
                    .withCardOnBattlefield(1, "Grizzly Bears")   // yours (2/2) — dies to graveyard
                    .withCardOnBattlefield(2, "Coral Merfolk")   // opponent's (2/1) — dies to exile
                    .withCardInHand(1, "Malicious Eclipse")
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cast = game.castSpell(1, "Malicious Eclipse")
                withClue("Malicious Eclipse should cast: ${cast.error}") { cast.error shouldBe null }
                if (game.getPendingDecision() is SelectManaSourcesDecision) game.submitManaSourcesAutoPay()
                game.resolveStack()

                withClue("your creature dies to the graveyard (the replacement only affects opponents)") {
                    game.isInGraveyard(1, "Grizzly Bears") shouldBe true
                }
                withClue("the opponent's creature is exiled instead of dying") {
                    game.isInExile(2, "Coral Merfolk") shouldBe true
                    game.isInGraveyard(2, "Coral Merfolk") shouldBe false
                }
            }

            test("the exile-instead-of-dying replacement lasts the whole turn") {
                val game = scenario()
                    .withPlayers("You", "Opponent")
                    .withCardOnBattlefield(2, "Hill Giant")   // opponent's 3/3 — survives -2/-2 as a 1/1
                    .withCardInHand(1, "Malicious Eclipse")
                    .withCardInHand(1, "Shock")
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val eclipse = game.castSpell(1, "Malicious Eclipse")
                withClue("Malicious Eclipse should cast: ${eclipse.error}") { eclipse.error shouldBe null }
                if (game.getPendingDecision() is SelectManaSourcesDecision) game.submitManaSourcesAutoPay()
                game.resolveStack()

                val giant = game.findPermanent("Hill Giant")
                    ?: error("Hill Giant should survive the -2/-2 as a 1/1")

                // Kill it later this turn — it must still be exiled, not sent to the graveyard.
                val shock = game.castSpell(1, "Shock", giant)
                withClue("Shock should cast: ${shock.error}") { shock.error shouldBe null }
                if (game.getPendingDecision() is SelectManaSourcesDecision) game.submitManaSourcesAutoPay()
                game.resolveStack()

                withClue("a later death this turn is still redirected to exile") {
                    game.isInExile(2, "Hill Giant") shouldBe true
                    game.isInGraveyard(2, "Hill Giant") shouldBe false
                }
            }
        }
    }
}

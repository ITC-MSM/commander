package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Tests for From Father to Son (FIN #20).
 *
 * From Father to Son {1}{W} Sorcery
 * Search your library for a Vehicle card, reveal it, and put it into your hand. If this spell
 * was cast from a graveyard, put that card onto the battlefield instead. Then shuffle.
 * Flashback {4}{W}{W}{W}
 *
 * The destination is chosen at resolution by where the spell was cast from
 * ([com.wingedsheep.sdk.scripting.conditions.WasCastFromZone] GRAVEYARD): hand on a normal cast,
 * battlefield on a flashback (graveyard) cast.
 */
class FromFatherToSonScenarioTest : ScenarioTestBase() {

    init {
        test("cast from hand puts the found Vehicle into hand") {
            val game = scenario()
                .withPlayers()
                .withCardInHand(1, "From Father to Son")
                .withLandsOnBattlefield(1, "Plains", 2) // {1}{W}
                .withCardInLibrary(1, "Cargo Ship")     // a Vehicle to find
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val cargoShip = game.findCardsInLibrary(1, "Cargo Ship").single()

            game.castSpell(1, "From Father to Son").error shouldBe null
            game.resolveStack()
            withClue("Search should pause to choose a Vehicle") {
                game.hasPendingDecision() shouldBe true
            }
            game.selectCards(listOf(cargoShip))
            game.resolveStack()

            withClue("Found Vehicle goes to hand on a normal cast") {
                game.isInHand(1, "Cargo Ship") shouldBe true
                game.isOnBattlefield("Cargo Ship") shouldBe false
            }
        }

        test("cast from graveyard via flashback puts the found Vehicle onto the battlefield") {
            val game = scenario()
                .withPlayers()
                .withCardInGraveyard(1, "From Father to Son")
                .withLandsOnBattlefield(1, "Plains", 7) // Flashback {4}{W}{W}{W}
                .withCardInLibrary(1, "Cargo Ship")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val cargoShip = game.findCardsInLibrary(1, "Cargo Ship").single()

            game.castSpellFromGraveyard(1, "From Father to Son").error shouldBe null
            game.resolveStack()
            withClue("Search should pause to choose a Vehicle") {
                game.hasPendingDecision() shouldBe true
            }
            game.selectCards(listOf(cargoShip))
            game.resolveStack()

            withClue("Found Vehicle enters the battlefield on a graveyard (flashback) cast") {
                game.isOnBattlefield("Cargo Ship") shouldBe true
                game.isInHand(1, "Cargo Ship") shouldBe false
            }
        }
    }
}

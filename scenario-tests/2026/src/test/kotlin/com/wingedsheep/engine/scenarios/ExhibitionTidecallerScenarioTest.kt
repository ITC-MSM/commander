package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

/**
 * Exhibition Tidecaller {U} 0/2 Djinn Wizard — "Opus — Whenever you cast an instant or sorcery
 * spell, target player mills three cards. If five or more mana was spent to cast that spell, that
 * player mills ten cards instead."
 *
 * The mill is the *low* tier: base = mill 3, replaced by mill 10 when 5+ mana was spent
 * (`insteadIfFiveOrMore`). The single targeted player carries across both tiers. Exercises both
 * sides of the 5-mana boundary.
 */
class ExhibitionTidecallerScenarioTest : ScenarioTestBase() {

    init {
        context("Exhibition Tidecaller — Opus mill 3 / mill 10 if 5+ mana") {

            test("a 1-mana spell: target player mills three cards") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Exhibition Tidecaller")
                    // Bolt a creature the *caster* controls so the corpse lands in player 1's
                    // graveyard — keeps player 2's graveyard a clean measure of the mill.
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInHand(1, "Lightning Bolt") // {R}, 1 mana
                    .withCardInLibrary(2, "Island")
                    .withCardInLibrary(2, "Forest")
                    .withCardInLibrary(2, "Mountain")
                    .withCardInLibrary(2, "Plains")
                    .withCardInLibrary(2, "Swamp")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val libBefore = game.librarySize(2)

                game.castSpell(1, "Lightning Bolt", targetId = bears).error shouldBe null
                game.resolveStack() // Opus trigger goes on stack, asks for its target player
                game.selectTargets(listOf(game.player2Id)).error shouldBe null
                game.resolveStack()

                game.librarySize(2) shouldBe (libBefore - 3)
                game.graveyardSize(2) shouldBe 3
            }

            test("a 5-mana spell: target player mills ten cards instead") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Exhibition Tidecaller")
                    .withCardInHand(1, "Blaze") // {X}{R}
                    // Blaze a creature the caster controls — corpse lands in player 1's graveyard.
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .also { builder ->
                        repeat(12) { builder.withCardInLibrary(2, "Island") }
                    }
                    .withLandsOnBattlefield(1, "Mountain", 5)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val libBefore = game.librarySize(2)

                // Blaze X=4 → {4}{R} → 5 mana spent (boundary).
                game.castXSpell(1, "Blaze", xValue = 4, targetId = bears).error shouldBe null
                game.resolveStack()
                game.selectTargets(listOf(game.player2Id)).error shouldBe null
                game.resolveStack()

                game.librarySize(2) shouldBe (libBefore - 10)
                game.graveyardSize(2) shouldBe 10
            }
        }
    }
}

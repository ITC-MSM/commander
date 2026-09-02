package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Spellstutter Sprite (LRW #89, {1}{U} Creature — Faerie Wizard 1/1).
 *
 *   Flash, flying.
 *   When this creature enters, counter target spell with mana value X or less, where X is the
 *   number of Faeries you control.
 *
 * The whole card is the cap, and the cap has exactly one shape that reads right while being wrong:
 * a filter that ignores the dynamic amount and lets the Sprite counter anything. The two tests are
 * the same board on either side of the threshold — three Faeries against a mana value 2 spell, and
 * one Faerie against the same spell — so a cap that isn't read gives the *same* answer in both and
 * fails the second.
 *
 * The 2007-10-01 ruling's re-check on resolution is the engine's generic target-legality pass, which
 * this card gets for free by carrying the cap on the target filter rather than checking it as the
 * ability resolves.
 */
class SpellstutterSpriteScenarioTest : ScenarioTestBase() {

    /** [otherFaeries] extra Faeries Alice controls *before* the Sprite itself enters. */
    private fun sprite(otherFaeries: Int) = scenario()
        .withPlayers("Alice", "Bob")
        .withCardInHand(1, "Spellstutter Sprite")
        .withLandsOnBattlefield(1, "Island", 3)
        .apply { repeat(otherFaeries) { withCardOnBattlefield(1, "Nightshade Stinger") } }
        .withCardInHand(2, "Grizzly Bears") // {1}{G}, mana value 2
        .withLandsOnBattlefield(2, "Forest", 3)
        .withCardInLibrary(1, "Island")
        .withCardInLibrary(2, "Forest")
        .withActivePlayer(2)
        .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
        .build()

    private fun TestGame.stackSpell(name: String): EntityId? =
        state.stack.find { state.getEntity(it)?.get<CardComponent>()?.name == name }

    init {
        context("Spellstutter Sprite") {

            test("three Faeries counter a mana value 2 spell") {
                val game = sprite(otherFaeries = 2)

                game.castSpell(2, "Grizzly Bears").error shouldBe null
                game.passPriority()
                game.castSpell(1, "Spellstutter Sprite").error shouldBe null

                game.resolveStack()
                if (game.hasPendingDecision()) {
                    val bears = game.stackSpell("Grizzly Bears")
                    if (bears != null) game.selectTargets(listOf(bears))
                    game.resolveStack()
                }

                withClue("two Stingers plus the Sprite itself is X = 3, so mana value 2 is in range") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                    game.isInGraveyard(2, "Grizzly Bears") shouldBe true
                }
                game.isOnBattlefield("Spellstutter Sprite") shouldBe true
            }

            test("the Sprite alone can't reach a mana value 2 spell") {
                val game = sprite(otherFaeries = 0)

                game.castSpell(2, "Grizzly Bears").error shouldBe null
                game.passPriority()
                game.castSpell(1, "Spellstutter Sprite").error shouldBe null

                game.resolveStack()
                if (game.hasPendingDecision()) {
                    val bears = game.stackSpell("Grizzly Bears")
                    if (bears != null) game.selectTargets(listOf(bears)) else game.skipTargets()
                    game.resolveStack()
                }

                withClue("X = 1 with only the Sprite, so a mana value 2 spell is never a legal target") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe true
                    game.isInGraveyard(2, "Grizzly Bears") shouldBe false
                }
                withClue("the Sprite still resolves — a trigger with no legal target is simply removed") {
                    game.isOnBattlefield("Spellstutter Sprite") shouldBe true
                }
            }
        }
    }
}

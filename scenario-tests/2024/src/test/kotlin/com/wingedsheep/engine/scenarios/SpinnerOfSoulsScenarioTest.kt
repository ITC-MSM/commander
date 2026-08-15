package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import io.kotest.matchers.shouldBe

/**
 * Spinner of Souls — "{2}{G} 4/3 Reach. Whenever another nontoken creature you control dies, you may
 * reveal cards from the top of your library until you reveal a creature card. Put that card into
 * your hand and the rest on the bottom of your library in a random order."
 *
 * Exercises the new `Patterns.Library.revealUntilMatchToHand` composition. The optional "may" pauses
 * on a YesNoDecision at resolution; the dies-trigger test accepts it explicitly, then asserts the
 * resulting board (the revealed creature goes to hand, the non-creature cards stay in the library).
 */
class SpinnerOfSoulsScenarioTest : ScenarioTestBase() {

    init {
        // {0} sorcery to send a chosen creature to the graveyard on demand.
        val slay = card("Slay") {
            manaCost = "{0}"
            typeLine = "Sorcery"
            spell {
                val c = target("target creature", Targets.Creature)
                effect = Effects.Destroy(c)
            }
        }
        cardRegistry.register(listOf(slay))

        test("a nontoken creature you control dies: dig to the first creature, rest stays in library") {
            val game = scenario()
                .withPlayers()
                .withCardOnBattlefield(1, "Spinner of Souls")
                .withCardOnBattlefield(1, "Aegis Turtle")    // the nontoken creature that dies
                .withCardInHand(1, "Slay")
                .withCardInLibrary(1, "Plains")
                .withCardInLibrary(1, "Bear Cub")            // the creature to reveal
                .withCardInLibrary(1, "Plains")
                .build()

            val turtle = game.findPermanent("Aegis Turtle")!!
            game.castSpell(1, "Slay", targetId = turtle).error shouldBe null
            game.resolveStack()

            // The dies trigger pauses on the "may" prompt; accept it to run the reveal.
            (game.state.pendingDecision != null) shouldBe true
            game.answerYesNo(true)

            // The revealed creature is put into hand; the non-creature cards go to the bottom.
            game.isInHand(1, "Bear Cub") shouldBe true
            game.findCardsInLibrary(1, "Plains").size shouldBe 2
            game.findCardsInLibrary(1, "Bear Cub").size shouldBe 0
        }

        test("a creature an OPPONENT controls dies: Spinner does not trigger") {
            val game = scenario()
                .withPlayers()
                .withCardOnBattlefield(1, "Spinner of Souls")
                .withCardOnBattlefield(2, "Aegis Turtle")    // opponent's creature
                .withCardInHand(1, "Slay")
                .withCardInLibrary(1, "Bear Cub")
                .build()

            val turtle = game.findPermanent("Aegis Turtle")!!
            game.castSpell(1, "Slay", targetId = turtle).error shouldBe null
            game.resolveStack()

            // "creature you control" — an opponent's death doesn't reveal anything.
            game.isInHand(1, "Bear Cub") shouldBe false
            game.findCardsInLibrary(1, "Bear Cub").size shouldBe 1
        }
    }
}

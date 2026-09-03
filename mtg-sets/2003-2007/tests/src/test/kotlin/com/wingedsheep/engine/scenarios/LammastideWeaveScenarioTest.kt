package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.LammastideWeave
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe

/**
 * Lammastide Weave (LRW #226) — "Choose a card name, then target player mills a card. If a card
 * with the chosen name was milled this way, you gain life equal to its mana value. Draw a card."
 *
 * The gamble is the whole card, so the test runs it both ways off the *same* board: the same
 * milled card, once with the name guessed right and once with it guessed wrong. That pairing is
 * what separates a working name check from one that fails open (always pays) or fails closed
 * (never pays) — either mis-wiring is invisible against a single run.
 *
 * "Equal to its mana value" is read off the milled card, not off Lammastide Weave, so the milled
 * card deliberately has a mana value ({3}{G}{G} = 5) that no other number on the board could be
 * confused for.
 *
 * The draw is a separate sentence and happens either way, which is the third assertion in both
 * directions. It is asserted as "the hand is the size it was before casting": one card left for
 * the spell, one came back from the draw.
 */
class LammastideWeaveScenarioTest : FunSpec({

    /** The board, the caster, and the caster's hand size with the Weave still in it. */
    data class Run(val driver: GameTestDriver, val caster: EntityId, val handBeforeCast: Int)

    /** Cast the Weave at the opponent over a stacked library, naming [nameToGuess]. */
    fun runWeave(nameToGuess: String, milledCard: String = "Force of Nature"): Run {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + listOf(LammastideWeave))
        d.initMirrorMatch(deck = Deck.of("Forest" to 40), startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val me = d.activePlayer!!
        val opponent = d.getOpponent(me)
        d.putCardOnTopOfLibrary(opponent, milledCard)
        val weave = d.putCardInHand(me, "Lammastide Weave")
        d.giveMana(me, Color.GREEN, 2)
        val handBeforeCast = d.getHandSize(me)

        d.castSpell(me, weave, listOf(opponent)).isSuccess shouldBe true
        d.bothPass()

        val decision = d.pendingDecision
        withClue("resolving the Weave asks for a card name") {
            (decision is ChooseOptionDecision) shouldBe true
        }
        decision as ChooseOptionDecision
        val index = decision.options.indexOf(nameToGuess)
        withClue("\"$nameToGuess\" should be offered among the nameable cards") {
            (index >= 0) shouldBe true
        }
        d.submitDecision(me, OptionChosenResponse(decision.id, index))

        // Settle whatever is left of the resolution without over-passing into another step.
        var guard = 0
        while (guard++ < 8 && (d.state.stack.isNotEmpty() || d.pendingDecision != null)) {
            if (d.pendingDecision != null) d.autoResolveDecision() else d.bothPass()
        }
        return Run(d, me, handBeforeCast)
    }

    test("guessing the milled card's name gains life equal to its mana value") {
        val (d, me, handBeforeCast) = runWeave(nameToGuess = "Force of Nature")
        val opponent = d.getOpponent(me)

        withClue("the target player milled the card") {
            d.getGraveyardCardNames(opponent) shouldContain "Force of Nature"
        }
        withClue("{3}{G}{G} is mana value 5, and the life goes to the Weave's controller") {
            d.getLifeTotal(me) shouldBe 25
            d.getLifeTotal(opponent) shouldBe 20
        }
        withClue("the draw is a separate sentence — one card out, one card in") {
            d.getHandSize(me) shouldBe handBeforeCast
        }
    }

    test("guessing wrong mills and draws all the same, but gains nothing") {
        val (d, me, handBeforeCast) = runWeave(nameToGuess = "Centaur Courser")
        val opponent = d.getOpponent(me)

        withClue("the mill is unconditional") {
            d.getGraveyardCardNames(opponent) shouldContain "Force of Nature"
        }
        withClue("no card with the chosen name was milled, so no life") {
            d.getLifeTotal(me) shouldBe 20
        }
        withClue("and the draw still happens") {
            d.getHandSize(me) shouldBe handBeforeCast
        }
    }
})

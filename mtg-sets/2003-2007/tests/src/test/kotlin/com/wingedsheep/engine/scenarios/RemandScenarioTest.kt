package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.identity.CantBeCounteredComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.rav.cards.Remand
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Remand (RAV #63) — "Counter target spell. If that spell is countered this way, put it into its
 * owner's hand instead of into that player's graveyard. Draw a card."
 *
 * This pins the new `CounterDestination.Hand` where it differs from a bounce. Remand is a genuine
 * counter, so an **uncounterable** spell is neither countered nor returned — it resolves — while
 * the draw still happens (2021-03-19 ruling). `ReturnSpellToOwnersHandEffect` would have "countered"
 * it, which is exactly the mis-modelling this destination exists to avoid.
 *
 * The caster is the *non*-active player: a creature spell is sorcery-speed, so only the active
 * player can put one on the stack, and the responder is the one holding the instant.
 */
class RemandScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + Remand)
        d.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    /** Casts Remand from [caster] at [spellOnStack], then resolves the stack out. */
    fun GameTestDriver.remand(caster: EntityId, spellOnStack: EntityId) {
        // The caster of a spell keeps priority; hand it over before the responder can answer.
        if (priorityPlayer != caster) passPriority(getOpponent(caster))
        giveMana(caster, Color.BLUE, 2)
        val remandCard = putCardInHand(caster, "Remand")
        val cast = castSpellWithTargets(caster, remandCard, listOf(ChosenTarget.Spell(spellOnStack)))
        withClue(cast.error ?: "casting Remand failed") { cast.isSuccess shouldBe true }
        var guard = 0
        while (stackSize > 0 && guard++ < 20) bothPass()
    }

    test("the countered spell goes to its owner's hand, not their graveyard, and you draw") {
        val d = driver()
        val caster = d.player2   // holds Remand
        val victim = d.player1   // active player, casts the creature

        d.giveMana(victim, Color.GREEN, 3)
        val courser = d.putCardInHand(victim, "Centaur Courser")
        d.castSpell(victim, courser).isSuccess shouldBe true
        val handBefore = d.getHandSize(caster)

        d.remand(caster, courser)

        withClue("the spell landed in its owner's hand") {
            d.getHand(victim).contains(courser) shouldBe true
        }
        withClue("and not in their graveyard") {
            d.getGraveyardCardNames(victim).contains("Centaur Courser") shouldBe false
        }
        withClue("no permanent resolved") {
            d.findPermanent(victim, "Centaur Courser") shouldBe null
        }
        withClue("Remand's own draw happens (Remand itself left the hand to be cast)") {
            d.getHandSize(caster) shouldBe handBefore + 1
        }
    }

    test("an uncounterable spell is neither countered nor returned, but you still draw") {
        val d = driver()
        val caster = d.player2
        val victim = d.player1

        d.giveMana(victim, Color.GREEN, 3)
        val courser = d.putCardInHand(victim, "Centaur Courser")
        d.castSpell(victim, courser).isSuccess shouldBe true
        d.addComponent(courser, CantBeCounteredComponent)
        val handBefore = d.getHandSize(caster)

        d.remand(caster, courser)

        withClue("the spell resolved — it was not countered, so it was not returned either") {
            (d.findPermanent(victim, "Centaur Courser") != null) shouldBe true
        }
        withClue("it is not sitting in the owner's hand") {
            d.getHand(victim).contains(courser) shouldBe false
        }
        withClue("the draw clause is independent of the counter (2021-03-19 ruling)") {
            d.getHandSize(caster) shouldBe handBefore + 1
        }
    }
})

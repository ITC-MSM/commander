package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.WailOfTheForgotten
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

/**
 * Wail of the Forgotten — {U}{B} Sorcery
 *
 * "Descend 8 — Choose one. If there are eight or more permanent cards in your graveyard as you
 *  cast this spell, choose one or more instead.
 *  • Return target nonland permanent to its owner's hand.
 *  • Target opponent discards a card.
 *  • Look at the top three cards of your library. Put one of them into your hand and the rest
 *    into your graveyard."
 *
 * Pins the cast-time conditional modal count: the floor stays "choose one", and the cap rises to
 * all three modes only when there are eight or more permanent cards in the caster's graveyard as
 * the spell is cast (descend 8 — an ability word, CR 207.2c, with no rules entry of its own).
 *
 * Modes: 0 = return target nonland permanent to owner's hand,
 *        1 = target opponent discards a card,
 *        2 = look at top three, keep one, rest to graveyard.
 */
class WailOfTheForgottenScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all)
        d.registerCard(WailOfTheForgotten)
        return d
    }

    fun castSetup(d: GameTestDriver): Pair<EntityId, EntityId> {
        d.initMirrorMatch(deck = Deck.of("Swamp" to 20, "Island" to 20), startingLife = 20)
        val p1 = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        d.giveMana(p1, Color.BLUE, 1)
        d.giveMana(p1, Color.BLACK, 1)
        return p1 to d.getOpponent(p1)
    }

    /** Fill the caster's graveyard with [n] permanent cards so descend 8 is satisfied. */
    fun fillGraveyardWithPermanents(d: GameTestDriver, playerId: EntityId, n: Int) {
        repeat(n) { d.putCardInGraveyard(playerId, "Grizzly Bears") }
    }

    test("not descended 8 — choosing two modes is illegal (effective max is one)") {
        val d = driver()
        val (p1, p2) = castSetup(d)

        val creature = d.putCreatureOnBattlefield(p2, "Centaur Courser")

        val spell = d.putCardInHand(p1, "Wail of the Forgotten")
        val result = d.submit(CastSpell(
            playerId = p1,
            cardId = spell,
            targets = listOf(ChosenTarget.Permanent(creature), ChosenTarget.Player(p2)),
            chosenModes = listOf(0, 1),
            modeTargetsOrdered = listOf(
                listOf(ChosenTarget.Permanent(creature)),
                listOf(ChosenTarget.Player(p2))
            )
        ))

        result.isSuccess shouldBe false
    }

    test("not descended 8 — mode 0 returns target nonland permanent to its owner's hand") {
        val d = driver()
        val (p1, p2) = castSetup(d)

        val creature = d.putCreatureOnBattlefield(p2, "Centaur Courser")

        val spell = d.putCardInHand(p1, "Wail of the Forgotten")
        val result = d.submit(CastSpell(
            playerId = p1,
            cardId = spell,
            targets = listOf(ChosenTarget.Permanent(creature)),
            chosenModes = listOf(0),
            modeTargetsOrdered = listOf(listOf(ChosenTarget.Permanent(creature)))
        ))
        if (!result.isSuccess) throw AssertionError("cast failed: ${result.error}")

        d.bothPass()
        d.findPermanent(p2, "Centaur Courser").shouldBeNull()
        d.findCardsInHand(p2, "Centaur Courser").size shouldBe 1
    }

    test("not descended 8 — mode 1 makes the target opponent discard a card") {
        val d = driver()
        val (p1, p2) = castSetup(d)

        val doomed = d.putCardInHand(p2, "Grizzly Bears")

        val spell = d.putCardInHand(p1, "Wail of the Forgotten")
        val result = d.submit(CastSpell(
            playerId = p1,
            cardId = spell,
            targets = listOf(ChosenTarget.Player(p2)),
            chosenModes = listOf(1),
            modeTargetsOrdered = listOf(listOf(ChosenTarget.Player(p2)))
        ))
        if (!result.isSuccess) throw AssertionError("cast failed: ${result.error}")

        d.bothPass()
        // The opponent chooses which card to discard.
        d.submitCardSelection(p2, listOf(doomed))
        d.findCardsInHand(p2, "Grizzly Bears").shouldBe(emptyList())
        d.getGraveyardCardNames(p2) shouldContain "Grizzly Bears"
    }

    test("not descended 8 — mode 2 keeps one of the top three and mills the rest") {
        val d = driver()
        val (p1, _) = castSetup(d)

        // Seed the top three cards (last pushed ends up on top).
        d.putCardOnTopOfLibrary(p1, "Centaur Courser")
        d.putCardOnTopOfLibrary(p1, "Savannah Lions")
        val keep = d.putCardOnTopOfLibrary(p1, "Grizzly Bears")

        val spell = d.putCardInHand(p1, "Wail of the Forgotten")
        val result = d.submit(CastSpell(
            playerId = p1,
            cardId = spell,
            targets = emptyList(),
            chosenModes = listOf(2),
            modeTargetsOrdered = listOf(emptyList())
        ))
        if (!result.isSuccess) throw AssertionError("cast failed: ${result.error}")

        d.bothPass()
        // Keep Grizzly Bears; the other two go to the graveyard.
        d.submitCardSelection(p1, listOf(keep))
        d.findCardsInHand(p1, "Grizzly Bears").size shouldBe 1
        d.getGraveyardCardNames(p1) shouldContain "Centaur Courser"
        d.getGraveyardCardNames(p1) shouldContain "Savannah Lions"
    }

    test("descended 8 — may choose two modes: bounce a permanent AND make the opponent discard") {
        val d = driver()
        val (p1, p2) = castSetup(d)

        fillGraveyardWithPermanents(d, p1, 8)

        val creature = d.putCreatureOnBattlefield(p2, "Centaur Courser")
        val doomed = d.putCardInHand(p2, "Savannah Lions")

        val spell = d.putCardInHand(p1, "Wail of the Forgotten")
        val result = d.submit(CastSpell(
            playerId = p1,
            cardId = spell,
            targets = listOf(ChosenTarget.Permanent(creature), ChosenTarget.Player(p2)),
            chosenModes = listOf(0, 1),
            modeTargetsOrdered = listOf(
                listOf(ChosenTarget.Permanent(creature)),
                listOf(ChosenTarget.Player(p2))
            )
        ))
        if (!result.isSuccess) throw AssertionError("cast failed: ${result.error}")

        d.bothPass()
        d.submitCardSelection(p2, listOf(doomed))

        d.findPermanent(p2, "Centaur Courser").shouldBeNull()
        d.findCardsInHand(p2, "Centaur Courser").size shouldBe 1
        d.getGraveyardCardNames(p2) shouldContain "Savannah Lions"
    }

    test("descended 8 — may still choose only one mode") {
        val d = driver()
        val (p1, p2) = castSetup(d)

        fillGraveyardWithPermanents(d, p1, 8)

        val creature = d.putCreatureOnBattlefield(p2, "Centaur Courser")

        val spell = d.putCardInHand(p1, "Wail of the Forgotten")
        val result = d.submit(CastSpell(
            playerId = p1,
            cardId = spell,
            targets = listOf(ChosenTarget.Permanent(creature)),
            chosenModes = listOf(0),
            modeTargetsOrdered = listOf(listOf(ChosenTarget.Permanent(creature)))
        ))
        if (!result.isSuccess) throw AssertionError("cast failed: ${result.error}")

        d.bothPass()
        d.findPermanent(p2, "Centaur Courser").shouldBeNull()
        d.findCardsInHand(p2, "Centaur Courser").size shouldBe 1
    }
})

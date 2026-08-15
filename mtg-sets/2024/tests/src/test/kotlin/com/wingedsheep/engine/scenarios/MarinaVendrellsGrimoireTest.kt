package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dsk.cards.MarinaVendrellsGrimoire
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Marina Vendrell's Grimoire (DSK 64).
 *
 *  - When it enters, if you cast it, draw five cards.
 *  - You have no maximum hand size and don't lose the game for having 0 or less life.
 *  - Whenever you gain life, draw that many cards.
 *  - Whenever you lose life, discard that many cards. Then if you have no cards in hand, you lose.
 */
class MarinaVendrellsGrimoireTest : FunSpec({

    val gainThree = card("Test Lifegain") {
        manaCost = "{1}"
        typeLine = "Sorcery"
        oracleText = "You gain 3 life."
        spell { effect = Effects.GainLife(3) }
    }

    val loseThree = card("Test Lifeloss") {
        manaCost = "{1}"
        typeLine = "Sorcery"
        oracleText = "You lose 3 life."
        spell { effect = Effects.LoseLife(3, EffectTarget.Controller) }
    }

    fun driver(startingLife: Int = 20): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + listOf(MarinaVendrellsGrimoire, gainThree, loseThree))
        d.initMirrorMatch(
            deck = Deck.of("Island" to 30, "Grizzly Bears" to 10),
            skipMulligans = true,
            startingLife = startingLife,
        )
        return d
    }

    // Resolve the stack, auto-picking the required cards for any forced discard along the way.
    fun GameTestDriver.settle(player: EntityId) {
        var guard = 0
        while (!state.gameOver && guard++ < 40) {
            val pd = pendingDecision
            when {
                pd is SelectCardsDecision && pd.playerId == player -> {
                    val pick = getHand(player).take(pd.minSelections)
                    submitCardSelection(player, pick)
                }
                state.stack.isNotEmpty() || state.priorityPlayerId != null -> bothPass()
                else -> break
            }
        }
    }

    test("cast: ETB draws five cards (the 'if you cast it' clause)") {
        val d = driver()
        val p1 = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val grimoire = d.putCardInHand(p1, MarinaVendrellsGrimoire.name)
        d.giveMana(p1, Color.BLUE, 1)
        d.giveColorlessMana(p1, 5)
        val before = d.getHandSize(p1)
        d.castSpell(p1, grimoire).isSuccess shouldBe true
        var guard = 0
        while (d.state.stack.isNotEmpty() && guard++ < 20) d.bothPass()

        // -1 (Grimoire leaves hand) + 5 (ETB draw) = +4.
        (d.getHandSize(p1) - before) shouldBe 4
        d.state.getBattlefield().any { d.getCardName(it) == MarinaVendrellsGrimoire.name } shouldBe true
    }

    test("put onto battlefield without casting does NOT draw five") {
        val d = driver()
        val p1 = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val before = d.getHandSize(p1)
        d.putPermanentOnBattlefield(p1, MarinaVendrellsGrimoire.name)
        var guard = 0
        while (d.state.stack.isNotEmpty() && guard++ < 20) d.bothPass()

        // No "if you cast it" → no draw.
        d.getHandSize(p1) shouldBe before
    }

    test("whenever you gain life, draw that many cards") {
        val d = driver()
        val p1 = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        d.putPermanentOnBattlefield(p1, MarinaVendrellsGrimoire.name)

        val spell = d.putCardInHand(p1, "Test Lifegain")
        d.giveColorlessMana(p1, 1)
        val before = d.getHandSize(p1)
        d.castSpell(p1, spell).isSuccess shouldBe true
        d.settle(p1)

        // -1 (cast) + 3 (gained 3 life → draw 3) = +2, and life went up by 3.
        (d.getHandSize(p1) - before) shouldBe 2
        d.getLifeTotal(p1) shouldBe 23
    }

    test("whenever you lose life, discard that many cards") {
        val d = driver()
        val p1 = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        d.putPermanentOnBattlefield(p1, MarinaVendrellsGrimoire.name)

        val spell = d.putCardInHand(p1, "Test Lifeloss")
        d.giveColorlessMana(p1, 1)
        val before = d.getHandSize(p1)
        d.castSpell(p1, spell).isSuccess shouldBe true
        d.settle(p1)

        // -1 (cast) - 3 (discard 3 for losing 3 life) = -4; hand still non-empty so no loss.
        (d.getHandSize(p1) - before) shouldBe -4
        d.state.gameOver.shouldBeFalse()
    }

    test("don't lose the game for having 0 or less life (704.5a suppressed)") {
        val d = driver(startingLife = 3)
        val p1 = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        d.putPermanentOnBattlefield(p1, MarinaVendrellsGrimoire.name)

        // Lose exactly to 0. The lose-life trigger discards 3 cards, but the opening hand is large
        // enough that it isn't emptied, so the only thing that could kill p1 is the 0-life SBA.
        val spell = d.putCardInHand(p1, "Test Lifeloss")
        d.giveColorlessMana(p1, 1)
        d.castSpell(p1, spell).isSuccess shouldBe true
        d.settle(p1)

        d.getLifeTotal(p1) shouldBe 0
        d.state.gameOver.shouldBeFalse()
    }

    test("losing life that empties your hand makes you lose the game") {
        val d = driver()
        val p1 = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        d.putPermanentOnBattlefield(p1, MarinaVendrellsGrimoire.name)

        // Reduce the hand to only the lifeloss spell, so after casting it the hand is empty; the
        // life loss then discards nothing and the "no cards in hand → you lose the game" clause fires.
        val spell = d.putCardInHand(p1, "Test Lifeloss")
        var st = d.state
        for (cardId in d.getHand(p1)) {
            if (cardId != spell) {
                st = st.removeFromZone(ZoneKey(p1, Zone.HAND), cardId)
                st = st.withoutEntity(cardId)
            }
        }
        val stateField = d::class.java.getDeclaredField("_state")
        stateField.isAccessible = true
        stateField.set(d, st)

        d.giveColorlessMana(p1, 1)
        d.castSpell(p1, spell).isSuccess shouldBe true
        d.settle(p1)

        d.state.gameOver.shouldBeTrue()
    }
})

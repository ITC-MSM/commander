package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.sos.cards.StealTheShow
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Steal the Show {2}{R} Sorcery (SOS canonical).
 *
 * Choose one or both —
 * • Target player discards any number of cards, then draws that many cards.
 * • Steal the Show deals damage equal to the number of instant and sorcery cards
 *   in your graveyard to target creature or planeswalker.
 */
class StealTheShowScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(StealTheShow))
        return driver
    }

    fun payManaFor(driver: GameTestDriver, player: com.wingedsheep.sdk.model.EntityId) {
        driver.giveMana(player, Color.RED, 3) // {R} + {2} generic
    }

    test("mode 0 — target player discards two and draws two (net hand unchanged)") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val me = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Two extra discardable cards in hand.
        val extra1 = driver.putCardInHand(me, "Grizzly Bears")
        val extra2 = driver.putCardInHand(me, "Grizzly Bears")

        payManaFor(driver, me)
        val spell = driver.putCardInHand(me, "Steal the Show")
        val handWithSpell = driver.getHandSize(me)

        driver.submit(
            CastSpell(
                playerId = me,
                cardId = spell,
                targets = listOf(ChosenTarget.Player(me)),
                chosenModes = listOf(0),
                modeTargetsOrdered = listOf(listOf(ChosenTarget.Player(me)))
            )
        ).error shouldBe null

        // Pass priority so the spell resolves; resolution pauses for the discard selection.
        driver.bothPass()
        driver.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
        driver.submitCardSelection(me, listOf(extra1, extra2))
        while (driver.state.stack.isNotEmpty()) driver.bothPass()

        // Cast -1, discard -2, draw +2 → handWithSpell - 1.
        driver.getHandSize(me) shouldBe (handWithSpell - 1)
    }

    test("mode 1 — deals damage equal to instants/sorceries in graveyard") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Three instant/sorcery cards in my graveyard → 3 damage.
        driver.putCardInGraveyard(me, "Lightning Bolt")
        driver.putCardInGraveyard(me, "Counterspell")
        driver.putCardInGraveyard(me, "Doom Blade")
        // A creature card in the graveyard must NOT count.
        driver.putCardInGraveyard(me, "Grizzly Bears")

        // 3/3 dies to 3 damage; 5/5 survives.
        driver.putCreatureOnBattlefield(opp, "Centaur Courser") // 3/3

        payManaFor(driver, me)
        val spell = driver.putCardInHand(me, "Steal the Show")
        val courser = driver.findPermanent(opp, "Centaur Courser")!!

        driver.submit(
            CastSpell(
                playerId = me,
                cardId = spell,
                targets = listOf(ChosenTarget.Permanent(courser)),
                chosenModes = listOf(1),
                modeTargetsOrdered = listOf(listOf(ChosenTarget.Permanent(courser)))
            )
        ).isSuccess shouldBe true
        driver.bothPass()

        // 3 damage kills the 3/3.
        driver.findPermanent(opp, "Centaur Courser") shouldBe null
    }

    test("both modes resolve when both are chosen") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCardInGraveyard(me, "Lightning Bolt")
        driver.putCardInGraveyard(me, "Counterspell")
        val extra1 = driver.putCardInHand(me, "Grizzly Bears")

        // 1/1 (Savannah Lions) survives 2 damage? No — 2 >= 1 toughness, it dies.
        driver.putCreatureOnBattlefield(opp, "Savannah Lions") // 1/1

        payManaFor(driver, me)
        val spell = driver.putCardInHand(me, "Steal the Show")
        val handWithSpell = driver.getHandSize(me)
        val lion = driver.findPermanent(opp, "Savannah Lions")!!

        driver.submit(
            CastSpell(
                playerId = me,
                cardId = spell,
                targets = listOf(ChosenTarget.Player(me), ChosenTarget.Permanent(lion)),
                chosenModes = listOf(0, 1),
                modeTargetsOrdered = listOf(
                    listOf(ChosenTarget.Player(me)),
                    listOf(ChosenTarget.Permanent(lion))
                )
            )
        ).error shouldBe null

        // Pass priority so the spell resolves; mode 0 pauses for the discard selection.
        driver.bothPass()
        driver.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
        driver.submitCardSelection(me, listOf(extra1))
        while (driver.state.stack.isNotEmpty()) driver.bothPass()

        // Discard 1 + draw 1 net zero from the loot; cast -1 → handWithSpell - 1.
        driver.getHandSize(me) shouldBe (handWithSpell - 1)
        // 2 instants/sorceries in graveyard → 2 damage kills the 1/1.
        driver.findPermanent(opp, "Savannah Lions") shouldBe null
    }
})

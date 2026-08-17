package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Proft's Eidetic Memory (MKM #67) — {1}{U} legendary enchantment.
 *
 * "At the beginning of combat on your turn, if you've drawn more than one card this turn, put X
 *  +1/+1 counters on target creature you control, where X is the number of cards you've drawn this
 *  turn minus one."
 *
 * The gate and the amount read the same tracker but are **not** the same number, which is where a
 * wrong implementation lands: X is the count *minus one*, so two draws is one counter, not two. And
 * "more than one" is a real threshold — at one draw the ability must not trigger at all (the first
 * printed ruling), which an `interveningIf` of `YouDrewCardsThisTurn(1)` would silently pass.
 *
 * Draws are made with a free test instant rather than `putCardInHand`, because only a genuine draw
 * increments `CardsDrawnThisTurnComponent`. Note the enchantment's own enters trigger draws one, so
 * the turn it lands it has already banked a draw toward next turn's threshold.
 */
class ProftsEideticMemoryScenarioTest : FunSpec({

    val drawOne = card("Proft Draw One Test") {
        manaCost = "{0}"
        typeLine = "Instant"
        oracleText = "Draw a card."
        spell { effect = Effects.DrawCards(1) }
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + drawOne)
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.drawWithSpell(player: EntityId) {
        val card = putCardInHand(player, "Proft Draw One Test")
        castSpell(player, card).error shouldBe null
        bothPass()
    }

    fun GameTestDriver.plusOneCounters(id: EntityId): Int =
        state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    test("one draw is not 'more than one' — the trigger never goes on the stack") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        val bears = driver.putCreatureOnBattlefield(player, "Grizzly Bears")
        driver.putPermanentOnBattlefield(player, "Proft's Eidetic Memory")

        driver.drawWithSpell(player)
        driver.passPriorityUntil(Step.BEGIN_COMBAT)

        withClue("CR 603.4: the intervening-if failed, so nothing was put on the stack") {
            driver.stackSize shouldBe 0
            driver.plusOneCounters(bears) shouldBe 0
        }
    }

    test("three draws put two counters on the target — X is the count minus one") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        val bears = driver.putCreatureOnBattlefield(player, "Grizzly Bears")
        driver.putPermanentOnBattlefield(player, "Proft's Eidetic Memory")

        repeat(3) { driver.drawWithSpell(player) }
        driver.passPriorityUntil(Step.BEGIN_COMBAT)
        driver.submitTargetSelection(player, listOf(bears)).error shouldBe null
        driver.bothPass()

        withClue("three drawn cards, minus one, is two counters — not three") {
            driver.plusOneCounters(bears) shouldBe 2
            driver.state.projectedState.getPower(bears) shouldBe 4
            driver.state.projectedState.getToughness(bears) shouldBe 4
        }
    }

    test("the enters trigger draws a card, so it replaces itself") {
        val driver = createDriver()
        val player = driver.activePlayer!!

        val card = driver.putCardInHand(player, "Proft's Eidetic Memory")
        val handHolding = driver.getHandSize(player)
        driver.giveMana(player, Color.BLUE, 1)
        driver.giveColorlessMana(player, 1)
        driver.submit(
            CastSpell(playerId = player, cardId = card, paymentStrategy = PaymentStrategy.FromPool)
        ).error shouldBe null
        driver.bothPass()
        driver.bothPass()

        withClue("it resolved onto the battlefield") {
            driver.findPermanent(player, "Proft's Eidetic Memory").shouldNotBeNull()
        }
        withClue("one card left the hand to cast it, one came back from the draw") {
            driver.getHandSize(player) shouldBe handHolding
        }
    }
})

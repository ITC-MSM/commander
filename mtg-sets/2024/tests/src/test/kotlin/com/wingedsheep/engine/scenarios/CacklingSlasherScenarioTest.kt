package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.player.CreaturesDiedThisTurnComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Cackling Slasher — {3}{B} Creature — Human Assassin, 3/3, Deathtouch
 *   "This creature enters with a +1/+1 counter on it if a creature died this turn."
 *
 * The conditional enters-with-counter is an `EntersWithCounters(condition = CreatureDiedThisTurn)`
 * replacement effect. `CreatureDiedThisTurn` is global (any player's creature). The death tracker
 * (`CreaturesDiedThisTurnComponent`) is per-player and credited to the dying creature's controller,
 * so the gate is satisfied if any player's count is positive.
 */
class CacklingSlasherScenarioTest : FunSpec({

    fun driver(): GameTestDriver = GameTestDriver().apply {
        registerCards(TestCards.all)
        initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
    }

    fun GameTestDriver.plusOneCounters(id: EntityId): Int =
        state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    test("enters with a +1/+1 counter when a creature died this turn") {
        val d = driver()
        val you = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // A creature died under your control this turn — gate satisfied.
        d.addComponent(you, CreaturesDiedThisTurnComponent(1))

        val card = d.putCardInHand(you, "Cackling Slasher")
        d.giveMana(you, Color.BLACK, 4) // {3}{B}
        d.castSpell(you, card)
        d.bothPass() // resolve onto the battlefield

        val slasher = d.findPermanent(you, "Cackling Slasher")!!
        d.plusOneCounters(slasher) shouldBe 1
    }

    test("enters with NO counter when no creature died this turn") {
        val d = driver()
        val you = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val card = d.putCardInHand(you, "Cackling Slasher")
        d.giveMana(you, Color.BLACK, 4)
        d.castSpell(you, card)
        d.bothPass()

        val slasher = d.findPermanent(you, "Cackling Slasher")!!
        d.plusOneCounters(slasher) shouldBe 0
    }

    test("an opponent's creature dying also satisfies the global condition") {
        val d = driver()
        val you = d.activePlayer!!
        val opponent = d.getOpponent(you)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Only the opponent's creature died — CreatureDiedThisTurn is global, so the gate still holds.
        d.addComponent(opponent, CreaturesDiedThisTurnComponent(1))

        val card = d.putCardInHand(you, "Cackling Slasher")
        d.giveMana(you, Color.BLACK, 4)
        d.castSpell(you, card)
        d.bothPass()

        val slasher = d.findPermanent(you, "Cackling Slasher")!!
        d.plusOneCounters(slasher) shouldBe 1
    }
})

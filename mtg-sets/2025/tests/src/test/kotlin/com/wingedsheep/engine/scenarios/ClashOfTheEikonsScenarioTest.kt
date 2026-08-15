package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.ClashOfTheEikons
import com.wingedsheep.mtg.sets.definitions.fin.cards.SummonFatChocobo
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Clash of the Eikons — {G} Sorcery (FIN).
 *
 * Choose one or more —
 * • Target creature you control fights target creature an opponent controls.
 * • Remove a lore counter from target Saga you control.
 * • Put a lore counter on target Saga you control.
 */
class ClashOfTheEikonsScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(ClashOfTheEikons, SummonFatChocobo))
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.lore(saga: EntityId): Int =
        state.getEntity(saga)?.get<CountersComponent>()?.getCount(CounterType.LORE) ?: 0

    test("fight mode — my creature fights and kills the opponent's") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)

        // 3/3 fights 1/1: the Lions takes 3 (dies); the Courser takes 1 (survives).
        val courser = driver.putCreatureOnBattlefield(me, "Centaur Courser") // 3/3
        val lions = driver.putCreatureOnBattlefield(opp, "Savannah Lions")    // 1/1

        driver.giveMana(me, Color.GREEN, 1)
        val spell = driver.putCardInHand(me, "Clash of the Eikons")

        driver.submit(
            CastSpell(
                playerId = me,
                cardId = spell,
                targets = listOf(ChosenTarget.Permanent(courser), ChosenTarget.Permanent(lions)),
                chosenModes = listOf(0),
                modeTargetsOrdered = listOf(
                    listOf(ChosenTarget.Permanent(courser), ChosenTarget.Permanent(lions))
                )
            )
        ).error shouldBe null
        driver.bothPass()

        driver.findPermanent(opp, "Savannah Lions") shouldBe null
        driver.findPermanent(me, "Centaur Courser") shouldBe courser
    }

    test("add-lore mode — puts a lore counter on a Saga I control") {
        val driver = createDriver()
        val me = driver.activePlayer!!

        val saga = driver.putPermanentOnBattlefield(me, "Summon: Fat Chocobo")
        val loreBefore = driver.lore(saga)

        driver.giveMana(me, Color.GREEN, 1)
        val spell = driver.putCardInHand(me, "Clash of the Eikons")

        driver.submit(
            CastSpell(
                playerId = me,
                cardId = spell,
                targets = listOf(ChosenTarget.Permanent(saga)),
                chosenModes = listOf(2),
                modeTargetsOrdered = listOf(listOf(ChosenTarget.Permanent(saga)))
            )
        ).error shouldBe null
        // Resolve the spell and any chapter trigger that the added lore counter sets off.
        var guard = 0
        while ((driver.state.stack.isNotEmpty() || driver.state.pendingDecision != null) && guard++ < 50) {
            val pd = driver.state.pendingDecision
            if (pd != null) driver.autoResolveDecision() else driver.bothPass()
        }

        // A lore counter was added (chapter I fired; the Saga is still around at lore 1 of IV).
        driver.lore(driver.findPermanent(me, "Summon: Fat Chocobo")!!) shouldBe (loreBefore + 1)
    }
})

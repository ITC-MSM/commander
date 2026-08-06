package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.msh.cards.TheAstonishingAntMan
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * The Astonishing Ant-Man (MSH) — "{2}{G}, {T}, Remove any number of +1/+1 counters from The
 * Astonishing Ant-Man: Create that many 1/1 green Insect creature tokens."
 *
 * X here is a *counter* count, not mana. Playtesting could only ever choose X = 0: the cap is
 * computed by matching battlefield permanents against the cost's filter, which is `sourceItself()`
 * (StatePredicate.IsSource) — and the predicate context was built without a sourceId, so nothing
 * matched and the cap came out 0.
 */
class TheAstonishingAntManScenarioTest : FunSpec({

    fun driver() = GameTestDriver().apply {
        registerCards(TestCards.all)
        registerCard(TheAstonishingAntMan)
        initMirrorMatch(Deck.of("Forest" to 40), skipMulligans = true, startingPlayer = 0)
        passPriorityUntil(Step.PRECOMBAT_MAIN)
    }

    fun GameTestDriver.addCounters(id: EntityId, type: CounterType, count: Int) {
        replaceState(state.updateEntity(id) { container ->
            val existing = container.get<CountersComponent>() ?: CountersComponent()
            container.with(existing.withAdded(type, count))
        })
    }

    test("X is capped by the counters on it, not by leftover mana") {
        val d = driver()
        val antMan = d.putCreatureOnBattlefield(d.player1, "The Astonishing Ant-Man")
        d.removeSummoningSickness(antMan) // the ability has a {T} cost
        d.addCounters(antMan, CounterType.PLUS_ONE_PLUS_ONE, 3)
        d.giveMana(d.player1, Color.GREEN, 3)

        val activation = d.legalActions(d.player1)
            .filter { it.actionType == "ActivateAbility" }
            .firstOrNull { it.maxAffordableX != null }

        withClue("no X-cost activation was offered at all") { (activation != null) shouldBe true }
        withClue("counters=3, so X must be choosable up to 3") {
            activation!!.maxAffordableX shouldBe 3
        }
    }
})

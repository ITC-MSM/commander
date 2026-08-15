package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.TendrilOfTheMycotyrant
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for Tendril of the Mycotyrant (LCI #215) — {1}{G} 2/2 Creature — Fungus Wizard.
 *
 * "{5}{G}{G}: Put seven +1/+1 counters on target noncreature land you control. It becomes a
 *  0/0 Fungus creature with haste. It's still a land."
 *
 * Covered:
 *  1. Activating the ability on a noncreature land you control puts seven +1/+1 counters on it
 *     and permanently animates it into a 0/0 Fungus creature — a 7/7 while the counters remain —
 *     that is still a land.
 */
class TendrilOfTheMycotyrantScenarioTest : FunSpec({

    val abilityId = TendrilOfTheMycotyrant.activatedAbilities.first().id

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(TendrilOfTheMycotyrant)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun plusOneCounters(driver: GameTestDriver, entityId: EntityId): Int =
        driver.state.getEntity(entityId)
            ?.get<CountersComponent>()
            ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    test("animating a noncreature land adds seven +1/+1 counters and makes it a 0/0 Fungus creature that is still a land") {
        val driver = newDriver()
        val me = driver.player1

        // The Tendril on the battlefield provides the activated ability.
        val tendril = driver.putPermanentOnBattlefield(me, "Tendril of the Mycotyrant")

        // A noncreature land you control to target.
        val land = driver.putLandOnBattlefield(me, "Forest")

        // {5}{G}{G}: 5 generic + 2 green.
        driver.giveColorlessMana(me, 5)
        driver.giveMana(me, Color.GREEN, 2)

        val result = driver.submit(
            ActivateAbility(
                playerId = me,
                sourceId = tendril,
                abilityId = abilityId,
                targets = listOf(ChosenTarget.Permanent(land))
            )
        )
        result.isSuccess shouldBe true

        driver.bothPass()

        // Seven +1/+1 counters landed on the target land.
        plusOneCounters(driver, land) shouldBe 7

        // The land is now a creature (permanent BecomeCreature) and still on the battlefield.
        driver.findPermanent(me, "Forest") shouldNotBe null
        driver.state.projectedState.isCreature(land) shouldBe true

        // It's still a land.
        driver.state.projectedState.hasType(land, "LAND") shouldBe true

        // Base 0/0 plus seven +1/+1 counters => a 7/7 body.
        driver.state.projectedState.getPower(land) shouldBe 7
        driver.state.projectedState.getToughness(land) shouldBe 7
    }
})

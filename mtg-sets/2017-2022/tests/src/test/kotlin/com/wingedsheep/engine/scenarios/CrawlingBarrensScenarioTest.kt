package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.znr.cards.CrawlingBarrens
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Crawling Barrens (ZNR #262).
 *
 * Land
 *  {T}: Add {C}.
 *  {4}: Put two +1/+1 counters on this land. Then you may have it become a 0/0 Elemental
 *       creature until end of turn. It's still a land.
 *
 * Pins the ordering that keeps the animate half safe: the counters land first, so accepting the
 * "may" never produces a 0/0 that dies to state-based actions. Declining still leaves the counters
 * on a noncreature land, and repeat activations stack.
 */
class CrawlingBarrensScenarioTest : FunSpec({

    val animateAbilityId = CrawlingBarrens.activatedAbilities[1].id // {4}: counters + may animate
    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    /** Activate the {4} ability and answer its "may become a creature" prompt with [accept]. */
    fun activate(driver: GameTestDriver, player: EntityId, barrens: EntityId, accept: Boolean) {
        driver.giveColorlessMana(player, 4)
        val activation = driver.submit(
            ActivateAbility(playerId = player, sourceId = barrens, abilityId = animateAbilityId)
        )
        withClue("Activating {4} should be accepted: ${activation.error}") {
            activation.error shouldBe null
        }
        driver.bothPass()
        withClue("resolution should pause on the optional animate") {
            (driver.pendingDecision is YesNoDecision) shouldBe true
        }
        driver.submitYesNo(player, accept)
    }

    fun plusOneCounters(driver: GameTestDriver, id: EntityId): Int =
        driver.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    test("accepting the may turns it into a 2/2 Elemental that is still a land") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        val barrens = driver.putLandOnBattlefield(player, "Crawling Barrens")

        activate(driver, player, barrens, accept = true)

        withClue("two +1/+1 counters were placed") { plusOneCounters(driver, barrens) shouldBe 2 }

        val projected = projector.project(driver.state)
        projected.hasType(barrens, "LAND") shouldBe true   // it's still a land
        projected.hasType(barrens, "CREATURE") shouldBe true
        projected.hasSubtype(barrens, "Elemental") shouldBe true
        withClue("0/0 base plus two +1/+1 counters = 2/2") {
            projected.getPower(barrens) shouldBe 2
            projected.getToughness(barrens) shouldBe 2
        }
        withClue("a 2/2 does not die to state-based actions") {
            driver.state.getBattlefield().contains(barrens) shouldBe true
        }
    }

    test("declining the may leaves a noncreature land that still keeps the two counters") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        val barrens = driver.putLandOnBattlefield(player, "Crawling Barrens")

        activate(driver, player, barrens, accept = false)

        withClue("the counters are placed even when the animate is declined") {
            plusOneCounters(driver, barrens) shouldBe 2
        }
        val projected = projector.project(driver.state)
        projected.hasType(barrens, "LAND") shouldBe true
        projected.hasType(barrens, "CREATURE") shouldBe false
        projected.hasSubtype(barrens, "Elemental") shouldBe false
        driver.state.getBattlefield().contains(barrens) shouldBe true
    }

    test("activating twice then animating gives a 4/4") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        val barrens = driver.putLandOnBattlefield(player, "Crawling Barrens")

        activate(driver, player, barrens, accept = false)
        activate(driver, player, barrens, accept = true)

        withClue("two activations = four +1/+1 counters") {
            plusOneCounters(driver, barrens) shouldBe 4
        }
        val projected = projector.project(driver.state)
        projected.hasType(barrens, "CREATURE") shouldBe true
        projected.getPower(barrens) shouldBe 4
        projected.getToughness(barrens) shouldBe 4
    }
})

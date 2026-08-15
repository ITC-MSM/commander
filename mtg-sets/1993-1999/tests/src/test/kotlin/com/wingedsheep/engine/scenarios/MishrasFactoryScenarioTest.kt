package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.atq.cards.MishrasFactory
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Mishra's Factory (ATQ #80).
 *
 * Land —
 *  {T}: Add {C}.
 *  {1}: This land becomes a 2/2 Assembly-Worker artifact creature until end of turn. It's still a land.
 *  {T}: Target Assembly-Worker creature gets +1/+1 until end of turn.
 *
 * Exercises the animate-also-grants-artifact gap: `BecomeCreatureEffect.addTypes` now lets the
 * land become a 2/2 that is BOTH a land and an artifact creature of subtype Assembly-Worker.
 */
class MishrasFactoryScenarioTest : FunSpec({

    val animateAbilityId = MishrasFactory.activatedAbilities[1].id // {1}: become 2/2
    val pumpAbilityId = MishrasFactory.activatedAbilities[2].id     // {T}: +1/+1 to Assembly-Worker
    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        return driver
    }

    test("animates into a 2/2 that is a land AND an artifact creature with subtype Assembly-Worker") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val factory = driver.putLandOnBattlefield(player, "Mishra's Factory")
        driver.giveColorlessMana(player, 1)

        driver.submit(
            ActivateAbility(playerId = player, sourceId = factory, abilityId = animateAbilityId)
        ).isSuccess shouldBe true
        driver.bothPass()

        val projected = projector.project(driver.state)
        projected.hasType(factory, "CREATURE") shouldBe true
        projected.hasType(factory, "ARTIFACT") shouldBe true
        projected.hasType(factory, "LAND") shouldBe true // it's still a land
        projected.hasSubtype(factory, "Assembly-Worker") shouldBe true
        projected.getPower(factory) shouldBe 2
        projected.getToughness(factory) shouldBe 2
    }

    test("animated factory reverts to a plain land at the next turn") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val factory = driver.putLandOnBattlefield(player, "Mishra's Factory")
        driver.giveColorlessMana(player, 1)
        driver.submit(
            ActivateAbility(playerId = player, sourceId = factory, abilityId = animateAbilityId)
        )
        driver.bothPass()
        projector.project(driver.state).hasType(factory, "CREATURE") shouldBe true

        driver.passPriorityUntil(Step.UPKEEP)
        val next = projector.project(driver.state)
        next.hasType(factory, "CREATURE") shouldBe false
        next.hasType(factory, "ARTIFACT") shouldBe false
        next.hasType(factory, "LAND") shouldBe true
    }

    test("the +1/+1 pump can target an Assembly-Worker creature") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Factory A is animated into an Assembly-Worker; Factory B pumps it via {T}.
        val factoryA = driver.putLandOnBattlefield(player, "Mishra's Factory")
        val factoryB = driver.putLandOnBattlefield(player, "Mishra's Factory")
        driver.giveColorlessMana(player, 1)

        driver.submit(
            ActivateAbility(playerId = player, sourceId = factoryA, abilityId = animateAbilityId)
        ).isSuccess shouldBe true
        driver.bothPass()

        // Factory B taps to pump the Assembly-Worker (factory A).
        driver.submit(
            ActivateAbility(
                playerId = player,
                sourceId = factoryB,
                abilityId = pumpAbilityId,
                targets = listOf(ChosenTarget.Permanent(factoryA))
            )
        ).isSuccess shouldBe true
        driver.bothPass()

        val projected = projector.project(driver.state)
        projected.getPower(factoryA) shouldBe 3  // 2 + 1
        projected.getToughness(factoryA) shouldBe 3 // 2 + 1
    }
})

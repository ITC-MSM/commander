package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.ChooseColorDecision
import com.wingedsheep.engine.core.ColorChosenResponse
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dsk.cards.TwitchingDoll
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Twitching Doll (DSK #201) — {1}{G} Artifact Creature — Spider Toy 2/2.
 *
 * "{T}: Add one mana of any color. Put a nest counter on this creature.
 *  {T}, Sacrifice this creature: Create a 2/2 green Spider creature token with reach for each
 *  counter on this creature. Activate only as a sorcery."
 *
 * Exercises the new NEST passive counter, the mana ability that adds it, and the sacrifice ability
 * that reads the pre-cost counter count as last-known information
 * (`DynamicAmount.LastKnownSourceCounters`) to scale the token payoff.
 */
class TwitchingDollScenarioTest : FunSpec({

    val manaAbilityId = TwitchingDoll.activatedAbilities[0].id
    val sacAbilityId = TwitchingDoll.activatedAbilities[1].id

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun spiderTokenCount(driver: GameTestDriver, player: com.wingedsheep.sdk.model.EntityId): Int =
        driver.getCreatures(player).count {
            driver.state.getEntity(it)
                ?.get<com.wingedsheep.engine.state.components.identity.CardComponent>()
                ?.typeLine?.subtypes
                ?.any { s -> s.value.equals("Spider", ignoreCase = true) } == true
        }

    test("mana ability puts a nest counter on the doll") {
        val driver = newDriver()
        val player = driver.player1
        val doll = driver.putCreatureOnBattlefield(player, "Twitching Doll")
        driver.removeSummoningSickness(doll)

        driver.submit(ActivateAbility(playerId = player, sourceId = doll, abilityId = manaAbilityId))
        // The mana ability pauses to ask which color of mana to add; answer it so the rest of the
        // composite (the nest counter) resolves.
        val decision = driver.pendingDecision
        decision.shouldBeInstanceOf<ChooseColorDecision>()
        driver.submitDecision(player, ColorChosenResponse(decision.id, com.wingedsheep.sdk.core.Color.GREEN))

        val counters = driver.state.getEntity(doll)?.get<CountersComponent>()?.counters ?: emptyMap()
        counters[CounterType.NEST] shouldBe 1
    }

    test("sacrifice ability creates a Spider token for each counter (read as last-known info)") {
        val driver = newDriver()
        val player = driver.player1
        val doll = driver.putCreatureOnBattlefield(player, "Twitching Doll")
        driver.removeSummoningSickness(doll)

        // Seed two nest counters directly, then sacrifice: the cost wipes the counters, so the
        // token count must come from the pre-cost snapshot (LastKnownSourceCounters).
        driver.addComponent(doll, CountersComponent(mapOf(CounterType.NEST to 2)))

        driver.submit(
            ActivateAbility(playerId = player, sourceId = doll, abilityId = sacAbilityId)
        ).isSuccess shouldBe true
        driver.bothPass() // resolve the activated ability

        driver.isPaused shouldBe false
        // The doll was sacrificed as a cost.
        driver.findPermanent(player, "Twitching Doll") shouldBe null
        // Two nest counters -> two 2/2 Spider tokens.
        spiderTokenCount(driver, player) shouldBe 2
    }

    test("with no counters, the sacrifice ability creates no tokens") {
        val driver = newDriver()
        val player = driver.player1
        val doll = driver.putCreatureOnBattlefield(player, "Twitching Doll")
        driver.removeSummoningSickness(doll)

        driver.submit(
            ActivateAbility(playerId = player, sourceId = doll, abilityId = sacAbilityId)
        ).isSuccess shouldBe true
        driver.bothPass()

        driver.isPaused shouldBe false
        driver.findPermanent(player, "Twitching Doll") shouldBe null
        spiderTokenCount(driver, player) shouldBe 0
    }
})

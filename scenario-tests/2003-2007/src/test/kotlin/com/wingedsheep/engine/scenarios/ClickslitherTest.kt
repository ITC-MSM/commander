package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lgn.cards.Clickslither
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for Clickslither:
 * {1}{R}{R}{R} Creature — Insect 3/3
 * Haste
 * Sacrifice a Goblin: Clickslither gets +2/+2 and gains trample until end of turn.
 */
class ClickslitherTest : FunSpec({

    val projector = StateProjector()

    val TestGoblin = CardDefinition.creature(
        name = "Test Goblin",
        manaCost = ManaCost.parse("{R}"),
        subtypes = setOf(Subtype("Goblin")),
        power = 1,
        toughness = 1
    )

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(Clickslither, TestGoblin))
        return driver
    }

    test("sacrificing a goblin gives +2/+2 and trample") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val clickslither = driver.putCreatureOnBattlefield(activePlayer, "Clickslither")
        val goblin = driver.putCreatureOnBattlefield(activePlayer, "Test Goblin")

        val abilityId = Clickslither.activatedAbilities.first().id

        val result = driver.submit(
            ActivateAbility(
                playerId = activePlayer,
                sourceId = clickslither,
                abilityId = abilityId,
                costPayment = AdditionalCostPayment(sacrificedPermanents = listOf(goblin))
            )
        )
        result.isSuccess shouldBe true

        // Resolve the ability
        driver.bothPass()

        // Goblin should be gone
        driver.findPermanent(activePlayer, "Test Goblin") shouldBe null

        // Clickslither should be 5/5 with trample
        projector.getProjectedPower(driver.state, clickslither) shouldBe 5
        projector.getProjectedToughness(driver.state, clickslither) shouldBe 5
    }

    test("sacrificing multiple goblins stacks the bonus") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val clickslither = driver.putCreatureOnBattlefield(activePlayer, "Clickslither")
        val goblin1 = driver.putCreatureOnBattlefield(activePlayer, "Test Goblin")
        val goblin2 = driver.putCreatureOnBattlefield(activePlayer, "Test Goblin")

        val abilityId = Clickslither.activatedAbilities.first().id

        // Sacrifice first goblin
        driver.submit(
            ActivateAbility(
                playerId = activePlayer,
                sourceId = clickslither,
                abilityId = abilityId,
                costPayment = AdditionalCostPayment(sacrificedPermanents = listOf(goblin1))
            )
        )
        driver.bothPass()

        // Sacrifice second goblin
        driver.submit(
            ActivateAbility(
                playerId = activePlayer,
                sourceId = clickslither,
                abilityId = abilityId,
                costPayment = AdditionalCostPayment(sacrificedPermanents = listOf(goblin2))
            )
        )
        driver.bothPass()

        // Clickslither should be 7/7 (3+2+2 / 3+2+2)
        projector.getProjectedPower(driver.state, clickslither) shouldBe 7
        projector.getProjectedToughness(driver.state, clickslither) shouldBe 7
    }

    test("cannot activate without a goblin to sacrifice") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val clickslither = driver.putCreatureOnBattlefield(activePlayer, "Clickslither")
        // No goblins on battlefield

        val abilityId = Clickslither.activatedAbilities.first().id

        val result = driver.submit(
            ActivateAbility(
                playerId = activePlayer,
                sourceId = clickslither,
                abilityId = abilityId
            )
        )
        result.isSuccess shouldBe false
    }
})

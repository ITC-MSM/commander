package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.DamageComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.tokens.PredefinedTokens
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe

/**
 * Ancient Adamantoise:
 *  - redirects all damage to its controller and their other permanents onto itself,
 *  - keeps its marked damage through cleanup steps ([DamagePersistsThroughCleanup]),
 *  - on death exiles itself and makes ten tapped Treasure tokens.
 */
class AncientAdamantoiseScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + PredefinedTokens.allTokens)
        return driver
    }

    fun GameTestDriver.markDamage(entityId: EntityId, amount: Int) {
        replaceState(state.updateEntity(entityId) { c -> c.with(DamageComponent(amount)) })
    }

    fun GameTestDriver.damageOn(entityId: EntityId): Int =
        state.getEntity(entityId)?.get<DamageComponent>()?.amount ?: 0

    fun GameTestDriver.treasuresControlledBy(playerId: EntityId): List<EntityId> =
        state.getBattlefield().filter {
            state.getEntity(it)?.get<CardComponent>()?.name == "Treasure" &&
                getController(it) == playerId
        }

    test("damage to you and to your other permanents is redirected onto the Adamantoise") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 30, "Mountain" to 10), startingLife = 20)
        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val adamantoise = driver.putCreatureOnBattlefield(p1, "Ancient Adamantoise")
        val bear = driver.putCreatureOnBattlefield(p1, "Centaur Courser") // 3/3 vanilla

        // Bolt aimed at the controller — redirected onto the Adamantoise; life untouched.
        driver.giveMana(p1, Color.RED, 1)
        val bolt1 = driver.putCardInHand(p1, "Lightning Bolt")
        driver.castSpellWithTargets(p1, bolt1, listOf(ChosenTarget.Player(p1)))
        driver.bothPass()

        driver.getLifeTotal(p1) shouldBe 20
        driver.damageOn(adamantoise) shouldBe 3

        // Bolt aimed at another permanent you control — also redirected; accumulates.
        driver.giveMana(p1, Color.RED, 1)
        val bolt2 = driver.putCardInHand(p1, "Lightning Bolt")
        driver.castSpellWithTargets(p1, bolt2, listOf(ChosenTarget.Permanent(bear)))
        driver.bothPass()

        driver.damageOn(bear) shouldBe 0
        driver.damageOn(adamantoise) shouldBe 6
    }

    test("marked damage persists through cleanup, unlike an ordinary creature") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 30, "Mountain" to 10), startingLife = 20)
        val p1 = driver.activePlayer!!
        val p2 = driver.getOpponent(p1)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val adamantoise = driver.putCreatureOnBattlefield(p1, "Ancient Adamantoise")
        val bear = driver.putCreatureOnBattlefield(p1, "Centaur Courser")

        driver.markDamage(adamantoise, 5) // non-lethal against 20 toughness
        driver.markDamage(bear, 2)

        // Advance through this turn's cleanup into the opponent's upkeep.
        driver.passPriorityUntil(Step.UPKEEP)
        driver.activePlayer shouldBe p2

        driver.damageOn(adamantoise) shouldBe 5   // survived the CR 514.2 cleanup removal
        driver.state.getEntity(bear)?.has<DamageComponent>() shouldBe false // ordinary removal
    }

    test("dying exiles the Adamantoise and creates ten tapped Treasure tokens") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 30, "Mountain" to 10), startingLife = 20)
        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val adamantoise = driver.putCreatureOnBattlefield(p1, "Ancient Adamantoise")
        // Bring it near death, then finish it with a real damage event so state-based actions run.
        // A bolt aimed at the Adamantoise itself is not redirected (a redirect whose destination
        // equals the original recipient is skipped), so all 3 land on it: 18 + 3 = 21 >= 20.
        driver.markDamage(adamantoise, 18)
        driver.giveMana(p1, Color.RED, 1)
        val bolt = driver.putCardInHand(p1, "Lightning Bolt")
        driver.castSpellWithTargets(p1, bolt, listOf(ChosenTarget.Permanent(adamantoise)))
        // Resolve the bolt (lethal), then the dies trigger that exiles it and makes Treasures.
        driver.passPriorityUntil(Step.END)

        driver.state.getBattlefield().contains(adamantoise) shouldBe false
        driver.getExileCardNames(p1) shouldContain "Ancient Adamantoise"

        val treasures = driver.treasuresControlledBy(p1)
        treasures.size shouldBe 10
        treasures.all { driver.isTapped(it) }.shouldBeTrue()
    }
})

package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.combat.AttackingComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.TokenComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.AnimPakal
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Anim Pakal, Thousandth Moon (LCI #223) — {1}{R}{W} Legendary Creature — Human Soldier 1/2.
 *
 * "Whenever you attack with one or more non-Gnome creatures, put a +1/+1 counter on Anim Pakal,
 * then create X 1/1 colorless Gnome artifact creature tokens that are tapped and attacking, where
 * X is the number of +1/+1 counters on Anim Pakal."
 *
 * Two behaviors proved end-to-end:
 *  1. First attack: the trigger fires, Anim Pakal gains a +1/+1 counter, and exactly 1 Gnome
 *     token is created — tapped, attacking, a token, and an artifact creature.
 *  2. Counter scaling: when Anim Pakal already has 1 +1/+1 counter before the attack, the
 *     trigger fires, increments to 2 counters, and creates 2 tapped-and-attacking Gnome tokens
 *     (N equals the post-increment count, not the pre-attack count).
 */
class AnimPakalScenarioTest : FunSpec({

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(AnimPakal)
        return driver
    }

    /** Return all Gnome tokens controlled by [playerId]. */
    fun GameTestDriver.gnomeTokens(playerId: EntityId): List<EntityId> =
        getPermanents(playerId).filter { id ->
            val entity = state.getEntity(id)
            entity?.has<TokenComponent>() == true &&
                entity.get<CardComponent>()?.name == "Gnome Token"
        }

    /** Read +1/+1 counter count on a permanent. */
    fun GameTestDriver.plusOneCounters(id: EntityId): Int =
        state.getEntity(id)?.get<CountersComponent>()?.counters?.get(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    // ─────────────────────────────────────────────────────────────────────────
    // Test 1: first attack puts 1 counter and creates 1 tapped-and-attacking Gnome token
    // ─────────────────────────────────────────────────────────────────────────
    test("first attack gains one +1/+1 counter and creates exactly one tapped and attacking Gnome token") {
        val driver = newDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 20, "Mountain" to 20), startingLife = 20)

        val attacker = driver.activePlayer!!
        val defender = driver.getOpponent(attacker)

        val animPakal = driver.putCreatureOnBattlefield(attacker, "Anim Pakal, Thousandth Moon")
        driver.removeSummoningSickness(animPakal)

        driver.gnomeTokens(attacker).size shouldBe 0
        driver.plusOneCounters(animPakal) shouldBe 0

        // Advance to declare-attackers and attack with Anim Pakal (a non-Gnome creature).
        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(attacker, listOf(animPakal), defender)
        // Resolve the YouAttackWithFilter trigger: AddCounters then CreateToken.
        driver.bothPass()

        // Anim Pakal now has 1 +1/+1 counter.
        driver.plusOneCounters(animPakal) shouldBe 1

        // Exactly 1 Gnome token was created.
        val tokens = driver.gnomeTokens(attacker)
        tokens.size shouldBe 1

        val token = tokens.first()
        val tokenEntity = driver.state.getEntity(token)

        // Token is tapped.
        tokenEntity?.has<TappedComponent>() shouldBe true

        // Token is attacking the defender.
        val attacking = tokenEntity?.get<AttackingComponent>()
        attacking shouldNotBe null
        attacking!!.defenderId shouldBe defender
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Test 2: counter scaling — second attack creates N tokens = post-increment counter count
    // ─────────────────────────────────────────────────────────────────────────
    test("second attack with one pre-existing counter creates two Gnome tokens") {
        val driver = newDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 20, "Mountain" to 20), startingLife = 20)

        val attacker = driver.activePlayer!!
        val defender = driver.getOpponent(attacker)

        val animPakal = driver.putCreatureOnBattlefield(attacker, "Anim Pakal, Thousandth Moon")
        driver.removeSummoningSickness(animPakal)

        // Simulate Anim Pakal already having 1 +1/+1 counter from a previous attack.
        driver.addComponent(animPakal, CountersComponent(mapOf(CounterType.PLUS_ONE_PLUS_ONE to 1)))
        driver.plusOneCounters(animPakal) shouldBe 1

        // Advance to declare-attackers and attack with Anim Pakal.
        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(attacker, listOf(animPakal), defender)
        driver.bothPass()

        // Trigger incremented counter from 1 → 2.
        driver.plusOneCounters(animPakal) shouldBe 2

        // Exactly 2 Gnome tokens were created (X = 2 counters after incrementing).
        val tokens = driver.gnomeTokens(attacker)
        tokens.size shouldBe 2

        // Both tokens are tapped and attacking the defender.
        for (token in tokens) {
            val entity = driver.state.getEntity(token)
            entity?.has<TappedComponent>() shouldBe true
            entity?.get<AttackingComponent>()?.defenderId shouldBe defender
        }
    }
})

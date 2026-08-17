package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.CombatResolutionDecision
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.state.components.battlefield.DamageComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Wolverine, Fierce Fighter (MSH #240) — {2}{R}{G} 3/5 with three clauses:
 *  - Haste,
 *  - "When Wolverine enters, he fights up to one other target creature." (an optional-target
 *    `Effects.Fight` off an ETB trigger), and
 *  - "If damage would be dealt to Wolverine, instead that damage is dealt, but all other damage
 *    already dealt to him is healed." — the `RecipientFilter.Self` `HealOtherDamage` replacement
 *    (CR 701.69a), wired on both creature-damage paths.
 *
 * Mechanism-level coverage of the replacement lives in `HealOtherDamageReplacementTest`; this file
 * asserts the printed card behaves as printed.
 */
class WolverineFierceFighterScenarioTest : FunSpec({

    // TestCards' only burn is Lightning Bolt (3), which can never be lethal to a 3/5 that heals
    // between events. A single 5-damage instance is what proves the damage itself isn't reduced.
    val meteor: CardDefinition = card("Test Meteor") {
        manaCost = "{0}"
        typeLine = "Instant"
        oracleText = "Test Meteor deals 5 damage to target creature."
        spell {
            val t = target("target creature", Targets.Creature)
            effect = Effects.DealDamage(5, t)
        }
    }

    fun newGame(): Triple<GameTestDriver, EntityId, EntityId> {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(meteor))
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val you = driver.activePlayer!!
        val opponent = driver.state.turnOrder.first { it != you }
        return Triple(driver, you, opponent)
    }

    fun resolveStack(driver: GameTestDriver) {
        var guard = 0
        while (guard++ < 30 && driver.state.stack.isNotEmpty() && !driver.isPaused) driver.bothPass()
    }

    fun GameTestDriver.bolt(player: EntityId, target: EntityId) {
        giveMana(player, Color.RED, 1)
        val b = putCardInHand(player, "Lightning Bolt")
        castSpellWithTargets(player, b, listOf(ChosenTarget.Permanent(target)))
        bothPass()
        resolveStack(this)
    }

    fun GameTestDriver.markedDamage(entityId: EntityId): Int =
        state.getEntity(entityId)?.get<DamageComponent>()?.amount ?: 0

    /**
     * Push the combat damage step through to completion. Wolverine facing two blockers has to
     * order them (CR 510.1c), so the engine pauses on a [CombatResolutionDecision] that a plain
     * `bothPass` loop would never answer.
     */
    fun GameTestDriver.finishCombatDamage() {
        var guard = 0
        while (guard++ < 30) {
            when {
                pendingDecision is CombatResolutionDecision -> confirmCombatDamage()
                pendingDecision != null -> autoResolveDecision()
                state.stack.isNotEmpty() -> bothPass()
                else -> return
            }
        }
    }

    /** Hard-cast Wolverine ({2}{R}{G}) so the ETB fight trigger actually fires. */
    fun GameTestDriver.castWolverine(player: EntityId): EntityId {
        val wolverine = putCardInHand(player, "Wolverine, Fierce Fighter")
        repeat(4) { putLandOnBattlefield(player, "Forest") }
        giveMana(player, Color.RED, 1)
        submit(
            CastSpell(playerId = player, cardId = wolverine, paymentStrategy = PaymentStrategy.AutoPay),
        ).isSuccess shouldBe true
        return wolverine
    }

    test("healing factor: repeated noncombat damage never accumulates on Wolverine") {
        val (driver, you, _) = newGame()
        val wolverine = driver.putCreatureOnBattlefield(you, "Wolverine, Fierce Fighter") // 3/5

        driver.bolt(you, wolverine)
        driver.markedDamage(wolverine) shouldBe 3

        // A second Bolt would be lethal on any ordinary 3/5 (3 + 3 ≥ 5). Wolverine heals the first.
        driver.bolt(you, wolverine)
        withClue("the first Bolt's 3 is healed as part of the second Bolt's replacement") {
            driver.markedDamage(wolverine) shouldBe 3
        }
        driver.state.getBattlefield().contains(wolverine) shouldBe true
    }

    test("healing factor: a single instance of 5 still kills him — the damage itself is not reduced") {
        val (driver, you, _) = newGame()
        val wolverine = driver.putCreatureOnBattlefield(you, "Wolverine, Fierce Fighter") // 3/5

        driver.bolt(you, wolverine)
        driver.markedDamage(wolverine) shouldBe 3

        // The replacement heals, it does not prevent or clamp: the earlier 3 goes away, but all 5
        // of this instance is marked on a 3/5 and he dies as a state-based action (CR 704.5g).
        val meteor = driver.putCardInHand(you, "Test Meteor")
        driver.castSpellWithTargets(you, meteor, listOf(ChosenTarget.Permanent(wolverine)))
        driver.bothPass()
        resolveStack(driver)

        withClue("5 in one event ≥ toughness 5 — the heal removed the old 3, not this instance") {
            driver.state.getBattlefield().contains(wolverine) shouldBe false
        }
    }

    test("healing factor: one combat damage step is a single event, so a double block can still kill him") {
        val (driver, you, opponent) = newGame()
        val wolverine = driver.putCreatureOnBattlefield(you, "Wolverine, Fierce Fighter") // 3/5
        driver.removeSummoningSickness(wolverine)
        val blockerA = driver.putCreatureOnBattlefield(opponent, "Centaur Courser") // 3/3
        val blockerB = driver.putCreatureOnBattlefield(opponent, "Centaur Courser") // 3/3

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(you, listOf(wolverine), defendingPlayer = opponent).error shouldBe null
        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)
        driver.declareBlockers(opponent, mapOf(blockerA to listOf(wolverine), blockerB to listOf(wolverine)))
        driver.passPriorityUntil(Step.COMBAT_DAMAGE)
        driver.finishCombatDamage()

        withClue("all combat damage in a step is simultaneous (CR 510.2) — 6 damage on a 3/5") {
            driver.state.getBattlefield().contains(wolverine) shouldBe false
        }
    }

    test("healing factor: a first striker's damage is healed away by the regular combat damage step") {
        val (driver, you, opponent) = newGame()
        val wolverine = driver.putCreatureOnBattlefield(you, "Wolverine, Fierce Fighter") // 3/5
        driver.removeSummoningSickness(wolverine)
        val striker = driver.putCreatureOnBattlefield(opponent, "First Strike Knight") // 3/1 first strike
        val courser = driver.putCreatureOnBattlefield(opponent, "Centaur Courser")     // 3/3

        // 3 + 3 = 6 would kill a 3/5 outright, but the two damage steps are separate events.
        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(you, listOf(wolverine), defendingPlayer = opponent).error shouldBe null
        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)
        driver.declareBlockers(opponent, mapOf(striker to listOf(wolverine), courser to listOf(wolverine)))
        driver.passPriorityUntil(Step.COMBAT_DAMAGE)
        driver.finishCombatDamage()
        driver.passPriorityUntil(Step.END_COMBAT)
        driver.finishCombatDamage()

        withClue("only the regular step's 3 remains marked") {
            driver.markedDamage(wolverine) shouldBe 3
        }
        driver.state.getBattlefield().contains(wolverine) shouldBe true
    }

    test("ETB: Wolverine fights the chosen creature, and his own damage is healed by the next hit") {
        val (driver, you, opponent) = newGame()
        val courser = driver.putCreatureOnBattlefield(opponent, "Centaur Courser") // 3/3
        val wolverine = driver.castWolverine(you)

        driver.bothPass() // resolve Wolverine; the ETB fight trigger goes on the stack and wants a target
        driver.pendingDecision.shouldNotBeNull()
        driver.submitTargetSelection(you, listOf(courser))
        resolveStack(driver)

        withClue("Wolverine's 3 power kills the 3/3") {
            driver.state.getBattlefield().contains(courser) shouldBe false
        }
        withClue("the 3/3 dealt 3 back — marked, not prevented") {
            driver.markedDamage(wolverine) shouldBe 3
        }

        // The fight damage is then healed by the next damage event rather than adding to it.
        driver.bolt(you, wolverine)
        driver.markedDamage(wolverine) shouldBe 3
        driver.state.getBattlefield().contains(wolverine) shouldBe true
    }

    test("ETB: 'up to one' can pick nothing — the fight is skipped and Wolverine is unhurt") {
        val (driver, you, _) = newGame()
        val wolverine = driver.castWolverine(you)

        // Wolverine is the only creature anywhere, so "up to one *other* target creature" has no
        // legal choice. The engine still offers the (empty) choice — an optional target always
        // prompts — and choosing nothing must resolve the trigger harmlessly rather than making
        // him fight himself or fizzle the ETB.
        driver.bothPass()
        driver.pendingDecision.shouldNotBeNull()
        driver.submitTargetSelection(you, emptyList())
        resolveStack(driver)

        driver.pendingDecision.shouldBeNull()
        driver.state.getBattlefield().contains(wolverine) shouldBe true
        driver.markedDamage(wolverine) shouldBe 0
    }
})

package com.wingedsheep.engine

import com.wingedsheep.engine.core.CombatResolutionDecision
import com.wingedsheep.engine.state.components.battlefield.DamageComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.HealOtherDamage
import com.wingedsheep.sdk.scripting.events.DamageType
import com.wingedsheep.sdk.scripting.events.RecipientFilter
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * The `HealOtherDamage` damage replacement as a *primitive* (CR 701.69a — heal): the damage is
 * dealt in full, and all damage marked before it is removed. Card-level coverage of the one printed
 * card that uses it lives in `WolverineFierceFighterScenarioTest`; this file exercises the mechanism
 * with synthetic cards so the two do not have to move together.
 *
 * The properties under test:
 *
 *  1. **Marked damage doesn't accumulate across damage events** — two separate burn spells leave
 *     only the second one's damage marked, on the `DamageUtils.dealDamageToTarget` path.
 *  2. **Once per damage event, not per instance** — all combat damage in a step is dealt
 *     simultaneously (CR 510.2), so a double-blocked healer keeps *both* blockers' damage and heals
 *     only what was marked before the step (the `CombatDamageManager` path).
 *  3. **The first-strike step is a separate event** — a first striker's damage is healed away by
 *     the regular combat damage step.
 *  4. **`appliesTo` is a real filter** — a `RecipientFilter.CreatureYouControl` host heals *other*
 *     creatures its controller controls and leaves the opponent's alone, so the primitive is not
 *     hardwired to `RecipientFilter.Self`.
 *  5. **Deathtouch still kills** — the heal clears the whole `DamageComponent` including its
 *     deathtouch flag, but the damage being dealt in the same event re-stamps it (CR 704.5h).
 *  6. **`damageType` is a real filter** — a `DamageType.NonCombat` host heals through burn but not
 *     through combat damage, so the type axis is honoured rather than ignored.
 *
 * Every accumulation assertion is paired with a control creature that has no replacement, so a
 * regression that stopped marking damage at all would fail rather than pass.
 */
class HealOtherDamageReplacementTest : FunSpec({

    /** 3/7 whose own damage is healed by anything that damages it — the Wolverine shape. */
    val Healer: CardDefinition = card("Test Healer") {
        manaCost = "{2}{R}{G}"
        typeLine = "Creature — Mutant"
        power = 3
        toughness = 7
        oracleText = "If damage would be dealt to this creature, instead that damage is dealt, but " +
            "all other damage already dealt to it is healed."
        replacementEffect(
            HealOtherDamage(appliesTo = EventPattern.DamageEvent(recipient = RecipientFilter.Self))
        )
    }

    /** 0/4 enchantment-shaped host that heals *every creature its controller controls*. */
    val FieldMedic: CardDefinition = card("Test Field Medic") {
        manaCost = "{2}{W}"
        typeLine = "Enchantment"
        oracleText = "If damage would be dealt to a creature you control, instead that damage is " +
            "dealt, but all other damage already dealt to it is healed."
        replacementEffect(
            HealOtherDamage(
                appliesTo = EventPattern.DamageEvent(recipient = RecipientFilter.CreatureYouControl)
            )
        )
    }

    /** 3/7 whose heal is gated to *noncombat* damage — exercises the `damageType` filter axis. */
    val BurnWard: CardDefinition = card("Test Burn Ward") {
        manaCost = "{2}{R}"
        typeLine = "Creature — Mutant"
        power = 3
        toughness = 7
        oracleText = "If noncombat damage would be dealt to this creature, instead that damage is " +
            "dealt, but all other damage already dealt to it is healed."
        replacementEffect(
            HealOtherDamage(
                appliesTo = EventPattern.DamageEvent(
                    recipient = RecipientFilter.Self,
                    damageType = DamageType.NonCombat
                )
            )
        )
    }

    /** 3/7 with no replacement at all — the control for every accumulation assertion. */
    val PlainBeast: CardDefinition = card("Test Plain Beast") {
        manaCost = "{2}{G}{G}"
        typeLine = "Creature — Beast"
        power = 3
        toughness = 7
    }

    fun newGame(): Triple<GameTestDriver, EntityId, EntityId> {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(Healer, FieldMedic, BurnWard, PlainBeast))
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true, startingPlayer = 0)
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
     * Push the combat damage step through to completion. A 3-power attacker facing two blockers
     * has to order them (CR 510.1c), so the engine pauses on a [CombatResolutionDecision] that a
     * plain `bothPass` loop would never answer.
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

    test("noncombat: damage from a second source heals the first — marked damage never accumulates") {
        val (driver, you, _) = newGame()
        val healer = driver.putCreatureOnBattlefield(you, "Test Healer")     // 3/7
        val control = driver.putCreatureOnBattlefield(you, "Test Plain Beast") // 3/7, no replacement

        driver.bolt(you, healer)
        driver.bolt(you, control)
        withClue("first bolt marks normally on both") {
            driver.markedDamage(healer) shouldBe 3
            driver.markedDamage(control) shouldBe 3
        }

        driver.bolt(you, healer)
        driver.bolt(you, control)
        withClue("the healer's earlier 3 is healed; the control creature accumulates") {
            driver.markedDamage(healer) shouldBe 3
            driver.markedDamage(control) shouldBe 6
        }

        // 3 + 3 = 6 would still be under a toughness of 7, so survival alone proves nothing —
        // a third bolt would kill the control creature (9 ≥ 7) but never the healer.
        driver.bolt(you, healer)
        driver.bolt(you, control)
        driver.markedDamage(healer) shouldBe 3
        driver.state.getBattlefield().contains(healer) shouldBe true
        driver.state.getBattlefield().contains(control) shouldBe false
    }

    test("combat: all damage in a step is one event — a double block still stacks both blockers' damage") {
        val (driver, you, opponent) = newGame()
        val healer = driver.putCreatureOnBattlefield(you, "Test Healer") // 3/7
        driver.removeSummoningSickness(healer)
        val blockerA = driver.putCreatureOnBattlefield(opponent, "Centaur Courser") // 3/3
        val blockerB = driver.putCreatureOnBattlefield(opponent, "Centaur Courser") // 3/3

        // Pre-existing damage from before the combat damage step — this is what gets healed.
        driver.bolt(you, healer)
        driver.markedDamage(healer) shouldBe 3

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(you, listOf(healer), defendingPlayer = opponent).error shouldBe null
        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)
        driver.declareBlockers(opponent, mapOf(blockerA to listOf(healer), blockerB to listOf(healer)))
        driver.passPriorityUntil(Step.COMBAT_DAMAGE)
        driver.finishCombatDamage()

        withClue("the pre-combat 3 is healed, both blockers' 3 stay — heal fires once per step, not per instance") {
            driver.markedDamage(healer) shouldBe 6
        }
        driver.state.getBattlefield().contains(healer) shouldBe true
    }

    test("combat: the first-strike step is a separate damage event, so its damage is healed away") {
        val (driver, you, opponent) = newGame()
        val healer = driver.putCreatureOnBattlefield(you, "Test Healer") // 3/7
        driver.removeSummoningSickness(healer)
        val striker = driver.putCreatureOnBattlefield(opponent, "First Strike Knight") // 3/1 first strike
        val courser = driver.putCreatureOnBattlefield(opponent, "Centaur Courser")     // 3/3

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(you, listOf(healer), defendingPlayer = opponent).error shouldBe null
        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)
        driver.declareBlockers(opponent, mapOf(striker to listOf(healer), courser to listOf(healer)))
        driver.passPriorityUntil(Step.COMBAT_DAMAGE)
        driver.finishCombatDamage()
        driver.passPriorityUntil(Step.END_COMBAT)
        driver.finishCombatDamage()

        withClue("the first striker's 3 was healed by the regular step; only the Courser's 3 remains") {
            driver.markedDamage(healer) shouldBe 3
        }
        driver.state.getBattlefield().contains(healer) shouldBe true
    }

    test("appliesTo is a real filter: a CreatureYouControl host heals your creatures, not the opponent's") {
        val (driver, you, opponent) = newGame()
        driver.putPermanentOnBattlefield(you, "Test Field Medic")
        val yours = driver.putCreatureOnBattlefield(you, "Test Plain Beast")      // 3/7, no own replacement
        val theirs = driver.putCreatureOnBattlefield(opponent, "Test Plain Beast") // 3/7, no own replacement

        driver.bolt(you, yours)
        driver.bolt(you, theirs)
        driver.bolt(you, yours)
        driver.bolt(you, theirs)

        withClue("the medic's replacement reads 'you' as its own controller") {
            driver.markedDamage(yours) shouldBe 3
            driver.markedDamage(theirs) shouldBe 6
        }
    }

    test("damageType is a real filter: a NonCombat-gated heal ignores combat damage") {
        val (driver, you, opponent) = newGame()
        val ward = driver.putCreatureOnBattlefield(you, "Test Burn Ward") // 3/7, noncombat-only heal
        driver.removeSummoningSickness(ward)
        val courser = driver.putCreatureOnBattlefield(opponent, "Centaur Courser") // 3/3

        driver.bolt(you, ward)
        driver.bolt(you, ward)
        withClue("noncombat damage matches the filter, so the first bolt's 3 is healed") {
            driver.markedDamage(ward) shouldBe 3
        }

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(you, listOf(ward), defendingPlayer = opponent).error shouldBe null
        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)
        driver.declareBlockers(opponent, mapOf(courser to listOf(ward)))
        driver.passPriorityUntil(Step.COMBAT_DAMAGE)
        driver.finishCombatDamage()

        withClue("combat damage does NOT match, so the blocker's 3 accumulates on top of the bolt's 3") {
            driver.markedDamage(ward) shouldBe 6
        }
        driver.state.getBattlefield().contains(ward) shouldBe true
    }

    test("deathtouch still kills through the heal — the flag is re-stamped by the same damage event") {
        val (driver, you, opponent) = newGame()
        val healer = driver.putCreatureOnBattlefield(you, "Test Healer") // 3/7
        driver.removeSummoningSickness(healer)
        val rat = driver.putCreatureOnBattlefield(opponent, "Deathtouch Rat") // 1/1 deathtouch

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(you, listOf(healer), defendingPlayer = opponent).error shouldBe null
        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)
        driver.declareBlockers(opponent, mapOf(rat to listOf(healer)))
        driver.passPriorityUntil(Step.COMBAT_DAMAGE)
        driver.finishCombatDamage()

        withClue("healing clears the deathtouch flag, but this event's deathtouch damage re-sets it (CR 704.5h)") {
            driver.state.getBattlefield().contains(healer) shouldBe false
        }
    }
})

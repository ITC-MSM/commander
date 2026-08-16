package com.wingedsheep.engine.triggers

import com.wingedsheep.engine.event.GlobalGrantedTriggeredAbility
import com.wingedsheep.engine.state.components.player.CreaturesDiedThisTurnComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.conditions.CreatureDiedThisTurnCondition
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.engine.core.AbilityFizzledEvent
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe

/**
 * CR 608.2a runs before CR 608.2b.
 *
 * A resolving triggered ability checks its intervening-"if" clause *first* — "if it isn't [true],
 * the ability is removed from the stack and does nothing" — and only then checks whether its
 * targets are still legal. The distinction is invisible until both fail at once, which is exactly
 * what the first test below sets up: it is the intervening-"if" that ends the resolution, so it is
 * the intervening-"if" that the fizzle reports.
 *
 * The two controls pin the other corners: an ability whose condition still holds but whose target
 * is gone must still fizzle on targets, and one where neither has changed must resolve.
 */
class InterveningIfBeforeTargetLegalityTest : FunSpec({

    /**
     * A trigger at your end step that deals 2 damage to target creature, gated by an
     * intervening-"if" ("… if a creature died this turn"). Both halves are independently
     * falsifiable at resolution: drop [CreaturesDiedThisTurnComponent] to break the condition,
     * move the target off the battlefield to break its legality.
     */
    fun setUp(): Triple<GameTestDriver, EntityId, EntityId> {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40))
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val you = driver.player1
        val source = driver.putPermanentOnBattlefield(you, "Test Enchantment")
        val victim = driver.putCreatureOnBattlefield(driver.player2, "Centaur Courser")

        val ability = TriggeredAbility.create(
            trigger = EventPattern.StepEvent(Step.END, Player.You),
            effect = DealDamageEffect(2, EffectTarget.ContextTarget(0)),
            targetRequirement = TargetCreature(),
            interveningIf = CreatureDiedThisTurnCondition
        )
        driver.replaceState(
            driver.state.copy(
                globalGrantedTriggeredAbilities = listOf(
                    GlobalGrantedTriggeredAbility(
                        ability = ability,
                        controllerId = you,
                        sourceId = source,
                        sourceName = "Test Intervening-If Ability",
                        duration = Duration.Permanent
                    )
                )
            )
        )

        // Make the intervening-"if" true so the ability triggers at all (CR 603.4's first check).
        driver.replaceState(
            driver.state.updateEntity(you) { container ->
                container.with(CreaturesDiedThisTurnComponent(count = 1))
            }
        )

        // Trigger it and choose the still-legal target.
        driver.passPriorityUntil(Step.END)
        driver.submitTargetSelection(you, listOf(victim))
        driver.stackSize shouldBe 1

        return Triple(driver, source, victim)
    }

    fun breakCondition(driver: GameTestDriver) {
        driver.replaceState(
            driver.state.updateEntity(driver.player1) { container ->
                container.without<CreaturesDiedThisTurnComponent>()
            }
        )
    }

    test("both checks fail: CR 608.2a's intervening-if ends the resolution, not CR 608.2b's targets") {
        val (driver, _, victim) = setUp()

        breakCondition(driver)
        driver.moveToGraveyard(victim)

        val result = driver.bothPass()

        val fizzles = result.events.filterIsInstance<AbilityFizzledEvent>()
        fizzles.map { it.reason } shouldContain "Intervening-if condition is no longer true"
        // CR 608.2b never got to speak.
        fizzles.map { it.reason }.contains("All targets are invalid") shouldBe false
        driver.stackSize shouldBe 0
    }

    test("control: condition still true, target gone — CR 608.2b fizzles on targets as before") {
        val (driver, _, victim) = setUp()

        driver.moveToGraveyard(victim)

        val result = driver.bothPass()

        val fizzles = result.events.filterIsInstance<AbilityFizzledEvent>()
        fizzles.map { it.reason } shouldContain "All targets are invalid"
        driver.stackSize shouldBe 0
    }

    test("control: condition true and target legal — the ability resolves and deals its damage") {
        val (driver, _, victim) = setUp()

        val result = driver.bothPass()

        result.events.filterIsInstance<AbilityFizzledEvent>() shouldBe emptyList()
        driver.stackSize shouldBe 0
        // Centaur Courser is a 3/3; 2 damage leaves it on the battlefield with damage marked.
        driver.state.getEntity(victim)
            ?.get<com.wingedsheep.engine.state.components.battlefield.DamageComponent>()
            ?.amount shouldBe 2
    }
})

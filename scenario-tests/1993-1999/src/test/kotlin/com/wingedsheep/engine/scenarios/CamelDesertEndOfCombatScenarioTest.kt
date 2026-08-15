package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.DeclareAttackers
import com.wingedsheep.engine.state.components.battlefield.DamageComponent
import com.wingedsheep.engine.state.components.combat.AttackingComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.arn.cards.Camel
import com.wingedsheep.mtg.sets.definitions.arn.cards.Desert
import com.wingedsheep.mtg.sets.definitions.arn.cards.MoorishCavalry
import com.wingedsheep.mtg.sets.definitions.arn.cards.WarElephant
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Camel (ARN #3) + the real Desert (ARN #72), exercising the *end of combat step* timing.
 *
 * Desert's pinger reads "deals 1 damage to target attacking creature. Activate only during the end
 * of combat step." Creatures are removed from combat as the combat phase *ends* — i.e. when the end
 * of combat step ends — not when it begins, so they're still attacking while players hold priority
 * during that step. These tests pin that timing (a regression guard: the engine previously cleared
 * combat on entry to the end of combat step, leaving Desert with no legal target) and confirm
 * Camel's Desert-damage prevention covers the Camel and its band but not an unrelated attacker.
 */
class CamelDesertEndOfCombatScenarioTest : FunSpec({

    val desertPing = Desert.activatedAbilities.first { !it.isManaAbility }.id

    fun damageOn(driver: GameTestDriver, entityId: EntityId): Int =
        driver.state.getEntity(entityId)?.get<DamageComponent>()?.amount ?: 0

    /**
     * Sets up an attacking Camel + War Elephant band plus a lone Moorish Cavalry, advances to the
     * end of combat step, then pings the creature chosen by [selectTarget] with the attacker's
     * real Desert. Returns the marked damage on that creature.
     */
    fun runDesertPing(
        selectTarget: (camel: EntityId, mate: EntityId, loner: EntityId) -> EntityId,
    ): Int {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(Camel, Desert, WarElephant, MoorishCavalry))
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        val attacker = driver.activePlayer!!
        val defender = driver.getOpponent(attacker)

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val camel = driver.putCreatureOnBattlefield(attacker, "Camel")
        val mate = driver.putCreatureOnBattlefield(attacker, "War Elephant")
        val loner = driver.putCreatureOnBattlefield(attacker, "Moorish Cavalry")
        driver.removeSummoningSickness(camel)
        driver.removeSummoningSickness(mate)
        driver.removeSummoningSickness(loner)
        driver.putLandOnBattlefield(attacker, "Desert")

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        val attackers = mapOf(camel to defender, mate to defender, loner to defender)
        // Camel + War Elephant form one band; Moorish Cavalry attacks alone.
        driver.submit(DeclareAttackers(attacker, attackers, bands = listOf(setOf(camel, mate))))

        driver.passPriorityUntil(Step.END_COMBAT)
        // Regression guard: attackers must still be in combat during the end of combat step.
        driver.state.getEntity(camel)?.has<AttackingComponent>() shouldBe true
        driver.state.getEntity(loner)?.has<AttackingComponent>() shouldBe true

        val target = selectTarget(camel, mate, loner)
        val desert = driver.findPermanent(attacker, "Desert")!!
        driver.submit(
            ActivateAbility(
                playerId = attacker,
                sourceId = desert,
                abilityId = desertPing,
                targets = listOf(ChosenTarget.Permanent(target)),
            ),
        ).error shouldBe null
        driver.bothPass()
        return damageOn(driver, target)
    }

    test("real Desert can ping an attacking creature during the end of combat step") {
        // Moorish Cavalry is not in the Camel's band, so its 1 damage is not prevented.
        runDesertPing { _, _, loner -> loner } shouldBe 1
    }

    test("Camel prevents the real Desert's damage to itself while attacking") {
        runDesertPing { camel, _, _ -> camel } shouldBe 0
    }

    test("Camel prevents the real Desert's damage to a banded creature") {
        runDesertPing { _, mate, _ -> mate } shouldBe 0
    }
})

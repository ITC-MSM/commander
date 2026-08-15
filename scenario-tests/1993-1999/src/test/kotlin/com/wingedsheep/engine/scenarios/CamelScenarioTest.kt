package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.DeclareAttackers
import com.wingedsheep.engine.state.components.battlefield.DamageComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.arn.cards.Camel
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Camel (ARN #3) — {W} 0/1 Creature — Camel, Banding.
 *
 * "As long as this creature is attacking, prevent all damage Deserts would deal to this
 * creature and to creatures banded with this creature."
 *
 * Implemented as a continuous [com.wingedsheep.sdk.scripting.PreventDamage] replacement whose
 * recipient filter is "in the same band as the source" (CR 702.22) and whose source filter is
 * "a Desert". These tests exercise the novel band-scoped recipient logic with a Desert-subtyped
 * pinger hitting attacking creatures:
 *   1. the Camel itself                        → prevented
 *   2. a creature banded with the Camel         → prevented
 *   3. a co-attacker NOT in the Camel's band    → not prevented (control)
 *
 * The pinger is a synthetic "Test Desert" rather than the real Desert card so the test stays in
 * the rules engine and depends only on the Desert *subtype* (which is all Camel's filter cares
 * about).
 */
class CamelScenarioTest : FunSpec({

    // Plain 2/2 with no banding — a legal "up to one without banding" band member, and also
    // usable as an unrelated lone attacker.
    val footSoldier = CardDefinition.creature(
        name = "Test Foot Soldier",
        manaCost = ManaCost.parse("{2}"),
        subtypes = setOf(Subtype("Soldier")),
        power = 2,
        toughness = 2,
        oracleText = ""
    )

    // A Desert that pings an attacking creature for 1 (no end-of-combat restriction, so it can
    // fire while attackers are on the battlefield in the declare-attackers step).
    val testDesert = card("Test Desert") {
        typeLine = "Land — Desert"
        colorIdentity = ""
        activatedAbility {
            cost = Costs.Tap
            val creature = target(
                "target attacking creature",
                TargetObject(filter = TargetFilter(GameObjectFilter.Creature.attacking())),
            )
            effect = Effects.DealDamage(1, creature)
        }
    }
    val desertPing = testDesert.activatedAbilities.first { !it.isManaAbility }.id

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(Camel, testDesert, footSoldier))
        return driver
    }

    fun damageOn(driver: GameTestDriver, entityId: EntityId): Int =
        driver.state.getEntity(entityId)?.get<DamageComponent>()?.amount ?: 0

    /**
     * Declares an attacking Camel + Foot Soldier band (plus an optional lone attacker), pings
     * the creature chosen by [selectTarget] with an attacker-controlled Test Desert, and returns
     * the marked damage on that creature.
     */
    fun runDesertPing(
        driver: GameTestDriver,
        includeLoner: Boolean,
        selectTarget: (camel: EntityId, mate: EntityId, loner: EntityId?) -> EntityId,
    ): Int {
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        val attacker = driver.activePlayer!!
        val defender = driver.getOpponent(attacker)

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val camel = driver.putCreatureOnBattlefield(attacker, "Camel")
        val mate = driver.putCreatureOnBattlefield(attacker, "Test Foot Soldier")
        val loner = if (includeLoner) driver.putCreatureOnBattlefield(attacker, "Test Foot Soldier") else null
        driver.removeSummoningSickness(camel)
        driver.removeSummoningSickness(mate)
        loner?.let { driver.removeSummoningSickness(it) }
        driver.putLandOnBattlefield(attacker, "Test Desert")

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        val attackers = buildMap {
            put(camel, defender); put(mate, defender)
            loner?.let { put(it, defender) }
        }
        // Camel + mate form one band; the loner (if any) attacks on its own.
        driver.submit(DeclareAttackers(attacker, attackers, bands = listOf(setOf(camel, mate))))

        val target = selectTarget(camel, mate, loner)
        val desert = driver.findPermanent(attacker, "Test Desert")!!
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

    test("prevents Desert damage dealt to the attacking Camel itself") {
        val driver = createDriver()
        // The 0/1 Camel survives only if all 1 damage is prevented.
        runDesertPing(driver, includeLoner = false) { camel, _, _ -> camel } shouldBe 0
    }

    test("prevents Desert damage dealt to a creature banded with the Camel") {
        val driver = createDriver()
        runDesertPing(driver, includeLoner = false) { _, mate, _ -> mate } shouldBe 0
    }

    test("does not prevent Desert damage to a co-attacker not in the Camel's band") {
        val driver = createDriver()
        runDesertPing(driver, includeLoner = true) { _, _, loner -> loner!! } shouldBe 1
    }
})

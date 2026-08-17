package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.handlers.effects.DamageUtils
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.events.DamageType
import com.wingedsheep.sdk.scripting.events.RecipientFilter
import com.wingedsheep.sdk.scripting.values.ContextPropertyKey
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Gap 12 substrate: `DealsDamageEvent(requireExcess = true)` fires only when the recipient took
 * damage past lethal (CR 120.4a), and `ContextPropertyKey.TRIGGER_EXCESS_DAMAGE_AMOUNT` exposes
 * the excess amount to the trigger's effect.
 *
 * Built for Fall of Cair Andros — "Whenever a creature an opponent controls is dealt excess
 * noncombat damage, amass Orcs X, where X is the excess damage." Verifies (a) the trigger
 * fires only on excess damage, (b) the excess amount is correct, (c) the trigger's recipient/
 * damage-type filters still gate, and (d) at-lethal damage produces no excess and no trigger.
 */
class ExcessDamageTriggerScenarioTest : FunSpec({

    val projector = StateProjector()

    // {0} 0/1 enchantment-creature-style observer. ANY binding so it watches the whole table.
    // "Whenever a creature an opponent controls is dealt excess noncombat damage, amass Orcs X,
    // where X is the excess damage."
    val Watcher = card("Excess Watcher") {
        manaCost = "{0}"
        typeLine = "Creature — Spirit"
        power = 0; toughness = 1
        triggeredAbility {
            trigger = Triggers.dealsDamage(
                damageType = DamageType.NonCombat,
                recipient = RecipientFilter.CreatureOpponentControls,
                binding = TriggerBinding.ANY,
                requireExcess = true,
            )
            effect = Effects.Amass(
                DynamicAmount.ContextProperty(ContextPropertyKey.TRIGGER_EXCESS_DAMAGE_AMOUNT),
                "Orc"
            )
        }
    }

    // {0} sorcery that throws 5 damage at a target creature (noncombat). Lets us pick the hit
    // amount without depending on combat math.
    val BigBolt = card("Big Bolt") {
        manaCost = "{0}"
        typeLine = "Sorcery"
        oracleText = "This deals 5 damage to target creature."
        spell {
            val creature = target("target creature", Targets.Creature)
            effect = Effects.DealDamage(5, creature)
        }
    }

    // {0} sorcery dealing exactly 2 — exactly lethal to a 2/2 with no excess.
    val ExactBolt = card("Exact Bolt") {
        manaCost = "{0}"
        typeLine = "Sorcery"
        oracleText = "This deals 2 damage to target creature."
        spell {
            val creature = target("target creature", Targets.Creature)
            effect = Effects.DealDamage(2, creature)
        }
    }

    val Bear = card("Bear") {
        manaCost = "{0}"
        typeLine = "Creature — Bear"
        power = 2; toughness = 2
    }

    // Same shape as Watcher but watches combat damage. Used for the combat-damage and
    // deathtouch tests below — Fall of Cair Andros itself is noncombat-only, but the engine's
    // combat-damage path computes excess in CombatDamageManager via a parallel formula and
    // needs its own coverage.
    val CombatWatcher = card("Combat Excess Watcher") {
        manaCost = "{0}"
        typeLine = "Creature — Spirit"
        power = 0; toughness = 3
        triggeredAbility {
            trigger = Triggers.dealsDamage(
                damageType = DamageType.Combat,
                recipient = RecipientFilter.CreatureOpponentControls,
                binding = TriggerBinding.ANY,
                requireExcess = true,
            )
            effect = Effects.Amass(
                DynamicAmount.ContextProperty(ContextPropertyKey.TRIGGER_EXCESS_DAMAGE_AMOUNT),
                "Orc"
            )
        }
    }

    // 3/3 deathtouch attacker. Per CR 120.4a (refs 702.2), a deathtouch source's lethal
    // damage is a flat 1 — any damage past 1 is excess regardless of marked damage.
    val DeathtouchAdder = CardDefinition.creature(
        name = "Deathtouch Adder",
        manaCost = ManaCost.parse("{0}"),
        subtypes = setOf(Subtype("Snake")),
        power = 3,
        toughness = 3,
        oracleText = "Deathtouch",
        keywords = setOf(Keyword.DEATHTOUCH),
    )

    // 0/2 indestructible blocker. Kept around so the combat-damage tests can verify the
    // trigger fires via `CreatureOpponentControls` — that matcher reads the recipient's
    // ControllerComponent from base state, which is stripped when an entity leaves the
    // battlefield, so a blocker that dies from the same damage event silently fails the
    // filter even though the rule (CR 603.10) says the trigger sees last-known info.
    val IndestructibleWall = CardDefinition.creature(
        name = "Indestructible Wall",
        manaCost = ManaCost.parse("{0}"),
        subtypes = setOf(Subtype("Wall")),
        power = 0,
        toughness = 2,
        oracleText = "Defender, indestructible",
        keywords = setOf(Keyword.DEFENDER, Keyword.INDESTRUCTIBLE),
    )

    val WitherGiant = CardDefinition.creature(
        name = "Wither Giant",
        manaCost = ManaCost.parse("{0}"),
        subtypes = setOf(Subtype("Giant")),
        power = 5,
        toughness = 5,
        oracleText = "Wither",
        keywords = setOf(Keyword.WITHER),
    )

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(
            TestCards.all + listOf(Watcher, CombatWatcher, BigBolt, ExactBolt, Bear, DeathtouchAdder, IndestructibleWall, WitherGiant)
        )
        return driver
    }

    fun GameTestDriver.armiesControlledBy(player: EntityId): List<EntityId> {
        val projected = projector.project(state)
        return projected.getBattlefieldControlledBy(player)
            .filter { projected.isCreature(it) && projected.hasSubtype(it, "Army") }
    }

    fun GameTestDriver.plusOneCounters(id: EntityId): Int =
        state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    test("wither damage still reports excess above the creature's pre-damage toughness") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 20))
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val source = driver.putCreatureOnBattlefield(active, "Wither Giant")
        val target = driver.putCreatureOnBattlefield(opponent, "Bear")
        val result = DamageUtils.dealDamageToTarget(driver.state, target, 5, sourceId = source)

        val damage = result.events
            .filterIsInstance<com.wingedsheep.engine.core.DamageDealtEvent>()
            .single { it.targetId == target }
        damage.excessAmount shouldBe 3
        result.state.getEntity(target)?.get<CountersComponent>()
            ?.getCount(CounterType.MINUS_ONE_MINUS_ONE) shouldBe 5
    }

    test("redirected excess wither damage puts only the lethal counters on the creature") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 20))
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val source = driver.putCreatureOnBattlefield(active, "Wither Giant")
        val target = driver.putCreatureOnBattlefield(opponent, "Bear")
        val result = DamageUtils.dealDamageToTarget(
            driver.state, target, 5, sourceId = source, excessToController = true
        )

        result.state.getEntity(target)?.get<CountersComponent>()
            ?.getCount(CounterType.MINUS_ONE_MINUS_ONE) shouldBe 2
        result.state.lifeTotal(opponent) shouldBe 17
    }

    test("3 excess damage to an opponent's 2/2 triggers amass for exactly the excess (Fall of Cair Andros)") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Active controls the Watcher; opponent controls a 2/2 Bear.
        driver.putCreatureOnBattlefield(active, "Excess Watcher")
        val bear = driver.putCreatureOnBattlefield(opponent, "Bear")

        // Cast Big Bolt at the Bear. 5 damage to a 2/2 = 3 excess. Bear dies; trigger fires
        // on the active player's Watcher even though Bear left the battlefield.
        val bolt = driver.putCardInHand(active, "Big Bolt")
        driver.castSpell(active, bolt, listOf(bear))
        driver.bothPass()  // resolve Big Bolt → trigger goes on stack
        driver.bothPass()  // resolve the Amass trigger

        val armies = driver.armiesControlledBy(active)
        armies.size shouldBe 1
        val army = armies.single()
        // X = 3 → Orc Army has three +1/+1 counters (3/3, projected).
        driver.plusOneCounters(army) shouldBe 3
        projector.project(driver.state).getPower(army) shouldBe 3
    }

    test("exact-lethal damage to an opponent's creature does not trigger the excess-damage ability") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(active, "Excess Watcher")
        val bear = driver.putCreatureOnBattlefield(opponent, "Bear")

        // 2 damage to a 2/2 = exactly lethal, 0 excess. The DealsDamageEvent fires, but the
        // requireExcess gate filters out the trigger; no Army should be created.
        val bolt = driver.putCardInHand(active, "Exact Bolt")
        driver.castSpell(active, bolt, listOf(bear))
        driver.bothPass()

        driver.armiesControlledBy(active).size shouldBe 0
    }

    test("excess damage to the controller's own creature does not match the opponent-creature recipient filter") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))
        val active = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(active, "Excess Watcher")
        val ownBear = driver.putCreatureOnBattlefield(active, "Bear")

        // 5 damage to *active's own* 2/2 = 3 excess — but the trigger's recipient is
        // CreatureOpponentControls, so the Watcher must not fire.
        val bolt = driver.putCardInHand(active, "Big Bolt")
        driver.castSpell(active, bolt, listOf(ownBear))
        driver.bothPass()

        driver.armiesControlledBy(active).size shouldBe 0
    }

    test("combat damage past lethal triggers the excess-combat-damage trigger with the right excess") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40))
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // 5/5 attacker vs indestructible 0/2 blocker. CombatWatcher watches combat damage
        // to opponent creatures and amasses for the excess. 5 damage to a 2-toughness
        // blocker → lethalNeeded = 2 → excess = 3. We use an indestructible wall so the
        // blocker survives state-based actions and the CreatureOpponentControls matcher
        // still finds its ControllerComponent at trigger-detection time (combat-damage
        // SBAs run before trigger detection — a separate engine-wide LKI gap).
        driver.putCreatureOnBattlefield(active, "Combat Excess Watcher")
        val attacker = driver.putCreatureOnBattlefield(active, "Force of Nature")
        val blocker = driver.putCreatureOnBattlefield(opponent, "Indestructible Wall")
        driver.removeSummoningSickness(attacker)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(active, listOf(attacker), opponent).isSuccess shouldBe true
        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)
        driver.declareBlockers(opponent, mapOf(blocker to listOf(attacker))).isSuccess shouldBe true

        driver.passPriorityUntil(Step.POSTCOMBAT_MAIN)

        val armies = driver.armiesControlledBy(active)
        armies.size shouldBe 1
        val army = armies.single()
        // X = excess = 5 - 2 = 3 → Orc Army with three +1/+1 counters.
        driver.plusOneCounters(army) shouldBe 3
        projector.project(driver.state).getPower(army) shouldBe 3
    }

    test("deathtouch source collapses lethal to a flat 1 — combat damage of 3 produces excess 2") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40))
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // 3/3 deathtouch attacker vs indestructible 0/2 blocker. CR 120.4a (refs 702.2):
        // with a deathtouch source, any damage greater than 1 is excess — lethalNeeded
        // collapses to a flat 1 regardless of the recipient's toughness or marked damage.
        // So 3 damage → excess 2, not 3 - 2 = 1. Same indestructible-blocker reasoning as
        // the previous test.
        driver.putCreatureOnBattlefield(active, "Combat Excess Watcher")
        val adder = driver.putCreatureOnBattlefield(active, "Deathtouch Adder")
        val blocker = driver.putCreatureOnBattlefield(opponent, "Indestructible Wall")
        driver.removeSummoningSickness(adder)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(active, listOf(adder), opponent).isSuccess shouldBe true
        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)
        driver.declareBlockers(opponent, mapOf(blocker to listOf(adder))).isSuccess shouldBe true

        driver.passPriorityUntil(Step.POSTCOMBAT_MAIN)

        val armies = driver.armiesControlledBy(active)
        armies.size shouldBe 1
        val army = armies.single()
        // X = deathtouch excess = 3 - 1 = 2 → Orc Army with two +1/+1 counters.
        driver.plusOneCounters(army) shouldBe 2
        projector.project(driver.state).getPower(army) shouldBe 2
    }
})

package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.MagmaticGalleon
import com.wingedsheep.mtg.sets.tokens.PredefinedTokens
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Magmatic Galleon (LCI #157) — {3}{R}{R} Artifact — Vehicle, 5/5, Crew 2.
 *
 * - "When this Vehicle enters, it deals 5 damage to target creature an opponent controls."
 * - "Whenever one or more creatures your opponents control are dealt excess noncombat damage,
 *    create a Treasure token."
 *
 * VERIFY focus: the second ability is the Gap 12 excess-damage trigger primitive
 * (`Triggers.dealsDamage(damageType = NonCombat, recipient = CreatureOpponentControls,
 * requireExcess = true)`, ANY binding — same primitive Fall of Cair Andros composes) with
 * `batch = true` for the "one or more creatures" wording, paired with
 * `Effects.CreateTreasure()`. Tests prove: (a) the Galleon's own ETB 5-damage strike that
 * exceeds lethal makes a Treasure, (b) the `requireExcess` gate suppresses the Treasure when the
 * damage is exactly lethal, (c) the ANY-binding trigger also fires for excess noncombat
 * damage from a *different* source while the Galleon sits on the battlefield, and (d) the batch
 * semantics (CR 603.2c): one sweep dealing excess damage to several opposing creatures
 * simultaneously makes exactly one Treasure.
 */
class MagmaticGalleonScenarioTest : FunSpec({

    // Opponent's 2/2 — 5 damage overshoots by 3 (excess).
    val TargetBear = CardDefinition.creature(
        name = "Galleon Test Bear",
        manaCost = ManaCost.parse("{0}"),
        subtypes = setOf(Subtype("Bear")),
        power = 2,
        toughness = 2,
    )

    // Opponent's 0/5 — 5 damage is exactly lethal, no excess.
    val ToughWall = CardDefinition.creature(
        name = "Galleon Test Wall",
        manaCost = ManaCost.parse("{0}"),
        subtypes = setOf(Subtype("Wall")),
        power = 0,
        toughness = 5,
    )

    // {0} sorcery dealing 5 noncombat damage — an external excess source for test (c).
    val BigBolt = card("Galleon Big Bolt") {
        manaCost = "{0}"
        typeLine = "Sorcery"
        oracleText = "This deals 5 damage to target creature."
        spell {
            val creature = target("target creature", Targets.Creature)
            effect = Effects.DealDamage(5, creature)
        }
    }

    // {0} Pyroclasm-at-5: simultaneous noncombat damage to every creature — the batch case (d).
    val BigSweep = card("Galleon Big Sweep") {
        manaCost = "{0}"
        typeLine = "Sorcery"
        oracleText = "This deals 5 damage to each creature."
        spell {
            effect = Effects.ForEachInGroup(
                GroupFilter(GameObjectFilter.Creature),
                DealDamageEffect(5, EffectTarget.Self)
            )
        }
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(
            TestCards.all + listOf(MagmaticGalleon, PredefinedTokens.Treasure, TargetBear, ToughWall, BigBolt, BigSweep)
        )
        return driver
    }

    fun GameTestDriver.treasureCount(): Int =
        state.getBattlefield()
            .mapNotNull { state.getEntity(it)?.get<CardComponent>() }
            .count { it.name == "Treasure" }

    fun GameTestDriver.drainStack() {
        var guard = 0
        while (state.stack.isNotEmpty() && guard++ < 20) bothPass()
    }

    test("ETB 5 damage to an opponent's 2/2 deals 3 excess noncombat damage and makes one Treasure") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val bear = driver.putCreatureOnBattlefield(opponent, "Galleon Test Bear")
        val galleon = driver.putCardInHand(active, "Magmatic Galleon")
        driver.giveMana(active, Color.RED, 5) // {3}{R}{R}
        driver.castSpell(active, galleon, emptyList())

        // Resolve the Galleon; its ETB trigger goes on the stack and asks for a target.
        var guard = 0
        while (driver.pendingDecision == null && guard++ < 20) driver.bothPass()
        driver.submitTargetSelection(active, listOf(bear)).isSuccess shouldBe true

        // Resolve the ETB damage (kills the bear with 3 excess) and the resulting Treasure trigger.
        driver.drainStack()

        driver.findPermanent(opponent, "Galleon Test Bear") shouldBe null
        driver.treasureCount() shouldBe 1
    }

    test("ETB 5 damage to an opponent's 0/5 is exactly lethal — no excess, no Treasure") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val wall = driver.putCreatureOnBattlefield(opponent, "Galleon Test Wall")
        val galleon = driver.putCardInHand(active, "Magmatic Galleon")
        driver.giveMana(active, Color.RED, 5)
        driver.castSpell(active, galleon, emptyList())

        var guard = 0
        while (driver.pendingDecision == null && guard++ < 20) driver.bothPass()
        driver.submitTargetSelection(active, listOf(wall)).isSuccess shouldBe true
        driver.drainStack()

        // 5 damage to a 5-toughness creature = exactly lethal, 0 excess → requireExcess gate blocks.
        driver.findPermanent(opponent, "Galleon Test Wall") shouldBe null
        driver.treasureCount() shouldBe 0
    }

    test("batch: simultaneous excess noncombat damage to several opposing creatures makes exactly one Treasure") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(active, "Magmatic Galleon")
        // Two 2/2s take 3 excess each; the 0/5 takes exactly lethal (no excess). "One or more
        // creatures ... are dealt excess noncombat damage" is a batch trigger (CR 603.2c): one
        // simultaneous sweep = one event = ONE Treasure, not one per excess-damaged creature.
        driver.putCreatureOnBattlefield(opponent, "Galleon Test Bear")
        driver.putCreatureOnBattlefield(opponent, "Galleon Test Bear")
        driver.putCreatureOnBattlefield(opponent, "Galleon Test Wall")

        val sweep = driver.putCardInHand(active, "Galleon Big Sweep")
        driver.castSpell(active, sweep, emptyList())
        driver.drainStack()

        driver.findPermanent(opponent, "Galleon Test Bear") shouldBe null
        driver.findPermanent(opponent, "Galleon Test Wall") shouldBe null
        driver.treasureCount() shouldBe 1
    }

    test("ANY-binding: excess noncombat damage from another source to an opponent's creature makes a Treasure") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Galleon already on the battlefield (placed directly — no ETB target needed here).
        driver.putPermanentOnBattlefield(active, "Magmatic Galleon")
        val bear = driver.putCreatureOnBattlefield(opponent, "Galleon Test Bear")

        // A separate spell deals 5 to the opponent's 2/2 = 3 excess → Galleon's trigger fires.
        val bolt = driver.putCardInHand(active, "Galleon Big Bolt")
        driver.castSpell(active, bolt, listOf(bear))
        driver.drainStack()

        driver.findPermanent(opponent, "Galleon Test Bear") shouldBe null
        driver.treasureCount() shouldBe 1
    }
})

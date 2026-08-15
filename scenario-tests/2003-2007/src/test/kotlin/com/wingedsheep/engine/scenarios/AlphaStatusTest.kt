package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantDynamicStatsEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityReference
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Alpha Status: "Enchanted creature gets +2/+2 for each OTHER creature on the battlefield that
 * shares a creature type with it."
 *
 * Exercises the composition that replaced the bespoke `DynamicAmount.CreaturesSharingTypeWithEntity`
 * variant: `AggregateBattlefield(Player.Each, Creature.sharingCreatureTypeWith(AffectedEntity),
 * excludeSelf = true)`. Two engine behaviours are under test:
 *   - `EntityReference.AffectedEntity` resolves inside an `AggregateBattlefield` filter predicate
 *     during projection (so the filter knows which creature "it" refers to).
 *   - `excludeSelf` excludes the *affected* (enchanted) creature, not the Aura source — otherwise
 *     the enchanted creature would count itself (it trivially shares all its own types).
 */
class AlphaStatusTest : FunSpec({

    val projector = StateProjector()

    // Mirror of the real Alpha Status static ability, defined inline because rules-engine tests
    // cannot depend on mtg-sets.
    val AlphaStatus = card("Alpha Status") {
        manaCost = "{2}{G}"
        typeLine = "Enchantment — Aura"
        auraTarget = Targets.Creature

        staticAbility {
            val sharedTypeCount = DynamicAmount.AggregateBattlefield(
                player = Player.Each,
                filter = GameObjectFilter.Creature.sharingCreatureTypeWith(EntityReference.AffectedEntity),
                excludeSelf = true,
            )
            ability = GrantDynamicStatsEffect(
                filter = GroupFilter.attachedCreature(),
                powerBonus = DynamicAmount.Multiply(sharedTypeCount, 2),
                toughnessBonus = DynamicAmount.Multiply(sharedTypeCount, 2),
            )
        }
    }

    fun goblin(name: String) = CardDefinition.creature(
        name = name,
        manaCost = ManaCost.parse("{1}{R}"),
        subtypes = setOf(Subtype("Goblin")),
        power = 2,
        toughness = 2,
    )

    val Elf = CardDefinition.creature(
        name = "Test Elf",
        manaCost = ManaCost.parse("{G}"),
        subtypes = setOf(Subtype("Elf")),
        power = 1,
        toughness = 1,
    )

    val Typeless = CardDefinition.creature(
        name = "Test Typeless",
        manaCost = ManaCost.parse("{1}"),
        subtypes = emptySet(),
        power = 3,
        toughness = 3,
    )

    fun GameTestDriver.attachAura(auraId: EntityId, targetId: EntityId) {
        replaceState(state.updateEntity(auraId) { container ->
            container.with(AttachedToComponent(targetId))
        })
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(AlphaStatus)
        driver.registerCard(goblin("Test Goblin A"))
        driver.registerCard(goblin("Test Goblin B"))
        driver.registerCard(goblin("Test Goblin C"))
        driver.registerCard(Elf)
        driver.registerCard(Typeless)
        return driver
    }

    test("counts other creatures sharing a type, excluding the enchanted creature and unrelated types") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40))
        val p1 = driver.activePlayer!!
        val p2 = driver.getOpponent(p1)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Enchanted Goblin (2/2) + two more Goblins (one on each side) + an Elf + a typeless creature.
        val enchanted = driver.putCreatureOnBattlefield(p1, "Test Goblin A")
        driver.putCreatureOnBattlefield(p1, "Test Goblin B")
        driver.putCreatureOnBattlefield(p2, "Test Goblin C") // shared types count across all players
        driver.putCreatureOnBattlefield(p1, "Test Elf")
        driver.putCreatureOnBattlefield(p1, "Test Typeless")

        val aura = driver.putPermanentOnBattlefield(p1, "Alpha Status")
        driver.attachAura(aura, enchanted)

        // 2 OTHER Goblins share the type → +2/+2 each = +4/+4 on a 2/2 base → 6/6.
        // The enchanted Goblin itself, the Elf, and the typeless creature are not counted.
        val projected = projector.project(driver.state)
        projected.getPower(enchanted) shouldBe 6
        projected.getToughness(enchanted) shouldBe 6
    }

    test("enchanted creature with no creature subtypes gets no bonus") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40))
        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val enchanted = driver.putCreatureOnBattlefield(p1, "Test Typeless")
        driver.putCreatureOnBattlefield(p1, "Test Goblin A")
        driver.putCreatureOnBattlefield(p1, "Test Goblin B")

        val aura = driver.putPermanentOnBattlefield(p1, "Alpha Status")
        driver.attachAura(aura, enchanted)

        // Shares no creature type with anything → +0/+0, stays 3/3.
        val projected = projector.project(driver.state)
        projected.getPower(enchanted) shouldBe 3
        projected.getToughness(enchanted) shouldBe 3
    }
})

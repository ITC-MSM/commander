package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.state.components.battlefield.NotedCreatureTypesComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe

/**
 * Tests for [com.wingedsheep.sdk.scripting.effects.NoteCreatureTypeEffect] and the per-permanent
 * [NotedCreatureTypesComponent] it populates. The realistic shape mirrors Long List of the Ents
 * (LTR): a permanent with an activated ability that "notes" a creature type, dedup'd against the
 * source's previously-noted types, and persists on the source.
 *
 * Engine guarantees under test:
 *  1. The first activation populates `NotedCreatureTypesComponent.types`.
 *  2. A subsequent activation on the SAME source excludes the previously-noted type from the
 *     decision's option list and accumulates the new pick.
 *  3. Two different source permanents keep independent noted sets (component is per-source,
 *     not per-controller or global).
 *  4. The component disappears with the source when it leaves the battlefield, and a permanent
 *     that re-enters starts with a fresh (empty) noted set (CR 400.7 — new object, no memory).
 */
class NoteCreatureTypeEffectTest : FunSpec({

    // 0-mana enchantment with an activated ability that notes a creature type.
    // Activate cost is `{0}, {T}` so we can fire it multiple times per turn via untap-between-tests.
    val NoteTester = card("Note Tester") {
        manaCost = "{0}"
        typeLine = "Enchantment"
        oracleText = "{T}: Note a creature type that hasn't been noted for this enchantment."
        activatedAbility {
            cost = Costs.Tap
            effect = Effects.NoteCreatureType("notedType")
        }
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(NoteTester))
        return driver
    }

    val abilityId = NoteTester.activatedAbilities.first().id

    test("First activation populates NotedCreatureTypesComponent and presents every creature type") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40))
        val active = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val source = driver.putPermanentOnBattlefield(active, "Note Tester")
        // No summoning-sickness on enchantments, but be safe.
        driver.state.getEntity(source)
            ?.get<NotedCreatureTypesComponent>() shouldBe null

        driver.submit(ActivateAbility(playerId = active, sourceId = source, abilityId = abilityId))
        driver.bothPass()

        val decision = driver.pendingDecision as ChooseOptionDecision
        // Sanity: the source's noted set was empty, so the option list is the full creature-type set.
        decision.options.size shouldBe com.wingedsheep.sdk.core.Subtype.ALL_CREATURE_TYPES.size
        val elfIndex = decision.options.indexOf("Elf")
        driver.submitDecision(active, OptionChosenResponse(decision.id, elfIndex))

        driver.pendingDecision shouldBe null
        driver.state.getEntity(source)
            ?.get<NotedCreatureTypesComponent>()
            ?.types shouldBe setOf("Elf")
    }

    test("Second activation on the same source excludes already-noted types") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40))
        val active = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val source = driver.putPermanentOnBattlefield(active, "Note Tester")

        // First activation — note Elf.
        driver.submit(ActivateAbility(playerId = active, sourceId = source, abilityId = abilityId))
        driver.bothPass()
        var decision = driver.pendingDecision as ChooseOptionDecision
        driver.submitDecision(active, OptionChosenResponse(decision.id, decision.options.indexOf("Elf")))

        // Untap the source so it can be tapped again.
        driver.state.updateEntity(source) {
            it.without<com.wingedsheep.engine.state.components.battlefield.TappedComponent>()
        }.let { driver.replaceState(it) }

        // Second activation — Elf must be absent from the option list.
        driver.submit(ActivateAbility(playerId = active, sourceId = source, abilityId = abilityId))
        driver.bothPass()
        decision = driver.pendingDecision as ChooseOptionDecision
        decision.options shouldNotContain "Elf"
        // Pick a different type.
        val goblinIndex = decision.options.indexOf("Goblin")
        driver.submitDecision(active, OptionChosenResponse(decision.id, goblinIndex))

        driver.state.getEntity(source)
            ?.get<NotedCreatureTypesComponent>()
            ?.types
            ?.shouldContainExactly(setOf("Elf", "Goblin"))
    }

    test("Two source permanents keep independent noted sets") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40))
        val active = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val sourceA = driver.putPermanentOnBattlefield(active, "Note Tester")
        val sourceB = driver.putPermanentOnBattlefield(active, "Note Tester")

        driver.submit(ActivateAbility(playerId = active, sourceId = sourceA, abilityId = abilityId))
        driver.bothPass()
        var decision = driver.pendingDecision as ChooseOptionDecision
        driver.submitDecision(active, OptionChosenResponse(decision.id, decision.options.indexOf("Elf")))

        driver.submit(ActivateAbility(playerId = active, sourceId = sourceB, abilityId = abilityId))
        driver.bothPass()
        decision = driver.pendingDecision as ChooseOptionDecision
        // sourceB has no notes yet — Elf is still available on it.
        decision.options shouldContainExactly com.wingedsheep.sdk.core.Subtype.ALL_CREATURE_TYPES
        driver.submitDecision(active, OptionChosenResponse(decision.id, decision.options.indexOf("Elf")))

        driver.state.getEntity(sourceA)
            ?.get<NotedCreatureTypesComponent>()?.types shouldBe setOf("Elf")
        driver.state.getEntity(sourceB)
            ?.get<NotedCreatureTypesComponent>()?.types shouldBe setOf("Elf")
    }

    test("Noted state disappears when the source leaves the battlefield") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40))
        val active = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val source = driver.putPermanentOnBattlefield(active, "Note Tester")
        driver.submit(ActivateAbility(playerId = active, sourceId = source, abilityId = abilityId))
        driver.bothPass()
        val decision = driver.pendingDecision as ChooseOptionDecision
        driver.submitDecision(active, OptionChosenResponse(decision.id, decision.options.indexOf("Elf")))
        driver.state.getEntity(source)
            ?.get<NotedCreatureTypesComponent>()?.types shouldBe setOf("Elf")

        // Move the source off the battlefield to its graveyard, then back onto the battlefield as a
        // fresh object. Per CR 400.7 the returning permanent has no memory of its previous existence,
        // so its noted set must start empty again — Elf must be selectable once more.
        val moved = com.wingedsheep.engine.handlers.effects.ZoneMovementUtils.moveCardToZone(
            driver.zones,
            driver.state, source, Zone.GRAVEYARD
        )
        driver.replaceState(moved.state)
        driver.state.getBattlefield().contains(source) shouldBe false

        val reentered = driver.putPermanentOnBattlefield(active, "Note Tester")
        driver.state.getEntity(reentered)
            ?.get<NotedCreatureTypesComponent>() shouldBe null

        driver.submit(ActivateAbility(playerId = active, sourceId = reentered, abilityId = abilityId))
        driver.bothPass()
        val freshDecision = driver.pendingDecision as ChooseOptionDecision
        // Fresh object — Elf is available again (the old noted set did not carry over).
        freshDecision.options shouldContain "Elf"
        driver.submitDecision(active, OptionChosenResponse(freshDecision.id, freshDecision.options.indexOf("Elf")))
        driver.state.getEntity(reentered)
            ?.get<NotedCreatureTypesComponent>()?.types shouldBe setOf("Elf")
    }
})

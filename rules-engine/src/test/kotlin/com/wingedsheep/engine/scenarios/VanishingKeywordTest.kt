package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.mechanics.layers.ActiveFloatingEffect
import com.wingedsheep.engine.mechanics.layers.FloatingEffectData
import com.wingedsheep.engine.mechanics.layers.Layer
import com.wingedsheep.engine.mechanics.layers.SerializableModification
import com.wingedsheep.engine.mechanics.layers.addFloatingEffects
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.KeywordAbility
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

/**
 * Mechanic-level tests for Vanishing N (CR 702.62).
 *
 * A vanishing card declares one keyword ability; the engine supplies all three printed abilities
 * from [com.wingedsheep.sdk.scripting.Vanishing] — the enters-with-N-time-counters replacement
 * (702.62a) at the entry seam, and the upkeep countdown (702.62b) plus the last-counter sacrifice
 * (702.62c) as keyword-derived triggers.
 *
 * Three things are worth pinning beyond the happy path:
 *  - the countdown is **yours**, not every upkeep;
 *  - the sacrifice fires on the removal that empties the counters **whatever removed them**, which
 *    is why 702.62b and 702.62c are two abilities here rather than suspend's single fused
 *    countdown;
 *  - the triggers are derived from the **projected** keyword, so a creature that merely *gains*
 *    vanishing counts down too.
 */
class VanishingKeywordTest : FunSpec({

    val vanishingBear = card("Vanishing Bear") {
        manaCost = "{1}{G}"
        typeLine = "Creature — Bear"
        power = 2
        toughness = 2
        oracleText = "Vanishing 2"
        keywordAbility(KeywordAbility.vanishing(2))
    }

    /** A plain 2/2 with no vanishing, used as the "gains vanishing" subject. */
    val plainBear = card("Plain Bear") {
        manaCost = "{1}{G}"
        typeLine = "Creature — Bear"
        power = 2
        toughness = 2
    }

    /** Removes a time counter at instant speed — the off-turn drain CR 702.62c must notice. */
    val timeSiphon = card("Time Siphon") {
        manaCost = "{U}"
        typeLine = "Instant"
        oracleText = "Remove a time counter from target creature."
        spell {
            val victim = target("target creature", Targets.Creature)
            effect = Effects.RemoveCounters(Counters.TIME, 1, victim)
        }
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(vanishingBear, plainBear, timeSiphon))
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40))
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun timeCounters(driver: GameTestDriver, perm: EntityId): Int =
        driver.state.getEntity(perm)?.get<CountersComponent>()?.getCount(CounterType.TIME) ?: 0

    /** Cast [cardName] from hand with mana granted, and resolve it. */
    fun castAndResolve(driver: GameTestDriver, player: EntityId, cardName: String, targets: List<EntityId> = emptyList()) {
        driver.giveMana(player, Color.GREEN, 3)
        driver.giveMana(player, Color.BLUE, 3)
        val cardId = driver.putCardInHand(player, cardName)
        val result = driver.submit(
            CastSpell(player, cardId, targets.map { ChosenTarget.Permanent(it) })
        )
        if (!result.isSuccess) throw AssertionError("cast of $cardName failed: ${result.error}")
        driver.bothPass()
    }

    /** Advance to [owner]'s next upkeep, resolving the countdown trigger that fires there. */
    fun resolveNextOwnerUpkeep(driver: GameTestDriver, owner: EntityId) {
        do {
            driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
            driver.passPriorityUntil(Step.UPKEEP)
        } while (driver.activePlayer != owner)
        driver.bothPass()
    }

    test("a declared Vanishing N enters the battlefield with N time counters") {
        val driver = createDriver()
        val player = driver.activePlayer!!

        castAndResolve(driver, player, "Vanishing Bear")

        val bear = driver.findPermanent(player, "Vanishing Bear")!!
        timeCounters(driver, bear) shouldBe 2
    }

    test("the countdown is the controller's upkeep only, and the last removal sacrifices it") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        castAndResolve(driver, player, "Vanishing Bear")
        val bear = driver.findPermanent(player, "Vanishing Bear")!!

        // The opponent's upkeep comes first and must not count anything down.
        driver.passPriorityUntil(Step.UPKEEP)
        (driver.activePlayer == player) shouldBe false
        timeCounters(driver, bear) shouldBe 2

        resolveNextOwnerUpkeep(driver, player)
        timeCounters(driver, bear) shouldBe 1
        (driver.findPermanent(player, "Vanishing Bear") != null) shouldBe true

        // The countdown and the sacrifice are two abilities, so the emptying upkeep needs two
        // resolutions: the countdown removes the last counter, which queues the sacrifice trigger.
        resolveNextOwnerUpkeep(driver, player)
        driver.bothPass()
        driver.findPermanent(player, "Vanishing Bear").shouldBeNull()
        driver.getGraveyardCardNames(player).contains("Vanishing Bear") shouldBe true
    }

    test("draining the last counter off-turn sacrifices it immediately, not at the next upkeep") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        castAndResolve(driver, player, "Vanishing Bear")
        val bear = driver.findPermanent(player, "Vanishing Bear")!!

        // Take it to one counter the ordinary way, then strip the last one with an instant.
        resolveNextOwnerUpkeep(driver, player)
        timeCounters(driver, bear) shouldBe 1

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        castAndResolve(driver, player, "Time Siphon", targets = listOf(bear))
        driver.bothPass() // resolve the sacrifice trigger

        driver.findPermanent(player, "Vanishing Bear").shouldBeNull()
    }

    test("a creature that merely gains vanishing counts down too") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        castAndResolve(driver, player, "Plain Bear")
        val bear = driver.findPermanent(player, "Plain Bear")!!

        // Grant the keyword and hand it counters — the shape a "it gains vanishing 2" effect
        // produces. The triggers are derived from the projected keyword, so this is enough.
        var s = driver.state.updateEntity(bear) { container ->
            container.with(CountersComponent().withAdded(CounterType.TIME, 1))
        }
        s = s.addFloatingEffects(
            listOf(
                ActiveFloatingEffect(
                    id = EntityId.generate(),
                    effect = FloatingEffectData(
                        layer = Layer.ABILITY,
                        modification = SerializableModification.GrantKeyword(Keyword.VANISHING.name),
                        affectedEntities = setOf(bear),
                    ),
                    duration = Duration.Permanent,
                    sourceId = bear,
                    sourceName = "Plain Bear",
                    controllerId = player,
                    timestamp = s.timestamp,
                )
            )
        )
        driver.replaceState(s)
        driver.state.projectedState.hasKeyword(bear, Keyword.VANISHING) shouldBe true

        resolveNextOwnerUpkeep(driver, player)
        driver.bothPass() // resolve the sacrifice trigger the emptying removal queued

        driver.findPermanent(player, "Plain Bear").shouldBeNull()
    }

    test("Vanishing.printedCount sums multiple printed instances (CR 702.62d)") {
        val doubled = card("Doubly Vanishing Bear") {
            manaCost = "{1}{G}"
            typeLine = "Creature — Bear"
            power = 2
            toughness = 2
            keywordAbilities(KeywordAbility.vanishing(2), KeywordAbility.vanishing(3))
        }
        com.wingedsheep.sdk.scripting.Vanishing.printedCount(doubled) shouldBe 5
        com.wingedsheep.sdk.scripting.Vanishing.printedCount(plainBear).shouldBeNull()
    }
})

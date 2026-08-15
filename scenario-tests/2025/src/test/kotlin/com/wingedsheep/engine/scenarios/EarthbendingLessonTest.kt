package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.tla.AvatarTheLastAirbenderSet
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.MoveToZoneEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for Earthbending Lesson (TLA), and by extension the Earthbend keyword action.
 *
 * Earthbend N — Target land you control becomes a 0/0 land creature with haste,
 * put N +1/+1 counters on it. When it dies or is exiled, return it to the
 * battlefield tapped under your control.
 *
 * The mechanic is intentionally NOT modeled as a special engine keyword — it
 * composes from existing primitives (AnimateLand + GrantKeyword(haste) +
 * AddCounters + two granted self-triggers). These tests exercise that the
 * granted self-triggers ride the engine's existing trigger pipeline.
 */
class EarthbendingLessonTest : FunSpec({

    val projector = StateProjector()

    // Test-only sorcery that exiles a target creature, used to drive the
    // "exiled from battlefield" branch of the granted self-trigger.
    val exileTargetCreature = CardDefinition.sorcery(
        name = "Exile Target Creature",
        manaCost = ManaCost.parse("{1}{W}"),
        oracleText = "Exile target creature.",
        script = CardScript.spell(
            effect = MoveToZoneEffect(EffectTarget.ContextTarget(0), Zone.EXILE),
            TargetObject(filter = TargetFilter(GameObjectFilter.Creature), id = "target")
        )
    )

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + AvatarTheLastAirbenderSet.cards + listOf(exileTargetCreature))
        return driver
    }

    test("animates target land to a 4/4 creature-land with haste") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)

        val you = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val forest = driver.putLandOnBattlefield(you, "Forest")
        val lesson = driver.putCardInHand(you, "Earthbending Lesson")
        driver.giveMana(you, Color.GREEN, 4)

        driver.castSpell(you, lesson, listOf(forest)).isSuccess shouldBe true
        driver.bothPass()  // resolve the spell

        val projected = projector.project(driver.state)
        projected.hasType(forest, "LAND") shouldBe true
        projected.hasType(forest, "CREATURE") shouldBe true
        projected.getPower(forest) shouldBe 4
        projected.getToughness(forest) shouldBe 4
        projected.hasKeyword(forest, Keyword.HASTE) shouldBe true
    }

    test("dies — returns to the battlefield tapped, no longer a creature") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40, "Swamp" to 40), startingLife = 20)

        val you = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val forest = driver.putLandOnBattlefield(you, "Forest")
        val lesson = driver.putCardInHand(you, "Earthbending Lesson")
        driver.giveMana(you, Color.GREEN, 4)
        driver.castSpell(you, lesson, listOf(forest)).isSuccess shouldBe true
        driver.bothPass()

        // Earthbended land is a 4/4 creature — Doom Blade can target it (it's nonblack).
        val doomBlade = driver.putCardInHand(you, "Doom Blade")
        driver.giveMana(you, Color.BLACK, 2)
        driver.castSpell(you, doomBlade, listOf(forest)).isSuccess shouldBe true
        driver.bothPass()  // resolve Doom Blade — Forest dies, granted "when this dies" trigger goes on stack
        driver.bothPass()  // resolve the granted return trigger

        // Land is back on the battlefield…
        driver.state.getBattlefield().contains(forest) shouldBe true
        // …tapped…
        driver.isTapped(forest) shouldBe true

        // …and no longer a creature: floating effects are tied to the prior entity-on-battlefield
        // instance and don't follow the card across the zone change. The Forest is just a Forest.
        val projected = projector.project(driver.state)
        projected.hasType(forest, "LAND") shouldBe true
        projected.hasType(forest, "CREATURE") shouldBe false
        projected.hasKeyword(forest, Keyword.HASTE) shouldBe false
    }

    test("exiled — returns to the battlefield tapped") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40, "Plains" to 40), startingLife = 20)

        val you = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val forest = driver.putLandOnBattlefield(you, "Forest")
        val lesson = driver.putCardInHand(you, "Earthbending Lesson")
        driver.giveMana(you, Color.GREEN, 4)
        driver.castSpell(you, lesson, listOf(forest)).isSuccess shouldBe true
        driver.bothPass()

        val exile = driver.putCardInHand(you, "Exile Target Creature")
        driver.giveMana(you, Color.WHITE, 2)
        driver.castSpell(you, exile, listOf(forest)).isSuccess shouldBe true
        driver.bothPass()  // resolve exile — Forest moves to exile, granted "when this is exiled" trigger goes on stack
        driver.bothPass()  // resolve the granted return trigger

        driver.state.getBattlefield().contains(forest) shouldBe true
        driver.isTapped(forest) shouldBe true
    }

    test("earthbended twice — client view shows a single Granted Ability badge") {
        // Earthbending the same land twice grants the identical "return it tapped" self-trigger
        // twice, so state holds two grant entries (each with its own AbilityId). To the player
        // that's one ability, so the client view must collapse them into a single badge rather
        // than stacking two duplicate "Granted Ability" tiles. See ClientStateTransformer dedup.
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)

        val you = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val forest = driver.putLandOnBattlefield(you, "Forest")

        val lesson1 = driver.putCardInHand(you, "Earthbending Lesson")
        driver.giveMana(you, Color.GREEN, 4)
        driver.castSpell(you, lesson1, listOf(forest)).isSuccess shouldBe true
        driver.bothPass()

        val lesson2 = driver.putCardInHand(you, "Earthbending Lesson")
        driver.giveMana(you, Color.GREEN, 4)
        driver.castSpell(you, lesson2, listOf(forest)).isSuccess shouldBe true
        driver.bothPass()

        // Premise of the bug: two grant entries now ride the same land.
        driver.state.grantedTriggeredAbilities.count { it.entityId == forest } shouldBe 2

        // …but the client view collapses them to one badge.
        val transformer = com.wingedsheep.engine.view.ClientStateTransformer(driver.cardRegistry)
        val card = transformer.transform(driver.state, you).cards[forest]!!
        card.activeEffects.count { it.icon == "granted-ability" } shouldBe 1
    }

    test("Rule 400.7 — granted abilities are dropped when the land re-enters the battlefield") {
        // The earthbended land carries a granted "When this dies or is exiled, return…" trigger
        // while it's animated. Once the granted trigger fires and the land returns to the
        // battlefield, the new instance is a fresh object (CR 400.7) — the grant should not
        // persist, otherwise dying twice would return it twice and the FE would show a stale
        // "Granted Ability" badge on a non-creature Forest.
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40, "Swamp" to 40), startingLife = 20)

        val you = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val forest = driver.putLandOnBattlefield(you, "Forest")
        val lesson = driver.putCardInHand(you, "Earthbending Lesson")
        driver.giveMana(you, Color.GREEN, 4)
        driver.castSpell(you, lesson, listOf(forest)).isSuccess shouldBe true
        driver.bothPass()

        // While earthbended, the grant exists.
        driver.state.grantedTriggeredAbilities.any { it.entityId == forest } shouldBe true

        // Kill the earthbended land — return trigger fires, land comes back tapped.
        val doomBlade = driver.putCardInHand(you, "Doom Blade")
        driver.giveMana(you, Color.BLACK, 2)
        driver.castSpell(you, doomBlade, listOf(forest)).isSuccess shouldBe true
        driver.bothPass()  // Doom Blade resolves
        driver.bothPass()  // return trigger resolves

        driver.state.getBattlefield().contains(forest) shouldBe true
        // Rule 400.7: the returned Forest is a new object — no leftover grants.
        driver.state.grantedTriggeredAbilities.any { it.entityId == forest } shouldBe false
    }
})

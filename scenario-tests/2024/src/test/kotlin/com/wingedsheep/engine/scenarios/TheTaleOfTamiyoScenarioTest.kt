package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dsk.cards.TheTaleOfTamiyo
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.RepeatCondition
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.shouldBe

/**
 * Scenarios for The Tale of Tamiyo (DSK #75) — exercises the new engine building blocks it needs:
 *
 *   - chapters I–III: [Effects.RepeatWhile] over a mill body whose repeat/draw are gated on
 *     [Conditions.CollectionSharesCardType] (mill two; if they share a card type, draw and repeat).
 *   - chapter IV: [Effects.CopyCollectionIntoCollection] + [Effects.CastAnyNumberFromCollection]
 *     (exile any number of targets, copy them, may cast any number of the copies *paying* costs).
 *
 * Chapter IV's pipeline is tested in isolation through a synthetic sorcery — driving the real Saga to
 * its fourth chapter would require three intervening upkeeps whose own mill loops perturb the
 * library/graveyard; the sorcery exercises the identical effect chain deterministically.
 */
class TheTaleOfTamiyoScenarioTest : FunSpec({

    val bear = card("Tamiyo Test Bear") {
        manaCost = "{1}{G}"
        typeLine = "Creature — Bear"
        power = 2
        toughness = 2
    }
    val bolt = card("Tamiyo Test Bolt") {
        manaCost = "{R}"
        typeLine = "Instant"
        spell {
            effect = Effects.DealDamage(3, EffectTarget.PlayerRef(Player.EachOpponent))
        }
    }

    // Synthetic stand-in for chapter IV — same effect chain (exile targets, copy them, may cast any
    // number of the copies paying their costs).
    val tamiyoChapterFour = card("Tamiyo IV Probe") {
        manaCost = "{U}"
        typeLine = "Sorcery"
        spell {
            target(
                "any number of target instant or sorcery cards from your graveyard",
                TargetObject(
                    unlimited = true,
                    filter = TargetFilter(
                        GameObjectFilter.InstantOrSorcery.ownedByYou(),
                        zone = Zone.GRAVEYARD,
                    ),
                )
            )
            effect = Effects.Composite(
                ForEachTargetEffect(listOf(Effects.Move(EffectTarget.ContextTarget(0), Zone.EXILE))),
                GatherCardsEffect(source = CardSource.ChosenTargets, storeAs = "tamiyoExiled"),
                Effects.CopyCollectionIntoCollection(from = "tamiyoExiled", storeAs = "tamiyoCopies"),
                Effects.CastAnyNumberFromCollection(from = "tamiyoCopies"),
            )
        }
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(TheTaleOfTamiyo, tamiyoChapterFour, bear, bolt))
        return driver
    }

    fun GameTestDriver.resolveStack() {
        var guard = 0
        while (state.stack.isNotEmpty() && guard < 80) {
            if (state.pendingDecision != null) autoResolveDecision() else bothPass()
            guard++
        }
    }

    test("Chapter I mills two and, while the milled pair shares a card type, draws and repeats") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val controller = driver.activePlayer!!

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Rig the library top→bottom: Bear, Bear, Mountain, Bolt, Mountain.
        //   pass 1 mills Bear+Bear (share Creature) → draw the Mountain, repeat.
        //   pass 2 mills Bolt+Mountain (Instant vs Land, no shared type) → stop.
        // putCardOnTopOfLibrary pushes onto the top, so push bottom-of-segment first.
        driver.putCardOnTopOfLibrary(controller, "Mountain")            // milled in pass 2 (bottom)
        driver.putCardOnTopOfLibrary(controller, "Tamiyo Test Bolt")    // milled in pass 2
        driver.putCardOnTopOfLibrary(controller, "Mountain")            // drawn after pass 1
        driver.putCardOnTopOfLibrary(controller, "Tamiyo Test Bear")    // milled in pass 1
        driver.putCardOnTopOfLibrary(controller, "Tamiyo Test Bear")    // milled in pass 1 (top)

        val handBefore = driver.getHandSize(controller)
        val saga = driver.putCardInHand(controller, "The Tale of Tamiyo")
        driver.giveMana(controller, Color.BLUE, 3) // {2}{U}
        driver.castSpell(controller, saga)
        driver.resolveStack() // Saga enters → lore 1 → chapter I resolves the mill loop

        driver.getGraveyardCardNames(controller).shouldContainExactlyInAnyOrder(
            "Tamiyo Test Bear", "Tamiyo Test Bear", "Tamiyo Test Bolt", "Mountain"
        )
        // The Saga was added then cast away (net 0 vs the baseline); the loop drew exactly one card.
        driver.getHandSize(controller) shouldBe handBefore + 1
    }

    test("Chapter I stops immediately when the first milled pair shares no card type") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val controller = driver.activePlayer!!

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Top two: Bolt (Instant) + Mountain (Land) — no shared card type → no draw, no repeat.
        driver.putCardOnTopOfLibrary(controller, "Mountain")
        driver.putCardOnTopOfLibrary(controller, "Tamiyo Test Bolt")

        val handBefore = driver.getHandSize(controller)
        val saga = driver.putCardInHand(controller, "The Tale of Tamiyo")
        driver.giveMana(controller, Color.BLUE, 3) // {2}{U}
        driver.castSpell(controller, saga)
        driver.resolveStack()

        driver.getGraveyardCardNames(controller).shouldContainExactlyInAnyOrder(
            "Tamiyo Test Bolt", "Mountain"
        )
        // The Saga was added then cast away (net 0 vs the baseline); nothing was drawn.
        driver.getHandSize(controller) shouldBe handBefore
    }

    test("Chapter IV exiles the targets, copies them, and casts a copy paying its cost") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val controller = driver.activePlayer!!
        val opponent = driver.state.turnOrder.first { it != controller }

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val boltId = driver.putCardInGraveyard(controller, "Tamiyo Test Bolt")
        driver.giveMana(controller, Color.BLUE, 1) // {U} for the sorcery
        driver.giveMana(controller, Color.RED, 1)  // {R} for the copied Bolt
        val probe = driver.putCardInHand(controller, "Tamiyo IV Probe")
        driver.castSpellWithTargets(
            controller, probe,
            listOf(ChosenTarget.Card(cardId = boltId, ownerId = controller, zone = Zone.GRAVEYARD)),
        )

        var pickedCopy = false
        var guard = 0
        while ((driver.state.stack.isNotEmpty() || driver.state.pendingDecision != null) && guard < 80) {
            val decision = driver.state.pendingDecision
            when {
                decision is ChooseTargetsDecision -> driver.submitTargetSelection(controller, listOf(boltId))
                decision is SelectCardsDecision && !pickedCopy -> {
                    pickedCopy = true
                    driver.submitCardSelection(controller, listOf(decision.options.first()))
                }
                decision is SelectCardsDecision -> driver.submitCardSelection(controller, emptyList())
                decision != null -> driver.autoResolveDecision()
                else -> driver.bothPass()
            }
            guard++
        }

        // The copy resolved and dealt 3 to the opponent…
        driver.getLifeTotal(opponent) shouldBe 17
        // …the original Bolt is in exile (it was exiled, then a copy was cast)…
        driver.getExileCardNames(controller) shouldContainExactlyInAnyOrder listOf("Tamiyo Test Bolt")
        // …and the instant copy ceased to exist rather than lingering in the graveyard.
        driver.getGraveyard(controller).size shouldBeGreaterThanOrEqual 0
        driver.getGraveyardCardNames(controller).contains("Tamiyo Test Bolt") shouldBe false
    }
})

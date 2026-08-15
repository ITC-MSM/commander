package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.blb.cards.YgraEaterOfAll
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Regression test for sacrifice-cost ward: Ygra, Eater of All's "Ward—Sacrifice a Food"
 * (CR 702.21).
 *
 * Ygra makes every *other* creature a Food artifact, so when an opponent targets Ygra the
 * Food they must sacrifice to pay ward can be one of their own creatures. Previously the
 * `WardCost.Sacrifice` branch of the ward executor was a no-op, so the trigger never asked
 * the caster to sacrifice anything — the spell resolved for free (the reported bug, hit with
 * Early Winter exiling Ygra). A non-modal "exile target creature" instant stands in for Early
 * Winter so the test exercises the ward path without the modal mode-selection dance.
 */
class WardSacrificeFoodCounterTest : FunSpec({

    val plainBear: CardDefinition = CardDefinition.creature(
        name = "Plain Bear",
        manaCost = ManaCost.parse("{1}{G}"),
        subtypes = setOf(Subtype("Bear")),
        power = 2,
        toughness = 2,
        oracleText = ""
    )

    val exileInstant: CardDefinition = card("Test Exile Instant") {
        manaCost = "{B}"
        typeLine = "Instant"
        spell {
            val creature = target("target creature to exile", Targets.Creature)
            effect = Effects.Exile(creature)
        }
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(YgraEaterOfAll, plainBear, exileInstant))
        return driver
    }

    test("targeting Ygra with no Food to sacrifice counters the spell (fizzles)") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 20, "Swamp" to 20))
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Opponent controls Ygra; the active player controls no creatures, so they have no
        // Food they can sacrifice to pay ward.
        val ygra = driver.putCreatureOnBattlefield(opponent, "Ygra, Eater of All")

        driver.giveMana(active, Color.BLACK, 1)
        val spell = driver.putCardInHand(active, "Test Exile Instant")
        driver.castSpellWithTargets(active, spell, listOf(ChosenTarget.Permanent(ygra)))

        // Let the ward trigger resolve.
        repeat(4) { if (driver.state.priorityPlayerId != null) driver.bothPass() }

        // No Food to sacrifice → ward counters the spell, so Ygra is untouched and the
        // caster is never prompted.
        driver.pendingDecision shouldBe null
        driver.findPermanent(opponent, "Ygra, Eater of All") shouldNotBe null
        driver.state.getZone(opponent, Zone.EXILE).contains(ygra) shouldBe false
    }

    test("caster can sacrifice a creature (now a Food via Ygra) to pay ward and resolve the spell") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 20, "Swamp" to 20))
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Opponent controls Ygra; the active player controls a vanilla Bear. Ygra makes that
        // Bear a Food artifact, so it is valid fodder for "Ward—Sacrifice a Food".
        val ygra = driver.putCreatureOnBattlefield(opponent, "Ygra, Eater of All")
        val bear = driver.putCreatureOnBattlefield(active, "Plain Bear")

        // Sanity check: the caster's Bear is a Food in projected state.
        driver.state.projectedState.hasSubtype(bear, Subtype.FOOD.value) shouldBe true

        driver.giveMana(active, Color.BLACK, 1)
        val spell = driver.putCardInHand(active, "Test Exile Instant")
        driver.castSpellWithTargets(active, spell, listOf(ChosenTarget.Permanent(ygra)))

        // Ward resolves → caster is prompted to choose a Food to sacrifice.
        driver.bothPass()
        val decision = driver.pendingDecision
        decision.shouldNotBeNull()
        decision.shouldBeInstanceOf<SelectCardsDecision>()
        decision.playerId shouldBe active
        decision.options shouldContain bear

        // Pay the ward by sacrificing the Bear.
        driver.submitDecision(active, CardsSelectedResponse(decision.id, listOf(bear)))

        // Resolve the food-death trigger (Ygra growing) and the exile spell.
        repeat(6) { if (driver.state.priorityPlayerId != null) driver.bothPass() }

        // Bear was sacrificed; the spell resolved and exiled Ygra.
        driver.findPermanent(active, "Plain Bear") shouldBe null
        driver.state.getZone(active, Zone.GRAVEYARD).contains(bear) shouldBe true
        driver.findPermanent(opponent, "Ygra, Eater of All") shouldBe null
        driver.state.getZone(opponent, Zone.EXILE).contains(ygra) shouldBe true
    }

    test("declining the ward sacrifice counters the spell") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 20, "Swamp" to 20))
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val ygra = driver.putCreatureOnBattlefield(opponent, "Ygra, Eater of All")
        val bear = driver.putCreatureOnBattlefield(active, "Plain Bear")

        driver.giveMana(active, Color.BLACK, 1)
        val spell = driver.putCardInHand(active, "Test Exile Instant")
        driver.castSpellWithTargets(active, spell, listOf(ChosenTarget.Permanent(ygra)))

        driver.bothPass()
        val decision = driver.pendingDecision
        decision.shouldNotBeNull()
        decision.shouldBeInstanceOf<SelectCardsDecision>()

        // Decline by selecting no Food — the spell is countered.
        driver.submitDecision(active, CardsSelectedResponse(decision.id, emptyList()))

        repeat(4) { if (driver.state.priorityPlayerId != null) driver.bothPass() }

        driver.findPermanent(opponent, "Ygra, Eater of All") shouldNotBe null
        driver.state.getZone(opponent, Zone.EXILE).contains(ygra) shouldBe false
        // The Bear was kept (not sacrificed) since the caster declined.
        driver.findPermanent(active, "Plain Bear") shouldNotBe null
    }
})

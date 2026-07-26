package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.AlternativeCostType
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.handlers.ConditionEvaluator
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.legalactions.LegalActionEnumerator
import com.wingedsheep.engine.state.components.battlefield.CastChoicesComponent
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.webSlinging
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import com.wingedsheep.sdk.scripting.ChoiceSlot
import com.wingedsheep.sdk.scripting.EntersWithDynamicCounters
import com.wingedsheep.sdk.scripting.conditions.WebSlungCostWasPaid
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Tests for the Web-slinging [cost] keyword (CR 702.188, Marvel's Spider-Man).
 *
 * "Web-slinging [cost]" — *"You may cast this spell by paying [cost] and returning a tapped creature
 * you control to its owner's hand rather than paying its mana cost."* (CR 702.188a) It is an
 * alternative cost with a bundled return-a-tapped-creature payment, cast at the spell's normal
 * timing (no timing permission of its own). The mana value is unchanged (CR 118.9c); a rider can
 * read that the web-slinging cost was paid and the returned creature's mana value.
 *
 * Exercised with inline cards so the engine behavior is pinned independent of the SPM set.
 */
class WebSlingingTest : FunSpec({

    // A vanilla web-slinger ({2}{W}, Web-slinging {W}) — mirrors Spider-Man, Web-Slinger.
    val webVanilla = card("Web Vanilla") {
        manaCost = "{2}{W}"
        typeLine = "Creature — Spider"
        power = 3
        toughness = 3
        webSlinging("{W}")
    }

    // A web-slinger whose ETB fires only if it was web-slung — mirrors Spiders-Man, Heroic Horde.
    val webPayoff = card("Web Payoff") {
        manaCost = "{1}{G}"
        typeLine = "Creature — Spider"
        power = 2
        toughness = 3
        webSlinging("{4}{G}{G}")
        triggeredAbility {
            trigger = Triggers.EntersBattlefield
            effect = ConditionalEffect(
                condition = Conditions.WebSlungCostWasPaid,
                effect = Effects.GainLife(3)
            )
        }
    }

    // A web-slinger that enters with +1/+1 counters equal to the returned creature's mana value —
    // mirrors Scarlet Spider, Ben Reilly.
    val webCounters = card("Web Counters") {
        manaCost = "{1}{R}{G}"
        typeLine = "Creature — Spider"
        power = 4
        toughness = 3
        webSlinging("{R}{G}")
        replacementEffect(
            EntersWithDynamicCounters(count = DynamicAmount.CastChoice(ChoiceSlot.WEB_SLUNG_RETURNED_MV))
        )
    }

    // The creature returned to pay the web-slinging cost — mana value 3 ({2}{G}).
    val returnable = card("Mana Three Beast") {
        manaCost = "{2}{G}"
        typeLine = "Creature — Beast"
        power = 2
        toughness = 2
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(webVanilla, webPayoff, webCounters, returnable))
        return driver
    }

    test("cast for web-slinging: pay the web-slinging mana + return a tapped creature; spell resolves and flag is set") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        val player = driver.activePlayer!!

        val beast = driver.putCreatureOnBattlefield(player, "Mana Three Beast")
        driver.tapPermanent(beast)
        val spider = driver.putCardInHand(player, "Web Vanilla")

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.giveMana(player, Color.WHITE, 1)
        val result = driver.submit(
            CastSpell(
                playerId = player,
                cardId = spider,
                useAlternativeCost = true,
                alternativeCostType = AlternativeCostType.WEB_SLINGING,
                additionalCostPayment = AdditionalCostPayment(bouncedPermanents = listOf(beast)),
                paymentStrategy = PaymentStrategy.FromPool
            )
        )
        io.kotest.assertions.withClue("error=${result.error} pending=${result.pendingDecision}") {
            result.isSuccess shouldBe true
        }
        while (driver.state.stack.isNotEmpty()) driver.bothPass()

        // The tapped Beast was returned to its owner's hand as part of the cost.
        driver.getHand(player) shouldContain beast
        driver.findPermanent(player, "Mana Three Beast") shouldBe null

        // The web-slinger resolved and carries the durable web-slung flag; the condition agrees.
        val perm = driver.findPermanent(player, "Web Vanilla")
        perm.shouldNotBeNull()
        driver.state.getEntity(perm)?.get<CastChoicesComponent>()?.chosen?.containsKey(ChoiceSlot.WEB_SLUNG) shouldBe true
        ConditionEvaluator().evaluate(
            driver.state,
            WebSlungCostWasPaid,
            EffectContext(sourceId = perm, controllerId = player)
        ).shouldBeTrue()
    }

    test("web-slinging is offered as a legal action only while a tapped creature is available") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        val player = driver.activePlayer!!

        val beast = driver.putCreatureOnBattlefield(player, "Mana Three Beast")
        driver.putCardInHand(player, "Web Vanilla")
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.giveMana(player, Color.WHITE, 1)

        val enumerator = LegalActionEnumerator.create(driver.cardRegistry)
        fun webActions() = enumerator.enumerate(driver.state, player)
            .mapNotNull { it.action as? CastSpell }
            .filter { it.alternativeCostType == AlternativeCostType.WEB_SLINGING }

        // Beast untapped: no tapped creature to return, so no web-slinging option.
        webActions().isEmpty().shouldBeTrue()

        // Tap the Beast: the web-slinging option now appears.
        driver.tapPermanent(beast)
        webActions().isNotEmpty().shouldBeTrue()
    }

    test("cannot web-sling by returning an untapped creature") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        val player = driver.activePlayer!!

        val beast = driver.putCreatureOnBattlefield(player, "Mana Three Beast") // left untapped
        val spider = driver.putCardInHand(player, "Web Vanilla")
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.giveMana(player, Color.WHITE, 1)

        driver.submitExpectFailure(
            CastSpell(
                playerId = player,
                cardId = spider,
                useAlternativeCost = true,
                alternativeCostType = AlternativeCostType.WEB_SLINGING,
                additionalCostPayment = AdditionalCostPayment(bouncedPermanents = listOf(beast)),
                paymentStrategy = PaymentStrategy.FromPool
            )
        )
    }

    test("web-slung enters-with-counters rider reads the returned creature's mana value (CR 118.9c)") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val player = driver.activePlayer!!

        val beast = driver.putCreatureOnBattlefield(player, "Mana Three Beast") // mana value 3
        driver.tapPermanent(beast)
        val spider = driver.putCardInHand(player, "Web Counters")

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.giveMana(player, Color.RED, 1)
        driver.giveMana(player, Color.GREEN, 1)
        driver.submit(
            CastSpell(
                playerId = player,
                cardId = spider,
                useAlternativeCost = true,
                alternativeCostType = AlternativeCostType.WEB_SLINGING,
                additionalCostPayment = AdditionalCostPayment(bouncedPermanents = listOf(beast)),
                paymentStrategy = PaymentStrategy.FromPool
            )
        ).isSuccess shouldBe true
        while (driver.state.stack.isNotEmpty()) driver.bothPass()

        val perm = driver.findPermanent(player, "Web Counters")
        perm.shouldNotBeNull()
        // Beast's mana value is 3, so the web-slinger enters with 3 +1/+1 counters.
        driver.state.getEntity(perm)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 3
    }

    test("web-slung ETB payoff fires; a normal cast leaves the flag false and skips the payoff") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val player = driver.activePlayer!!

        // Normal cast of the payoff for its full {1}{G}: no web-slinging, so no life gain and no flag.
        val normal = driver.putCardInHand(player, "Web Payoff")
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.giveMana(player, Color.GREEN, 2)
        driver.castSpell(player, normal).isSuccess shouldBe true
        while (driver.state.stack.isNotEmpty()) driver.bothPass()
        driver.assertLifeTotal(player, 20)
        val normalPerm = driver.findPermanent(player, "Web Payoff")
        normalPerm.shouldNotBeNull()
        ConditionEvaluator().evaluate(
            driver.state,
            WebSlungCostWasPaid,
            EffectContext(sourceId = normalPerm, controllerId = player)
        ).shouldBeFalse()
    }

    test("web-slung ETB payoff gains 3 life when cast using web-slinging") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val player = driver.activePlayer!!

        val beast = driver.putCreatureOnBattlefield(player, "Mana Three Beast")
        driver.tapPermanent(beast)
        val spider = driver.putCardInHand(player, "Web Payoff")

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        // Web-slinging cost is {4}{G}{G}.
        driver.giveMana(player, Color.GREEN, 6)
        driver.submit(
            CastSpell(
                playerId = player,
                cardId = spider,
                useAlternativeCost = true,
                alternativeCostType = AlternativeCostType.WEB_SLINGING,
                additionalCostPayment = AdditionalCostPayment(bouncedPermanents = listOf(beast)),
                paymentStrategy = PaymentStrategy.FromPool
            )
        ).isSuccess shouldBe true
        while (driver.state.stack.isNotEmpty()) driver.bothPass()

        driver.assertLifeTotal(player, 23)
    }
})

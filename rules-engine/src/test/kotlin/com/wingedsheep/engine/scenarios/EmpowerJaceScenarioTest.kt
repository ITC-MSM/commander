package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.TokenComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.engine.view.ClientStateTransformer
import com.wingedsheep.mtg.sets.tokens.PredefinedTokens
import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.grantedLoyaltyAbility
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import com.wingedsheep.sdk.scripting.effects.CREATED_TOKENS
import com.wingedsheep.sdk.scripting.effects.CreatePredefinedTokenEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Empower Jace N (CR 701.71a): "If you don't control a Jace planeswalker token, create a blue Jace
 * planeswalker token with 0 loyalty, '[−1]: Surveil 1,' and '[−3]: Draw a card.' Choose a Jace
 * planeswalker token you control. Put N loyalty counters on it."
 *
 * [Patterns.Mechanic.empowerJace] composes existing pipeline atoms around the predefined `Jace`
 * token. These tests pin each clause of the rule, the zero-loyalty state-based action (CR 704.5i)
 * for empower Jace 0, the token's own loyalty abilities, and loyalty abilities granted to it by a
 * "Planeswalkers you control have …" static.
 */
class EmpowerJaceScenarioTest : FunSpec({

    fun empowerSorcery(name: String, n: Int) = card(name) {
        manaCost = "{0}"
        typeLine = "Sorcery"
        oracleText = "Empower Jace $n."
        spell { effect = Patterns.Mechanic.empowerJace(n) }
    }

    val empowerTwo = empowerSorcery("Empower Two", 2)
    val empowerThree = empowerSorcery("Empower Three", 3)
    val empowerZero = empowerSorcery("Empower Zero", 0)

    // A second Jace token with one loyalty, bypassing empower's find-or-create (a copy effect's shape).
    val mintJace = card("Mint Jace") {
        manaCost = "{0}"
        typeLine = "Sorcery"
        spell {
            effect = CreatePredefinedTokenEffect("Jace")
                .then(Effects.AddCountersToCollection(CREATED_TOKENS, Counters.LOYALTY, 1))
        }
    }

    // A nontoken Jace planeswalker — "Jace planeswalker token" must not match it.
    val jaceCard = card("Jace, Test Walker") {
        manaCost = "{0}"
        typeLine = "Legendary Planeswalker — Jace"
        startingLoyalty = 4
        oracleText = "+1: You gain 1 life."
        loyaltyAbility(1) { effect = Effects.GainLife(1) }
    }

    // "Planeswalkers you control have '[−2]: You gain 3 life.'"
    val lifeGrant = card("Grant Of Vigor") {
        manaCost = "{0}"
        typeLine = "Enchantment"
        oracleText = "Planeswalkers you control have \"[−2]: You gain 3 life.\""
        staticAbility {
            ability = GrantActivatedAbility(
                ability = grantedLoyaltyAbility(-2) {
                    effect = Effects.GainLife(3)
                    description = "You gain 3 life."
                },
                filter = GroupFilter(GameObjectFilter.Planeswalker.youControl())
            )
        }
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(
            TestCards.all + PredefinedTokens.allTokens +
                listOf(empowerTwo, empowerThree, empowerZero, mintJace, jaceCard, lifeGrant)
        )
        driver.initMirrorMatch(deck = Deck.of("Island" to 40))
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.jaceTokens(player: EntityId): List<EntityId> =
        getPermanents(player).filter {
            state.getEntity(it)?.has<TokenComponent>() == true && getCardName(it) == "Jace"
        }

    fun GameTestDriver.loyalty(id: EntityId): Int =
        state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.LOYALTY) ?: 0

    fun GameTestDriver.cast(player: EntityId, name: String) {
        castSpell(player, putCardInHand(player, name)).isSuccess shouldBe true
        bothPass()
    }

    fun GameTestDriver.activate(player: EntityId, source: EntityId, description: String) {
        val action = legalActions(player)
            .map { it.action }
            .filterIsInstance<ActivateAbility>()
            .filter { it.sourceId == source }
            .firstOrNull { a ->
                legalActions(player).first { it.action == a }.description.contains(description)
            }
            .shouldNotBeNull()
        submitSuccess(action)
    }

    fun GameTestDriver.hasActivation(player: EntityId, source: EntityId): Boolean =
        legalActions(player).map { it.action }.filterIsInstance<ActivateAbility>().any { it.sourceId == source }

    test("with no Jace token, empower Jace creates a blue nonlegendary Jace planeswalker token with N loyalty") {
        val driver = createDriver()
        val me = driver.activePlayer!!

        driver.cast(me, "Empower Two")

        val jace = driver.jaceTokens(me).single()
        val card = driver.state.getEntity(jace)!!.get<CardComponent>()!!
        card.typeLine.cardTypes shouldContain CardType.PLANESWALKER
        card.typeLine.isLegendary shouldBe false
        card.typeLine.subtypes.map { it.value } shouldContain "Jace"
        card.colors shouldBe setOf(Color.BLUE)
        driver.loyalty(jace) shouldBe 2
    }

    test("with a Jace token already, empower Jace adds the counters to it and creates no second token") {
        val driver = createDriver()
        val me = driver.activePlayer!!

        driver.cast(me, "Empower Two")
        val jace = driver.jaceTokens(me).single()

        driver.cast(me, "Empower Three")

        driver.jaceTokens(me) shouldBe listOf(jace)
        driver.loyalty(jace) shouldBe 5
    }

    test("with two Jace tokens, the controller chooses which one gets the counters") {
        val driver = createDriver()
        val me = driver.activePlayer!!

        driver.cast(me, "Empower Two")
        val first = driver.jaceTokens(me).single()
        driver.cast(me, "Mint Jace")
        val second = driver.jaceTokens(me).single { it != first }
        driver.loyalty(second) shouldBe 1

        driver.castSpell(me, driver.putCardInHand(me, "Empower Three")).isSuccess shouldBe true
        driver.bothPass()

        val decision = driver.pendingDecision
        (decision is SelectCardsDecision) shouldBe true
        decision as SelectCardsDecision
        decision.options.toSet() shouldBe setOf(first, second)
        driver.submitDecision(me, CardsSelectedResponse(decision.id, listOf(second)))

        driver.loyalty(first) shouldBe 2
        driver.loyalty(second) shouldBe 4
    }

    test("a nontoken Jace planeswalker is not a Jace token, so empower Jace still creates one") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val walker = driver.putPermanentOnBattlefield(me, "Jace, Test Walker")
        val walkerLoyalty = driver.loyalty(walker)

        driver.cast(me, "Empower Two")

        val jace = driver.jaceTokens(me).single()
        driver.loyalty(jace) shouldBe 2
        driver.loyalty(walker) shouldBe walkerLoyalty
    }

    test("empower Jace 0 with no Jace token leaves a 0-loyalty token that dies to CR 704.5i") {
        val driver = createDriver()
        val me = driver.activePlayer!!

        driver.cast(me, "Empower Zero")

        driver.jaceTokens(me) shouldBe emptyList()
    }

    test("the Jace token's −3 draws a card, and it gets only one loyalty activation per turn") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        driver.cast(me, "Empower Three")
        driver.cast(me, "Empower Two")
        val jace = driver.jaceTokens(me).single()
        driver.loyalty(jace) shouldBe 5

        val handBefore = driver.getHand(me).size
        driver.activate(me, jace, "Draw")
        driver.bothPass()
        driver.getHand(me).size shouldBe handBefore + 1
        driver.loyalty(jace) shouldBe 2

        // Once per turn (CR 606.3): no further loyalty activation this turn.
        driver.hasActivation(me, jace) shouldBe false
    }

    test("a granted loyalty ability works on the Jace token, shares the once-per-turn limit, and is listed in its menu") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        driver.putPermanentOnBattlefield(me, "Grant Of Vigor")
        driver.cast(me, "Empower Three")
        val jace = driver.jaceTokens(me).single()

        val view = ClientStateTransformer(driver.cardRegistry).transform(driver.state, me)
        val menu = view.cards[jace]!!.planeswalkerAbilities!!
        menu.map { it.loyaltyChange }.toSet() shouldBe setOf(-1, -3, -2)
        menu.single { it.loyaltyChange == -2 }.description shouldBe "You gain 3 life."
        menu.single { it.loyaltyChange == -1 }.description shouldBe "Surveil 1"

        val life = driver.getLifeTotal(me)
        driver.activate(me, jace, "gain 3 life")
        driver.bothPass()
        driver.getLifeTotal(me) shouldBe life + 3
        driver.loyalty(jace) shouldBe 1
        driver.hasActivation(me, jace) shouldBe false
    }
})

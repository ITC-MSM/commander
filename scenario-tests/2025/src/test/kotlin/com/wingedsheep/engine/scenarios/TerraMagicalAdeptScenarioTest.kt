package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.ChooseNumberDecision
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.NumberChosenResponse
import com.wingedsheep.engine.core.TargetsResponse
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.core.YesNoResponse
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.battlefield.SagaComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.TokenComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.TerraMagicalAdept
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Terra, Magical Adept // Esper Terra (FIN #245).
 *
 * Exercises the card end-to-end: the front ETB mill-five-take-an-enchantment; the Trance
 * exile-and-return-transformed into the Summon-Saga back (a new object, fresh lore); and — the
 * point of the accompanying engine work — Esper Terra's chapter I–III copy, where the "if it's a
 * Saga, put up to three lore counters on it" clause is composed as
 * `ConditionalEffect(CollectionContainsMatch(CREATED_TOKENS, Saga), AddCountersUpTo(LORE, 3, …))`.
 */
class TerraMagicalAdeptScenarioTest : FunSpec({

    val projector = StateProjector()

    // A plain nonlegendary enchantment — the "copy target that is NOT a Saga" case (no lore prompt).
    val testSigil = card("Test Sigil") {
        manaCost = "{1}"
        colorIdentity = ""
        typeLine = "Enchantment"
        oracleText = ""
    }

    // A nonlegendary Saga with five benign (no-target) chapters — the "copy target that IS a Saga"
    // case. Five chapters so putting up to three extra lore on a token copy (which enters with one)
    // never reaches the final chapter and self-sacrifices mid-test.
    val testChronicle = card("Test Chronicle") {
        manaCost = "{2}"
        colorIdentity = ""
        typeLine = "Enchantment — Saga"
        oracleText = "I, II, III, IV, V — You gain 1 life."
        for (n in 1..5) sagaChapter(n) { effect = Effects.GainLife(1) }
    }

    fun resolveStack(driver: GameTestDriver) {
        var guard = 0
        while (guard++ < 40 && driver.state.stack.isNotEmpty() && !driver.isPaused) driver.bothPass()
    }

    fun clearBenignDecisions(driver: GameTestDriver) {
        var guard = 0
        while (guard++ < 20 && driver.isPaused) {
            when (val decision = driver.pendingDecision) {
                is YesNoDecision ->
                    driver.submitDecision(decision.playerId, YesNoResponse(decision.id, false))
                is ChooseTargetsDecision -> {
                    val chosen = decision.targetRequirements.associate { req ->
                        req.index to decision.legalTargets[req.index].orEmpty().take(req.minTargets)
                    }
                    driver.submitDecision(decision.playerId, TargetsResponse(decision.id, chosen))
                }
                else -> driver.autoResolveDecision()
            }
        }
    }

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(TerraMagicalAdept, testSigil, testChronicle))
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun tokenCopies(driver: GameTestDriver, player: EntityId, name: String): List<EntityId> =
        driver.state.getZone(player, Zone.BATTLEFIELD).filter { id ->
            val c = driver.state.getEntity(id) ?: return@filter false
            c.get<CardComponent>()?.name == name && c.get<TokenComponent>() != null
        }

    // Cast Terra; on ETB resolution, either select the milled enchantment [takeMilled] into hand or
    // decline. Returns Terra's entity id.
    fun castTerra(driver: GameTestDriver, player: EntityId, takeMilled: EntityId? = null): EntityId {
        val terra = driver.putCardInHand(player, "Terra, Magical Adept")
        driver.giveMana(player, Color.RED, 1)
        driver.giveMana(player, Color.GREEN, 1)
        driver.giveColorlessMana(player, 1)
        driver.castSpell(player, terra)
        driver.bothPass()
        resolveStack(driver)
        // ETB "put up to one enchantment milled this way into your hand" — a card-selection prompt.
        if (driver.isPaused) {
            val decisionId = driver.pendingDecision!!.id
            driver.submitDecision(player, CardsSelectedResponse(decisionId, listOfNotNull(takeMilled)))
        }
        resolveStack(driver)
        clearBenignDecisions(driver)
        return driver.findPermanent(player, "Terra, Magical Adept")!!
    }

    fun trance(driver: GameTestDriver, player: EntityId, terra: EntityId) {
        driver.removeSummoningSickness(terra)
        driver.giveMana(player, Color.RED, 1)
        driver.giveMana(player, Color.GREEN, 1)
        driver.giveColorlessMana(player, 4)
        val abilityId = TerraMagicalAdept.activatedAbilities.first().id
        driver.submit(ActivateAbility(playerId = player, sourceId = terra, abilityId = abilityId))
            .isSuccess shouldBe true
        driver.bothPass()
    }

    test("front ETB mills five and may put an enchantment milled this way into hand") {
        val driver = newDriver()
        val active = driver.activePlayer!!
        // Seed a plain enchantment on top of the library so it is among the five milled.
        val sigil = driver.putCardOnTopOfLibrary(active, "Test Sigil")

        castTerra(driver, active, takeMilled = sigil)

        driver.state.getZone(active, Zone.HAND) shouldContain sigil
        // Five cards left the top of the library (the enchantment then moved on to the hand).
        driver.state.getZone(active, Zone.GRAVEYARD).size shouldBeGreaterThanOrEqual 4
        driver.findPermanent(active, "Terra, Magical Adept") shouldNotBe null
    }

    test("Trance exiles Terra and returns it transformed as Esper Terra — a 6/6 flying Saga, fresh lore") {
        val driver = newDriver()
        val active = driver.activePlayer!!
        val terra = castTerra(driver, active)

        trance(driver, active, terra)
        clearBenignDecisions(driver) // no nonlegendary enchantment to copy -> chapter I has no target
        resolveStack(driver)
        clearBenignDecisions(driver)

        val container = driver.state.getEntity(terra)!!
        container.get<CardComponent>()!!.name shouldBe "Esper Terra"
        val projected = projector.project(driver.state)
        projected.isCreature(terra) shouldBe true
        projected.hasType(terra, "Saga") shouldBe true
        projected.getPower(terra) shouldBe 6
        projected.getToughness(terra) shouldBe 6
        projected.hasKeyword(terra, Keyword.FLYING) shouldBe true
        container.get<SagaComponent>() shouldNotBe null
        container.get<CountersComponent>()!!.getCount(CounterType.LORE) shouldBe 1
    }

    test("chapter I copying a non-Saga enchantment makes a haste token and offers no lore prompt") {
        val driver = newDriver()
        val active = driver.activePlayer!!
        val terra = castTerra(driver, active)
        driver.putPermanentOnBattlefield(active, "Test Sigil")

        trance(driver, active, terra)
        // Chapter I triggers on transform; target the (only) nonlegendary enchantment we control.
        var guard = 0
        while (guard++ < 15 && driver.isPaused) {
            val decision = driver.pendingDecision
            // A lore ChooseNumber must NOT appear for a non-Saga copy.
            (decision is ChooseNumberDecision) shouldBe false
            clearBenignDecisions(driver)
            resolveStack(driver)
        }
        resolveStack(driver)

        val tokens = tokenCopies(driver, active, "Test Sigil")
        tokens.size shouldBe 1
        projector.project(driver.state).hasKeyword(tokens.first(), Keyword.HASTE).shouldBeTrue()
    }

    test("chapter I copying a Saga offers 'up to three lore counters' and places the chosen count") {
        val driver = newDriver()
        val active = driver.activePlayer!!
        val terra = castTerra(driver, active)
        driver.putPermanentOnBattlefield(active, "Test Chronicle")

        trance(driver, active, terra)

        // Resolve everything up to (and including) the lore ChooseNumber prompt, which proves the
        // Saga gate + AddCountersUpTo composition fired. Answer it with 3.
        var sawLorePrompt = false
        var guard = 0
        while (guard++ < 25 && driver.isPaused) {
            when (val decision = driver.pendingDecision) {
                is ChooseNumberDecision -> {
                    decision.maxValue shouldBe 3
                    sawLorePrompt = true
                    driver.submitDecision(decision.playerId, NumberChosenResponse(decision.id, 3))
                }
                is ChooseTargetsDecision -> {
                    // Target the Saga we control for the copy.
                    val chosen = decision.targetRequirements.associate { req ->
                        req.index to decision.legalTargets[req.index].orEmpty().take(1)
                    }
                    driver.submitDecision(decision.playerId, TargetsResponse(decision.id, chosen))
                }
                else -> clearBenignDecisions(driver)
            }
            resolveStack(driver)
        }

        sawLorePrompt.shouldBeTrue()
        val tokens = tokenCopies(driver, active, "Test Chronicle")
        tokens.size shouldBe 1
        // The token copy entered as a Saga with CR 714.2b's on-enter lore counter (1); the
        // AddCountersUpTo composition then placed the three chosen lore counters — total 4.
        driver.state.getEntity(tokens.first())!!.get<CountersComponent>()!!
            .getCount(CounterType.LORE) shouldBe 4
    }
})

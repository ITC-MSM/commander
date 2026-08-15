package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.battlefield.SagaComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.TokenComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.HuatliPoetOfUnity
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import com.wingedsheep.sdk.scripting.effects.GrantStaticAbilityEffect
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Huatli, Poet of Unity // Roar of the Fifth People (LCI #189).
 *
 * The novel behavior proven here is a **creature front transforming into a non-creature Saga back**
 * (assembled with `CardDefinition.doubleFacedPermanent`): the sorcery-speed activated ability
 * exile-returns Huatli as Roar, which re-enters as a Saga, picks up CR 714.3a's on-enter lore
 * counter, and fires chapter I (two 3/3 green Dinosaur tokens). Chapter II then grants Roar itself a
 * lasting Citanul-Hierophants static — "creatures you control have '{T}: Add {R}, {G}, or {W}'" —
 * proven by a controlled creature gaining an activatable mana ability it lacked before.
 *
 * Chapters III (search a Dinosaur card to hand) and IV (Dinosaurs gain double strike + trample) use
 * already-proven primitives (`Patterns.Library.searchLibrary`, `ForEachInGroup` + `GrantKeyword`)
 * and aren't re-exercised here.
 */
class HuatliPoetOfUnityScenarioTest : FunSpec({

    val projector = StateProjector()

    // A plain 2/2 to receive chapter II's granted "{T}: Add {R}, {G}, or {W}" mana ability.
    val testBear = card("Test Bear") {
        manaCost = "{1}{G}"
        colorIdentity = "G"
        typeLine = "Creature — Bear"
        oracleText = ""
        power = 2
        toughness = 2
    }

    fun resolveStack(driver: GameTestDriver) {
        var guard = 0
        while (guard++ < 40 && driver.state.stack.isNotEmpty() && !driver.isPaused) driver.bothPass()
    }

    fun clearBenignDecisions(driver: GameTestDriver) {
        var guard = 0
        while (guard++ < 20 && driver.isPaused) driver.autoResolveDecision()
    }

    fun advanceToNextTurnMain(driver: GameTestDriver) {
        driver.passPriorityUntil(Step.END, maxPasses = 300)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN, maxPasses = 300)
        clearBenignDecisions(driver)
        resolveStack(driver)
        clearBenignDecisions(driver)
    }

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(HuatliPoetOfUnity, testBear))
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun loreOf(driver: GameTestDriver, id: EntityId): Int =
        driver.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.LORE) ?: 0

    // Cast Huatli and resolve her ETB basic-land tutor, taking [takeLand] into hand. Returns her id.
    fun castHuatli(driver: GameTestDriver, player: EntityId, takeLand: EntityId?): EntityId {
        val huatli = driver.putCardInHand(player, "Huatli, Poet of Unity")
        driver.giveMana(player, Color.GREEN, 1)
        driver.giveColorlessMana(player, 2)
        driver.castSpell(player, huatli)
        driver.bothPass()
        resolveStack(driver)
        // ETB "search your library for a basic land card ... put it into your hand" selection.
        if (driver.isPaused) {
            val decisionId = driver.pendingDecision!!.id
            driver.submitDecision(player, CardsSelectedResponse(decisionId, listOfNotNull(takeLand)))
        }
        resolveStack(driver)
        clearBenignDecisions(driver)
        return driver.findPermanent(player, "Huatli, Poet of Unity")!!
    }

    // Activate the sorcery-speed transform ability; resolve chapter I.
    fun transform(driver: GameTestDriver, player: EntityId, huatli: EntityId) {
        driver.giveMana(player, Color.RED, 2)
        driver.giveColorlessMana(player, 3)
        val abilityId = HuatliPoetOfUnity.activatedAbilities.first().id
        driver.submit(ActivateAbility(playerId = player, sourceId = huatli, abilityId = abilityId))
            .isSuccess shouldBe true
        driver.bothPass()
        resolveStack(driver)
        clearBenignDecisions(driver)
    }

    fun controlledTokens(driver: GameTestDriver, player: EntityId): List<EntityId> =
        driver.state.getZone(player, Zone.BATTLEFIELD).filter { id ->
            driver.state.getEntity(id)?.get<TokenComponent>() != null
        }

    test("transform ability is sorcery-speed ('activate only as a sorcery')") {
        HuatliPoetOfUnity.activatedAbilities.first().timing shouldBe
            com.wingedsheep.sdk.scripting.TimingRule.SorcerySpeed
    }

    test("front ETB searches the library for a basic land and puts it into hand") {
        val driver = newDriver()
        val active = driver.activePlayer!!
        val land = driver.putCardOnTopOfLibrary(active, "Forest")

        castHuatli(driver, active, takeLand = land)

        driver.state.getZone(active, Zone.HAND) shouldContain land
    }

    test("transform returns Huatli as the Roar Saga (lore 1) and chapter I makes two 3/3 green Dinosaurs") {
        val driver = newDriver()
        val active = driver.activePlayer!!
        val huatli = castHuatli(driver, active, takeLand = null)

        transform(driver, active, huatli)

        // Same entity id, now the back face — a Saga with a fresh on-enter lore counter.
        val container = driver.state.getEntity(huatli)!!
        container.get<CardComponent>()!!.name shouldBe "Roar of the Fifth People"
        val projected = projector.project(driver.state)
        projected.hasType(huatli, "Saga") shouldBe true
        container.get<SagaComponent>() shouldNotBe null
        loreOf(driver, huatli) shouldBe 1

        // Chapter I created two token Dinosaurs; each is a 3/3 green Dinosaur.
        val tokens = controlledTokens(driver, active)
        tokens.size shouldBe 2
        tokens.forEach { token ->
            projected.getPower(token) shouldBe 3
            projected.getToughness(token) shouldBe 3
            projected.hasType(token, "Dinosaur") shouldBe true
        }
    }

    test("chapter II grants creatures you control a '{T}: Add R/G/W' mana ability") {
        val driver = newDriver()
        val active = driver.activePlayer!!
        val bear = driver.putPermanentOnBattlefield(active, "Test Bear")
        val huatli = castHuatli(driver, active, takeLand = null)

        transform(driver, active, huatli)

        // The granted mana ability's id — baked into chapter II's GrantStaticAbility →
        // GrantActivatedAbility (the id is stable across the run, generated at definition load).
        val chapter2 = HuatliPoetOfUnity.backFace!!.sagaChapters.first { it.chapter == 2 }
        val manaAbilityId =
            ((chapter2.effect as GrantStaticAbilityEffect).ability as GrantActivatedAbility).ability.id

        // Before chapter II the vanilla bear doesn't have the ability — activation is rejected.
        driver.submit(ActivateAbility(playerId = active, sourceId = bear, abilityId = manaAbilityId))
            .isSuccess shouldBe false

        // Accrue lore to 2 — chapter II resolves during the advance, granting Roar the lasting static
        // "creatures you control have '{T}: Add {R}, {G}, or {W}'".
        var guard = 0
        while (guard++ < 8 && loreOf(driver, huatli) < 2) advanceToNextTurnMain(driver)
        loreOf(driver, huatli) shouldBe 2

        // The bear (a creature we control) can now activate the granted mana ability — the
        // activation handler honors a GrantActivatedAbility that was itself granted to Roar, not
        // just printed ones. (Resolution of the ManaColorSet.Specific choice is the pre-existing
        // AddManaOfChoice path, exercised by the Devotee / Vivi Ornitier cards.)
        driver.removeSummoningSickness(bear)
        driver.submit(ActivateAbility(playerId = active, sourceId = bear, abilityId = manaAbilityId))
            .isSuccess shouldBe true
    }
})

package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.SummonFatChocobo
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Summon: Fat Chocobo — {4}{G} Enchantment Creature — Saga Bird, 4/4 (FIN).
 *
 *   I — Wark — Create a 2/2 green Bird creature token with "Whenever a land you control enters,
 *       this token gets +1/+0 until end of turn."
 *   II, III, IV — Kerplunk — Creatures you control gain trample until end of turn.
 *
 * The generic saga-creature machinery (lore accrual, chapter triggers, sacrifice after the final
 * chapter) is covered by CreatureSagaTest; this pins Fat Chocobo's chapter I token — its body, type,
 * and embedded land-enters self-pump trigger — and the chapter II–IV trample grant.
 */
class SummonFatChocoboScenarioTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(SummonFatChocobo))
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.resolveAll() {
        var guard = 0
        while ((state.stack.isNotEmpty() || state.pendingDecision != null) && guard++ < 50) {
            val pd = state.pendingDecision
            if (pd != null) autoResolveDecision() else bothPass()
        }
    }

    fun GameTestDriver.castChocobo(me: EntityId): EntityId {
        val spell = putCardInHand(me, "Summon: Fat Chocobo")
        giveColorlessMana(me, 4)
        giveMana(me, com.wingedsheep.sdk.core.Color.GREEN, 1)
        castSpell(me, spell)
        resolveAll()
        return spell
    }

    test("chapter I creates a 2/2 green Bird token") {
        val driver = createDriver()
        val me = driver.activePlayer!!

        driver.castChocobo(me)

        val saga = driver.findPermanent(me, "Summon: Fat Chocobo")!!
        // The token is the creature I control that isn't the Saga itself.
        val token = driver.getCreatures(me).firstOrNull { it != saga }
        token shouldNotBe null

        val projected = projector.project(driver.state)
        projected.getPower(token!!) shouldBe 2
        projected.getToughness(token) shouldBe 2
        projected.hasType(token, "Bird") shouldBe true
    }

    test("the chapter I token gets +1/+0 when a land enters under my control") {
        val driver = createDriver()
        val me = driver.activePlayer!!

        driver.castChocobo(me)
        val saga = driver.findPermanent(me, "Summon: Fat Chocobo")!!
        val token = driver.getCreatures(me).first { it != saga }

        projector.project(driver.state).getPower(token) shouldBe 2

        // Play a land: the token's "whenever a land you control enters" trigger fires for +1/+0.
        val land = driver.putCardInHand(me, "Forest")
        driver.playLand(me, land)
        driver.resolveAll()

        projector.project(driver.state).getPower(driver.getCreatures(me).first { it != saga }) shouldBe 3
    }

    test("chapter II grants creatures I control trample") {
        val driver = createDriver()
        val me = driver.activePlayer!!

        val saga = driver.castChocobo(me)
        val sagaId = driver.findPermanent(me, "Summon: Fat Chocobo")!!

        // Advance turns until the Saga has accrued a second lore counter (chapter II resolves).
        var guard = 0
        while (guard++ < 1000) {
            val token = driver.getCreatures(me).firstOrNull { it != sagaId }
            if (token != null && projector.project(driver.state).hasKeyword(token, Keyword.TRAMPLE)) break
            if (driver.findPermanent(me, "Summon: Fat Chocobo") == null) {
                throw AssertionError("Saga was sacrificed before chapter II granted trample")
            }
            val pd = driver.state.pendingDecision
            when {
                pd != null -> driver.autoResolveDecision()
                driver.state.priorityPlayerId != null -> {
                    driver.autoSubmitCombatDeclarationIfNeeded()
                    driver.passPriority(driver.state.priorityPlayerId!!)
                }
            }
        }

        val token = driver.getCreatures(me).first { it != sagaId }
        projector.project(driver.state).hasKeyword(token, Keyword.TRAMPLE) shouldBe true
    }
})

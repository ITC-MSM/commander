package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.VenatHeartOfHydaelyn
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.TimingRule
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Venat, Heart of Hydaelyn // Hydaelyn, the Mothercrystal (FIN).
 *
 * Front:
 *   - "Whenever you cast a legendary spell, draw a card. This ability triggers only once each turn."
 *   - "Hero's Sundering — {7}, {T}: Exile target nonland permanent. Transform Venat. Activate only as a sorcery."
 * Back (Hydaelyn, the Mothercrystal — 4/4 God):
 *   - Indestructible
 *   - "Blessing of Light — At the beginning of combat on your turn, put a +1/+1 counter on another
 *     target creature you control. Until your next turn, it gains indestructible. If that creature is
 *     legendary, draw a card."
 */
class VenatHeartOfHydaelynScenarioTest : FunSpec({

    val projector = StateProjector()

    // Minimal legendary / vanilla spells to drive the front trigger deterministically.
    val legendOne = card("Test Legend One") {
        manaCost = "{1}"
        typeLine = "Legendary Creature — Human"
        power = 1; toughness = 1
    }
    val legendTwo = card("Test Legend Two") {
        manaCost = "{1}"
        typeLine = "Legendary Creature — Human"
        power = 1; toughness = 1
    }
    val vanilla = card("Test Vanilla") {
        manaCost = "{1}"
        typeLine = "Creature — Human"
        power = 1; toughness = 1
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(VenatHeartOfHydaelyn)
        driver.registerCard(legendOne)
        driver.registerCard(legendTwo)
        driver.registerCard(vanilla)
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun resolveStack(driver: GameTestDriver) {
        var guard = 0
        while (guard++ < 40 && driver.state.stack.isNotEmpty() && !driver.isPaused) driver.bothPass()
    }

    test("front: casting a legendary spell draws a card, but only once each turn") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        driver.putPermanentOnBattlefield(me, "Venat, Heart of Hydaelyn")

        // First legendary spell this turn -> the draw offsets the card leaving hand (net 0).
        driver.putCardInHand(me, "Test Legend One").let { spell ->
            driver.giveColorlessMana(me, 1)
            val before = driver.getHandSize(me)
            driver.castSpell(me, spell)
            resolveStack(driver)
            driver.getHandSize(me) shouldBe before // -1 cast, +1 draw
        }

        // Second legendary spell the SAME turn -> "triggers only once each turn": no draw (net -1).
        driver.putCardInHand(me, "Test Legend Two").let { spell ->
            driver.giveColorlessMana(me, 1)
            val before = driver.getHandSize(me)
            driver.castSpell(me, spell)
            resolveStack(driver)
            driver.getHandSize(me) shouldBe before - 1 // -1 cast, no draw
        }
    }

    test("front: casting a NON-legendary spell does not draw") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        driver.putPermanentOnBattlefield(me, "Venat, Heart of Hydaelyn")

        val spell = driver.putCardInHand(me, "Test Vanilla")
        driver.giveColorlessMana(me, 1)
        val before = driver.getHandSize(me)
        driver.castSpell(me, spell)
        resolveStack(driver)
        driver.getHandSize(me) shouldBe before - 1 // -1 cast, no draw
    }

    test("Hero's Sundering is sorcery-speed") {
        VenatHeartOfHydaelyn.activatedAbilities.first().timing shouldBe TimingRule.SorcerySpeed
    }

    test("Hero's Sundering: {7},{T} exiles target nonland permanent and transforms Venat") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)

        val venat = driver.putPermanentOnBattlefield(me, "Venat, Heart of Hydaelyn")
        driver.removeSummoningSickness(venat) // for the {T} cost
        val victim = driver.putCreatureOnBattlefield(opp, "Test Vanilla")
        driver.giveColorlessMana(me, 7)

        val abilityId = VenatHeartOfHydaelyn.activatedAbilities.first().id
        driver.submitSuccess(
            ActivateAbility(
                playerId = me,
                sourceId = venat,
                abilityId = abilityId,
                targets = listOf(ChosenTarget.Permanent(victim)),
            )
        )
        driver.bothPass()
        resolveStack(driver)

        // Target exiled (gone from the battlefield).
        driver.findPermanent(opp, "Test Vanilla") shouldBe null
        // Venat transformed in place to its back face — same entity, now Hydaelyn (4/4, indestructible).
        val container = driver.state.getEntity(venat)!!
        container.get<CardComponent>()!!.name shouldBe "Hydaelyn, the Mothercrystal"
        val projected = projector.project(driver.state)
        projected.getPower(venat) shouldBe 4
        projected.getToughness(venat) shouldBe 4
        projected.hasKeyword(venat, Keyword.INDESTRUCTIBLE) shouldBe true
    }

    test("Blessing of Light: legendary target gets +1/+1, indestructible, and draws a card") {
        val driver = createDriver()
        val me = driver.activePlayer!!

        driver.putPermanentOnBattlefield(me, "Hydaelyn, the Mothercrystal")
        val ally = driver.putCreatureOnBattlefield(me, "Test Legend One") // another legendary creature
        val before = driver.getHandSize(me)

        driver.passPriorityUntil(Step.BEGIN_COMBAT)
        // Only legal target is the legendary ally; resolve the trigger (auto-target if needed).
        var guard = 0
        while (guard++ < 12 && (driver.isPaused || driver.state.stack.isNotEmpty())) {
            if (driver.isPaused) driver.submitTargetSelection(me, listOf(ally)) else driver.bothPass()
        }

        driver.state.getEntity(ally)!!.get<CountersComponent>()!!
            .getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 1
        projector.project(driver.state).hasKeyword(ally, Keyword.INDESTRUCTIBLE) shouldBe true
        driver.getHandSize(me) shouldBe before + 1 // legendary -> draw a card
    }

    test("Blessing of Light: non-legendary target gets +1/+1 and indestructible but no draw") {
        val driver = createDriver()
        val me = driver.activePlayer!!

        driver.putPermanentOnBattlefield(me, "Hydaelyn, the Mothercrystal")
        val ally = driver.putCreatureOnBattlefield(me, "Test Vanilla") // non-legendary
        val before = driver.getHandSize(me)

        driver.passPriorityUntil(Step.BEGIN_COMBAT)
        var guard = 0
        while (guard++ < 12 && (driver.isPaused || driver.state.stack.isNotEmpty())) {
            if (driver.isPaused) driver.submitTargetSelection(me, listOf(ally)) else driver.bothPass()
        }

        driver.state.getEntity(ally)!!.get<CountersComponent>()!!
            .getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 1
        projector.project(driver.state).hasKeyword(ally, Keyword.INDESTRUCTIBLE) shouldBe true
        driver.getHandSize(me) shouldBe before // non-legendary -> no draw
    }
})

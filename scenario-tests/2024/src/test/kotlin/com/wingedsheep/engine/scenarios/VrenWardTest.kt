package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ManaSourcesSelectedResponse
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.blb.cards.VrenTheRelentless
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Vren, the Relentless (BLB): "Ward {2}".
 *
 * Regression guard: Vren used to carry only the bare WARD keyword (display-only, no
 * cost), so opponents could target it for free. It must be KeywordAbility.Ward with a
 * {2} mana cost: a spell an opponent casts targeting Vren is countered unless they pay.
 */
class VrenWardTest : FunSpec({

    val testExile: CardDefinition = card("Test Exile Instant") {
        manaCost = "{B}"
        typeLine = "Instant"
        spell {
            val creature = target("target creature to exile", Targets.Creature)
            effect = Effects.Exile(creature)
        }
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(VrenTheRelentless, testExile))
        return driver
    }

    test("targeting Vren without mana to pay ward counters the spell") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40))
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val vren = driver.putCreatureOnBattlefield(opponent, "Vren, the Relentless")

        // Exactly enough mana for the spell itself — nothing left for ward.
        driver.giveMana(active, Color.BLACK, 1)
        val spell = driver.putCardInHand(active, "Test Exile Instant")
        driver.castSpellWithTargets(active, spell, listOf(ChosenTarget.Permanent(vren)))

        repeat(4) { if (driver.state.priorityPlayerId != null) driver.bothPass() }

        // Ward could not be paid → the spell is countered; Vren survives.
        driver.findPermanent(opponent, "Vren, the Relentless") shouldNotBe null
        driver.state.getZone(opponent, Zone.EXILE).contains(vren) shouldBe false
    }

    test("paying {2} for ward lets the spell resolve") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40))
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val vren = driver.putCreatureOnBattlefield(opponent, "Vren, the Relentless")

        // Two untapped lands to pay ward with, plus floating mana for the spell.
        driver.putLandOnBattlefield(active, "Swamp")
        driver.putLandOnBattlefield(active, "Swamp")
        driver.giveMana(active, Color.BLACK, 1)
        val spell = driver.putCardInHand(active, "Test Exile Instant")
        driver.castSpellWithTargets(active, spell, listOf(ChosenTarget.Permanent(vren)))

        // Ward trigger resolves → the caster is prompted to pay {2}.
        driver.bothPass()
        val decision = driver.pendingDecision
        decision.shouldNotBeNull()
        decision.shouldBeInstanceOf<SelectManaSourcesDecision>()
        decision.playerId shouldBe active

        driver.submitDecision(active, ManaSourcesSelectedResponse(decision.id, autoPay = true))

        repeat(4) { if (driver.state.priorityPlayerId != null) driver.bothPass() }

        // Ward was paid — the exile spell resolves. Vren's own replacement effect only
        // covers dying (battlefield→graveyard), so it is exiled.
        driver.findPermanent(opponent, "Vren, the Relentless") shouldBe null
        driver.state.getZone(opponent, Zone.EXILE).contains(vren) shouldBe true
    }
})

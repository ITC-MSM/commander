package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.RestlessVents
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Restless Vents (LCI #284).
 *
 * Land
 *  This land enters tapped.
 *  {T}: Add {B} or {R}.
 *  {1}{B}{R}: Until end of turn, this land becomes a 2/3 black and red Insect creature with menace.
 *    It's still a land.
 *  Whenever this land attacks, you may discard a card. If you do, draw a card.
 *
 * Exercises the Restless creature-land cycle: enters-tapped replacement, the manland animate into a
 * 2/3 black-red Insect with menace that is still a land, and the printed rummage attack trigger
 * (discard-then-draw, with the "if you do" gate so declining draws nothing).
 */
class RestlessVentsScenarioTest : FunSpec({

    val animateAbilityId = RestlessVents.activatedAbilities[2].id // {1}{B}{R}: become 2/3 Insect
    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCards(listOf(RestlessVents))
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        return driver
    }

    fun animate(driver: GameTestDriver, player: EntityId, land: EntityId) {
        driver.giveMana(player, Color.BLACK, 1)
        driver.giveMana(player, Color.RED, 1)
        driver.giveColorlessMana(player, 1)
        driver.submit(
            ActivateAbility(playerId = player, sourceId = land, abilityId = animateAbilityId)
        ).isSuccess shouldBe true
        driver.bothPass()
    }

    test("enters tapped when played from hand") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val vents = driver.putCardInHand(player, "Restless Vents")
        driver.playLand(player, vents).isSuccess shouldBe true

        driver.isTapped(vents) shouldBe true
    }

    test("animates into a 2/3 black-red Insect with menace that is still a land") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val vents = driver.putLandOnBattlefield(player, "Restless Vents")
        animate(driver, player, vents)

        val projected = projector.project(driver.state)
        projected.hasType(vents, "CREATURE") shouldBe true
        projected.hasType(vents, "LAND") shouldBe true // it's still a land
        projected.hasSubtype(vents, "Insect") shouldBe true
        projected.hasKeyword(vents, Keyword.MENACE) shouldBe true
        projected.getPower(vents) shouldBe 2
        projected.getToughness(vents) shouldBe 3
    }

    test("reverts to a plain land next turn") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val vents = driver.putLandOnBattlefield(player, "Restless Vents")
        animate(driver, player, vents)
        projector.project(driver.state).hasType(vents, "CREATURE") shouldBe true

        driver.passPriorityUntil(Step.UPKEEP)
        val next = projector.project(driver.state)
        next.hasType(vents, "CREATURE") shouldBe false
        next.hasType(vents, "LAND") shouldBe true
    }

    test("attack trigger: accepting discards a card, then draws a card") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val vents = driver.putLandOnBattlefield(player, "Restless Vents")
        driver.removeSummoningSickness(vents)
        animate(driver, player, vents)

        // A card to discard, and a known top-of-library card to draw.
        driver.putCardInHand(player, "Swamp")
        val drawTarget = driver.putCardOnTopOfLibrary(player, "Mountain")

        val handBefore = driver.getHandSize(player)
        val gyBefore = driver.getGraveyard(player).size

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(player, listOf(vents), opponent).isSuccess shouldBe true

        // Rummage trigger: yes, discard the card, then draw the top card.
        var safety = 0
        while (safety < 40) {
            when (val pending = driver.state.pendingDecision) {
                is YesNoDecision -> driver.submitYesNo(pending.playerId, true)
                is SelectCardsDecision -> driver.submitCardSelection(pending.playerId, pending.options.take(1))
                else -> if (driver.stackSize > 0) driver.bothPass() else break
            }
            safety++
        }

        // Net hand size unchanged (discard one, draw one); a card hit the graveyard; drew the seeded card.
        driver.getHandSize(player) shouldBe handBefore
        (driver.getGraveyard(player).size - gyBefore) shouldBe 1
        driver.getHand(player).contains(drawTarget) shouldBe true
    }

    test("attack trigger: declining discards nothing and draws nothing") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val vents = driver.putLandOnBattlefield(player, "Restless Vents")
        driver.removeSummoningSickness(vents)
        animate(driver, player, vents)

        driver.putCardInHand(player, "Swamp")
        val handBefore = driver.getHandSize(player)
        val gyBefore = driver.getGraveyard(player).size

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(player, listOf(vents), opponent).isSuccess shouldBe true

        var safety = 0
        while (safety < 40) {
            when (val pending = driver.state.pendingDecision) {
                is YesNoDecision -> driver.submitYesNo(pending.playerId, false)
                is SelectCardsDecision -> driver.submitCardSelection(pending.playerId, emptyList())
                else -> if (driver.stackSize > 0) driver.bothPass() else break
            }
            safety++
        }

        driver.getHandSize(player) shouldBe handBefore
        driver.getGraveyard(player).size shouldBe gyBefore
    }
})

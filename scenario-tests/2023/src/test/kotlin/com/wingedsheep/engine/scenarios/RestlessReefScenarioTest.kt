package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.RestlessReef
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Restless Reef (LCI #282).
 *
 * Land
 *  This land enters tapped.
 *  {T}: Add {U} or {B}.
 *  {2}{U}{B}: Until end of turn, this land becomes a 4/4 blue and black Shark creature with
 *    deathtouch. It's still a land.
 *  Whenever this land attacks, target player mills four cards.
 *
 * Exercises the Restless creature-land cycle: enters-tapped replacement, the manland animate into a
 * 4/4 blue-black Shark with deathtouch that is still a land, reverting next turn, and the printed
 * attack trigger that mills four cards from a target player.
 */
class RestlessReefScenarioTest : FunSpec({

    val animateAbilityId = RestlessReef.activatedAbilities[2].id // {2}{U}{B}: become 4/4 Shark
    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCards(listOf(RestlessReef))
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), startingLife = 20)
        return driver
    }

    fun animate(driver: GameTestDriver, player: EntityId, reef: EntityId) {
        driver.giveMana(player, Color.BLUE, 1)
        driver.giveMana(player, Color.BLACK, 1)
        driver.giveColorlessMana(player, 2)
        driver.submit(
            ActivateAbility(playerId = player, sourceId = reef, abilityId = animateAbilityId)
        ).isSuccess shouldBe true
        driver.bothPass()
    }

    test("enters tapped when played from hand") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val reef = driver.putCardInHand(player, "Restless Reef")
        driver.playLand(player, reef).isSuccess shouldBe true

        driver.isTapped(reef) shouldBe true
    }

    test("animates into a 4/4 blue-black Shark with deathtouch that is still a land") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val reef = driver.putLandOnBattlefield(player, "Restless Reef")
        animate(driver, player, reef)

        val projected = projector.project(driver.state)
        projected.hasType(reef, "CREATURE") shouldBe true
        projected.hasType(reef, "LAND") shouldBe true // it's still a land
        projected.hasSubtype(reef, "Shark") shouldBe true
        projected.getPower(reef) shouldBe 4
        projected.getToughness(reef) shouldBe 4
        projected.hasKeyword(reef, Keyword.DEATHTOUCH) shouldBe true
    }

    test("reverts to a plain land next turn") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val reef = driver.putLandOnBattlefield(player, "Restless Reef")
        animate(driver, player, reef)
        projector.project(driver.state).hasType(reef, "CREATURE") shouldBe true

        driver.passPriorityUntil(Step.UPKEEP)
        val next = projector.project(driver.state)
        next.hasType(reef, "CREATURE") shouldBe false
        next.hasType(reef, "LAND") shouldBe true
    }

    test("attack trigger mills four cards from the target player") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val reef = driver.putLandOnBattlefield(player, "Restless Reef")
        driver.removeSummoningSickness(reef)
        animate(driver, player, reef)

        val graveyardBefore = driver.getGraveyard(opponent).size

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(player, listOf(reef), opponent)
        // The attack trigger goes on the stack; choose the milled player, then resolve.
        driver.submitTargetSelection(player, listOf(opponent))
        driver.bothPass()

        driver.getGraveyard(opponent).size shouldBe graveyardBefore + 4
    }
})

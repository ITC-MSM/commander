package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.RestlessAnchorage
import com.wingedsheep.mtg.sets.tokens.PredefinedTokens
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Restless Anchorage (LCI #280) — Land, creature-land "Restless" cycle.
 *
 * This land enters tapped.
 * {T}: Add {W} or {U}.
 * {1}{W}{U}: Until end of turn, this land becomes a 2/3 white and blue Bird creature with
 *   flying. It's still a land.
 * Whenever this land attacks, create a Map token.
 *
 * Mirrors Raging Ravine's structure (enters tapped + dual mana + BecomeCreature animate) but the
 * attack trigger is an intrinsic ability of the land itself, so it's a separate triggeredAbility.
 */
class RestlessAnchorageScenarioTest : FunSpec({

    // activatedAbilities: [0] = Add {W}, [1] = Add {U}, [2] = animate ({1}{W}{U})
    val animateAbilityId = RestlessAnchorage.activatedAbilities[2].id
    val whiteManaAbilityId = RestlessAnchorage.activatedAbilities[0].id
    val projector = StateProjector()

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCards(PredefinedTokens.allTokens)
        driver.registerCard(RestlessAnchorage)
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), startingLife = 20)
        return driver
    }

    test("enters the battlefield tapped") {
        val driver = newDriver()
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val landCard = driver.putCardInHand(player, "Restless Anchorage")
        driver.playLand(player, landCard).isSuccess shouldBe true

        driver.isTapped(landCard) shouldBe true
    }

    test("mana ability adds white mana") {
        val driver = newDriver()
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val land = driver.putLandOnBattlefield(player, "Restless Anchorage")
        driver.submit(
            ActivateAbility(playerId = player, sourceId = land, abilityId = whiteManaAbilityId)
        ).isSuccess shouldBe true

        val pool = driver.state.getEntity(player)?.get<ManaPoolComponent>()
        (pool?.white ?: 0) shouldBe 1
        driver.isTapped(land) shouldBe true
    }

    test("animates into a 2/3 white and blue Bird with flying that is still a land") {
        val driver = newDriver()
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val land = driver.putLandOnBattlefield(player, "Restless Anchorage")
        driver.giveColorlessMana(player, 1)
        driver.giveMana(player, Color.WHITE, 1)
        driver.giveMana(player, Color.BLUE, 1)

        driver.submit(
            ActivateAbility(playerId = player, sourceId = land, abilityId = animateAbilityId)
        ).isSuccess shouldBe true
        driver.bothPass()

        val projected = projector.project(driver.state)
        projected.hasType(land, "CREATURE") shouldBe true
        projected.hasType(land, "LAND") shouldBe true // it's still a land
        projected.hasSubtype(land, "Bird") shouldBe true
        projected.hasKeyword(land, Keyword.FLYING) shouldBe true
        projected.hasColor(land, Color.WHITE) shouldBe true
        projected.hasColor(land, Color.BLUE) shouldBe true
        projected.getPower(land) shouldBe 2
        projected.getToughness(land) shouldBe 3
    }

    test("reverts to a plain land at the next turn") {
        val driver = newDriver()
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val land = driver.putLandOnBattlefield(player, "Restless Anchorage")
        driver.giveColorlessMana(player, 1)
        driver.giveMana(player, Color.WHITE, 1)
        driver.giveMana(player, Color.BLUE, 1)
        driver.submit(
            ActivateAbility(playerId = player, sourceId = land, abilityId = animateAbilityId)
        )
        driver.bothPass()
        projector.project(driver.state).hasType(land, "CREATURE") shouldBe true

        driver.passPriorityUntil(Step.UPKEEP)
        val next = projector.project(driver.state)
        next.hasType(land, "CREATURE") shouldBe false
        next.hasType(land, "LAND") shouldBe true
    }

    test("attacking as a creature creates a Map token") {
        val driver = newDriver()
        val player = driver.activePlayer!!
        val opponent = if (player == driver.player1) driver.player2 else driver.player1
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val land = driver.putLandOnBattlefield(player, "Restless Anchorage")
        driver.giveColorlessMana(player, 1)
        driver.giveMana(player, Color.WHITE, 1)
        driver.giveMana(player, Color.BLUE, 1)
        driver.submit(
            ActivateAbility(playerId = player, sourceId = land, abilityId = animateAbilityId)
        )
        driver.bothPass()

        // No Map token yet.
        driver.findPermanent(player, "Map") shouldBe null

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(player, listOf(land), opponent).isSuccess shouldBe true
        driver.bothPass()

        // The attack trigger created a Map token on the controller's battlefield.
        driver.findPermanent(player, "Map") shouldNotBe null
    }
})

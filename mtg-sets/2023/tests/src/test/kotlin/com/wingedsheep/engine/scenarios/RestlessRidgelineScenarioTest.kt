package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.RestlessRidgeline
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Restless Ridgeline (LCI #283).
 *
 * Land
 *  This land enters tapped.
 *  {T}: Add {R} or {G}.
 *  {2}{R}{G}: This land becomes a 3/4 red and green Dinosaur creature until end of turn. It's still a land.
 *  Whenever this land attacks, another target attacking creature gets +2/+0 until end of turn. Untap that creature.
 *
 * Exercises the Restless creature-land cycle: enters-tapped replacement, the manland animate into a
 * 3/4 red-green Dinosaur that is still a land, and the printed attack trigger that pumps ANOTHER
 * target attacking creature (+2/+0) and untaps it.
 */
class RestlessRidgelineScenarioTest : FunSpec({

    val animateAbilityId = RestlessRidgeline.activatedAbilities[2].id // {2}{R}{G}: become 3/4 Dinosaur
    val projector = StateProjector()

    // A plain 2/2 vanilla creature to serve as the "another attacking creature" target.
    val genericBear = CardDefinition.creature(
        name = "Generic Test Bear",
        manaCost = ManaCost.parse("{2}"),
        subtypes = setOf(Subtype("Bear")),
        power = 2,
        toughness = 2,
    )

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCards(listOf(RestlessRidgeline, genericBear))
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        return driver
    }

    fun animate(driver: GameTestDriver, player: EntityId, ridgeline: EntityId) {
        driver.giveMana(player, Color.RED, 1)
        driver.giveMana(player, Color.GREEN, 1)
        driver.giveColorlessMana(player, 2)
        driver.submit(
            ActivateAbility(playerId = player, sourceId = ridgeline, abilityId = animateAbilityId)
        ).isSuccess shouldBe true
        driver.bothPass()
    }

    test("enters tapped when played from hand") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val ridgeline = driver.putCardInHand(player, "Restless Ridgeline")
        driver.playLand(player, ridgeline).isSuccess shouldBe true

        driver.isTapped(ridgeline) shouldBe true
    }

    test("animates into a 3/4 red-green Dinosaur that is still a land") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val ridgeline = driver.putLandOnBattlefield(player, "Restless Ridgeline")
        animate(driver, player, ridgeline)

        val projected = projector.project(driver.state)
        projected.hasType(ridgeline, "CREATURE") shouldBe true
        projected.hasType(ridgeline, "LAND") shouldBe true // it's still a land
        projected.hasSubtype(ridgeline, "Dinosaur") shouldBe true
        projected.getPower(ridgeline) shouldBe 3
        projected.getToughness(ridgeline) shouldBe 4
    }

    test("reverts to a plain land next turn") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val ridgeline = driver.putLandOnBattlefield(player, "Restless Ridgeline")
        animate(driver, player, ridgeline)
        projector.project(driver.state).hasType(ridgeline, "CREATURE") shouldBe true

        driver.passPriorityUntil(Step.UPKEEP)
        val next = projector.project(driver.state)
        next.hasType(ridgeline, "CREATURE") shouldBe false
        next.hasType(ridgeline, "LAND") shouldBe true
    }

    test("attack trigger gives another attacking creature +2/+0 and untaps it") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val ridgeline = driver.putLandOnBattlefield(player, "Restless Ridgeline")
        val bear = driver.putCreatureOnBattlefield(player, "Generic Test Bear")
        driver.removeSummoningSickness(ridgeline)
        driver.removeSummoningSickness(bear)

        animate(driver, player, ridgeline)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(player, listOf(ridgeline, bear), opponent)

        // Attack trigger requires "another target attacking creature" — pick the bear.
        (driver.pendingDecision is ChooseTargetsDecision) shouldBe true
        driver.submitTargetSelection(player, listOf(bear)).isSuccess shouldBe true
        driver.bothPass()

        val projected = projector.project(driver.state)
        // Bear gets +2/+0.
        projected.getPower(bear) shouldBe 4
        projected.getToughness(bear) shouldBe 2
        // ...and is untapped by the trigger even though it was tapped attacking.
        driver.isTapped(bear) shouldBe false
        // The Ridgeline itself is not buffed (it's "another" target) and stays tapped from attacking.
        projected.getPower(ridgeline) shouldBe 3
        driver.isTapped(ridgeline) shouldBe true
    }
})

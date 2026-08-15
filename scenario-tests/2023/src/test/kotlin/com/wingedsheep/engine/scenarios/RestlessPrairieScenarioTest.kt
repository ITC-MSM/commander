package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.RestlessPrairie
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Restless Prairie (LCI #281).
 *
 * Land
 *  This land enters tapped.
 *  {T}: Add {G} or {W}.
 *  {2}{G}{W}: This land becomes a 3/3 green and white Llama creature until end of turn. It's still a land.
 *  Whenever this land attacks, other creatures you control get +1/+1 until end of turn.
 *
 * Exercises the Restless creature-land cycle: enters-tapped replacement, the manland animate into a
 * 3/3 green-white Llama that is still a land, and the printed attack trigger that pumps OTHER creatures
 * you control (excluding the land itself).
 */
class RestlessPrairieScenarioTest : FunSpec({

    val animateAbilityId = RestlessPrairie.activatedAbilities[2].id // {2}{G}{W}: become 3/3 Llama
    val projector = StateProjector()

    // A plain 2/2 vanilla creature to observe the attack-trigger team pump.
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
        driver.registerCards(listOf(RestlessPrairie, genericBear))
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        return driver
    }

    test("enters tapped when played from hand") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val prairie = driver.putCardInHand(player, "Restless Prairie")
        driver.playLand(player, prairie).isSuccess shouldBe true

        driver.isTapped(prairie) shouldBe true
    }

    test("animates into a 3/3 green-white Llama that is still a land") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val prairie = driver.putLandOnBattlefield(player, "Restless Prairie")
        driver.giveMana(player, Color.GREEN, 1)
        driver.giveMana(player, Color.WHITE, 1)
        driver.giveColorlessMana(player, 2)

        driver.submit(
            ActivateAbility(playerId = player, sourceId = prairie, abilityId = animateAbilityId)
        ).isSuccess shouldBe true
        driver.bothPass()

        val projected = projector.project(driver.state)
        projected.hasType(prairie, "CREATURE") shouldBe true
        projected.hasType(prairie, "LAND") shouldBe true // it's still a land
        projected.hasSubtype(prairie, "Llama") shouldBe true
        projected.getPower(prairie) shouldBe 3
        projected.getToughness(prairie) shouldBe 3
    }

    test("reverts to a plain land next turn") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val prairie = driver.putLandOnBattlefield(player, "Restless Prairie")
        driver.giveMana(player, Color.GREEN, 1)
        driver.giveMana(player, Color.WHITE, 1)
        driver.giveColorlessMana(player, 2)
        driver.submit(
            ActivateAbility(playerId = player, sourceId = prairie, abilityId = animateAbilityId)
        )
        driver.bothPass()
        projector.project(driver.state).hasType(prairie, "CREATURE") shouldBe true

        driver.passPriorityUntil(Step.UPKEEP)
        val next = projector.project(driver.state)
        next.hasType(prairie, "CREATURE") shouldBe false
        next.hasType(prairie, "LAND") shouldBe true
    }

    test("attack trigger pumps other creatures you control but not the land itself") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val prairie = driver.putLandOnBattlefield(player, "Restless Prairie")
        val bear = driver.putCreatureOnBattlefield(player, "Generic Test Bear")
        driver.removeSummoningSickness(prairie)
        driver.removeSummoningSickness(bear)

        // Animate the land so it can attack.
        driver.giveMana(player, Color.GREEN, 1)
        driver.giveMana(player, Color.WHITE, 1)
        driver.giveColorlessMana(player, 2)
        driver.submit(
            ActivateAbility(playerId = player, sourceId = prairie, abilityId = animateAbilityId)
        ).isSuccess shouldBe true
        driver.bothPass()

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(player, listOf(prairie), opponent)
        driver.bothPass()

        val projected = projector.project(driver.state)
        // Other creature gets +1/+1.
        projected.getPower(bear) shouldBe 3
        projected.getToughness(bear) shouldBe 3
        // The land itself is excluded (stays 3/3, no extra buff).
        projected.getPower(prairie) shouldBe 3
        projected.getToughness(prairie) shouldBe 3
    }
})

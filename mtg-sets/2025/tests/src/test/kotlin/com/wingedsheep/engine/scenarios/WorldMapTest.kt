package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.CapitalCity
import com.wingedsheep.mtg.sets.definitions.fin.cards.WorldMap
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * World Map — {1} Artifact
 * "{1}, {T}, Sacrifice this artifact: Search your library for a basic land card ..."
 * "{3}, {T}, Sacrifice this artifact: Search your library for a land card ..."
 *
 * Verifies the second ability searches for ANY land (the generated draft
 * originally restricted it to basic lands like the first ability).
 */
class WorldMapTest : FunSpec({

    // Second activated ability: "{3}, {T}, Sacrifice: search for a land card".
    val anyLandAbilityId = WorldMap.activatedAbilities[1].id

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(WorldMap)
        driver.registerCard(CapitalCity)
        return driver
    }

    test("the {3} ability can search up a nonbasic land") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 30), startingLife = 20)

        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val map = driver.putPermanentOnBattlefield(player, "World Map")
        val capitalCity = driver.putCardOnTopOfLibrary(player, "Capital City")
        driver.giveColorlessMana(player, 3)

        driver.submit(ActivateAbility(playerId = player, sourceId = map, abilityId = anyLandAbilityId))
        driver.bothPass() // resolve the ability off the stack -> library search decision

        // Library search offers the nonbasic land.
        val decision = driver.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
        decision.options.contains(capitalCity) shouldBe true

        driver.submitCardSelection(player, listOf(capitalCity))

        driver.findCardInHand(player, "Capital City") shouldNotBe null
    }
})

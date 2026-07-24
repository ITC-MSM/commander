package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dgm.cards.MazesEnd
import com.wingedsheep.mtg.sets.definitions.rtr.cards.AzoriusGuildgate
import com.wingedsheep.mtg.sets.definitions.gtc.cards.BorosGuildgate
import com.wingedsheep.mtg.sets.definitions.gtc.cards.DimirGuildgate
import com.wingedsheep.mtg.sets.definitions.rtr.cards.GolgariGuildgate
import com.wingedsheep.mtg.sets.definitions.gtc.cards.GruulGuildgate
import com.wingedsheep.mtg.sets.definitions.rtr.cards.IzzetGuildgate
import com.wingedsheep.mtg.sets.definitions.gtc.cards.OrzhovGuildgate
import com.wingedsheep.mtg.sets.definitions.rtr.cards.RakdosGuildgate
import com.wingedsheep.mtg.sets.definitions.rtr.cards.SelesnyaGuildgate
import com.wingedsheep.mtg.sets.definitions.gtc.cards.SimicGuildgate
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Maze's End (DGM #152, reprinted in FDN #727) — Land.
 *
 * "This land enters tapped.
 *  {T}: Add {C}.
 *  {3}, {T}, Return this land to its owner's hand: Search your library for a Gate card, put it
 *  onto the battlefield, then shuffle. If you control ten or more Gates with different names,
 *  you win the game."
 *
 * Covers the self-bounce activation cost (`Costs.ReturnSelfToHand`), the Gate tutor, and the
 * distinct-name win check evaluated at resolution *after* the search.
 */
class MazesEndScenarioTest : FunSpec({

    val guildgates = listOf(
        AzoriusGuildgate, BorosGuildgate, DimirGuildgate, GolgariGuildgate, GruulGuildgate,
        IzzetGuildgate, OrzhovGuildgate, RakdosGuildgate, SelesnyaGuildgate, SimicGuildgate
    )

    /** The tutor ability — the second activated ability (the first is the {T}: Add {C} mana ability). */
    val tutorAbilityId = MazesEnd.activatedAbilities[1].id

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(MazesEnd)
        driver.registerCards(guildgates)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    /**
     * Put [count] differently-named Guildgates onto [player]'s battlefield, then seed the library
     * with the next distinct one so the tutor has something to find. Returns the library card's id.
     */
    fun setUpGates(driver: GameTestDriver, player: EntityId, count: Int): EntityId {
        guildgates.take(count).forEach { driver.putLandOnBattlefield(player, it.name) }
        return driver.putCardOnTopOfLibrary(player, guildgates[count].name)
    }

    fun activateTutor(driver: GameTestDriver, player: EntityId, mazesEnd: EntityId) {
        driver.giveMana(player, Color.GREEN, 3)
        driver.submit(
            ActivateAbility(playerId = player, sourceId = mazesEnd, abilityId = tutorAbilityId)
        ).isSuccess shouldBe true
    }

    test("returning Maze's End to hand is part of the cost — it is in hand before the ability resolves") {
        val driver = newDriver()
        val player = driver.player1

        setUpGates(driver, player, 2)
        val mazesEnd = driver.putLandOnBattlefield(player, "Maze's End")

        activateTutor(driver, player, mazesEnd)

        // Cost paid on activation, before anyone gets priority: the land has already left.
        driver.state.getZone(ZoneKey(player, Zone.HAND)).contains(mazesEnd) shouldBe true
        driver.state.getZone(ZoneKey(player, Zone.BATTLEFIELD)).contains(mazesEnd) shouldBe false
    }

    test("tutors a Gate onto the battlefield") {
        val driver = newDriver()
        val player = driver.player1

        val libraryGate = setUpGates(driver, player, 2)
        val mazesEnd = driver.putLandOnBattlefield(player, "Maze's End")

        activateTutor(driver, player, mazesEnd)
        driver.bothPass()
        driver.submitCardSelection(player, listOf(libraryGate))

        driver.state.getZone(ZoneKey(player, Zone.BATTLEFIELD)).contains(libraryGate) shouldBe true
        driver.state.gameOver shouldBe false
    }

    test("nine differently-named Gates after the search is not enough to win") {
        val driver = newDriver()
        val player = driver.player1

        val libraryGate = setUpGates(driver, player, 8) // 8 + the tutored one = 9
        val mazesEnd = driver.putLandOnBattlefield(player, "Maze's End")

        activateTutor(driver, player, mazesEnd)
        driver.bothPass()
        driver.submitCardSelection(player, listOf(libraryGate))

        driver.state.getZone(ZoneKey(player, Zone.BATTLEFIELD)).contains(libraryGate) shouldBe true
        driver.state.gameOver shouldBe false
    }

    test("tutoring the tenth differently-named Gate wins the game") {
        val driver = newDriver()
        val player = driver.player1

        val libraryGate = setUpGates(driver, player, 9) // 9 + the tutored one = 10
        val mazesEnd = driver.putLandOnBattlefield(player, "Maze's End")

        activateTutor(driver, player, mazesEnd)
        driver.bothPass()
        driver.submitCardSelection(player, listOf(libraryGate))

        driver.assertGameOver(expectedWinner = player)
    }

    test("duplicate Gate names don't count twice") {
        val driver = newDriver()
        val player = driver.player1

        // Nine Gates on the battlefield, but two of them share a name → eight distinct names.
        guildgates.take(8).forEach { driver.putLandOnBattlefield(player, it.name) }
        driver.putLandOnBattlefield(player, guildgates[0].name)
        val libraryGate = driver.putCardOnTopOfLibrary(player, guildgates[8].name)
        val mazesEnd = driver.putLandOnBattlefield(player, "Maze's End")

        activateTutor(driver, player, mazesEnd)
        driver.bothPass()
        driver.submitCardSelection(player, listOf(libraryGate))

        // Nine distinct names — no win.
        driver.state.getZone(ZoneKey(player, Zone.BATTLEFIELD)).contains(libraryGate) shouldBe true
        driver.state.gameOver shouldBe false
    }
})

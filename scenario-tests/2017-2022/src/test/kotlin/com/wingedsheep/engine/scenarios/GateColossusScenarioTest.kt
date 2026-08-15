package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.engine.state.ZoneKey
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Gate Colossus (RNA #232).
 *
 * {8} Artifact Creature — Construct 8/8
 *  Affinity for Gates
 *  This creature can't be blocked by creatures with power 2 or less.
 *  Whenever a Gate you control enters, you may put this card from your graveyard on top of
 *  your library.
 *
 * Covers all three lines: the cost reduction from Gates on the battlefield, the graveyard-zone
 * recursion trigger (accepted and declined), and the block restriction.
 */
class GateColossusScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun libraryTop(driver: GameTestDriver, player: EntityId): EntityId? =
        driver.state.getZone(ZoneKey(player, Zone.LIBRARY)).firstOrNull()

    test("Affinity for Gates: three Gates make the 8-drop cost 5") {
        val driver = createDriver()
        val player = driver.activePlayer!!

        repeat(3) { driver.putLandOnBattlefield(player, "Golgari Guildgate") }
        val colossus = driver.putCardInHand(player, "Gate Colossus")
        driver.giveColorlessMana(player, 5)

        val cast = driver.castSpell(player, colossus)
        withClue("{8} minus three Gates = {5}: ${cast.error}") { cast.error shouldBe null }
        driver.bothPass()

        driver.state.getBattlefield().contains(colossus) shouldBe true
    }

    test("Affinity for Gates does not over-reduce: three Gates and only 4 mana is not enough") {
        val driver = createDriver()
        val player = driver.activePlayer!!

        repeat(3) { driver.putLandOnBattlefield(player, "Golgari Guildgate") }
        val colossus = driver.putCardInHand(player, "Gate Colossus")
        driver.giveColorlessMana(player, 4)

        val cast = driver.castSpell(player, colossus)
        withClue("{5} cannot be paid with 4 mana") { (cast.error != null) shouldBe true }
        driver.state.getBattlefield().contains(colossus) shouldBe false
    }

    test("a Gate entering while Gate Colossus is in the graveyard puts it on top of the library") {
        val driver = createDriver()
        val player = driver.activePlayer!!

        val colossus = driver.putCardInGraveyard(player, "Gate Colossus")
        val gate = driver.putCardInHand(player, "Golgari Guildgate")

        val play = driver.playLand(player, gate)
        withClue("playing the Gate should succeed: ${play.error}") { play.error shouldBe null }
        driver.bothPass()

        withClue("the graveyard trigger offers the optional recursion") {
            (driver.pendingDecision is YesNoDecision) shouldBe true
        }
        driver.submitYesNo(player, true)

        withClue("Gate Colossus has left the graveyard") {
            driver.getGraveyard(player).contains(colossus) shouldBe false
        }
        withClue("Gate Colossus is on TOP of the library") {
            libraryTop(driver, player) shouldBe colossus
        }
    }

    test("declining the trigger leaves Gate Colossus in the graveyard") {
        val driver = createDriver()
        val player = driver.activePlayer!!

        val colossus = driver.putCardInGraveyard(player, "Gate Colossus")
        val gate = driver.putCardInHand(player, "Golgari Guildgate")

        driver.playLand(player, gate)
        driver.bothPass()

        (driver.pendingDecision is YesNoDecision) shouldBe true
        driver.submitYesNo(player, false)

        withClue("declining keeps Gate Colossus in the graveyard") {
            driver.getGraveyard(player).contains(colossus) shouldBe true
        }
        (libraryTop(driver, player) == colossus) shouldBe false
    }

    test("can't be blocked by a power-2 creature, but a power-3 creature can block") {
        val driver = createDriver()
        val player = driver.player1
        val opponent = driver.player2

        val colossus = driver.putCreatureOnBattlefield(player, "Gate Colossus")
        driver.removeSummoningSickness(colossus)
        val bears = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")   // 2/2
        val giant = driver.putCreatureOnBattlefield(opponent, "Hill Giant")      // 3/3
        driver.removeSummoningSickness(bears)
        driver.removeSummoningSickness(giant)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(player, listOf(colossus), opponent)
        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)

        val illegal = driver.declareBlockers(opponent, mapOf(bears to listOf(colossus)))
        withClue("a power-2 creature may not block Gate Colossus") {
            (illegal.error != null) shouldBe true
        }

        val legal = driver.declareBlockers(opponent, mapOf(giant to listOf(colossus)))
        withClue("a power-3 creature may block: ${legal.error}") { legal.error shouldBe null }
    }
})

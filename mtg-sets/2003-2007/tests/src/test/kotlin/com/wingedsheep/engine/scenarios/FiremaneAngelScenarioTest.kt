package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.legalactions.EnumerationMode
import com.wingedsheep.engine.legalactions.LegalActionEnumerator
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.rav.cards.FiremaneAngel
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Firemane Angel (RAV #205) — 4/3 flying, first strike.
 *
 * "At the beginning of your upkeep, if Firemane Angel is in your graveyard or on the battlefield,
 *  you may gain 1 life.
 *  {6}{R}{R}{W}{W}: Return this card from your graveyard to the battlefield. Activate only during
 *  your upkeep."
 *
 * The whole card turns on one thing: an ability that functions from **two** zones (CR 113.6b). A
 * card whose trigger silently kept the default battlefield-only `activeZones` still looks correct
 * on the battlefield and does nothing at all from the graveyard — which is the half that makes the
 * Angel playable — so the graveyard case is asserted first and the battlefield case beside it. The
 * "your upkeep" rider is asserted in both directions, because a trigger scoped to `Player.Each`
 * would pass a positive-only test.
 */
class FiremaneAngelScenarioTest : FunSpec({

    val returnAbility = FiremaneAngel.activatedAbilities.single().id

    /**
     * Player 2 takes the first turn, so the very next upkeep after turn 1's main phase belongs to
     * player 1 — the seat every test below sets up.
     */
    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + FiremaneAngel)
        driver.initMirrorMatch(
            deck = Deck.of("Mountain" to 40),
            startingLife = 20,
            startingPlayer = 1,
            skipMulligans = true,
        )
        return driver
    }

    fun canActivate(driver: GameTestDriver, player: EntityId, angel: EntityId): Boolean {
        val enumerator = LegalActionEnumerator.create(driver.cardRegistry)
        return enumerator.enumerate(driver.state, player, EnumerationMode.FULL)
            .any { (it.action as? ActivateAbility)?.sourceId == angel }
    }

    fun GameTestDriver.drainStack() {
        var guard = 0
        while (stackSize > 0 && guard++ < 50) bothPass()
    }

    /** Pay for the {6}{R}{R}{W}{W} recursion. */
    fun GameTestDriver.giveRecursionMana(player: EntityId) {
        giveColorlessMana(player, 6)
        giveMana(player, Color.RED, 2)
        giveMana(player, Color.WHITE, 2)
    }

    test("the upkeep trigger fires while the Angel is in your graveyard") {
        val driver = newDriver()
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.player1
        driver.putCardInGraveyard(me, "Firemane Angel")

        driver.passPriorityUntil(Step.UPKEEP)
        driver.activePlayer shouldBe me

        withClue("a battlefield-only activeZones would leave the stack empty here") {
            driver.stackSize shouldBe 1
        }
        driver.bothPass()
        driver.submitYesNo(me, true)
        driver.drainStack()

        driver.getLifeTotal(me) shouldBe 21
    }

    test("the same trigger fires from the battlefield") {
        val driver = newDriver()
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.player1
        driver.putPermanentOnBattlefield(me, "Firemane Angel")

        driver.passPriorityUntil(Step.UPKEEP)
        driver.activePlayer shouldBe me

        driver.stackSize shouldBe 1
        driver.bothPass()
        driver.submitYesNo(me, true)
        driver.drainStack()

        driver.getLifeTotal(me) shouldBe 21
    }

    test("declining the may gains nothing") {
        val driver = newDriver()
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.player1
        driver.putCardInGraveyard(me, "Firemane Angel")

        driver.passPriorityUntil(Step.UPKEEP)
        driver.bothPass()
        driver.submitYesNo(me, false)
        driver.drainStack()

        driver.getLifeTotal(me) shouldBe 20
    }

    test("it does not trigger on an opponent's upkeep") {
        val driver = newDriver()
        driver.passPriorityUntil(Step.UPKEEP)
        val opponent = driver.activePlayer!!
        val me = driver.getOpponent(opponent)
        driver.putCardInGraveyard(me, "Firemane Angel")
        driver.drainStack()

        // Advance to the *next* upkeep — the opponent's again, two turns on — without ever
        // reaching mine.
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.getLifeTotal(me) shouldBe 20
        withClue("nothing of mine goes on the stack during the active player's upkeep") {
            driver.stackSize shouldBe 0
        }
    }

    test("during your upkeep the Angel returns itself from the graveyard") {
        val driver = newDriver()
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.player1
        val angel = driver.putCardInGraveyard(me, "Firemane Angel")

        driver.passPriorityUntil(Step.UPKEEP)
        driver.bothPass()
        driver.submitYesNo(me, false)
        driver.drainStack()

        driver.giveRecursionMana(me)
        canActivate(driver, me, angel) shouldBe true

        driver.submit(ActivateAbility(playerId = me, sourceId = angel, abilityId = returnAbility))
            .isSuccess shouldBe true
        driver.drainStack()

        driver.state.getZone(ZoneKey(me, Zone.GRAVEYARD)).contains(angel) shouldBe false
        driver.getCreatures(me).size shouldBe 1
    }

    test("the recursion can't be activated outside your upkeep") {
        val driver = newDriver()
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.player1
        val angel = driver.putCardInGraveyard(me, "Firemane Angel")
        driver.giveRecursionMana(me)

        withClue("wrong turn *and* wrong step") {
            canActivate(driver, me, angel) shouldBe false
        }

        driver.passPriorityUntil(Step.UPKEEP)
        driver.bothPass()
        driver.submitYesNo(me, false)
        driver.drainStack()
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        withClue("right turn, wrong step") {
            canActivate(driver, me, angel) shouldBe false
        }
    }
})

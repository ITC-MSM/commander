package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.WindbriskHeights
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Windbrisk Heights (LRW #281).
 *
 * "Hideaway 4 … This land enters tapped. {T}: Add {W}.
 *  {W}, {T}: You may play the exiled card without paying its mana cost if you attacked with
 *  three or more creatures this turn."
 *
 * Covers the gate, which is the only part of the card that isn't shared with Mosswort Bridge:
 * the free-cast ability must be unavailable before an attack and after a two-creature attack,
 * and available once three creatures have been declared as attackers this turn.
 */
class WindbriskHeightsScenarioTest : FunSpec({

    /** Index 0 is the {T}: Add {W} mana ability; index 1 is the gated free-cast ability. */
    val freeCastAbilityId = WindbriskHeights.activatedAbilities[1].id

    fun freeCastOffered(driver: GameTestDriver, player: EntityId, land: EntityId): Boolean =
        driver.legalActions(player).any {
            val action = it.action
            action is ActivateAbility && action.sourceId == land && action.abilityId == freeCastAbilityId
        }

    /** Play the land from hand so hideaway runs, exile a card, then untap it (it enters tapped). */
    fun playWindbriskHeights(driver: GameTestDriver, player: EntityId): EntityId {
        val card = driver.putCardInHand(player, "Windbrisk Heights")
        driver.playLand(player, card)
        driver.bothPass() // resolve the hideaway trigger → pauses to exile one of the top four
        val decision = driver.pendingDecision
        require(decision is SelectCardsDecision) { "expected hideaway selection, got $decision" }
        driver.submitCardSelection(player, listOf(decision.options.first()))
        val land = driver.findPermanent(player, "Windbrisk Heights")!!
        driver.untapPermanent(land)
        return land
    }

    test("the free-cast ability is gated behind attacking with three or more creatures") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val land = playWindbriskHeights(driver, player)
        driver.giveMana(player, Color.WHITE, 1)

        freeCastOffered(driver, player, land) shouldBe false

        // Two attackers is not enough.
        val attackers = (1..3).map {
            driver.putCreatureOnBattlefield(player, "Grizzly Bears").also(driver::removeSummoningSickness)
        }
        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(player, attackers.take(2), driver.getOpponent(player))
        driver.giveMana(player, Color.WHITE, 1)

        freeCastOffered(driver, player, land) shouldBe false
    }

    test("attacking with three creatures opens the free-cast ability") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val land = playWindbriskHeights(driver, player)
        val attackers = (1..3).map {
            driver.putCreatureOnBattlefield(player, "Grizzly Bears").also(driver::removeSummoningSickness)
        }
        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(player, attackers, driver.getOpponent(player))
        driver.giveMana(player, Color.WHITE, 1)

        freeCastOffered(driver, player, land) shouldBe true
    }
})

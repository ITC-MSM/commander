package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.SpinerockKnoll
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Spinerock Knoll (LRW #274).
 *
 * "Hideaway 4 … This land enters tapped. {T}: Add {R}.
 *  {R}, {T}: You may play the exiled card without paying its mana cost if an opponent was dealt
 *  7 or more damage this turn."
 *
 * Covers the gate, which is the only part of the card that isn't shared with Mosswort Bridge:
 * six damage is not enough and seven is, and the threshold is measured against the opponent's
 * running total for the turn rather than against a single damage event.
 */
class SpinerockKnollScenarioTest : FunSpec({

    /** Index 0 is the {T}: Add {R} mana ability; index 1 is the gated free-cast ability. */
    val freeCastAbilityId = SpinerockKnoll.activatedAbilities[1].id

    fun freeCastOffered(driver: GameTestDriver, player: EntityId, land: EntityId): Boolean =
        driver.legalActions(player).any {
            val action = it.action
            action is ActivateAbility && action.sourceId == land && action.abilityId == freeCastAbilityId
        }

    /** Play the land from hand so hideaway runs, exile a card, then untap it (it enters tapped). */
    fun playSpinerockKnoll(driver: GameTestDriver, player: EntityId): EntityId {
        val card = driver.putCardInHand(player, "Spinerock Knoll")
        driver.playLand(player, card)
        driver.bothPass() // resolve the hideaway trigger → pauses to exile one of the top four
        val decision = driver.pendingDecision
        require(decision is SelectCardsDecision) { "expected hideaway selection, got $decision" }
        driver.submitCardSelection(player, listOf(decision.options.first()))
        val land = driver.findPermanent(player, "Spinerock Knoll")!!
        driver.untapPermanent(land)
        return land
    }

    /**
     * Attack an empty board with [count] 3/3s, so the defending player takes 3 × [count] damage.
     * Two of them is 6 — one short of the gate; three is 9.
     */
    fun attackWithBears(driver: GameTestDriver, player: EntityId, count: Int) {
        val attackers = (1..count).map {
            driver.putCreatureOnBattlefield(player, "Hill Giant").also(driver::removeSummoningSickness)
        }
        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(player, attackers, driver.getOpponent(player))
        driver.passPriorityUntil(Step.POSTCOMBAT_MAIN)
    }

    test("six damage to the opponent is not enough to open the free-cast ability") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val land = playSpinerockKnoll(driver, player)
        attackWithBears(driver, player, count = 2)
        driver.getLifeTotal(driver.getOpponent(player)) shouldBe 14
        driver.giveMana(player, Color.RED, 1)

        freeCastOffered(driver, player, land) shouldBe false
    }

    test("nine damage to the opponent opens the free-cast ability") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val land = playSpinerockKnoll(driver, player)
        attackWithBears(driver, player, count = 3)
        driver.getLifeTotal(driver.getOpponent(player)) shouldBe 11
        driver.giveMana(player, Color.RED, 1)

        freeCastOffered(driver, player, land) shouldBe true
    }
})

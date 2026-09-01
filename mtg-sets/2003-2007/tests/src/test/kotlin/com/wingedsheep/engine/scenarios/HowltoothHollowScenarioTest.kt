package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.HowltoothHollow
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Howltooth Hollow (LRW #269).
 *
 * "Hideaway 4 … This land enters tapped. {T}: Add {B}.
 *  {B}, {T}: You may play the exiled card without paying its mana cost if each player has no
 *  cards in hand."
 *
 * Covers the gate, which is the only part of the card that isn't shared with Mosswort Bridge.
 * "Each player" is universal, so a card in *either* hand — the controller's or the opponent's —
 * must keep the ability shut. Emptying only your own hand is the case a summed-over-opponents
 * reading would get right and a controller-only reading would get wrong, so both halves are
 * exercised separately.
 */
class HowltoothHollowScenarioTest : FunSpec({

    /** Index 0 is the {T}: Add {B} mana ability; index 1 is the gated free-cast ability. */
    val freeCastAbilityId = HowltoothHollow.activatedAbilities[1].id

    fun freeCastOffered(driver: GameTestDriver, player: EntityId, land: EntityId): Boolean =
        driver.legalActions(player).any {
            val action = it.action
            action is ActivateAbility && action.sourceId == land && action.abilityId == freeCastAbilityId
        }

    /** Play the land from hand so hideaway runs, exile a card, then untap it (it enters tapped). */
    fun playHowltoothHollow(driver: GameTestDriver, player: EntityId): EntityId {
        val card = driver.putCardInHand(player, "Howltooth Hollow")
        driver.playLand(player, card)
        driver.bothPass() // resolve the hideaway trigger → pauses to exile one of the top four
        val decision = driver.pendingDecision
        require(decision is SelectCardsDecision) { "expected hideaway selection, got $decision" }
        driver.submitCardSelection(player, listOf(decision.options.first()))
        val land = driver.findPermanent(player, "Howltooth Hollow")!!
        driver.untapPermanent(land)
        return land
    }

    fun emptyHand(driver: GameTestDriver, player: EntityId) {
        driver.getHand(player).forEach(driver::moveToGraveyard)
    }

    test("a card in either hand keeps the free-cast ability shut") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val land = playHowltoothHollow(driver, player)
        driver.giveMana(player, Color.BLACK, 1)

        // Both hands full.
        freeCastOffered(driver, player, land) shouldBe false

        // Only your own hand emptied — the opponent still holds cards, so the gate stays shut.
        emptyHand(driver, player)
        driver.giveMana(player, Color.BLACK, 1)
        freeCastOffered(driver, player, land) shouldBe false

        // Only the opponent's hand emptied.
        driver.putCardInHand(player, "Swamp")
        emptyHand(driver, opponent)
        driver.giveMana(player, Color.BLACK, 1)
        freeCastOffered(driver, player, land) shouldBe false
    }

    test("with every hand empty the free-cast ability is offered") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val land = playHowltoothHollow(driver, player)
        emptyHand(driver, player)
        emptyHand(driver, driver.getOpponent(player))
        driver.giveMana(player, Color.BLACK, 1)

        freeCastOffered(driver, player, land) shouldBe true
    }
})

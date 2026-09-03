package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.ShelldockIsle
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Shelldock Isle (LRW #272).
 *
 * "Hideaway 4 … This land enters tapped. {T}: Add {U}.
 *  {U}, {T}: You may play the exiled card without paying its mana cost if a library has twenty or
 *  fewer cards in it."
 *
 * The hideaway half is shared with the rest of the cycle and covered by
 * [SpinerockKnollScenarioTest]; what is unique here is the gate, and its whole difficulty is the
 * word **"a"**. It is an existential over every library in the game, so the test is aimed at the
 * asymmetric board — a big library of your own, a small one across the table — which is the one
 * shape where the two plausible wrong readings give the wrong answer:
 *
 *  - summing every library into one number never reaches "twenty or fewer";
 *  - reading only your own library says no while the card says yes.
 *
 * The third test pins the fail-closed direction: with both libraries above twenty the ability must
 * not even be offered. A gate that is never read gives the same (permissive) answer to every
 * board, so without this half the other two prove nothing.
 */
class ShelldockIsleScenarioTest : FunSpec({

    /** Index 0 is the {T}: Add {U} mana ability; index 1 is the gated free-cast ability. */
    val freeCastAbilityId = ShelldockIsle.activatedAbilities[1].id

    fun freeCastOffered(driver: GameTestDriver, player: EntityId, land: EntityId): Boolean =
        driver.legalActions(player).any {
            val action = it.action
            action is ActivateAbility && action.sourceId == land && action.abilityId == freeCastAbilityId
        }

    /** Play the land from hand so hideaway runs, exile a card, then untap it (it enters tapped). */
    fun playShelldockIsle(driver: GameTestDriver, player: EntityId): EntityId {
        val card = driver.putCardInHand(player, "Shelldock Isle")
        driver.playLand(player, card)
        driver.bothPass() // resolve the hideaway trigger → pauses to exile one of the top four
        val decision = driver.pendingDecision
        require(decision is SelectCardsDecision) { "expected hideaway selection, got $decision" }
        driver.submitCardSelection(player, listOf(decision.options.first()))
        val land = driver.findPermanent(player, "Shelldock Isle")!!
        driver.untapPermanent(land)
        return land
    }

    /**
     * Start a game whose two libraries differ in size. Decks are drawn down by the opening hand,
     * so a 40-card deck leaves roughly 33 cards and a 25-card deck roughly 18.
     */
    fun start(driver: GameTestDriver, yourDeckSize: Int, theirDeckSize: Int): EntityId {
        driver.registerCards(TestCards.all + listOf(ShelldockIsle))
        driver.initGame(
            deck1 = Deck.of("Island" to yourDeckSize),
            deck2 = Deck.of("Island" to theirDeckSize),
            startingLife = 20
        )
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return player
    }

    test("both libraries above twenty keeps the free-cast ability closed") {
        val driver = GameTestDriver()
        val player = start(driver, yourDeckSize = 40, theirDeckSize = 40)

        val land = playShelldockIsle(driver, player)
        driver.giveMana(player, Color.BLUE, 1)

        freeCastOffered(driver, player, land) shouldBe false
    }

    test("an opponent's library at twenty or fewer opens it, even though yours is large") {
        val driver = GameTestDriver()
        val player = start(driver, yourDeckSize = 40, theirDeckSize = 25)

        val land = playShelldockIsle(driver, player)
        driver.giveMana(player, Color.BLUE, 1)

        freeCastOffered(driver, player, land) shouldBe true
    }

    test("your own library at twenty or fewer opens it too — \"a library\" includes yours") {
        val driver = GameTestDriver()
        val player = start(driver, yourDeckSize = 25, theirDeckSize = 40)

        val land = playShelldockIsle(driver, player)
        driver.giveMana(player, Color.BLUE, 1)

        freeCastOffered(driver, player, land) shouldBe true
    }
})

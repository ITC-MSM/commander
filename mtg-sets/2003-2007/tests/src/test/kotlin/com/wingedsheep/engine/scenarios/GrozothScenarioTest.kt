package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import io.kotest.matchers.shouldBe

/**
 * Grozoth — defender, an unbounded nine-drop tutor on entry, a {4} defender shed, and transmute.
 *
 * Grozoth's own mana value is nine, so a second copy in the library is a legal find for both the
 * entry trigger and for transmute.
 */
class GrozothScenarioTest : ScenarioTestBase() {
    init {
        test("the entry trigger finds any number of mana value nine cards") {
            val game = scenario().withPlayers("P1", "P2")
                .withCardInHand(1, "Grozoth")
                .withLandsOnBattlefield(1, "Island", 9)
                .withCardInLibrary(1, "Grozoth")
                .withCardInLibrary(1, "Grozoth")
                .withCardInLibrary(1, "Hill Giant")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            game.castSpell(1, "Grozoth").error shouldBe null
            game.resolveStack()

            // "You may search" — the consent gate wraps the whole search.
            game.answerYesNo(true).error shouldBe null
            val decision = game.state.pendingDecision as SelectCardsDecision
            // Hill Giant's mana value is three, so it is not offered.
            decision.options.size shouldBe 2
            game.selectCards(decision.options).error shouldBe null
            game.resolveStack()

            game.findCardsInHand(1, "Grozoth").size shouldBe 2
            game.findCardsInLibrary(1, "Hill Giant").size shouldBe 1
        }

        test("finding nothing is a legal choice") {
            val game = scenario().withPlayers("P1", "P2")
                .withCardInHand(1, "Grozoth")
                .withLandsOnBattlefield(1, "Island", 9)
                .withCardInLibrary(1, "Grozoth")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            game.castSpell(1, "Grozoth").error shouldBe null
            game.resolveStack()
            game.answerYesNo(true).error shouldBe null
            game.selectCards(emptyList()).error shouldBe null
            game.resolveStack()

            game.findCardsInLibrary(1, "Grozoth").size shouldBe 1
            game.handSize(1) shouldBe 0
        }

        test("declining the may skips the search entirely") {
            val game = scenario().withPlayers("P1", "P2")
                .withCardInHand(1, "Grozoth")
                .withLandsOnBattlefield(1, "Island", 9)
                .withCardInLibrary(1, "Grozoth")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            game.castSpell(1, "Grozoth").error shouldBe null
            game.resolveStack()
            game.answerYesNo(false).error shouldBe null
            game.resolveStack()

            game.hasPendingDecision() shouldBe false
            game.findCardsInLibrary(1, "Grozoth").size shouldBe 1
            game.handSize(1) shouldBe 0
            game.isOnBattlefield("Grozoth") shouldBe true
        }

        test("paying four sheds defender for the turn") {
            val game = scenario().withPlayers("P1", "P2")
                .withCardOnBattlefield(1, "Grozoth")
                .withLandsOnBattlefield(1, "Island", 4)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            val grozoth = game.findPermanent("Grozoth")!!
            game.state.projectedState.hasKeyword(grozoth, Keyword.DEFENDER) shouldBe true

            val ability = cardRegistry.getCard("Grozoth")!!
                .activatedAbilities.single { it.activateFromZone != Zone.HAND }
            game.execute(ActivateAbility(game.player1Id, grozoth, ability.id)).error shouldBe null
            game.resolveStack()

            game.state.projectedState.hasKeyword(grozoth, Keyword.DEFENDER) shouldBe false
        }

        test("transmute searches for a card with mana value nine") {
            val game = scenario().withPlayers("P1", "P2")
                .withCardInHand(1, "Grozoth")
                .withCardInLibrary(1, "Hill Giant")
                .withCardInLibrary(1, "Grozoth")
                .withLandsOnBattlefield(1, "Island", 3)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            val source = game.findCardsInHand(1, "Grozoth").single()
            val ability = cardRegistry.getCard("Grozoth")!!
                .activatedAbilities.single { it.activateFromZone == Zone.HAND }
            game.execute(ActivateAbility(game.player1Id, source, ability.id)).error shouldBe null
            game.isInGraveyard(1, "Grozoth") shouldBe true
            game.resolveStack()

            val match = game.findCardsInLibrary(1, "Grozoth").single()
            (game.state.pendingDecision as SelectCardsDecision).options shouldBe listOf(match)
            game.selectCards(listOf(match)).error shouldBe null
            game.resolveStack()
            game.isInHand(1, "Grozoth") shouldBe true
        }
    }
}

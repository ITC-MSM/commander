package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

/**
 * Garna, Bloodfist of Keld — "Whenever another creature you control dies, draw a card if it was
 * attacking. Otherwise, Garna deals 1 damage to each opponent."
 *
 * The branch is decided from last-known information (CR 608.2h): by resolution the dead creature
 * is in the graveyard with its `AttackingComponent` gone, so `StatePredicate.IsAttacking` has to
 * read the battlefield-exit snapshot. These tests pin both legs plus the "another" scoping.
 */
class GarnaBloodfistOfKeldScenarioTest : ScenarioTestBase() {

    init {
        test("a non-attacking creature dying deals 1 damage to each opponent") {
            val game = scenario()
                .withPlayers()
                .withCardOnBattlefield(1, "Garna, Bloodfist of Keld")
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withCardInHand(1, "Shock")
                .withLandsOnBattlefield(1, "Mountain", 1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val handBefore = game.handSize(1)
            val opponentLifeBefore = game.getLifeTotal(2)

            // Shock the Bears: 2 damage kills a 2/2 that isn't attacking.
            game.castSpell(1, "Shock", game.findPermanent("Grizzly Bears")!!)
            game.resolveStack()

            game.isInGraveyard(1, "Grizzly Bears") shouldBe true
            game.getLifeTotal(2) shouldBe opponentLifeBefore - 1
            // Shock left hand, nothing was drawn.
            game.handSize(1) shouldBe handBefore - 1
        }

        test("an attacking creature dying draws a card instead") {
            val game = scenario()
                .withPlayers()
                .withCardOnBattlefield(1, "Garna, Bloodfist of Keld")
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withCardOnBattlefield(2, "Serra Angel")
                .withCardInLibrary(1, "Mountain")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val handBefore = game.handSize(1)
            val opponentLifeBefore = game.getLifeTotal(2)

            game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
            game.declareAttackers(mapOf("Grizzly Bears" to 2))
            game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
            // The 4/4 Angel blocks and kills the 2/2 while it is attacking.
            game.declareBlockers(mapOf("Serra Angel" to listOf("Grizzly Bears")))
            game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)

            game.isInGraveyard(1, "Grizzly Bears") shouldBe true
            game.handSize(1) shouldBe handBefore + 1
            // The draw leg fired, so no damage was dealt to the opponent.
            game.getLifeTotal(2) shouldBe opponentLifeBefore
        }

        test("Garna's own death does not trigger her ability") {
            val game = scenario()
                .withPlayers()
                .withCardOnBattlefield(1, "Garna, Bloodfist of Keld")
                .withCardInHand(1, "Murder")
                .withLandsOnBattlefield(1, "Swamp", 3)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val opponentLifeBefore = game.getLifeTotal(2)

            game.castSpell(1, "Murder", game.findPermanent("Garna, Bloodfist of Keld")!!)
            game.resolveStack()

            game.isInGraveyard(1, "Garna, Bloodfist of Keld") shouldBe true
            // "another creature you control" — the source's own death is excluded.
            game.getLifeTotal(2) shouldBe opponentLifeBefore
        }

        test("an opponent's creature dying does not trigger it") {
            val game = scenario()
                .withPlayers()
                .withCardOnBattlefield(1, "Garna, Bloodfist of Keld")
                .withCardOnBattlefield(2, "Grizzly Bears")
                .withCardInHand(1, "Shock")
                .withLandsOnBattlefield(1, "Mountain", 1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val opponentLifeBefore = game.getLifeTotal(2)

            game.castSpell(1, "Shock", game.findPermanent("Grizzly Bears")!!)
            game.resolveStack()

            game.isInGraveyard(2, "Grizzly Bears") shouldBe true
            // "a creature you control" — an opponent's death is out of scope.
            game.getLifeTotal(2) shouldBe opponentLifeBefore
        }
    }
}

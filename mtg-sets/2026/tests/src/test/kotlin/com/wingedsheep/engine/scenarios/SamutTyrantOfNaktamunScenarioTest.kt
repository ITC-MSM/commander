package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Samut, Tyrant of Naktamun (FRA #220) — {1}{U} Legendary Creature — Human Wizard 2/1.
 *
 *   Instant and sorcery spells you control have split second.
 *
 * The grant covers only instant and sorcery spells, and only the ones Samut's controller controls.
 */
class SamutTyrantOfNaktamunScenarioTest : ScenarioTestBase() {

    private fun board() = scenario().withPlayers()
        .withCardOnBattlefield(1, "Samut, Tyrant of Naktamun")
        .withCardInHand(1, "Lightning Bolt")
        .withCardInHand(1, "Grizzly Bears")
        .withLandsOnBattlefield(1, "Mountain", 1)
        .withLandsOnBattlefield(1, "Forest", 2)
        .withCardInHand(2, "Lightning Bolt")
        .withLandsOnBattlefield(2, "Mountain", 1)
        .withCardInLibrary(1, "Mountain")
        .withCardInLibrary(2, "Mountain")
        .withActivePlayer(1)
        .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
        .build()

    private fun TestGame.castOffers(playerNumber: Int) =
        getLegalActions(playerNumber).filter { it.actionType == "CastSpell" }

    init {
        test("your instant has split second — the opponent can't respond with a spell") {
            val game = board()

            game.castSpellTargetingPlayer(1, "Lightning Bolt", 2).error shouldBe null
            game.passPriority()
            game.state.priorityPlayerId shouldBe game.player2Id

            withClue("the opponent is offered no spell while Samut's Bolt is on the stack") {
                game.castOffers(2).shouldBeEmpty()
            }
            game.castSpellTargetingPlayer(2, "Lightning Bolt", 1).error shouldNotBe null

            game.resolveStack()
            game.getLifeTotal(2) shouldBe 17
            game.getLifeTotal(1) shouldBe 20
            game.isInHand(2, "Lightning Bolt") shouldBe true
        }

        test("a creature spell you control doesn't get split second") {
            val game = board()

            game.castSpell(1, "Grizzly Bears").error shouldBe null
            game.passPriority()

            game.castOffers(2).shouldNotBeEmpty()
            game.castSpellTargetingPlayer(2, "Lightning Bolt", 1).error shouldBe null
        }

        test("an opponent's instant doesn't get split second — you can still respond") {
            val game = board()

            game.passPriority() // empty stack: priority moves to the opponent
            game.state.priorityPlayerId shouldBe game.player2Id
            game.castSpellTargetingPlayer(2, "Lightning Bolt", 1).error shouldBe null
            game.passPriority()
            game.state.priorityPlayerId shouldBe game.player1Id

            withClue("Samut grants split second only to spells its controller controls") {
                game.castSpellTargetingPlayer(1, "Lightning Bolt", 2).error shouldBe null
            }
        }

        test("the lock ends when Samut's spell resolves") {
            val game = board()

            game.castSpellTargetingPlayer(1, "Lightning Bolt", 2).error shouldBe null
            game.resolveStack()

            game.state.stack.shouldBeEmpty()
            game.castSpell(1, "Grizzly Bears").error shouldBe null
            game.passPriority()
            game.castSpellTargetingPlayer(2, "Lightning Bolt", 1).error shouldBe null
        }
    }
}

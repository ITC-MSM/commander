package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.core.YesNoResponse
import com.wingedsheep.engine.support.ScenarioTestBase
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Zoyowa's Justice ({1}{R} instant): "The owner of target artifact or creature with mana value 1
 * or greater shuffles it into their library. Then that player discovers X, where X is its mana
 * value."
 *
 * The card composes three existing primitives — no engine change was needed:
 *  - a `CreatureOrArtifact.manaValueAtLeast(1)` target;
 *  - `Effects.ForEachPlayer(Player.OwnerOf(...))` to rebind the resolution controller to the
 *    target's *owner* (who need not be the spell's controller), so the shuffle and the discover
 *    are performed by, and the cast/hand decision is presented to, that owner;
 *  - `Effects.Discover` with the threshold read as the target's mana value
 *    (`EntityProperty(Target(0), ManaValue)`), which survives the shuffle because the entity keeps
 *    its id and CardComponent across the zone change.
 */
class ZoyowasJusticeScenarioTest : ScenarioTestBase() {

    init {
        context("Zoyowa's Justice") {

            test("the target's owner (an opponent) performs the discover, gated by X = the target's mana value") {
                // Player 1 targets player 2's Grizzly Bears (mana value 2). Player 2's library is all
                // Serra Angels (mana value 5), so X = 2 excludes every library card; the only nonland
                // with mana value <= 2 is the shuffled-in Grizzly Bears itself, which is therefore the
                // deterministic discovered card regardless of the post-shuffle order.
                val game = scenario()
                    .withPlayers()
                    .withCardInHand(1, "Zoyowa's Justice")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withCardInLibrary(2, "Serra Angel")
                    .withCardInLibrary(2, "Serra Angel")
                    .withCardInLibrary(2, "Serra Angel")
                    .withCardInLibrary(2, "Serra Angel")
                    // Keep player 1 from decking out on their own (unused) draw safety.
                    .withCardInLibrary(1, "Mountain")
                    .build()

                val bearsId = game.findPermanent("Grizzly Bears")!!

                val castResult = game.castSpell(1, "Zoyowa's Justice", targetId = bearsId)
                withClue("Casting Zoyowa's Justice should succeed: ${castResult.error}") {
                    castResult.error shouldBe null
                }
                game.resolveStack()

                // The discover must be made BY the owner (player 2), not the caster (player 1).
                val decision = game.getPendingDecision()
                withClue("Resolving should pause for the owner to make the discover cast/hand choice") {
                    decision.shouldNotBeNull()
                }
                val yesNo = decision.shouldBeInstanceOf<YesNoDecision>()
                withClue("Player 2 (the target's owner) must be the discovering player") {
                    yesNo.playerId shouldBe game.player2Id
                }

                // Player 2 declines the free cast -> the discovered card goes to their hand.
                val submit = game.submitDecision(YesNoResponse(yesNo.id, choice = false))
                withClue("Submitting the discover decision should succeed: ${submit.error}") {
                    submit.error shouldBe null
                }

                withClue("The mana-value-2 Grizzly Bears is the only card <= X, so it is discovered into player 2's hand") {
                    game.isInHand(2, "Grizzly Bears") shouldBe true
                }
                withClue("Serra Angel (mana value 5 > X) is above the threshold and is never discovered") {
                    game.isInHand(2, "Serra Angel") shouldBe false
                }
                withClue("The targeted Grizzly Bears was shuffled off the battlefield") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                }
            }

            test("targeting your own permanent makes you (owner == controller) the discovering player") {
                // Player 1 targets their own Grizzly Bears; their library is all mana-value-2 nonlands,
                // so the first card exiled is always a legal discover hit.
                val game = scenario()
                    .withPlayers()
                    .withCardInHand(1, "Zoyowa's Justice")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .build()

                val bearsId = game.findPermanent("Grizzly Bears")!!

                val castResult = game.castSpell(1, "Zoyowa's Justice", targetId = bearsId)
                withClue("Casting Zoyowa's Justice should succeed: ${castResult.error}") {
                    castResult.error shouldBe null
                }
                game.resolveStack()

                val decision = game.getPendingDecision()
                withClue("The controller (also the owner) should be prompted for the discover choice") {
                    decision.shouldNotBeNull()
                }
                val yesNo = decision.shouldBeInstanceOf<YesNoDecision>()
                withClue("Player 1 (owner == controller) must be the discovering player") {
                    yesNo.playerId shouldBe game.player1Id
                }

                val submit = game.submitDecision(YesNoResponse(yesNo.id, choice = false))
                withClue("Submitting the discover decision should succeed: ${submit.error}") {
                    submit.error shouldBe null
                }

                withClue("A mana-value-2 Grizzly Bears is discovered into player 1's hand") {
                    game.isInHand(1, "Grizzly Bears") shouldBe true
                }
            }
        }
    }
}

package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.PassPriority
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Divide by Zero (STX #41) — "Return target spell or permanent with mana value 1 or greater to
 * its owner's hand. Learn."
 *
 * The interesting half is the target: one restriction ("mana value 1 or greater") applied to
 * *both* sides of a spell-or-permanent choice. The permanent side was always filterable; the
 * stack side was not, and every spell on the stack used to be a legal target regardless of what
 * the card said. So the two "{0} is not a legal target" tests below are the regression, and the
 * Memnite one is the case that had no way of being expressed at all before this card.
 *
 * Learn (CR 701.48) is [com.wingedsheep.sdk.dsl.Patterns.Mechanic.learn] and is exercised in
 * depth by AcademicDisputeScenarioTest; here it is only checked to fire in the right order,
 * after the bounce.
 */
class DivideByZeroScenarioTest : ScenarioTestBase() {

    init {
        context("the permanent half") {

            test("bounces a permanent with mana value 1 or greater") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Divide by Zero")
                    .withCardInHand(1, "Hill Giant")             // something for Learn to offer
                    .withLandsOnBattlefield(1, "Island", 3)
                    .withCardOnBattlefield(2, "Grizzly Bears")   // {1}{G} — mana value 2
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                game.castSpell(1, "Divide by Zero", bears).error shouldBe null
                game.resolveStack()

                withClue("Grizzly Bears is back in its owner's hand") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                    game.isInHand(2, "Grizzly Bears") shouldBe true
                }
                withClue("Learn follows the bounce — with a card in hand it offers the discard") {
                    game.hasPendingDecision() shouldBe true
                }
            }

            test("a land is not a legal target — every land has mana value 0") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Divide by Zero")
                    .withLandsOnBattlefield(1, "Island", 3)
                    .withLandsOnBattlefield(2, "Forest", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val forest = game.findPermanent("Forest")!!

                withClue("A {0} permanent fails the 'mana value 1 or greater' clause") {
                    game.castSpell(1, "Divide by Zero", forest).error shouldNotBe null
                }
            }

            test("a {0} artifact creature is not a legal target either") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Divide by Zero")
                    .withLandsOnBattlefield(1, "Island", 3)
                    .withCardOnBattlefield(2, "Ornithopter")   // {0} — mana value 0
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val thopter = game.findPermanent("Ornithopter")!!

                withClue("Ornithopter is a nonland permanent, but its mana value is still 0") {
                    game.castSpell(1, "Divide by Zero", thopter).error shouldNotBe null
                }
            }
        }

        context("the spell half") {

            test("bounces a spell off the stack — it never resolves") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Divide by Zero")
                    .withLandsOnBattlefield(1, "Island", 3)
                    .withCardInHand(2, "Grizzly Bears")
                    .withLandsOnBattlefield(2, "Forest", 2)
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(2, "Grizzly Bears").error shouldBe null
                game.execute(PassPriority(game.player2Id))

                val result = game.castSpellTargetingStackSpell(1, "Divide by Zero", "Grizzly Bears")
                withClue("A {1}{G} spell on the stack clears 'mana value 1 or greater': ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()

                withClue("The bounced spell goes to hand, not the battlefield and not the graveyard") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                    game.isInHand(2, "Grizzly Bears") shouldBe true
                    game.isInGraveyard(2, "Grizzly Bears") shouldBe false
                }
            }

            test("a {0} spell on the stack is NOT a legal target") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Divide by Zero")
                    .withLandsOnBattlefield(1, "Island", 3)
                    .withCardInHand(2, "Memnite")   // {0} creature spell
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(2, "Memnite").error shouldBe null
                game.execute(PassPriority(game.player2Id))

                withClue("This is the case the stack half had no way to reject before") {
                    game.castSpellTargetingStackSpell(1, "Divide by Zero", "Memnite")
                        .error shouldNotBe null
                }
            }
        }
    }
}

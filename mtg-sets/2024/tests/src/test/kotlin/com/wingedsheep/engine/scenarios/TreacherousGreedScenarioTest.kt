package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeEmpty

/**
 * Treacherous Greed (MKM #237) — {1}{W}{B} Instant.
 *
 * "As an additional cost to cast this spell, sacrifice a creature that dealt damage this turn.
 *  Draw three cards. Each opponent loses 3 life and you gain 3 life."
 *
 * The whole card hangs on which creatures are legal fodder, so that is what these tests measure.
 * The filter is the **active**-voice `hasDealtDamageThisTurn()`, and the failure it guards against
 * is subtle: its passive sibling `wasDealtDamageThisTurn()` reads almost identically and selects
 * almost the opposite set. The third test pins exactly that — a creature that *took* damage but
 * never dealt any must not pay, and under the passive predicate it would.
 *
 * Combat is the natural way to stamp the tracker, so the attacker connects first and the spell is
 * cast in the postcombat main phase.
 */
class TreacherousGreedScenarioTest : ScenarioTestBase() {

    init {
        context("Treacherous Greed") {

            test("an attacker that connected pays the cost, and the spell drains for three") {
                val game = scenario()
                    .withPlayers("Boss", "Underling")
                    .withCardInHand(1, "Treacherous Greed")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Plains", 1)
                    .withLandsOnBattlefield(1, "Swamp", 2)
                    .withCardInLibrary(1, "Centaur Courser")
                    .withCardInLibrary(1, "Savannah Lions")
                    .withCardInLibrary(1, "Hill Giant")
                    .withCardInLibrary(1, "Goblin Guide")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Grizzly Bears" to 2))
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
                game.declareNoBlockers()
                game.passUntilPhase(Phase.COMBAT, Step.COMBAT_DAMAGE)
                game.resolveStack()

                withClue("the Bears connected for 2 — that is the stamp the cost looks for") {
                    game.getLifeTotal(2) shouldBe 18
                }

                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)
                val handBefore = game.handSize(1)

                val cast = game.castSpellWithAdditionalSacrifice(1, "Treacherous Greed", "Grizzly Bears")
                withClue("a creature that dealt damage this turn pays the cost: ${cast.error}") {
                    cast.error shouldBe null
                }
                withClue("the sacrifice happens on announcement, before the spell resolves") {
                    game.isInGraveyard(1, "Grizzly Bears") shouldBe true
                }
                game.resolveStack()

                withClue("draw three — the spell left the hand, so net is +2") {
                    game.handSize(1) shouldBe handBefore + 2
                }
                withClue("each opponent loses 3 on top of the 2 combat damage") {
                    game.getLifeTotal(2) shouldBe 15
                }
                withClue("and you gain 3") { game.getLifeTotal(1) shouldBe 23 }
            }

            test("a creature that never dealt damage can't pay the cost") {
                val game = scenario()
                    .withPlayers("Boss", "Underling")
                    .withCardInHand(1, "Treacherous Greed")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Plains", 1)
                    .withLandsOnBattlefield(1, "Swamp", 2)
                    .withCardInLibrary(1, "Centaur Courser")
                    .withCardInLibrary(1, "Savannah Lions")
                    .withCardInLibrary(1, "Hill Giant")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cast = game.castSpellWithAdditionalSacrifice(1, "Treacherous Greed", "Grizzly Bears")
                withClue("an idle creature is not legal fodder") {
                    cast.error.orEmpty().shouldNotBeEmpty()
                }
                withClue("nothing was sacrificed and nothing was drained") {
                    game.isInGraveyard(1, "Grizzly Bears") shouldBe false
                    game.getLifeTotal(2) shouldBe 20
                    game.getLifeTotal(1) shouldBe 20
                }
            }

            test("a creature that was only dealt damage can't pay — active voice, not passive") {
                val game = scenario()
                    .withPlayers("Boss", "Underling")
                    .withCardInHand(1, "Treacherous Greed")
                    // Combat would stamp *both* voices on a blocker, so the fodder here has to take
                    // damage without ever dealing any. A 5/5 bolted for 3 survives and has been
                    // dealt damage this turn — and has still dealt none.
                    .withCardOnBattlefield(1, "Force of Nature", summoningSickness = false)
                    .withCardInHand(1, "Lightning Bolt")
                    .withLandsOnBattlefield(1, "Plains", 1)
                    .withLandsOnBattlefield(1, "Swamp", 2)
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withCardInLibrary(1, "Centaur Courser")
                    .withCardInLibrary(1, "Savannah Lions")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val force = game.findPermanent("Force of Nature")!!
                val bolt = game.castSpell(1, "Lightning Bolt", force)
                withClue("bolting your own 5/5 should succeed: ${bolt.error}") {
                    bolt.error shouldBe null
                }
                game.resolveStack()

                withClue("the 5/5 survived 3 damage marked on it") {
                    game.findPermanent("Force of Nature") shouldBe force
                }

                val cast = game.castSpellWithAdditionalSacrifice(1, "Treacherous Greed", "Force of Nature")
                withClue("having been dealt damage is not having dealt damage") {
                    cast.error.orEmpty().shouldNotBeEmpty()
                }
                game.isInGraveyard(1, "Force of Nature") shouldBe false
            }
        }
    }
}

package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Massacre Girl, Known Killer (MKM #94) — {2}{B}{B} 4/4 Legendary Human Assassin.
 *
 * "Menace
 *  Creatures you control have wither.
 *  Whenever a creature an opponent controls dies, if its toughness was less than 1, draw a card."
 *
 * The card is one machine wearing two abilities. Wither turns your creatures' damage into -1/-1
 * counters, so an opposing creature killed by your board dies at toughness 0 or below rather than
 * at its printed toughness — which is exactly the condition the draw trigger reads.
 *
 * The tests are built around the three ways this can silently break:
 *
 *  - **the toughness is last-known information.** The creature is in the graveyard by the time the
 *    trigger is checked, where it has no toughness at all. If the check read live state instead of
 *    the value stamped on the zone-change event, the wither kill would draw nothing. The withered
 *    blocker there dies at *negative* toughness, which only the strict "less than 1" reading covers.
 *  - **the clauses must not collapse into "an opponent's creature died."** A creature that dies at
 *    its printed toughness — burned down rather than withered — must draw nothing.
 *  - **your own losses are not a payoff**, even though your creatures are the ones carrying wither.
 */
class MassacreGirlKnownKillerScenarioTest : ScenarioTestBase() {

    private val stateProjector = StateProjector()

    init {
        context("Massacre Girl, Known Killer") {

            test("she hands out wither to your whole board, herself included") {
                val game = scenario()
                    .withPlayers("Killer", "Victim")
                    .withCardOnBattlefield(1, "Massacre Girl, Known Killer", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardOnBattlefield(2, "Centaur Courser")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val projected = stateProjector.project(game.state)
                val girl = game.findPermanent("Massacre Girl, Known Killer")!!
                val yours = game.findPermanent("Grizzly Bears")!!
                val theirs = game.findPermanent("Centaur Courser")!!

                withClue("'creatures you control' includes the source") {
                    projected.hasKeyword(girl, Keyword.WITHER) shouldBe true
                }
                withClue("and every other creature you control") {
                    projected.hasKeyword(yours, Keyword.WITHER) shouldBe true
                }
                withClue("but not the opponent's") {
                    projected.hasKeyword(theirs, Keyword.WITHER) shouldBe false
                }
                withClue("menace is printed on her") {
                    projected.hasKeyword(girl, Keyword.MENACE) shouldBe true
                }
            }

            test("a withered blocker dies below 1 toughness and draws exactly one card") {
                val game = scenario()
                    .withPlayers("Killer", "Victim")
                    .withCardOnBattlefield(1, "Massacre Girl, Known Killer", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardOnBattlefield(2, "Savannah Lions")
                    .withCardInLibrary(1, "Hill Giant")
                    .withCardInLibrary(1, "Goblin Guide")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val handBefore = game.handSize(1)

                // The 1/1 Lions blocks the withering 2/2 Bears. The Bears' 2 damage arrives as two
                // -1/-1 counters, taking the Lions to -1/-1 — dead at *negative* toughness, which
                // only the strict "less than 1" reading covers. The Lions' 1 damage back is not
                // lethal to a 2/2, so the only death is on the opponent's side.
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Grizzly Bears" to 2))
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
                game.declareBlockers(mapOf("Savannah Lions" to listOf("Grizzly Bears")))
                game.passUntilPhase(Phase.COMBAT, Step.COMBAT_DAMAGE)
                game.resolveStack()

                withClue("the withered Lions died; the attacker survived a 1-power blocker") {
                    game.isInGraveyard(2, "Savannah Lions") shouldBe true
                    game.isInGraveyard(1, "Grizzly Bears") shouldBe false
                }
                withClue(
                    "exactly one card, off last-known information: the Lions had no toughness at " +
                        "all by the time the trigger was checked, so the -1 has to come off the " +
                        "zone change rather than live state"
                ) {
                    game.handSize(1) shouldBe handBefore + 1
                }
            }

            test("your own creature dying is never the payoff") {
                val game = scenario()
                    .withPlayers("Killer", "Victim")
                    .withCardOnBattlefield(1, "Massacre Girl, Known Killer", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardOnBattlefield(2, "Centaur Courser")
                    .withCardInLibrary(1, "Hill Giant")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val handBefore = game.handSize(1)

                // The 3/3 Courser survives two -1/-1 counters as a 1/1 and kills the 2/2 Bears.
                // Your creature carries the wither, but the trigger is scoped to theirs.
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Grizzly Bears" to 2))
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
                game.declareBlockers(mapOf("Centaur Courser" to listOf("Grizzly Bears")))
                game.passUntilPhase(Phase.COMBAT, Step.COMBAT_DAMAGE)
                game.resolveStack()

                withClue("your Bears died and their withered Courser is a 1/1 survivor") {
                    game.isInGraveyard(1, "Grizzly Bears") shouldBe true
                    val courser = game.findPermanent("Centaur Courser")!!
                    val projected = stateProjector.project(game.state)
                    projected.getPower(courser) shouldBe 1
                    projected.getToughness(courser) shouldBe 1
                }
                withClue("no opponent's creature died, so no card is drawn") {
                    game.handSize(1) shouldBe handBefore
                }
            }

            test("a creature that dies at its printed toughness draws nothing") {
                val game = scenario()
                    .withPlayers("Killer", "Victim")
                    .withCardOnBattlefield(1, "Massacre Girl, Known Killer", summoningSickness = false)
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withCardInHand(1, "Lightning Bolt")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withCardInLibrary(1, "Savannah Lions")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val handBefore = game.handSize(1)
                val bears = game.findPermanent("Grizzly Bears")!!

                val bolt = game.castSpell(1, "Lightning Bolt", bears)
                withClue("bolting the opponent's Bears should succeed: ${bolt.error}") {
                    bolt.error shouldBe null
                }
                game.resolveStack()

                withClue("the Bears died") { game.isInGraveyard(2, "Grizzly Bears") shouldBe true }
                withClue(
                    "but it died a 2/2 — wither only touches damage dealt by creatures you " +
                        "control, so the condition is false and the only hand change is the Bolt " +
                        "leaving it"
                ) {
                    game.handSize(1) shouldBe handBefore - 1
                }
            }
        }
    }
}

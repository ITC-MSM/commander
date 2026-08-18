package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Doppelgang (MKM #198) — {X}{X}{X}{G}{U} Sorcery.
 *
 * "For each of X target permanents, create X tokens that are copies of that permanent."
 *
 * One announced X is read in two different places — the number of permanents you may target and
 * the number of copies each of them gets — so the card produces X² tokens. A wiring that resolved
 * the copy count from anything other than the same announced X (a fixed 1, the target count, the
 * mana actually spent) would still look plausible at X=1, where every reading agrees. X=2 is the
 * smallest value that separates them: four tokens, two per target.
 */
class DoppelgangScenarioTest : ScenarioTestBase() {

    init {
        context("Doppelgang") {

            test("X = 2 makes two copies of each of two targets") {
                val game = scenario()
                    .withPlayers("Copier", "Opponent")
                    .withCardInHand(1, "Doppelgang")
                    .withLandsOnBattlefield(1, "Forest", 4)
                    .withLandsOnBattlefield(1, "Island", 4)
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardOnBattlefield(2, "Centaur Courser")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val courser = game.findPermanent("Centaur Courser")!!
                val doppelgang = game.findCardsInHand(1, "Doppelgang").single()

                val cast = game.execute(
                    CastSpell(
                        playerId = game.player1Id,
                        cardId = doppelgang,
                        targets = listOf(
                            ChosenTarget.Permanent(bears),
                            ChosenTarget.Permanent(courser)
                        ),
                        xValue = 2
                    )
                )
                withClue("casting with X = 2 and two targets should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                withClue("two copies of your own creature, plus the original") {
                    game.findAllPermanents("Grizzly Bears").size shouldBe 3
                }
                withClue("two copies of the opponent's creature, plus the original") {
                    game.findAllPermanents("Centaur Courser").size shouldBe 3
                }
            }

            test("X = 1 makes a single copy of a single target") {
                val game = scenario()
                    .withPlayers("Copier", "Opponent")
                    .withCardInHand(1, "Doppelgang")
                    .withLandsOnBattlefield(1, "Forest", 3)
                    .withLandsOnBattlefield(1, "Island", 2)
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val doppelgang = game.findCardsInHand(1, "Doppelgang").single()

                val cast = game.execute(
                    CastSpell(
                        playerId = game.player1Id,
                        cardId = doppelgang,
                        targets = listOf(ChosenTarget.Permanent(bears)),
                        xValue = 1
                    )
                )
                withClue("casting with X = 1 should succeed: ${cast.error}") { cast.error shouldBe null }
                game.resolveStack()

                withClue("one copy joins the original") {
                    game.findAllPermanents("Grizzly Bears").size shouldBe 2
                }
            }

            test("X = 0 is castable and copies nothing") {
                val game = scenario()
                    .withPlayers("Copier", "Opponent")
                    .withCardInHand(1, "Doppelgang")
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withLandsOnBattlefield(1, "Island", 1)
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val doppelgang = game.findCardsInHand(1, "Doppelgang").single()

                val cast = game.execute(
                    CastSpell(
                        playerId = game.player1Id,
                        cardId = doppelgang,
                        targets = emptyList(),
                        xValue = 0
                    )
                )
                withClue("X = 0 with no targets is a legal (if pointless) cast: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                withClue("no tokens were created") {
                    game.findAllPermanents("Grizzly Bears").size shouldBe 1
                }
            }
        }
    }
}

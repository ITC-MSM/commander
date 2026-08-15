package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Doc Ock, Sinister Scientist (SPM) — {4}{U} Legendary Creature — Human Scientist Villain 4/5.
 *
 *  "As long as there are eight or more cards in your graveyard, Doc Ock has base power and
 *   toughness 8/8.
 *   As long as you control another Villain, Doc Ock has hexproof."
 *
 * Two conditional static abilities. Verifies the base P/T flips with graveyard size and that
 * hexproof tracks controlling a *second* Villain.
 */
class DocOckSinisterScientistScenarioTest : ScenarioTestBase() {

    init {
        context("Doc Ock conditional statics") {

            test("base P/T is 4/5 with fewer than 8 graveyard cards, 8/8 with 8 or more") {
                var lowBuilder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Doc Ock, Sinister Scientist", summoningSickness = false)
                // 7 cards in the graveyard — below the threshold.
                repeat(7) { lowBuilder = lowBuilder.withCardInGraveyard(1, "Island") }
                val low = lowBuilder.build()

                val docLow = low.findPermanent("Doc Ock, Sinister Scientist")!!
                withClue("With 7 graveyard cards Doc Ock is the printed 4/5") {
                    low.state.projectedState.getPower(docLow) shouldBe 4
                    low.state.projectedState.getToughness(docLow) shouldBe 5
                }

                var highBuilder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Doc Ock, Sinister Scientist", summoningSickness = false)
                repeat(8) { highBuilder = highBuilder.withCardInGraveyard(1, "Island") }
                val high = highBuilder.build()

                val docHigh = high.findPermanent("Doc Ock, Sinister Scientist")!!
                withClue("With 8 graveyard cards Doc Ock's base P/T becomes 8/8") {
                    high.state.projectedState.getPower(docHigh) shouldBe 8
                    high.state.projectedState.getToughness(docHigh) shouldBe 8
                }
            }

            test("hexproof only while you control another Villain") {
                val withoutVillain = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Doc Ock, Sinister Scientist", summoningSickness = false)
                    .build()

                val docA = withoutVillain.findPermanent("Doc Ock, Sinister Scientist")!!
                withClue("With no other Villain, Doc Ock lacks hexproof") {
                    withoutVillain.state.projectedState.hasKeyword(docA, Keyword.HEXPROOF) shouldBe false
                }

                val withVillain = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Doc Ock, Sinister Scientist", summoningSickness = false)
                    .withCardOnBattlefield(1, "Kraven's Cats", summoningSickness = false)
                    .build()

                val docB = withVillain.findPermanent("Doc Ock, Sinister Scientist")!!
                withClue("Controlling a second Villain (Kraven's Cats) grants Doc Ock hexproof") {
                    withVillain.state.projectedState.hasKeyword(docB, Keyword.HEXPROOF) shouldBe true
                }
            }
        }
    }
}

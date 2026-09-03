package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Cruel Celebrant (WAR #188) — {W}{B} Creature — Vampire, 1/2.
 *
 *   Whenever this creature or another creature or planeswalker you control dies, each opponent
 *   loses 1 life and you gain 1 life.
 *
 * The trigger is the first of its shape in the corpus: one `ANY`-bound zone-change trigger whose
 * filter is `CreatureOrPlaneswalker.youControl()`. Three things follow from that spelling and
 * each gets a test here, because getting any of them wrong still compiles and still looks right
 * on the card:
 *
 *  - **"this creature or another" is the ANY binding**, not a second SELF-bound trigger. The
 *    Celebrant's own death has to drain — a `SELF`/`ANY` mix-up shows up nowhere else.
 *  - **"or planeswalker" is why the filter is not `Triggers.YourCreatureDies`.** A planeswalker
 *    going to the graveyard has to drain too.
 *  - **`youControl()` is load-bearing**: an opponent's creature dying must do nothing.
 */
class CruelCelebrantScenarioTest : ScenarioTestBase() {

    init {
        context("the drain trigger") {

            test("another creature you control dying drains each opponent") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Cruel Celebrant")
                    .withCardOnBattlefield(1, "Llanowar Elves")
                    .withCardInHand(1, "Lightning Bolt")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withLifeTotal(1, 20)
                    .withLifeTotal(2, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val elves = game.findPermanent("Llanowar Elves")!!
                val cast = game.castSpell(1, "Lightning Bolt", elves)
                withClue("Casting Lightning Bolt should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                withClue("The Elves died, so the Celebrant drained for 1") {
                    game.isOnBattlefield("Llanowar Elves") shouldBe false
                    game.getLifeTotal(2) shouldBe 19
                    game.getLifeTotal(1) shouldBe 21
                }
            }

            test("the Celebrant's own death drains — \"this creature or another\" is one ANY trigger") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Cruel Celebrant")
                    .withCardInHand(1, "Lightning Bolt")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withLifeTotal(1, 20)
                    .withLifeTotal(2, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val celebrant = game.findPermanent("Cruel Celebrant")!!
                val cast = game.castSpell(1, "Lightning Bolt", celebrant)
                withClue("Casting Lightning Bolt should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                withClue("The Celebrant saw its own death (last-known information) and drained") {
                    game.isOnBattlefield("Cruel Celebrant") shouldBe false
                    game.getLifeTotal(2) shouldBe 19
                    game.getLifeTotal(1) shouldBe 21
                }
            }

            test("a planeswalker you control dying drains too") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Cruel Celebrant")
                    .withCardOnBattlefield(1, "Liliana Vess")
                    .withCardInHand(1, "Hero's Downfall")
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withLifeTotal(1, 20)
                    .withLifeTotal(2, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val liliana = game.findPermanent("Liliana Vess")!!
                val cast = game.castSpell(1, "Hero's Downfall", liliana)
                withClue("Casting Hero's Downfall should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                withClue("\"or planeswalker\" is in the filter, so Liliana's death drained") {
                    game.isOnBattlefield("Liliana Vess") shouldBe false
                    game.getLifeTotal(2) shouldBe 19
                    game.getLifeTotal(1) shouldBe 21
                }
            }

            test("an opponent's creature dying does nothing — youControl() is load-bearing") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Cruel Celebrant")
                    .withCardOnBattlefield(2, "Llanowar Elves")
                    .withCardInHand(1, "Lightning Bolt")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withLifeTotal(1, 20)
                    .withLifeTotal(2, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val elves = game.findPermanent("Llanowar Elves")!!
                val cast = game.castSpell(1, "Lightning Bolt", elves)
                withClue("Casting Lightning Bolt should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                withClue("The dead creature was the opponent's, so no life moved") {
                    game.isOnBattlefield("Llanowar Elves") shouldBe false
                    game.getLifeTotal(2) shouldBe 20
                    game.getLifeTotal(1) shouldBe 20
                }
            }
        }
    }
}

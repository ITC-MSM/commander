package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Soul-Shackled Zombie {3}{B} — Creature — Zombie 4/2
 *   When this creature enters, exile up to two target cards from a single graveyard. If at least
 *   one creature card was exiled this way, each opponent loses 2 life and you gain 2 life.
 *
 * The single-graveyard exile reuses Arashin Sunshield's `TargetObject.sameOwner` path; here we
 * confirm the conditional drain fires only when a creature card is among the exiled cards.
 */
class SoulShackledZombieScenarioTest : ScenarioTestBase() {

    init {
        context("Soul-Shackled Zombie ETB") {

            test("exiling a creature card drains each opponent for 2") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Soul-Shackled Zombie")
                    .withLandsOnBattlefield(1, "Swamp", 4)
                    .withCardInGraveyard(2, "Hill Giant") // creature
                    .withCardInGraveyard(2, "Swamp")      // noncreature
                    .withCardInLibrary(1, "Swamp")
                    .withCardInLibrary(2, "Swamp")
                    .withLifeTotal(1, 20)
                    .withLifeTotal(2, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Soul-Shackled Zombie").error shouldBe null
                game.resolveStack() // creature enters → ETB trigger asks for targets

                val creatureCard = game.findCardsInGraveyard(2, "Hill Giant").first()
                val landCard = game.findCardsInGraveyard(2, "Swamp").first()
                game.selectTargets(listOf(creatureCard, landCard)).error shouldBe null
                game.resolveStack()

                withClue("Both targeted cards are exiled") {
                    game.state.getExile(game.player2Id).size shouldBe 2
                }
                withClue("A creature was exiled → opponent loses 2, you gain 2") {
                    game.getLifeTotal(2) shouldBe 18
                    game.getLifeTotal(1) shouldBe 22
                }
            }

            test("exiling only noncreature cards does not drain") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Soul-Shackled Zombie")
                    .withLandsOnBattlefield(1, "Swamp", 4)
                    .withCardInGraveyard(2, "Swamp")
                    .withCardInGraveyard(2, "Mountain")
                    .withCardInLibrary(1, "Swamp")
                    .withCardInLibrary(2, "Swamp")
                    .withLifeTotal(1, 20)
                    .withLifeTotal(2, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Soul-Shackled Zombie").error shouldBe null
                game.resolveStack()

                val land1 = game.findCardsInGraveyard(2, "Swamp").first()
                val land2 = game.findCardsInGraveyard(2, "Mountain").first()
                game.selectTargets(listOf(land1, land2)).error shouldBe null
                game.resolveStack()

                withClue("Both lands are exiled") {
                    game.state.getExile(game.player2Id).size shouldBe 2
                }
                withClue("No creature exiled → no life change") {
                    game.getLifeTotal(2) shouldBe 20
                    game.getLifeTotal(1) shouldBe 20
                }
            }
        }
    }
}

package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Wretched Gryff — {7} 3/4 Eldrazi Hippogriff with flying, emerge {5}{U}, and
 * "When you cast this spell, draw a card."
 *
 * The draw is a cast trigger, so it resolves before the Gryff itself.
 */
class WretchedGryffScenarioTest : ScenarioTestBase() {

    init {
        context("Wretched Gryff") {

            test("emerge pays {5}{U} minus the sacrificed creature's mana value and draws a card") {
                val game = scenario()
                    .withPlayers()
                    .withCardInHand(1, "Wretched Gryff")
                    .withCardOnBattlefield(1, "Centaur Courser") // {2}{G} → mana value 3
                    .withCardInLibrary(1, "Grizzly Bears")
                    // Emerge {5}{U} reduced by 3 → {2}{U}: three Islands is exactly enough.
                    .withLandsOnBattlefield(1, "Island", 3)
                    .build()

                val cast = game.castSpellWithEmerge(1, "Wretched Gryff", "Centaur Courser")
                withClue("the emerge cast should succeed: ${cast.error}") { cast.error shouldBe null }

                withClue("the creature is sacrificed as the cost is paid (CR 702.119c)") {
                    game.isInGraveyard(1, "Centaur Courser") shouldBe true
                    game.isOnBattlefield("Centaur Courser") shouldBe false
                }

                game.resolveStack()

                withClue("the cast trigger drew a card and the Gryff resolved") {
                    game.isInHand(1, "Grizzly Bears") shouldBe true
                    game.isOnBattlefield("Wretched Gryff") shouldBe true
                }
                val gryff = game.findPermanent("Wretched Gryff")!!
                game.state.projectedState.hasKeyword(gryff, Keyword.FLYING) shouldBe true
            }

            test("hard cast for {7} draws a card and sacrifices nothing") {
                val game = scenario()
                    .withPlayers()
                    .withCardInHand(1, "Wretched Gryff")
                    .withCardOnBattlefield(1, "Centaur Courser")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Island", 7)
                    .build()

                val cast = game.castSpell(1, "Wretched Gryff")
                withClue("the hard cast should succeed: ${cast.error}") { cast.error shouldBe null }
                game.resolveStack()

                game.isInHand(1, "Grizzly Bears") shouldBe true
                game.isOnBattlefield("Wretched Gryff") shouldBe true
                withClue("no emerge cost was chosen, so nothing was sacrificed") {
                    game.isOnBattlefield("Centaur Courser") shouldBe true
                }
            }
        }
    }
}

package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Hexhaven Invigorator (FRA #106) — {G}{G}{G}{G} Creature — Chimera Horror 6/6:
 *   Vigilance
 *   Whenever this creature is dealt damage, you may search your library for up to that many land
 *   cards, put them onto the battlefield tapped, then shuffle.
 *
 * Pins "that many" (3 damage caps the search at 3), that any land — not just basics — is findable,
 * that nonlands are not, that the lands arrive tapped, and that declining the "may" does nothing.
 */
class HexhavenInvigoratorScenarioTest : ScenarioTestBase() {

    private fun boltedInvigorator(): TestGame {
        val game = scenario().withPlayers()
            .withCardOnBattlefield(1, "Hexhaven Invigorator")
            .withCardInHand(1, "Lightning Bolt")
            .withLandsOnBattlefield(1, "Mountain", 1)
            .withCardInLibrary(1, "Forest")
            .withCardInLibrary(1, "Forest")
            .withCardInLibrary(1, "Tundra")
            .withCardInLibrary(1, "Island")
            .withCardInLibrary(1, "Grizzly Bears")
            .withCardInLibrary(2, "Plains")
            .withActivePlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            .build()
        val invigorator = game.findPermanent("Hexhaven Invigorator")!!
        game.castSpell(1, "Lightning Bolt", invigorator).error shouldBe null
        game.resolveStack()
        return game
    }

    init {
        test("3 damage lets you put up to three lands onto the battlefield tapped") {
            val game = boltedInvigorator()
            withClue("the trigger asks whether to search") {
                game.hasPendingDecision() shouldBe true
            }
            game.answerYesNo(true).error shouldBe null

            val forests = game.findCardsInLibrary(1, "Forest")
            val tundra = game.findCardsInLibrary(1, "Tundra").single()
            val island = game.findCardsInLibrary(1, "Island").single()
            val bears = game.findCardsInLibrary(1, "Grizzly Bears").single()

            withClue("a nonland card isn't a land card") {
                game.selectCards(listOf(bears)).error shouldNotBe null
            }
            withClue("\"up to that many\" — four is one too many for 3 damage") {
                game.selectCards(forests + tundra + island).error shouldNotBe null
            }
            game.selectCards(listOf(forests.first(), tundra, island)).error shouldBe null
            game.resolveStack()

            val tundraId = game.findPermanent("Tundra")
            tundraId shouldNotBe null
            game.findAllPermanents("Forest").size shouldBe 1
            game.isOnBattlefield("Island") shouldBe true
            withClue("the lands enter tapped") {
                listOf(tundraId!!, game.findPermanent("Island")!!, game.findAllPermanents("Forest").single())
                    .forEach { game.state.getEntity(it)!!.has<TappedComponent>() shouldBe true }
            }
            game.librarySize(1) shouldBe 2
            game.isOnBattlefield("Hexhaven Invigorator") shouldBe true
        }

        test("declining the search leaves the library alone") {
            val game = boltedInvigorator()
            game.hasPendingDecision() shouldBe true
            game.answerYesNo(false).error shouldBe null
            game.resolveStack()

            game.librarySize(1) shouldBe 5
            game.isOnBattlefield("Tundra") shouldBe false
        }
    }
}

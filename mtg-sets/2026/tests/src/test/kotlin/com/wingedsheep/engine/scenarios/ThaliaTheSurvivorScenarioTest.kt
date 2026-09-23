package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Thalia, the Survivor (FRA #205) — "Noncreature spells your opponents cast cost {1} more to cast."
 *
 * Pins the new `SpellCostTarget.OpponentsCast`: the tax lands on an opponent's noncreature spell,
 * not on their creature spell, not on Thalia's controller's own spells, and — as a cost increase
 * (CR 118.9d) — also on an opponent's alternative cost (flashback).
 */
class ThaliaTheSurvivorScenarioTest : ScenarioTestBase() {
    init {
        fun opponentTurn(mountains: Int) = scenario().withPlayers()
            .withCardOnBattlefield(1, "Thalia, the Survivor")
            .withCardInHand(2, "Lightning Bolt")
            .withLandsOnBattlefield(2, "Mountain", mountains)
            .withCardInLibrary(1, "Plains")
            .withCardInLibrary(2, "Mountain")
            .withActivePlayer(2)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            .build()

        test("an opponent's noncreature spell costs {1} more") {
            val game = opponentTurn(mountains = 1)
            withClue("one Mountain no longer pays for Lightning Bolt") {
                game.castSpellTargetingPlayer(2, "Lightning Bolt", 1).error shouldNotBe null
            }

            val paid = opponentTurn(mountains = 2)
            paid.castSpellTargetingPlayer(2, "Lightning Bolt", 1).error shouldBe null
            paid.resolveStack()
            paid.getLifeTotal(1) shouldBe 17
        }

        test("an opponent's creature spell is not taxed") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, "Thalia, the Survivor")
                .withCardInHand(2, "Grizzly Bears")
                .withLandsOnBattlefield(2, "Forest", 2)
                .withCardInLibrary(1, "Plains")
                .withCardInLibrary(2, "Forest")
                .withActivePlayer(2)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            game.castSpell(2, "Grizzly Bears").error shouldBe null
            game.resolveStack()
            game.isOnBattlefield("Grizzly Bears") shouldBe true
        }

        test("Thalia's controller pays nothing extra") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, "Thalia, the Survivor")
                .withCardInHand(1, "Lightning Bolt")
                .withLandsOnBattlefield(1, "Mountain", 1)
                .withCardInLibrary(1, "Plains")
                .withCardInLibrary(2, "Mountain")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            game.castSpellTargetingPlayer(1, "Lightning Bolt", 2).error shouldBe null
            game.resolveStack()
            game.getLifeTotal(2) shouldBe 17
        }

        test("the tax applies to an opponent's flashback cost") {
            fun flashbackGame(islands: Int) = scenario().withPlayers()
                .withCardOnBattlefield(1, "Thalia, the Survivor")
                .withCardInGraveyard(2, "Think Twice")
                .withLandsOnBattlefield(2, "Island", islands)
                .withCardInLibrary(1, "Plains")
                .withCardInLibrary(2, "Island")
                .withCardInLibrary(2, "Island")
                .withActivePlayer(2)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            // The flashback cast as the engine offers it — flagged as the alternative cost, not a
            // plain cast that happens to come from the graveyard.
            fun flashbackAction(game: TestGame): CastSpell? = game.getLegalActions(2)
                .mapNotNull { it.action as? CastSpell }
                .firstOrNull { it.useAlternativeCost && it.cardId in game.findCardsInGraveyard(2, "Think Twice") }

            withClue("flashback {2}{U} plus the tax needs four mana") {
                val short = flashbackGame(islands = 3)
                flashbackAction(short)?.let { short.execute(it).error } shouldNotBe null
            }
            val game = flashbackGame(islands = 4)
            val action = flashbackAction(game)
            action shouldNotBe null
            game.execute(action!!).error shouldBe null
            game.resolveStack()
            game.isInExile(2, "Think Twice") shouldBe true
        }
    }
}

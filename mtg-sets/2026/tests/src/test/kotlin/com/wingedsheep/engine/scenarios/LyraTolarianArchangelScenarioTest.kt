package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

/**
 * Lyra, Tolarian Archangel (FRA #217) — {1}{U}{U} Legendary Creature — Angel Wizard 3/3.
 *
 *   Flying
 *   At the beginning of each end step, if you've drawn three or more cards this turn, create a
 *   3/3 blue Angel creature token with flying.
 *   {3}{U}{U}: Until end of turn, whenever Lyra deals combat damage to a player, draw two cards.
 */
class LyraTolarianArchangelScenarioTest : ScenarioTestBase() {

    private fun TestGame.activateLyra() {
        val lyra = findPermanent("Lyra, Tolarian Archangel")!!
        val ability = cardRegistry.requireCard("Lyra, Tolarian Archangel").activatedAbilities.single().id
        execute(ActivateAbility(playerId = player1Id, sourceId = lyra, abilityId = ability)).error shouldBe null
        if (getPendingDecision() is SelectManaSourcesDecision) submitManaSourcesAutoPay()
        resolveStack()
    }

    private fun TestGame.attackWithLyraToPostcombat() {
        passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
        declareAttackers(mapOf("Lyra, Tolarian Archangel" to 2)).error shouldBe null
        passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)
    }

    private fun lyraBoard(islands: Int) = scenario().withPlayers()
        .withCardOnBattlefield(1, "Lyra, Tolarian Archangel")
        .withLandsOnBattlefield(1, "Island", islands)
        .apply { repeat(8) { withCardInLibrary(1, "Island") } }
        .withCardInLibrary(2, "Island")
        .withActivePlayer(1)
        .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
        .build()

    init {
        test("at the end step, having drawn three cards this turn makes a 3/3 flying Angel") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, "Lyra, Tolarian Archangel")
                .withCardInLibrary(1, "Island")
                .withCardInLibrary(2, "Island")
                .withCardsDrawnThisTurn(1, 3)
                .withActivePlayer(1)
                .inPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN).build()

            game.passUntilPhase(Phase.ENDING, Step.END)
            game.resolveStack()

            val angels = game.findPermanents("Angel Token")
            angels.size shouldBe 1
            val projected = game.state.projectedState
            projected.getPower(angels.single()) shouldBe 3
            projected.getToughness(angels.single()) shouldBe 3
            projected.hasKeyword(angels.single(), com.wingedsheep.sdk.core.Keyword.FLYING) shouldBe true
        }

        test("two cards drawn is not enough") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, "Lyra, Tolarian Archangel")
                .withCardInLibrary(1, "Island")
                .withCardInLibrary(2, "Island")
                .withCardsDrawnThisTurn(1, 2)
                .withActivePlayer(1)
                .inPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN).build()

            game.passUntilPhase(Phase.ENDING, Step.END)
            game.resolveStack()

            game.findPermanents("Angel Token").size shouldBe 0
        }

        test("without the activation, combat damage draws nothing") {
            val game = lyraBoard(islands = 0)
            val handBefore = game.handSize(1)
            game.attackWithLyraToPostcombat()
            game.getLifeTotal(2) shouldBe 17
            game.handSize(1) shouldBe handBefore
        }

        test("after activating, Lyra's combat damage to a player draws two cards") {
            val game = lyraBoard(islands = 5)
            game.activateLyra()
            val handBefore = game.handSize(1)
            game.attackWithLyraToPostcombat()
            game.getLifeTotal(2) shouldBe 17
            game.handSize(1) shouldBe handBefore + 2
        }

        test("activating twice sets up two instances — four cards") {
            val game = lyraBoard(islands = 10)
            game.activateLyra()
            game.activateLyra()
            val handBefore = game.handSize(1)
            game.attackWithLyraToPostcombat()
            game.handSize(1) shouldBe handBefore + 4
        }
    }
}

package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe
import io.kotest.matchers.nulls.shouldNotBeNull

class TerminalCriticismScenarioTest : ScenarioTestBase() {
    init {
        for (card in listOf("Merfolk of the Pearl Trident", "Goblin Piker", "Jace Beleren")) {
            test("destroys $card and gains one life") {
                val game = scenario().withPlayers()
                    .withCardInHand(1, "Terminal Criticism")
                    .withCardOnBattlefield(2, card)
                    .withLandsOnBattlefield(1, "Swamp", 2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
                game.castSpell(1, "Terminal Criticism", game.findPermanent(card)!!).error shouldBe null
                game.resolveStack()
                game.isInGraveyard(2, card) shouldBe true
                game.getLifeTotal(1) shouldBe 21
            }
        }
        for (card in listOf("Grizzly Bears", "Swamp", "Sol Ring")) {
            test("cannot target $card") {
                val game = scenario().withPlayers()
                    .withCardInHand(1, "Terminal Criticism")
                    .withCardOnBattlefield(2, card)
                    .withLandsOnBattlefield(1, "Swamp", 2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
                game.castSpell(1, "Terminal Criticism", game.findPermanent(card)!!).error.shouldNotBeNull()
                game.getLifeTotal(1) shouldBe 20
            }
        }
        test("an illegal target on resolution prevents the life gain") {
            val game = scenario().withPlayers()
                .withCardInHand(1, "Terminal Criticism")
                .withCardInHand(2, "Unsummon")
                .withCardOnBattlefield(2, "Goblin Piker")
                .withLandsOnBattlefield(1, "Swamp", 2)
                .withLandsOnBattlefield(2, "Island", 1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            val goblin = game.findPermanent("Goblin Piker")!!
            game.castSpell(1, "Terminal Criticism", goblin).error shouldBe null
            game.passPriority().error shouldBe null
            game.castSpell(2, "Unsummon", goblin).error shouldBe null
            game.resolveStack()
            game.isInHand(2, "Goblin Piker") shouldBe true
            game.getLifeTotal(1) shouldBe 20
        }
    }
}

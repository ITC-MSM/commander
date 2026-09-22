package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

class KonstrariCharmScenarioTest : ScenarioTestBase() {
    private fun board() = scenario().withPlayers()
        .withCardInHand(1, "Konstrari Charm")
        .withCardOnBattlefield(1, "Grizzly Bears")
        .withCardOnBattlefield(2, "Wind Drake")
        .withLandsOnBattlefield(1, "Mountain", 1)
        .withLandsOnBattlefield(1, "Forest", 1)
        .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

    init {
        test("deals 6 damage to a creature with flying") {
            val game = board()
            game.castSpellWithMode(1, "Konstrari Charm", 0, game.findPermanent("Wind Drake")!!).error shouldBe null
            game.resolveStack()
            game.isInGraveyard(2, "Wind Drake") shouldBe true
        }

        test("cannot deal damage to a creature without flying") {
            val game = board()
            game.castSpellWithMode(1, "Konstrari Charm", 0, game.findPermanent("Grizzly Bears")!!).error.shouldNotBeNull()
        }

        test("puts two +1/+1 counters on a creature and gives it trample") {
            val game = board()
            val bears = game.findPermanent("Grizzly Bears")!!
            game.castSpellWithMode(1, "Konstrari Charm", 1, bears).error shouldBe null
            game.resolveStack()
            game.state.projectedState.getPower(bears) shouldBe 4
            game.state.projectedState.hasKeyword(bears, Keyword.TRAMPLE) shouldBe true
        }

        test("adds three colorless mana") {
            val game = board()
            game.castSpellWithMode(1, "Konstrari Charm", 2).error shouldBe null
            game.resolveStack()
            game.state.getEntity(game.player1Id)!!.get<ManaPoolComponent>()!!.colorless shouldBe 3
        }
    }
}

package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Grim Repriser (FRA #136) — returns from the graveyard with a finality counter, but only once an
 * opponent has been dealt noncombat damage this turn.
 */
class GrimRepriserScenarioTest : ScenarioTestBase() {
    init {
        val returnAbility = cardRegistry.getCard("Grim Repriser")!!.script.activatedAbilities.single().id

        fun game() = scenario().withPlayers()
            .withCardInGraveyard(1, "Grim Repriser")
            .withCardOnBattlefield(1, "Grizzly Bears")
            .withCardsInHand(1, "Shock", 2)
            .withLandsOnBattlefield(1, "Mountain", 3)
            .withLandsOnBattlefield(1, "Swamp", 2)
            .withCardInLibrary(1, "Mountain")
            .withCardInLibrary(2, "Mountain")
            .withActivePlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            .build()

        test("activatable only after an opponent was dealt noncombat damage, and returns with a finality counter") {
            val game = game()
            val repriser = game.findCardsInGraveyard(1, "Grim Repriser").single()
            val bears = game.findPermanent("Grizzly Bears").shouldNotBeNull()

            withClue("nothing dealt yet") {
                game.execute(ActivateAbility(game.player1Id, repriser, returnAbility)).error shouldNotBe null
            }

            game.castSpell(1, "Shock", bears).error shouldBe null
            game.resolveStack()
            withClue("damage to a creature isn't damage to an opponent") {
                game.execute(ActivateAbility(game.player1Id, repriser, returnAbility)).error shouldNotBe null
            }

            game.castSpellTargetingPlayer(1, "Shock", 2).error shouldBe null
            game.resolveStack()

            game.execute(ActivateAbility(game.player1Id, repriser, returnAbility)).error shouldBe null
            game.resolveStack()

            val onField = game.findPermanent("Grim Repriser").shouldNotBeNull()
            game.state.getEntity(onField)!!.get<CountersComponent>()!!
                .getCount(CounterType.FINALITY) shouldBe 1
        }
    }
}

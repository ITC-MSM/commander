package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.battlefield.PreparedComponent
import com.wingedsheep.engine.state.components.battlefield.PreparedSpellCopyComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario test for Campus Composer // Aqueous Aria (Secrets of Strixhaven #40).
 *
 * Campus Composer enters prepared (it carries [com.wingedsheep.sdk.core.Keyword.PREPARED]). Becoming
 * prepared creates a copy of its prepare spell ("Aqueous Aria") in exile that its controller may cast
 * for {4}{U}; casting that copy creates a 3/3 blue and red Elemental with flying and unprepares the
 * creature.
 */
class CampusComposerScenarioTest : ScenarioTestBase() {

    private fun TestGame.findExileCopy(name: String): com.wingedsheep.sdk.model.EntityId? =
        state.getExile(player1Id).firstOrNull { id ->
            val e = state.getEntity(id)
            e?.get<CardComponent>()?.name == name && e.get<PreparedSpellCopyComponent>() != null
        }

    init {
        context("Campus Composer — Aqueous Aria (enters prepared)") {
            test("enters prepared; the copy makes a 3/3 flying Elemental and unprepares it") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Campus Composer")
                    .withLandsOnBattlefield(1, "Island", 9)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Campus Composer")
                game.resolveStack()

                val composer = game.findPermanent("Campus Composer")!!
                withClue("Campus Composer should be prepared on ETB") {
                    game.state.getEntity(composer)?.get<PreparedComponent>() shouldNotBe null
                }

                val copyId = game.findExileCopy("Campus Composer")!!

                fun elementalTokens(): Int = game.state.getBattlefield().count { id ->
                    game.state.getEntity(id)?.get<CardComponent>()?.typeLine?.subtypes?.any { it.value == "Elemental" } == true
                }
                val tokensBefore = elementalTokens()

                game.execute(CastSpell(game.player1Id, copyId, faceIndex = 0))
                game.resolveStack()

                withClue("Aqueous Aria creates one Elemental token") {
                    elementalTokens() shouldBe tokensBefore + 1
                }
                withClue("Campus Composer is no longer prepared after casting the copy") {
                    game.state.getEntity(composer)?.get<PreparedComponent>() shouldBe null
                }
            }
        }
    }
}

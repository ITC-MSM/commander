package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.collections.shouldContain

/**
 * Scenario tests for Molten Duplication (BIG #14).
 *
 * Molten Duplication — {1}{R} Sorcery.
 *   "Create a token that's a copy of target artifact or creature you control, except it's an
 *    artifact in addition to its other types. It gains haste until end of turn. Sacrifice it at
 *    the beginning of the next end step."
 */
class MoltenDuplicationScenarioTest : ScenarioTestBase() {

    private val projector = StateProjector()

    init {
        test("copies a creature you control as an artifact token with haste") {
            val game = scenario()
                .withPlayers()
                .withCardInHand(1, "Molten Duplication")
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withLandsOnBattlefield(1, "Mountain", 2)
                .withCardInLibrary(1, "Mountain")
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val bear = game.findPermanent("Grizzly Bears")!!
            game.castSpell(1, "Molten Duplication", bear).error shouldBe null
            game.resolveStack()

            val copies = game.findPermanents("Grizzly Bears")
            withClue("original + token copy = 2 Grizzly Bears") {
                copies.size shouldBe 2
            }
            val token = copies.first { it != bear }

            val tokenCard = game.state.getEntity(token)!!.get<CardComponent>()!!
            withClue("token is an artifact in addition to being a creature") {
                tokenCard.typeLine.cardTypes shouldContain CardType.ARTIFACT
                tokenCard.typeLine.cardTypes shouldContain CardType.CREATURE
            }

            withClue("token has haste") {
                projector.hasProjectedKeyword(game.state, token, Keyword.HASTE) shouldBe true
            }
            val projected = projector.project(game.state)
            withClue("token copies the 2/2 base stats") {
                projected.getPower(token) shouldBe 2
                projected.getToughness(token) shouldBe 2
            }
        }

        test("the copy is sacrificed at the next end step") {
            val game = scenario()
                .withPlayers()
                .withCardInHand(1, "Molten Duplication")
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withLandsOnBattlefield(1, "Mountain", 2)
                .withCardInLibrary(1, "Mountain")
                .withCardInLibrary(2, "Mountain")
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val bear = game.findPermanent("Grizzly Bears")!!
            game.castSpell(1, "Molten Duplication", bear).error shouldBe null
            game.resolveStack()
            game.findPermanents("Grizzly Bears").size shouldBe 2

            // Advance to the end step through real game flow; the delayed sacrifice fires there.
            game.passUntilPhase(Phase.ENDING, Step.END)
            game.resolveStack()

            withClue("the token copy is sacrificed at the beginning of the next end step") {
                game.findPermanents("Grizzly Bears").size shouldBe 1
            }
            withClue("the original Grizzly Bears survives") {
                game.findPermanent("Grizzly Bears") shouldBe bear
            }
        }

        test("can copy an artifact you control") {
            val game = scenario()
                .withPlayers()
                .withCardInHand(1, "Molten Duplication")
                .withCardOnBattlefield(1, "Mind Stone")
                .withLandsOnBattlefield(1, "Mountain", 2)
                .withCardInLibrary(1, "Mountain")
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val ring = game.findPermanent("Mind Stone")!!
            game.castSpell(1, "Molten Duplication", ring).error shouldBe null
            game.resolveStack()

            withClue("a token copy of the artifact is created") {
                game.findPermanents("Mind Stone").size shouldBe 2
            }
        }
    }
}

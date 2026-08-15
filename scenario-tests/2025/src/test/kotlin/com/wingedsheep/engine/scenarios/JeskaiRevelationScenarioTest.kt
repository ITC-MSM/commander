package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.mtg.sets.definitions.tdm.cards.JeskaiRevelation
import com.wingedsheep.sdk.model.CardDefinition
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Jeskai Revelation:
 *   {4}{U}{R}{W} Instant — "Return target spell or permanent to its owner's hand. …"
 *
 * Regression for the bug "Jeskai Revelation targeting a spell did not send the spell to the hand,
 * but left it on the stack": the bounce clause targets a "spell or permanent", so it must use the
 * stack-aware ReturnSpellOrPermanentToOwnersHand effect (not the battlefield-only ReturnToHand).
 */
class JeskaiRevelationScenarioTest : ScenarioTestBase() {

    init {
        cardRegistry.register(JeskaiRevelation)
        cardRegistry.register(
            CardDefinition.creature(
                name = "Test Ogre",
                manaCost = ManaCost.parse("{2}{R}"),
                subtypes = setOf(Subtype("Ogre")),
                power = 3,
                toughness = 3
            )
        )

        test("bouncing a spell on the stack returns it to its owner's hand; it does not resolve") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardInHand(1, "Jeskai Revelation")
                .withCardInHand(2, "Test Ogre")
                .withLandsOnBattlefield(1, "Island", 3)
                .withLandsOnBattlefield(1, "Mountain", 2)
                .withLandsOnBattlefield(1, "Plains", 2)
                .withLandsOnBattlefield(2, "Mountain", 3)
                .withActivePlayer(2)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            // Opponent casts a creature spell; it goes on the stack.
            game.castSpell(2, "Test Ogre").error shouldBe null
            val ogreSpellId = game.state.stack.first { entityId ->
                game.state.getEntity(entityId)?.get<CardComponent>()?.name == "Test Ogre"
            }

            // Active player passes; Player1 responds with Jeskai Revelation.
            game.passPriority()

            // Two targets: the spell on the stack (bounce) and a player (the 4 damage).
            val cast = game.execute(
                CastSpell(
                    game.player1Id,
                    game.findCardsInHand(1, "Jeskai Revelation").single(),
                    listOf(
                        ChosenTarget.Spell(ogreSpellId),
                        ChosenTarget.Player(game.player2Id)
                    )
                )
            )
            cast.error shouldBe null
            game.resolveStack()

            withClue("The targeted spell left the stack for its owner's hand (did not resolve)") {
                game.isInHand(2, "Test Ogre") shouldBe true
                game.isOnBattlefield("Test Ogre") shouldBe false
                game.state.stack.none { entityId ->
                    game.state.getEntity(entityId)?.get<CardComponent>()?.name == "Test Ogre"
                } shouldBe true
            }
        }
    }
}

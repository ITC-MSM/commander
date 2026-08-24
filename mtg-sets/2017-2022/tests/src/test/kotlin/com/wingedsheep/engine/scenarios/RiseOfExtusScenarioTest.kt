package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Rise of Extus (STX) — "Exile target creature. Exile up to one target instant or sorcery card
 * from a graveyard. Learn."
 *
 * The second target is genuinely optional ("up to one"), which is the half worth proving: the
 * spell must be castable and must resolve with only the creature chosen, and the declined
 * requirement must not shift the first target's position. `ContextTarget(1)` then resolves to
 * null and the second exile is a no-op rather than an error — `EffectContext.positionalTarget`
 * keeps requirement-index alignment precisely so that "target 0" still means the creature.
 */
class RiseOfExtusScenarioTest : ScenarioTestBase() {

    init {
        context("both targets chosen") {
            test("exiles the creature and the graveyard spell") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Rise of Extus")
                    .withLandsOnBattlefield(1, "Plains", 6)
                    .withCardOnBattlefield(2, "Centaur Courser")
                    .withCardInGraveyard(2, "Shock")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val courser = game.findPermanent("Centaur Courser")!!
                val shock = game.findCardsInGraveyard(2, "Shock").first()
                val cardId = game.state.getHand(game.player1Id)
                    .first { game.state.getEntity(it)?.get<CardComponent>()?.name == "Rise of Extus" }

                game.execute(
                    CastSpell(
                        playerId = game.player1Id,
                        cardId = cardId,
                        targets = listOf(
                            ChosenTarget.Permanent(courser),
                            ChosenTarget.Card(shock, game.player2Id, Zone.GRAVEYARD)
                        )
                    )
                ).error shouldBe null
                game.resolveStack()
                while (game.hasPendingDecision()) game.skipSelection()

                withClue("the creature is exiled, not destroyed") {
                    game.findPermanent("Centaur Courser") shouldBe null
                    game.isInExile(2, "Centaur Courser") shouldBe true
                    game.isInGraveyard(2, "Centaur Courser") shouldBe false
                }
                withClue("the graveyard instant is exiled too") {
                    game.isInExile(2, "Shock") shouldBe true
                    game.isInGraveyard(2, "Shock") shouldBe false
                }
            }
        }

        context("the optional target declined") {
            test("resolves with only the creature chosen, leaving the graveyard alone") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Rise of Extus")
                    .withLandsOnBattlefield(1, "Plains", 6)
                    .withCardOnBattlefield(2, "Centaur Courser")
                    .withCardInGraveyard(2, "Shock")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val courser = game.findPermanent("Centaur Courser")!!

                // Only the first requirement is satisfied — "up to one" means zero is legal.
                game.castSpell(1, "Rise of Extus", courser).error shouldBe null
                game.resolveStack()
                while (game.hasPendingDecision()) game.skipSelection()

                withClue("the creature still gets exiled — target 0 did not shift") {
                    game.findPermanent("Centaur Courser") shouldBe null
                    game.isInExile(2, "Centaur Courser") shouldBe true
                }
                withClue("nothing was exiled from the graveyard") {
                    game.isInGraveyard(2, "Shock") shouldBe true
                    game.isInExile(2, "Shock") shouldBe false
                }
            }
        }
    }
}

package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.engine.support.ScenarioTestBase.TestGame
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Takenuma, Abandoned Mire (NEO #278) — Legendary Land.
 *
 *   {T}: Add {B}.
 *   Channel — {3}{B}, Discard this card: Mill three cards, then return a creature or planeswalker
 *   card from your graveyard to your hand. This ability costs {1} less to activate for each
 *   legendary creature you control.
 *
 * See OtawaraSoaringCityScenarioTest for the shared channel shape. Takenuma's own points are the
 * printed order — the mill happens first, so a creature it mills is itself a legal choice — and
 * that the return is a mandatory *choice on resolution*, not a target, and not a "may".
 */
class TakenumaAbandonedMireScenarioTest : ScenarioTestBase() {

    private fun channelAbilityId() = cardRegistry.getCard("Takenuma, Abandoned Mire")!!
        .activatedAbilities.first { it.activateFromZone == Zone.HAND }.id


    /**
     * Clear whatever prompts an activation raises, without assuming their shape or count.
     * Bounded, because an unbounded `while (hasPendingDecision())` spins forever the moment a
     * decision arrives that the chosen responder can't answer — `skipSelection` is a no-op
     * against a yes/no prompt, which is exactly what the consolation search raises.
     */
    private fun declineAllPrompts(game: TestGame, max: Int = 8) {
        var guard = 0
        while (game.hasPendingDecision() && guard++ < max) {
            when (game.getPendingDecision()) {
                is YesNoDecision -> game.answerYesNo(false)
                else -> game.skipSelection()
            }
        }
        withClue("prompts should settle well inside the guard") {
            game.hasPendingDecision() shouldBe false
        }
    }

    init {
        context("Takenuma, Abandoned Mire") {

            test("mills three, then returns a creature card already in the graveyard") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Takenuma, Abandoned Mire")
                    .withLandsOnBattlefield(1, "Swamp", 4)
                    .withCardInGraveyard(1, "Grizzly Bears")
                    // The scenario library starts empty — give the mill something to eat.
                    .withCardInLibrary(1, "Hill Giant")
                    .withCardInLibrary(1, "Hill Giant")
                    .withCardInLibrary(1, "Hill Giant")
                    .withCardInLibrary(1, "Hill Giant")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val handCard = game.findCardsInHand(1, "Takenuma, Abandoned Mire").first()
                val librarySizeBefore = game.state.getZone(game.player1Id, Zone.LIBRARY).size

                val result = game.execute(
                    ActivateAbility(game.player1Id, handCard, channelAbilityId())
                )
                withClue("{3}{B} from four Swamps: ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()

                withClue("Three cards were milled") {
                    game.state.getZone(game.player1Id, Zone.LIBRARY).size shouldBe
                        librarySizeBefore - 3
                }

                withClue("The return is a choice made on resolution") {
                    game.hasPendingDecision() shouldBe true
                }
                val choice = game.getPendingDecision() as? SelectCardsDecision
                choice.shouldNotBeNull()
                val bears = choice.cardInfo!!.entries
                    .first { it.value.name == "Grizzly Bears" }.key
                game.selectCards(listOf(bears))

                withClue("Grizzly Bears is back in hand") {
                    game.isInHand(1, "Grizzly Bears") shouldBe true
                    game.isInGraveyard(1, "Grizzly Bears") shouldBe false
                }
            }

            test("with no creature or planeswalker card to return, the mill still happens") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Takenuma, Abandoned Mire")
                    .withLandsOnBattlefield(1, "Swamp", 4)
                    // Library is all lands, so the mill can never turn up a returnable card.
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(1, "Forest")
                    .withCardInGraveyard(1, "Lightning Bolt")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val handCard = game.findCardsInHand(1, "Takenuma, Abandoned Mire").first()
                val librarySizeBefore = game.state.getZone(game.player1Id, Zone.LIBRARY).size

                game.execute(ActivateAbility(game.player1Id, handCard, channelAbilityId()))
                    .error shouldBe null
                game.resolveStack()
                // Whatever the mill turned up may or may not be returnable; clear any prompt.
                declineAllPrompts(game)

                withClue("The mill is not conditional on the return finding anything") {
                    game.state.getZone(game.player1Id, Zone.LIBRARY).size shouldBe
                        librarySizeBefore - 3
                }
                withClue("Lightning Bolt is not a creature or planeswalker — it stays put") {
                    game.isInGraveyard(1, "Lightning Bolt") shouldBe true
                    game.isInHand(1, "Lightning Bolt") shouldBe false
                }
            }
        }
    }
}

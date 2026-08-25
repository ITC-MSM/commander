package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.PassPriority
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Eiganjo, Seat of the Empire (NEO #268) — Legendary Land.
 *
 *   {T}: Add {W}.
 *   Channel — {2}{W}, Discard this card: It deals 4 damage to target attacking or blocking
 *   creature. This ability costs {1} less to activate for each legendary creature you control.
 *
 * See OtawaraSoaringCityScenarioTest for the channel shape these five lands share; this test
 * covers Eiganjo's own restriction — the target must be *attacking or blocking*, which is what
 * makes it a combat trick rather than generic removal.
 */
class EiganjoSeatOfTheEmpireScenarioTest : ScenarioTestBase() {

    private fun channelAbilityId() = cardRegistry.getCard("Eiganjo, Seat of the Empire")!!
        .activatedAbilities.first { it.activateFromZone == Zone.HAND }.id

    init {
        context("Eiganjo, Seat of the Empire") {

            test("4 damage kills an attacking 3/3") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Eiganjo, Seat of the Empire")
                    .withLandsOnBattlefield(1, "Plains", 3)
                    .withCardOnBattlefield(2, "Centaur Courser")   // 3/3
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Centaur Courser" to 1)).error shouldBe null
                // The attacking player holds priority first in the declare-attackers step.
                game.execute(PassPriority(game.player2Id))

                val courser = game.findPermanent("Centaur Courser")!!
                val handCard = game.findCardsInHand(1, "Eiganjo, Seat of the Empire").first()

                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = handCard,
                        abilityId = channelAbilityId(),
                        targets = listOf(ChosenTarget.Permanent(courser))
                    )
                )
                withClue("{2}{W} from three Plains, targeting an attacker: ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()

                withClue("4 damage kills the 3/3") {
                    game.isOnBattlefield("Centaur Courser") shouldBe false
                }
                withClue("The land discarded itself to pay") {
                    game.isInGraveyard(1, "Eiganjo, Seat of the Empire") shouldBe true
                }
            }

            test("a creature that is neither attacking nor blocking is not a legal target") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Eiganjo, Seat of the Empire")
                    .withLandsOnBattlefield(1, "Plains", 3)
                    .withCardOnBattlefield(2, "Centaur Courser")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val courser = game.findPermanent("Centaur Courser")!!
                val handCard = game.findCardsInHand(1, "Eiganjo, Seat of the Empire").first()

                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = handCard,
                        abilityId = channelAbilityId(),
                        targets = listOf(ChosenTarget.Permanent(courser))
                    )
                )
                withClue("Outside combat there is nothing Eiganjo may point at") {
                    result.error shouldNotBe null
                }
            }
        }
    }
}

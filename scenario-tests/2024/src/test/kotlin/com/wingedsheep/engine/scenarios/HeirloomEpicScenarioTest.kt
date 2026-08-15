package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.AlternativePaymentChoice
import com.wingedsheep.sdk.scripting.ConvokePayment
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for Heirloom Epic.
 *
 * Card reference:
 * - Heirloom Epic ({1}): Artifact
 *   "{4}, {T}: Draw a card. For each mana in this ability's activation cost,
 *    you may tap an untapped creature you control rather than pay that mana.
 *    Activate only as a sorcery."
 *
 * Tests:
 * 1. Activating with enough mana draws a card
 * 2. Activating with convoke (tapping creatures) draws a card
 * 3. Cannot activate at instant speed (sorcery only)
 * 4. Legal actions include convoke creature info when Heirloom Epic is on the battlefield
 */
class HeirloomEpicScenarioTest : ScenarioTestBase() {

    init {
        context("Heirloom Epic - activated ability with convoke") {

            test("activating with mana draws a card") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Heirloom Epic")
                    .withLandsOnBattlefield(1, "Mountain", 4)
                    .withCardInLibrary(1, "Mountain")
                    .withCardInLibrary(1, "Mountain")
                    .withCardInLibrary(2, "Mountain")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val initialHandSize = game.handSize(1)

                // Get the ability ID
                val cardDef = cardRegistry.getCard("Heirloom Epic")!!
                val ability = cardDef.script.activatedAbilities.first()

                val epicId = game.findPermanent("Heirloom Epic")!!

                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = epicId,
                        abilityId = ability.id
                    )
                )
                withClue("Activating Heirloom Epic should succeed: ${result.error}") {
                    result.error shouldBe null
                }

                // Resolve the ability
                game.resolveStack()

                // Should have drawn a card
                game.handSize(1) shouldBe initialHandSize + 1
            }

            test("activating with convoke (tapping creatures to pay mana) draws a card") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Heirloom Epic")
                    .withCardOnBattlefield(1, "Glory Seeker")  // 2/2 creature to tap for convoke
                    .withCardOnBattlefield(1, "Glory Seeker")  // Another creature
                    .withLandsOnBattlefield(1, "Mountain", 2)  // Only 2 lands = not enough without convoke
                    .withCardInLibrary(1, "Mountain")
                    .withCardInLibrary(1, "Mountain")
                    .withCardInLibrary(2, "Mountain")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val initialHandSize = game.handSize(1)

                val cardDef = cardRegistry.getCard("Heirloom Epic")!!
                val ability = cardDef.script.activatedAbilities.first()

                val epicId = game.findPermanent("Heirloom Epic")!!
                val glorySeekers = game.findAllPermanents("Glory Seeker")
                glorySeekers.size shouldBe 2

                // Tap 2 creatures to pay for 2 of the 4 generic mana
                val convokeChoices = mapOf(
                    glorySeekers[0] to ConvokePayment(color = null),
                    glorySeekers[1] to ConvokePayment(color = null)
                )

                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = epicId,
                        abilityId = ability.id,
                        alternativePayment = AlternativePaymentChoice(convokedCreatures = convokeChoices)
                    )
                )
                withClue("Activating Heirloom Epic with convoke should succeed: ${result.error}") {
                    result.error shouldBe null
                }

                // Resolve the ability
                game.resolveStack()

                // Should have drawn a card
                game.handSize(1) shouldBe initialHandSize + 1
            }

            test("cannot activate without enough mana or creatures") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Heirloom Epic")
                    .withLandsOnBattlefield(1, "Mountain", 1)  // Only 1 land, not enough
                    .withCardInLibrary(1, "Mountain")
                    .withCardInLibrary(2, "Mountain")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                // Check legal actions — should not include activation
                val legalActions = game.getLegalActions(1)
                val epicActions = legalActions.filter {
                    it.actionType == "ActivateAbility" &&
                        (it.action as? ActivateAbility)?.sourceId == game.findPermanent("Heirloom Epic")
                }

                // The ability should either not appear or appear as unaffordable
                val affordableEpicActions = epicActions.filter { it.isAffordable }
                withClue("Heirloom Epic should not be affordable with only 1 land and no creatures") {
                    affordableEpicActions.size shouldBe 0
                }
            }

            test("legal actions include convoke creature info") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Heirloom Epic")
                    .withCardOnBattlefield(1, "Glory Seeker")
                    .withLandsOnBattlefield(1, "Mountain", 4)
                    .withCardInLibrary(1, "Mountain")
                    .withCardInLibrary(2, "Mountain")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val legalActions = game.getLegalActions(1)
                val epicAction = legalActions.find {
                    it.actionType == "ActivateAbility" &&
                        (it.action as? ActivateAbility)?.sourceId == game.findPermanent("Heirloom Epic") &&
                        it.isAffordable
                }

                withClue("Heirloom Epic action should have convoke info") {
                    epicAction shouldNotBe null
                    epicAction!!.hasConvoke shouldBe true
                    epicAction.validConvokeCreatures shouldNotBe null
                    epicAction.validConvokeCreatures!!.size shouldBe 1  // Glory Seeker
                }
            }
        }
    }
}

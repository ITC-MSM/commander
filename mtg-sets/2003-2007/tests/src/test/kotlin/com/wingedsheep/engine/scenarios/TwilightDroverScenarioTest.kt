package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.rav.cards.TwilightDrover
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Twilight Drover (RAV #33) — "Whenever a creature token leaves the battlefield, put a +1/+1
 * counter on this creature." / "{2}{W}, Remove a +1/+1 counter from this creature: Create two 1/1
 * white Spirit creature tokens with flying."
 *
 * The trigger is what's worth proving. It is an ANY-bound `from = BATTLEFIELD` zone change carrying
 * a token-only card predicate and no destination, and each of those three parts can fail silently:
 * it could miss tokens entirely, fire on an ordinary creature card, or be scoped to the Drover's
 * own controller. One test each, plus the loop the card is built around — spending a counter for
 * two Spirits that feed the trigger again when they go.
 */
class TwilightDroverScenarioTest : ScenarioTestBase() {

    private val makeSpirits = TwilightDrover.activatedAbilities.single().id

    init {
        context("Twilight Drover") {

            fun TestGame.droverCounters(): Int = findPermanent("Twilight Drover")
                ?.let { state.getEntity(it)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) }
                ?: 0

            test("a creature token leaving the battlefield puts a +1/+1 counter on the Drover") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Twilight Drover")
                    .withCardOnBattlefield(1, "Grizzly Bears", isToken = true)
                    .withCardInHand(1, "Shock")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                withClue("the Drover starts bare") { game.droverCounters() shouldBe 0 }

                game.castSpell(1, "Shock", game.findPermanent("Grizzly Bears")!!).error shouldBe null
                game.resolveStack()
                game.checkStateBasedActions()
                game.resolveStack()

                withClue("the token left the battlefield, so the Drover grew") {
                    game.findPermanent("Grizzly Bears") shouldBe null
                    game.droverCounters() shouldBe 1
                }
            }

            test("an ordinary creature card dying does not — the trigger is token-only") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Twilight Drover")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInHand(1, "Shock")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Shock", game.findPermanent("Grizzly Bears")!!).error shouldBe null
                game.resolveStack()
                game.checkStateBasedActions()
                game.resolveStack()

                withClue("a nontoken creature is not a creature token") {
                    game.findPermanent("Grizzly Bears") shouldBe null
                    game.droverCounters() shouldBe 0
                }
            }

            test("an opponent's token feeds it too — the trigger is not controller-scoped") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Twilight Drover")
                    .withCardOnBattlefield(2, "Grizzly Bears", isToken = true)
                    .withCardInHand(1, "Shock")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Shock", game.findPermanent("Grizzly Bears")!!).error shouldBe null
                game.resolveStack()
                game.checkStateBasedActions()
                game.resolveStack()

                withClue("any creature token, under any controller") { game.droverCounters() shouldBe 1 }
            }

            test("spending the counter makes two 1/1 white Spirits with flying") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Twilight Drover")
                    .withCardOnBattlefield(1, "Grizzly Bears", isToken = true)
                    .withCardInHand(1, "Shock")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withLandsOnBattlefield(1, "Plains", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                // Bank a counter off the token's death — the only way the Drover ever gets one.
                game.castSpell(1, "Shock", game.findPermanent("Grizzly Bears")!!).error shouldBe null
                game.resolveStack()
                game.checkStateBasedActions()
                game.resolveStack()
                game.droverCounters() shouldBe 1

                val drover = game.findPermanent("Twilight Drover")!!
                val activation = game.execute(
                    ActivateAbility(playerId = game.player1Id, sourceId = drover, abilityId = makeSpirits)
                )
                withClue("activation should succeed: ${activation.error}") { activation.error shouldBe null }
                game.resolveStack()

                val spirits: List<EntityId> = game.findAllPermanents("Spirit Token")
                withClue("two Spirits, and the counter is spent") {
                    spirits.size shouldBe 2
                    game.droverCounters() shouldBe 0
                }

                val projected = game.state.projectedState
                spirits.forEach { spirit ->
                    withClue("each Spirit is a 1/1 flier") {
                        projected.getPower(spirit) shouldBe 1
                        projected.getToughness(spirit) shouldBe 1
                        projected.hasKeyword(spirit, Keyword.FLYING) shouldBe true
                    }
                }
            }
        }
    }
}

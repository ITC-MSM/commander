package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.rav.cards.Leashling
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Leashling (RAV #265) — "Put a card from your hand on top of your library: Return this creature to
 * its owner's hand."
 *
 * The cost is `CostAtom.PutFromHandOnTopOfLibrary`: the player picks the card (it decides their next
 * draw), the card goes on top of the library — not the graveyard, it isn't a discard — before the
 * ability is on the stack, and an empty hand can't pay it at all.
 */
class LeashlingScenarioTest : ScenarioTestBase() {

    private val abilityId = Leashling.activatedAbilities.single().id

    init {
        context("Leashling") {

            test("the chosen hand card goes on top of the library and Leashling returns to hand") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Leashling")
                    .withCardInHand(1, "Grizzly Bears")
                    .withCardInHand(1, "Hill Giant")
                    .withCardInLibrary(1, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val leashling = game.findPermanent("Leashling")!!
                val giant = game.findCardsInHand(1, "Hill Giant").single()
                val bears = game.findCardsInHand(1, "Grizzly Bears").single()

                game.execute(ActivateAbility(playerId = game.player1Id, sourceId = leashling, abilityId = abilityId))
                    .error shouldBe null

                val decision = game.getPendingDecision()
                decision.shouldBeInstanceOf<SelectCardsDecision>()
                withClue("every card in hand is a legal payment") {
                    decision.options.toSet() shouldBe setOf(giant, bears)
                    decision.minSelections shouldBe 1
                    decision.maxSelections shouldBe 1
                }
                game.selectCards(listOf(giant)).error shouldBe null

                withClue("the cost is paid on activation: the Giant is on top before the ability resolves") {
                    game.state.getZone(ZoneKey(game.player1Id, Zone.LIBRARY)).first() shouldBe giant
                    game.state.stack.size shouldBe 1
                }
                withClue("putting a card on the library is not a discard") {
                    game.graveyardSize(1) shouldBe 0
                }

                game.resolveStack()

                withClue("Leashling is back in its owner's hand; the unchosen card stays") {
                    game.isInHand(1, "Leashling") shouldBe true
                    game.isOnBattlefield("Leashling") shouldBe false
                    game.isInHand(1, "Grizzly Bears") shouldBe true
                    game.isInHand(1, "Hill Giant") shouldBe false
                }
            }

            test("a single card in hand is the forced payment, no prompt") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Leashling")
                    .withCardInHand(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val leashling = game.findPermanent("Leashling")!!
                val bears = game.findCardsInHand(1, "Grizzly Bears").single()

                game.execute(ActivateAbility(playerId = game.player1Id, sourceId = leashling, abilityId = abilityId))
                    .error shouldBe null
                game.hasPendingDecision() shouldBe false
                game.state.getZone(ZoneKey(game.player1Id, Zone.LIBRARY)).first() shouldBe bears

                game.resolveStack()
                game.isInHand(1, "Leashling") shouldBe true
                game.handSize(1) shouldBe 1
            }

            test("with an empty hand the ability can't be activated") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Leashling")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val leashling = game.findPermanent("Leashling")!!
                game.getLegalActions(1)
                    .filter { (it.action as? ActivateAbility)?.sourceId == leashling }
                    .shouldBeEmpty()
            }

            test("the ability is offered when the hand holds a card") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Leashling")
                    .withCardInHand(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val leashling = game.findPermanent("Leashling")!!
                game.getLegalActions(1)
                    .count { (it.action as? ActivateAbility)?.sourceId == leashling } shouldBe 1
            }
        }
    }
}

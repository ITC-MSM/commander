package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Taster of Wares (ECL #121).
 *
 * "When this creature enters, target opponent reveals X cards from their hand, where X is the
 *  number of Goblins you control. You choose one of those cards. That player exiles it. If an
 *  instant or sorcery card is exiled this way, you may cast it for as long as you control this
 *  creature, and mana of any type can be spent to cast that spell."
 *
 * Regression guard for the playtest bug where nothing was ever exiled: the "you choose one of
 * those cards" step was modelled with [SelectionMode.ChooseUpTo], which produces a
 * `minSelections = 0` decision. Declining it — as a player clicking through, or the AI, will —
 * left the collection empty, so the exile step moved nothing. The clause is mandatory, so it must
 * be [SelectionMode.ChooseExactly].
 */
class TasterOfWaresScenarioTest : ScenarioTestBase() {

    private fun namesIn(game: TestGame, ids: List<com.wingedsheep.sdk.model.EntityId>) =
        ids.map { game.state.getEntity(it)?.get<CardComponent>()?.name }

    init {
        context("Taster of Wares") {

            test("the opponent reveals X cards and you must choose one — it is exiled") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardInHand(1, "Taster of Wares")
                    .withCardOnBattlefield(1, "Raging Goblin")
                    .withCardOnBattlefield(1, "Goblin Bully")
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withCardInHand(2, "Volcanic Hammer")
                    .withCardInHand(2, "Mind Rot")
                    .withCardInHand(2, "Hill Giant")
                    .withLandsOnBattlefield(2, "Mountain", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Taster of Wares")
                game.resolveStack()

                // Alice (the controller) picks which revealed card is exiled — and the choice is
                // mandatory: one card, minimum one.
                val decision = game.getPendingDecision()
                withClue("Alice should be asked to choose a card to exile") {
                    (decision != null) shouldBe true
                    decision?.playerId shouldBe game.player1Id
                }
                val select = decision as com.wingedsheep.engine.core.SelectCardsDecision
                withClue("The choice is mandatory — 'you choose one of those cards'") {
                    select.minSelections shouldBe 1
                }
                // Three Goblins (Raging Goblin, Goblin Bully, Taster itself) vs a three-card hand:
                // the whole hand is revealed.
                withClue("All three cards should be on offer") {
                    select.options.size shouldBe 3
                }

                val mindRot = select.options.first { id ->
                    game.state.getEntity(id)?.get<CardComponent>()?.name == "Mind Rot"
                }
                game.selectCards(listOf(mindRot))
                game.resolveStack()

                withClue("Mind Rot should be in Bob's exile zone: ${namesIn(game, game.state.getExile(game.player2Id))}") {
                    namesIn(game, game.state.getExile(game.player2Id)) shouldBe listOf("Mind Rot")
                }
                withClue("Bob's hand should be down to two cards") {
                    game.handSize(2) shouldBe 2
                }
            }

            test("an exiled sorcery can be cast from exile by the Taster's controller") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardInHand(1, "Taster of Wares")
                    .withCardOnBattlefield(1, "Raging Goblin")
                    .withLandsOnBattlefield(1, "Swamp", 5)
                    .withCardInHand(2, "Volcanic Hammer")
                    .withCardInHand(2, "Hill Giant")
                    .withLandsOnBattlefield(2, "Mountain", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Taster of Wares")
                game.resolveStack()

                // Two Goblins (Raging Goblin + Taster), two cards in hand — Bob reveals both.
                val decision = game.getPendingDecision() as com.wingedsheep.engine.core.SelectCardsDecision
                val hammer = decision.options.first { id ->
                    game.state.getEntity(id)?.get<CardComponent>()?.name == "Volcanic Hammer"
                }
                game.selectCards(listOf(hammer))
                game.resolveStack()

                withClue("Volcanic Hammer should be exiled") {
                    namesIn(game, game.state.getExile(game.player2Id)) shouldBe listOf("Volcanic Hammer")
                }
                // "you may cast it ... and mana of any type can be spent" — Alice has only Swamps,
                // so the permission has to relax {1}{R} for this to be legal at all.
                withClue("Alice should be offered Volcanic Hammer as a castable action") {
                    game.getLegalActions(1).any { action ->
                        action.description.contains("Volcanic Hammer")
                    } shouldBe true
                }
            }

            test("the cast permission ends when the Taster leaves the battlefield") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardInHand(1, "Taster of Wares")
                    .withCardOnBattlefield(1, "Raging Goblin")
                    .withLandsOnBattlefield(1, "Swamp", 5)
                    .withCardInHand(2, "Volcanic Hammer")
                    .withCardInHand(2, "Hill Giant")
                    .withLandsOnBattlefield(2, "Mountain", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Taster of Wares")
                game.resolveStack()

                val decision = game.getPendingDecision() as com.wingedsheep.engine.core.SelectCardsDecision
                val hammer = decision.options.first { id ->
                    game.state.getEntity(id)?.get<CardComponent>()?.name == "Volcanic Hammer"
                }
                game.selectCards(listOf(hammer))
                game.resolveStack()

                withClue("Alice can cast the exiled Hammer while she controls the Taster") {
                    game.getLegalActions(1).any { it.description.contains("Volcanic Hammer") } shouldBe true
                }

                // "for as long as you control this creature" — destroy the Taster and the
                // permission ends. The Hammer stays exiled; only the permission is revoked.
                val taster = game.findPermanent("Taster of Wares")!!
                game.state = com.wingedsheep.engine.handlers.effects.ZoneMovementUtils.moveCardToZone(
                    game.state,
                    taster,
                    com.wingedsheep.sdk.core.Zone.GRAVEYARD
                ).state
                game.checkStateBasedActions()

                withClue("Volcanic Hammer should still be exiled") {
                    namesIn(game, game.state.getExile(game.player2Id)) shouldBe listOf("Volcanic Hammer")
                }
                withClue("Alice can no longer cast it once the Taster is gone") {
                    game.getLegalActions(1).none { it.description.contains("Volcanic Hammer") } shouldBe true
                }
            }

            test("an exiled creature card grants no cast permission") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardInHand(1, "Taster of Wares")
                    .withLandsOnBattlefield(1, "Swamp", 5)
                    .withCardInHand(2, "Hill Giant")
                    .withCardInHand(2, "Grizzly Bears")
                    .withLandsOnBattlefield(2, "Mountain", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Taster of Wares")
                game.resolveStack()

                // Taster is the only Goblin, so X = 1: Bob picks which single card to reveal.
                val revealDecision = game.getPendingDecision() as com.wingedsheep.engine.core.SelectCardsDecision
                withClue("Bob chooses which card to reveal") {
                    revealDecision.playerId shouldBe game.player2Id
                }
                game.selectCards(listOf(revealDecision.options.first()))
                game.resolveStack()

                // Only one card was revealed, so Alice's mandatory choice auto-resolves to it.
                if (game.hasPendingDecision()) {
                    val pick = game.getPendingDecision() as com.wingedsheep.engine.core.SelectCardsDecision
                    game.selectCards(listOf(pick.options.first()))
                    game.resolveStack()
                }

                withClue("Exactly one card should be exiled") {
                    game.state.getExile(game.player2Id).size shouldBe 1
                }
                withClue("A creature card grants Alice no cast-from-exile permission") {
                    game.getLegalActions(1).none { action ->
                        action.description.contains("Hill Giant") ||
                            action.description.contains("Grizzly Bears")
                    } shouldBe true
                }
            }
        }
    }
}

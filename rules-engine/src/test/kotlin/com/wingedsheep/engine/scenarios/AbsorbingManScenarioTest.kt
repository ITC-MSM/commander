package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.PassPriority
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.RevertCopyAtYourNextTurnComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario test for Absorbing Man (MSH #199) — {1}{G}{U} Legendary Creature, 4/4, Rare.
 *
 * "Vigilance
 *  At the beginning of your first main phase, until your next turn, Absorbing Man becomes a copy of
 *  up to one target artifact, non-Aura enchantment, or land, except his name is Absorbing Man, he's
 *  a legendary 4/4 Human Villain creature in addition to his other types, and he has vigilance."
 *
 * Focus: the **additive** direction of copy exceptions (CR 205.1b's "in addition to its other
 * types" retention clause) plus P/T conjured onto a copy source that had none, and the
 * `Duration.UntilYourNextTurn` revert window — which is what lets his own trigger, wiped by the copy, come back and fire again.
 */
class AbsorbingManScenarioTest : ScenarioTestBase() {

    init {
        context("Absorbing Man") {

            // The "until your next turn" cases run three turns, so both libraries need cards —
            // the scenario builder starts them empty and a draw from an empty library ends the
            // game (CR 704.5b), which silently stalls any further turn advance.
            fun board(islands: Int = 2): ScenarioTestBase.TestGame {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Absorbing Man")
                    .withCardOnBattlefield(1, "Futurist Forge")
                    .withLandsOnBattlefield(1, "Island", islands)
                    .withActivePlayer(1)
                    .inPhase(Phase.BEGINNING, Step.UPKEEP)
                repeat(5) {
                    builder = builder.withCardInLibrary(1, "Island").withCardInLibrary(2, "Island")
                }
                return builder.build()
            }

            /** Pass priority until the first-main-phase trigger wants its target. */
            fun ScenarioTestBase.TestGame.advanceToFirstMainTrigger() {
                var iterations = 0
                while (!hasPendingDecision() &&
                    state.step != Step.PRECOMBAT_MAIN &&
                    iterations++ < 40
                ) {
                    val priority = state.priorityPlayerId ?: break
                    execute(PassPriority(priority))
                }
            }

            /**
             * Advance to the next player's upkeep. Routed through the end step on purpose:
             * `passUntilPhase` returns immediately when the game is already at the requested
             * phase/step, so two upkeep hops in a row would silently be a single hop.
             */
            fun ScenarioTestBase.TestGame.advanceToNextUpkeep() {
                passUntilPhase(Phase.ENDING, Step.END)
                passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
            }

            fun ScenarioTestBase.TestGame.copyOnto(targetId: EntityId) {
                advanceToFirstMainTrigger()
                if (hasPendingDecision()) selectTargets(listOf(targetId))
                resolveStack()
            }

            test("becomes the artifact but stays a legendary 4/4 Human Villain creature named Absorbing Man") {
                val game = board()
                val absorbingMan = game.findPermanent("Absorbing Man")!!
                val forge = game.findPermanent("Futurist Forge")!!

                game.copyOnto(forge)

                val card = game.state.getEntity(absorbingMan)!!.get<CardComponent>()!!
                withClue("except his name is Absorbing Man") {
                    card.name shouldBe "Absorbing Man"
                }
                withClue("the copied card type is kept — 'in addition to his other types'") {
                    card.typeLine.isArtifact shouldBe true
                }
                withClue("and the stated types are added on top") {
                    card.typeLine.isCreature shouldBe true
                    card.typeLine.isLegendary shouldBe true
                    card.typeLine.subtypes.contains(Subtype.HUMAN) shouldBe true
                    card.typeLine.subtypes.contains(Subtype.VILLAIN) shouldBe true
                }
                withClue("he's a 4/4 — base P/T conjured onto a copy source that had none") {
                    game.state.projectedState.getPower(absorbingMan) shouldBe 4
                    game.state.projectedState.getToughness(absorbingMan) shouldBe 4
                }
                withClue("and he has vigilance") {
                    game.state.projectedState.hasKeyword(absorbingMan, Keyword.VIGILANCE) shouldBe true
                }
                withClue("the copy is tagged to revert on its controller's next turn") {
                    game.state.getEntity(absorbingMan)!!
                        .get<RevertCopyAtYourNextTurnComponent>()?.playerId shouldBe game.player1Id
                }
            }

            test("the copy survives the opponent's turn and wears off on his controller's next turn") {
                val game = board()
                val absorbingMan = game.findPermanent("Absorbing Man")!!
                val forge = game.findPermanent("Futurist Forge")!!
                game.copyOnto(forge)

                game.advanceToNextUpkeep()
                withClue("still a copy during the opponent's turn") {
                    game.state.activePlayerId shouldBe game.player2Id
                    game.state.getEntity(absorbingMan)!!.get<CardComponent>()!!
                        .typeLine.isArtifact shouldBe true
                }

                game.advanceToNextUpkeep()
                withClue("reverted after the untap step of his controller's next turn") {
                    game.state.activePlayerId shouldBe game.player1Id
                    val card = game.state.getEntity(absorbingMan)!!.get<CardComponent>()!!
                    card.typeLine.isArtifact shouldBe false
                    card.name shouldBe "Absorbing Man"
                }
            }

            test("the trigger comes back with him, so he copies again every turn") {
                val game = board()
                val absorbingMan = game.findPermanent("Absorbing Man")!!
                val forge = game.findPermanent("Futurist Forge")!!
                game.copyOnto(forge)

                withClue("while the copy is up his printed trigger is gone with the rest of his text") {
                    game.state.getEntity(absorbingMan)!!.get<CardComponent>()!!
                        .cardDefinitionId.contains("Futurist Forge") shouldBe true
                }

                // Opponent's turn, then back to his controller's — the copy reverts at untap.
                game.advanceToNextUpkeep()
                game.advanceToNextUpkeep()

                withClue("he is himself again, so the printed trigger is back") {
                    game.state.getEntity(absorbingMan)!!.get<CardComponent>()!!
                        .cardDefinitionId.contains("Absorbing Man") shouldBe true
                }

                // Aim the re-fired trigger at a *land* this time — the other branch of his target
                // filter, and the case that has no base P/T of its own to copy.
                val island = game.findPermanent("Island")!!
                game.copyOnto(island)

                val card = game.state.getEntity(absorbingMan)!!.get<CardComponent>()!!
                withClue("the trigger really did fire again and resolved onto the new target") {
                    card.cardDefinitionId.contains("Island") shouldBe true
                    card.typeLine.isLand shouldBe true
                }
                withClue("and the same exception clause applies to the second copy") {
                    card.name shouldBe "Absorbing Man"
                    card.typeLine.isCreature shouldBe true
                    card.typeLine.isLegendary shouldBe true
                    game.state.projectedState.getPower(absorbingMan) shouldBe 4
                    game.state.projectedState.getToughness(absorbingMan) shouldBe 4
                }
            }

            // Playtest regression (u06): the copy's activated ability was never offered in the
            // running app. `ActivatedAbilityEnumerator` resolved the permanent's definition by
            // `CardComponent.name`, and the "except his name is Absorbing Man" clause keeps that
            // name pointing at the printed card — which has no activated ability at all. The
            // scenario tests missed it because they drive `ActivateAbility` directly, and
            // `ActivateAbilityHandler` had always resolved by `cardDefinitionId`. So the ability
            // was executable but not enumerable: the exact asymmetry a player sees as "the button
            // isn't there". Assert through the player-facing enumeration path, not the action.
            test("the copied card's activated ability is offered even though the copy keeps his own name") {
                // Four Islands: enough to actually pay the copied {3}{U} half of the cost, so the
                // assertion covers an offered *and* affordable action rather than a greyed-out one.
                val game = board(islands = 4)
                val absorbingMan = game.findPermanent("Absorbing Man")!!
                val forge = game.findPermanent("Futurist Forge")!!

                withClue("his printed side has no activated ability, so nothing is offered yet") {
                    game.getLegalActions(1)
                        .none { (it.action as? ActivateAbility)?.sourceId == absorbingMan } shouldBe true
                }

                game.copyOnto(forge)

                val card = game.state.getEntity(absorbingMan)!!.get<CardComponent>()!!
                withClue("the name still says Absorbing Man; only the definition id moved") {
                    card.name shouldBe "Absorbing Man"
                    card.cardDefinitionId shouldBe "Futurist Forge"
                }

                // Match on the copied ability's own id — the printed Forge is still on the
                // battlefield offering the same ability, so the source is what distinguishes them.
                val forgeAbilityId = cardRegistry.getCard("Futurist Forge")!!.activatedAbilities.single().id
                val offered = game.getLegalActions(1)
                val sacrificeAbility = offered.firstOrNull {
                    val activation = it.action as? ActivateAbility
                    activation?.sourceId == absorbingMan && activation.abilityId == forgeAbilityId
                }
                withClue(
                    "the copied '{3}{U}, Sacrifice this artifact: Draw two cards' must be enumerated; " +
                        "offered instead: ${offered.map { it.description }}"
                ) {
                    sacrificeAbility shouldNotBe null
                }
                withClue("and it is payable with four Islands untapped") {
                    sacrificeAbility!!.isAffordable shouldBe true
                }
                withClue("activating the enumerated action really draws two cards") {
                    val handBefore = game.handSize(1)
                    game.execute(sacrificeAbility!!.action)
                    game.resolveStack()
                    game.handSize(1) shouldBe handBefore + 2
                    game.isOnBattlefield("Absorbing Man") shouldBe false
                }
            }

            test("declining the optional target leaves him alone") {
                val game = board()
                val absorbingMan = game.findPermanent("Absorbing Man")!!

                game.advanceToFirstMainTrigger()
                if (game.hasPendingDecision()) game.skipTargets()
                game.resolveStack()

                val card = game.state.getEntity(absorbingMan)!!.get<CardComponent>()!!
                withClue("no copy source chosen — the copy effect is a no-op") {
                    card.typeLine.isArtifact shouldBe false
                    game.state.projectedState.getPower(absorbingMan) shouldBe 4
                }
            }
        }
    }
}

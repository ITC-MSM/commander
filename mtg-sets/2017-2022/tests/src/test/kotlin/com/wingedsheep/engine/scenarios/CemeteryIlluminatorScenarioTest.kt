package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CastFromTopOfLibraryUsesThisTurnComponent
import com.wingedsheep.engine.state.components.battlefield.LinkedExileComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Cemetery Illuminator (VOW #50) — {1}{U}{U} Creature — Spirit, 2/3.
 *
 * "Flying
 *  Whenever this creature enters or attacks, exile a card from a graveyard.
 *  You may look at the top card of your library any time.
 *  Once each turn, you may cast a spell from the top of your library if it shares a card type with
 *  a card exiled with this creature."
 *
 * The new vocabulary is `CardPredicate.SharesCardTypeWithLinkedExile` — the *pile-wide* card-type
 * comparison. Its cycle-mate Cemetery Prowler already reads the same pile on the cost side; what
 * this card needed was that reading as a **filter**, evaluated against the granting permanent, and
 * the cast-from-top permission path threaded with which permanent granted it.
 *
 * The tests below are the four the predicate can get wrong: an empty pile (matches nothing rather
 * than everything), a card type actually shared, a card type merely *present in the pile but not on
 * the spell*, and the `maxCastsPerTurn = 1` allowance that rides the same permission.
 */
class CemeteryIlluminatorScenarioTest : ScenarioTestBase() {

    init {
        context("Cemetery Illuminator — casting from the top of your library") {

            /**
             * An Illuminator on player 1's battlefield with [exiled] already linked to it, and
             * [topCard] on top of player 1's library. The linkage is seeded directly rather than
             * driven through the ETB trigger: the trigger is a plain `Effects.Pipeline` copied
             * verbatim from [CemeteryProwlerScenarioTest]'s card, and what is under test here is the
             * *payoff*, not the exile.
             */
            fun illuminatorWithExile(topCard: String, vararg exiled: String): TestGame {
                var builder = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Cemetery Illuminator")
                    // Forests, because the spell cast from the top below is {1}{G}: an
                    // unaffordable action is enumerated with `isAffordable = false`, and the
                    // assertions here are about the *permission*, not the mana.
                    .withCardOnBattlefield(1, "Forest")
                    .withCardOnBattlefield(1, "Forest")
                    .withCardOnBattlefield(1, "Forest")
                    .withCardInLibrary(1, topCard)
                    .withCardInLibrary(2, "Forest")
                exiled.forEach { builder = builder.withCardInExile(2, it) }
                val game = builder
                    .withActivePlayer(1)
                    .withPriorityPlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val illuminator = game.findPermanent("Cemetery Illuminator")!!
                val exiledIds: List<EntityId> = game.state.getExile(game.player2Id).toList()
                game.state = game.state.updateEntity(illuminator) { container ->
                    container.with(LinkedExileComponent(exiledIds))
                }
                return game
            }

            /** Whether player 1 is currently offered a cast of [cardName] out of their library. */
            fun canCastFromTop(game: TestGame, cardName: String): Boolean =
                game.getLegalActions(1).any {
                    it.actionType == "CastSpell" && it.description.contains(cardName)
                }

            test("nothing exiled → the top card is not castable") {
                val game = illuminatorWithExile("Grizzly Bears")
                withClue("an empty pile shares a card type with nothing — it must not match all") {
                    canCastFromTop(game, "Grizzly Bears") shouldBe false
                }
            }

            test("an exiled creature card makes a creature spell on top castable") {
                val game = illuminatorWithExile("Grizzly Bears", "Llanowar Elves")
                canCastFromTop(game, "Grizzly Bears") shouldBe true
            }

            test("an exiled land does nothing for a creature spell on top") {
                val game = illuminatorWithExile("Grizzly Bears", "Mountain")
                withClue("a creature spell shares no card type with an exiled land") {
                    canCastFromTop(game, "Grizzly Bears") shouldBe false
                }
            }

            test("the pile is read whole, not just its first card") {
                // The case an index-keyed `LinkedExiledCard(0)` reading fails: the *second* exile is
                // the one that shares a type. The Illuminator exiles on every enter and attack, so
                // this is the ordinary state of its pile, not a corner.
                val game = illuminatorWithExile("Grizzly Bears", "Mountain", "Llanowar Elves")
                canCastFromTop(game, "Grizzly Bears") shouldBe true
            }

            test("once each turn — a used allowance closes the permission") {
                val game = illuminatorWithExile("Grizzly Bears", "Llanowar Elves")
                val illuminator = game.findPermanent("Cemetery Illuminator")!!
                game.state = game.state.updateEntity(illuminator) { container ->
                    container.with(CastFromTopOfLibraryUsesThisTurnComponent(uses = 1))
                }
                withClue("maxCastsPerTurn = 1, and this permanent has spent its use") {
                    canCastFromTop(game, "Grizzly Bears") shouldBe false
                }
            }
        }
    }
}

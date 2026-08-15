package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Scenario tests for Moonlit Meditation (EOE #69, {2}{U}, Enchantment — Aura).
 *
 *   Enchant artifact or creature you control
 *   The first time you would create one or more tokens each turn, you may instead
 *   create that many tokens that are copies of enchanted permanent.
 *
 * Driven by Centaur Glade ({2}{G}{G}: create a 3/3 Centaur token) — a clean,
 * activatable single-token source with no token-count interactions of its own.
 */
class MoonlitMeditationScenarioTest : ScenarioTestBase() {

    init {
        context("Moonlit Meditation") {

            test("yes — first token creation each turn becomes a copy of the enchanted creature") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Moonlit Meditation")
                    .withCardOnBattlefield(1, "Centaur Glade")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Island", 3)
                    .withLandsOnBattlefield(1, "Forest", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val cast = game.castSpell(1, "Moonlit Meditation", bears)
                withClue("Casting Moonlit Meditation should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                val aura = game.findPermanent("Moonlit Meditation")
                withClue("Moonlit Meditation should be on the battlefield") {
                    aura.shouldNotBeNull()
                }
                withClue("Moonlit Meditation should be attached to Grizzly Bears") {
                    game.state.getEntity(aura!!)?.get<AttachedToComponent>()?.targetId shouldBe bears
                }

                // Activate Centaur Glade's {2}{G}{G} token ability.
                val glade = game.findPermanent("Centaur Glade")!!
                val gladeAbility = cardRegistry.getCard("Centaur Glade")!!.script.activatedAbilities.first()
                val activate = game.execute(
                    ActivateAbility(playerId = game.player1Id, sourceId = glade, abilityId = gladeAbility.id)
                )
                withClue("Activating Centaur Glade should succeed: ${activate.error}") {
                    activate.error shouldBe null
                }
                if (game.hasPendingDecision()) game.submitManaSourcesAutoPay()
                game.resolveStack()

                // The token creation should pause for the Moonlit Meditation yes/no.
                val decision = game.getPendingDecision()
                withClue("Token creation should pause for a YesNo decision; got $decision") {
                    decision.shouldBeInstanceOf<YesNoDecision>()
                }
                game.answerYesNo(true)
                game.resolveStack()

                withClue("No Centaur token should be created when replacement is taken") {
                    game.findPermanents("Centaur Token").size shouldBe 0
                }
                withClue("A token copy of Grizzly Bears should be on the battlefield (original + 1 copy)") {
                    game.findPermanents("Grizzly Bears").size shouldBe 2
                }
            }

            test("no — declining leaves the original Centaur token") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Moonlit Meditation")
                    .withCardOnBattlefield(1, "Centaur Glade")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Island", 3)
                    .withLandsOnBattlefield(1, "Forest", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                game.castSpell(1, "Moonlit Meditation", bears).error shouldBe null
                game.resolveStack()

                val glade = game.findPermanent("Centaur Glade")!!
                val gladeAbility = cardRegistry.getCard("Centaur Glade")!!.script.activatedAbilities.first()
                game.execute(
                    ActivateAbility(playerId = game.player1Id, sourceId = glade, abilityId = gladeAbility.id)
                ).error shouldBe null
                if (game.hasPendingDecision()) game.submitManaSourcesAutoPay()
                game.resolveStack()

                val decision = game.getPendingDecision()
                decision.shouldBeInstanceOf<YesNoDecision>()
                game.answerYesNo(false)
                game.resolveStack()

                withClue("A normal Centaur token should be created when replacement is declined") {
                    game.findPermanents("Centaur Token").size shouldBe 1
                }
                withClue("Grizzly Bears count is unchanged") {
                    game.findPermanents("Grizzly Bears").size shouldBe 1
                }
            }

            test("once per turn — declining locks the replacement out for the rest of the turn") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Moonlit Meditation")
                    .withCardOnBattlefield(1, "Centaur Glade")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Island", 3)
                    .withLandsOnBattlefield(1, "Forest", 8)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                game.castSpell(1, "Moonlit Meditation", bears).error shouldBe null
                game.resolveStack()

                val glade = game.findPermanent("Centaur Glade")!!
                val gladeAbility = cardRegistry.getCard("Centaur Glade")!!.script.activatedAbilities.first()

                // First activation: decline the replacement.
                game.execute(
                    ActivateAbility(playerId = game.player1Id, sourceId = glade, abilityId = gladeAbility.id)
                ).error shouldBe null
                if (game.hasPendingDecision()) game.submitManaSourcesAutoPay()
                game.resolveStack()
                game.getPendingDecision().shouldBeInstanceOf<YesNoDecision>()
                game.answerYesNo(false)
                game.resolveStack()

                // Second activation: no yes/no should be offered — the Aura already had
                // its one shot this turn (ruling: "If you choose not to apply the
                // replacement effect, you will not get the choice to apply it again
                // until the next turn.").
                game.execute(
                    ActivateAbility(playerId = game.player1Id, sourceId = glade, abilityId = gladeAbility.id)
                ).error shouldBe null
                if (game.hasPendingDecision()) game.submitManaSourcesAutoPay()
                game.resolveStack()

                withClue("Second activation must NOT offer the replacement again this turn") {
                    val pending = game.getPendingDecision()
                    if (pending is YesNoDecision) {
                        withClue("Unexpected YesNoDecision prompt: ${pending.prompt}") {
                            pending.prompt.contains("Moonlit Meditation") shouldBe false
                        }
                    }
                }

                withClue("Two Centaur tokens should now exist (both activations resolved normally)") {
                    game.findPermanents("Centaur Token").size shouldBe 2
                }
                withClue("No extra Grizzly Bears copies should exist") {
                    game.findPermanents("Grizzly Bears").size shouldBe 1
                }
            }

            test("non-creature token replaced too: an Aura on an artifact creates copies of the artifact") {
                // Per Scryfall ruling: "The effect of Moonlit Meditation's last ability can
                // apply to any token, not just artifact or creature tokens." Here we cover
                // the inverse axis — the replacement can fire when the enchanted permanent
                // is an artifact (the Aura's "Enchant artifact or creature" half).
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Moonlit Meditation")
                    .withCardOnBattlefield(1, "Centaur Glade")
                    .withCardOnBattlefield(1, "Sol Ring")
                    .withLandsOnBattlefield(1, "Island", 3)
                    .withLandsOnBattlefield(1, "Forest", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val solRing = game.findPermanent("Sol Ring")!!
                game.castSpell(1, "Moonlit Meditation", solRing).error shouldBe null
                game.resolveStack()

                val glade = game.findPermanent("Centaur Glade")!!
                val gladeAbility = cardRegistry.getCard("Centaur Glade")!!.script.activatedAbilities.first()
                game.execute(
                    ActivateAbility(playerId = game.player1Id, sourceId = glade, abilityId = gladeAbility.id)
                ).error shouldBe null
                if (game.hasPendingDecision()) game.submitManaSourcesAutoPay()
                game.resolveStack()

                val decision = game.getPendingDecision()
                decision.shouldBeInstanceOf<YesNoDecision>()
                game.answerYesNo(true)
                game.resolveStack()

                withClue("No Centaur token should be created when replacement is taken") {
                    game.findPermanents("Centaur Token").size shouldBe 0
                }
                // One original Sol Ring + 1 token copy = 2.
                withClue("A token copy of Sol Ring should be on the battlefield (original + 1 copy)") {
                    val ringIds = game.state.getBattlefield().filter { id ->
                        game.state.getEntity(id)?.get<CardComponent>()?.name == "Sol Ring"
                    }
                    ringIds.size shouldBe 2
                }
            }
        }
    }
}

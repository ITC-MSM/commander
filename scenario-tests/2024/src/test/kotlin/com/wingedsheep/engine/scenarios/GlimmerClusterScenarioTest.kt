package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.TokenComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.dsk.cards.Glimmerlight
import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Glimmer cluster — Duskmourn cards that create a "1/1 white Glimmer enchantment creature token".
 *
 * Exercises the new [com.wingedsheep.sdk.scripting.effects.CreateTokenEffect.enchantmentToken]
 * field across an instant (Glimmerburst), an Equipment ETB trigger (Glimmerlight), and a
 * conditional Survival trigger (Glimmer Seeker).
 */
class GlimmerClusterScenarioTest : ScenarioTestBase() {

    private val projector = StateProjector()

    /** Assert the token is a 1/1 white *enchantment creature* with the Glimmer subtype. */
    private fun TestGame.assertGlimmerToken(tokenId: com.wingedsheep.sdk.model.EntityId) {
        val card = state.getEntity(tokenId)!!.get<CardComponent>()!!
        state.getEntity(tokenId)!!.has<TokenComponent>() shouldBe true
        card.colors shouldBe setOf(Color.WHITE)
        withClue("Glimmer token must be both an enchantment and a creature") {
            card.typeLine.cardTypes.contains(CardType.ENCHANTMENT) shouldBe true
            card.typeLine.cardTypes.contains(CardType.CREATURE) shouldBe true
        }
        withClue("Glimmer token must have the Glimmer subtype") {
            card.typeLine.subtypes.any { it.value == "Glimmer" } shouldBe true
        }
        val projected = projector.project(state)
        projected.getPower(tokenId) shouldBe 1
        projected.getToughness(tokenId) shouldBe 1
    }

    init {
        context("Glimmerburst") {
            test("draws two cards and creates a 1/1 white Glimmer enchantment creature token") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardInHand(1, "Glimmerburst")
                    .withLandsOnBattlefield(1, "Island", 4)
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Glory Seeker")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val handBefore = game.state.getHand(game.player1Id).size
                val cast = game.castSpell(1, "Glimmerburst")
                withClue("Casting Glimmerburst should succeed: ${cast.error}") { cast.error shouldBe null }
                game.resolveStack()

                // Two cards drawn (hand had Glimmerburst removed when cast, +2 drawn).
                withClue("Glimmerburst draws two cards") {
                    game.state.getHand(game.player1Id).size shouldBe (handBefore - 1 + 2)
                }
                val token = game.findPermanent("Glimmer Token").shouldNotBeNull()
                game.assertGlimmerToken(token)
            }
        }

        context("Glimmerlight") {
            test("creates a Glimmer token when it enters and buffs the equipped creature +1/+1") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardInHand(1, "Glimmerlight")
                    .withLandsOnBattlefield(1, "Plains", 3)
                    .withCardOnBattlefield(1, "Grizzly Bears", tapped = false, summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cast = game.castSpell(1, "Glimmerlight")
                withClue("Casting Glimmerlight should succeed: ${cast.error}") { cast.error shouldBe null }
                game.resolveStack()

                // ETB token.
                val token = game.findPermanent("Glimmer Token").shouldNotBeNull()
                game.assertGlimmerToken(token)

                // Equip onto Grizzly Bears and confirm the +1/+1 buff.
                val equipment = game.findPermanent("Glimmerlight").shouldNotBeNull()
                val bears = game.findPermanent("Grizzly Bears").shouldNotBeNull()
                val abilityId = Glimmerlight.activatedAbilities.first().id
                val equip = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = equipment,
                        abilityId = abilityId,
                        targets = listOf(ChosenTarget.Permanent(bears))
                    )
                )
                withClue("Equipping should succeed: ${equip.error}") { equip.error shouldBe null }
                game.resolveStack()

                val projected = projector.project(game.state)
                withClue("Equipped Grizzly Bears should be 3/3 (2/2 +1/+1)") {
                    projected.getPower(bears) shouldBe 3
                    projected.getToughness(bears) shouldBe 3
                }
            }
        }

        context("Tunnel Surveyor") {
            test("creates a 1/1 white Glimmer enchantment creature token when it enters") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardInHand(1, "Tunnel Surveyor")
                    .withLandsOnBattlefield(1, "Island", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cast = game.castSpell(1, "Tunnel Surveyor")
                withClue("Casting Tunnel Surveyor should succeed: ${cast.error}") { cast.error shouldBe null }
                game.resolveStack()

                game.findPermanent("Tunnel Surveyor").shouldNotBeNull()
                val token = game.findPermanent("Glimmer Token").shouldNotBeNull()
                game.assertGlimmerToken(token)
            }
        }

        context("Glimmer Seeker") {
            test("creates a Glimmer token at second main if tapped and you control no Glimmer creature") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardOnBattlefield(1, "Glimmer Seeker", tapped = true, summoningSickness = false)
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val handBefore = game.state.getHand(game.player1Id).size
                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)
                game.resolveStack()

                // Glimmer Seeker is a Human Survivor, not a Glimmer, so the else-branch fires: a
                // Glimmer token is created instead of drawing.
                withClue("With no Glimmer creature, Glimmer Seeker creates a Glimmer token (else branch)") {
                    val token = game.findPermanent("Glimmer Token").shouldNotBeNull()
                    game.assertGlimmerToken(token)
                }
                withClue("Else-branch creates a token, does not draw") {
                    game.state.getHand(game.player1Id).size shouldBe handBefore
                }
            }

            test("does nothing extra when not tapped (intervening-if fails)") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardOnBattlefield(1, "Glimmer Seeker", tapped = false, summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)
                game.resolveStack()

                withClue("Untapped Glimmer Seeker's Survival trigger does not fire") {
                    game.findPermanent("Glimmer Token") shouldBe null
                }
            }

            test("draws a card when tapped and a Glimmer creature is already on the battlefield") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardOnBattlefield(1, "Glimmer Seeker", tapped = true, summoningSickness = false)
                    .withCardInHand(1, "Glimmerlight")
                    .withLandsOnBattlefield(1, "Plains", 3)
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                // Seed a Glimmer enchantment-creature onto the battlefield via Glimmerlight's ETB,
                // so the "if you control a Glimmer creature" branch is satisfied.
                game.castSpell(1, "Glimmerlight")
                game.resolveStack()
                game.findPermanent("Glimmer Token").shouldNotBeNull()

                val handBefore = game.state.getHand(game.player1Id).size
                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)
                game.resolveStack()

                withClue("With a Glimmer creature controlled, Glimmer Seeker draws a card") {
                    game.state.getHand(game.player1Id).size shouldBe (handBefore + 1)
                }
            }
        }
    }
}

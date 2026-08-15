package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.player.PlayerProtectionComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * The One Ring — {4} Legendary Artifact
 *   Indestructible
 *   When The One Ring enters, if you cast it, you gain protection from everything until your next turn.
 *   At the beginning of your upkeep, you lose 1 life for each burden counter on The One Ring.
 *   {T}: Put a burden counter on The One Ring, then draw a card for each burden counter on The One Ring.
 *
 * Exercises Gap 8 (player-level protection from everything) plus the burden-counter tap/upkeep loop.
 */
class TheOneRingScenarioTest : ScenarioTestBase() {

    private val tapAbilityId by lazy {
        cardRegistry.requireCard("The One Ring").activatedAbilities[0].id
    }

    // Puts a card from a graveyard onto the battlefield WITHOUT casting it — lets us exercise the
    // negative branch of The One Ring's "if you cast it" intervening-if gate (CR 603.4).
    private val reanimate = card("Test Reanimate") {
        manaCost = "{1}"
        typeLine = "Sorcery"
        spell {
            val t = target("target card in a graveyard", Targets.CardInGraveyard)
            effect = Effects.Move(t, Zone.BATTLEFIELD, fromZone = Zone.GRAVEYARD)
        }
    }

    init {
        cardRegistry.register(reanimate)

        test("casting The One Ring grants the controller protection from everything") {
            val game = scenario()
                .withPlayers()
                .withCardInHand(1, "The One Ring")
                .withLandsOnBattlefield(1, "Plains", 4)
                .withCardInHand(2, "Lightning Bolt")
                .withLandsOnBattlefield(2, "Mountain", 1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            game.castSpell(1, "The One Ring").error shouldBe null
            game.resolveStack()

            // ETB ("if you cast it") granted the controller player-level protection.
            game.state.getEntity(game.player1Id)?.get<PlayerProtectionComponent>() shouldNotBe null

            // A protected player can't be targeted: opponent's Lightning Bolt can't choose player 1.
            // Pass priority so player 2 gets a window during player 1's main phase.
            game.passPriority()
            game.castSpellTargetingPlayer(2, "Lightning Bolt", 1).error shouldNotBe null
        }

        test("tap adds a burden counter then draws that many; upkeep loses that much life") {
            val game = scenario()
                .withPlayers()
                .withCardOnBattlefield(1, "The One Ring")
                .withCardInLibrary(1, "Plains")
                .withCardInLibrary(1, "Island")
                .withCardInLibrary(1, "Forest")
                .withLifeTotal(1, 20)
                .withActivePlayer(2)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val ring = game.findPermanent("The One Ring")!!
            val handBefore = game.handSize(1)

            // Pass priority to player 1, who activates The One Ring's tap ability at instant speed.
            game.passPriority()
            // {T}: first activation -> 1 burden counter, draw 1.
            game.execute(
                ActivateAbility(playerId = game.player1Id, sourceId = ring, abilityId = tapAbilityId)
            ).error shouldBe null
            game.resolveStack()
            game.handSize(1) shouldBe handBefore + 1

            // Advance to player 1's upkeep (next turn): the "at the beginning of your upkeep"
            // trigger loses 1 life for the single burden counter.
            game.passUntilPhase(Phase.ENDING, Step.END)
            game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
            game.state.activePlayerId shouldBe game.player1Id
            game.resolveStack()
            game.getLifeTotal(1) shouldBe 19
        }

        test("burden grows each turn, so the draw scales — counter added before the draw (CR 608.2c)") {
            val game = scenario()
                .withPlayers()
                .withCardOnBattlefield(1, "The One Ring")
                .withCardInLibrary(1, "Plains")
                .withCardInLibrary(1, "Island")
                .withCardInLibrary(1, "Forest")
                .withCardInLibrary(1, "Swamp")
                .withCardInLibrary(1, "Mountain")
                .withCardInLibrary(1, "Plains")
                .withActivePlayer(2)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val ring = game.findPermanent("The One Ring")!!

            // First activation (during the opponent's turn, at instant speed): 1 burden → draw 1.
            game.passPriority()
            val handBeforeFirst = game.handSize(1)
            game.execute(
                ActivateAbility(playerId = game.player1Id, sourceId = ring, abilityId = tapAbilityId)
            ).error shouldBe null
            game.resolveStack()
            game.handSize(1) shouldBe handBeforeFirst + 1

            // Advance to the controller's own turn; the Ring untaps during their untap step.
            game.passUntilPhase(Phase.ENDING, Step.END)
            game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
            game.state.activePlayerId shouldBe game.player1Id
            game.resolveStack() // upkeep: lose 1 life for the single burden counter
            game.passUntilPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)

            // Second activation: the counter is added FIRST, so the draw reads the new count of 2.
            val handBeforeSecond = game.handSize(1)
            game.execute(
                ActivateAbility(playerId = game.player1Id, sourceId = ring, abilityId = tapAbilityId)
            ).error shouldBe null
            game.resolveStack()
            game.handSize(1) shouldBe handBeforeSecond + 2
        }

        test("entering without being cast grants no protection (intervening-if, CR 603.4)") {
            val game = scenario()
                .withPlayers()
                .withCardInHand(1, "Test Reanimate")
                .withCardInGraveyard(1, "The One Ring")
                .withLandsOnBattlefield(1, "Plains", 1)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val ring = game.findCardsInGraveyard(1, "The One Ring").first()
            val reanimateSpell = game.findCardsInHand(1, "Test Reanimate").first()
            game.execute(
                CastSpell(
                    game.player1Id,
                    reanimateSpell,
                    listOf(ChosenTarget.Card(ring, game.player1Id, Zone.GRAVEYARD))
                )
            ).error shouldBe null
            game.resolveStack()

            // The One Ring is on the battlefield, so its ETB trigger was evaluated...
            game.isOnBattlefield("The One Ring") shouldBe true
            // ...but it wasn't cast, so the "if you cast it" clause did nothing — no protection.
            game.state.getEntity(game.player1Id)?.get<PlayerProtectionComponent>() shouldBe null
        }
    }
}

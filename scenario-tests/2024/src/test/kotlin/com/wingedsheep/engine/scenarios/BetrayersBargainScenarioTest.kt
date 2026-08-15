package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Betrayer's Bargain (DSK #126) — {1}{R} Instant.
 *
 *   "As an additional cost to cast this spell, sacrifice a creature or enchantment or pay {2}.
 *    Betrayer's Bargain deals 5 damage to target creature. If that creature would die this turn,
 *    exile it instead."
 *
 * Pure composition: a non-modal two-mode cost fork (LashOfTheBalrog shape) — sacrifice a
 * creature/enchantment, or pay {2} — wrapping the same body, which first marks the target so any
 * death this turn exiles it (AgateAssault shape: MarkExileOnDeath) and then deals 5 damage.
 */
class BetrayersBargainScenarioTest : ScenarioTestBase() {

    init {
        context("Betrayer's Bargain — 5 damage, exile on death; sacrifice-or-pay cost") {

            test("pay {2}: deals 5 to the target, which dies and is exiled instead of going to the graveyard") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withLandsOnBattlefield(1, "Mountain", 4) // {1}{R} + {2}
                    .withCardInHand(1, "Betrayer's Bargain")
                    .withCardOnBattlefield(2, "Grizzly Bears") // opponent's 2/2
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val cardId = game.state.getHand(game.player1Id).first {
                    game.state.getEntity(it)
                        ?.get<com.wingedsheep.engine.state.components.identity.CardComponent>()?.name ==
                        "Betrayer's Bargain"
                }
                // Mode 1 = "Pay {2}" (the second mode, index 1).
                val targets = listOf<ChosenTarget>(ChosenTarget.Permanent(bears))
                game.execute(
                    CastSpell(
                        playerId = game.player1Id,
                        cardId = cardId,
                        targets = targets,
                        chosenModes = listOf(1),
                        modeTargetsOrdered = listOf(targets)
                    )
                ).error shouldBe null
                game.resolveStack()

                withClue("the 2/2 took 5 lethal damage and left the battlefield") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                }
                withClue("it was exiled instead of going to the graveyard") {
                    game.isInExile(2, "Grizzly Bears") shouldBe true
                    game.isInGraveyard(2, "Grizzly Bears") shouldBe false
                }
            }

            test("sacrifice a creature: pays the cost by sacrificing, deals 5, exiles the target") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withLandsOnBattlefield(1, "Mountain", 2) // only enough for {1}{R}
                    .withCardInHand(1, "Betrayer's Bargain")
                    .withCardOnBattlefield(1, "Grizzly Bears") // own creature, fodder to sacrifice
                    .withCardOnBattlefield(2, "Hill Giant") // opponent's 3/3 target
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val target = game.findPermanent("Hill Giant")!!
                val fodder = game.findPermanent("Grizzly Bears")!!
                val cardId = game.state.getHand(game.player1Id).first {
                    game.state.getEntity(it)
                        ?.get<com.wingedsheep.engine.state.components.identity.CardComponent>()?.name ==
                        "Betrayer's Bargain"
                }
                val targets = listOf<ChosenTarget>(ChosenTarget.Permanent(target))
                // Mode 0 = "Sacrifice a creature or enchantment".
                game.execute(
                    CastSpell(
                        playerId = game.player1Id,
                        cardId = cardId,
                        targets = targets,
                        chosenModes = listOf(0),
                        modeTargetsOrdered = listOf(targets),
                        additionalCostPayment = AdditionalCostPayment(sacrificedPermanents = listOf(fodder))
                    )
                ).error shouldBe null
                game.resolveStack()

                withClue("the sacrificed creature is gone (in its owner's graveyard)") {
                    game.state.getBattlefield(game.player1Id).contains(fodder) shouldBe false
                }
                withClue("the 3/3 target took 5 lethal damage and was exiled instead of dying") {
                    game.isOnBattlefield("Hill Giant") shouldBe false
                    game.isInExile(2, "Hill Giant") shouldBe true
                    game.isInGraveyard(2, "Hill Giant") shouldBe false
                }
            }
        }
    }
}

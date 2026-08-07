package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern.DealsDamageEvent
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggerSpec
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.events.RecipientFilter
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Engine-level tests for the **per-turn effect budget** — `TriggeredAbility.effectOncePerTurn`,
 * the printed rider "*Do this only once each turn*", lowered by `TriggerProcessor` into a
 * `Gate.OnceEachTurn` budget gate and resolved by `GatedEffectExecutor`.
 *
 * The point of the primitive is that it is an **effect** cap, not a **trigger** cap, so both are
 * exercised here side by side with two otherwise-identical cards:
 *
 *  - *Effect Cap Warden* — `effectOncePerTurn = true`. Per CR 603.2 the ability triggers once per
 *    matching event, so a sweeper hitting three creatures raises **three** may-questions; at most
 *    one of them may apply.
 *  - *Trigger Cap Warden* — `oncePerTurn = true`, the existing "This ability triggers only once each
 *    turn" cap. The same sweeper raises exactly **one** may-question. This is the regression guard:
 *    the new flag must not change how the old one behaves.
 *
 * Both are enchantments so the sweeper's damage to the *source* can't add a trigger of its own, and
 * both use a plain "you may gain 2 life" payoff so the number of prompts (instances that triggered)
 * and the life gained (instances that applied) are independently observable.
 */
private val EffectCapWarden = card("Effect Cap Warden") {
    manaCost = "{2}"
    colorIdentity = "W"
    typeLine = "Enchantment"
    oracleText = "Whenever a creature you control is dealt damage, you may gain 2 life. " +
        "Do this only once each turn."

    triggeredAbility {
        trigger = TriggerSpec(
            DealsDamageEvent(recipient = RecipientFilter.CreatureYouControl),
            TriggerBinding.ANY,
        )
        effect = MayEffect(Effects.GainLife(2))
        effectOncePerTurn = true
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "T1"
    }
}

private val TriggerCapWarden = card("Trigger Cap Warden") {
    manaCost = "{2}"
    colorIdentity = "W"
    typeLine = "Enchantment"
    oracleText = "Whenever a creature you control is dealt damage, you may gain 2 life. " +
        "This ability triggers only once each turn."

    triggeredAbility {
        trigger = TriggerSpec(
            DealsDamageEvent(recipient = RecipientFilter.CreatureYouControl),
            TriggerBinding.ANY,
        )
        effect = MayEffect(Effects.GainLife(2))
        oncePerTurn = true
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "T2"
    }
}

class EffectOncePerTurnTest : ScenarioTestBase() {

    init {
        cardRegistry.register(listOf(EffectCapWarden, TriggerCapWarden))

        /**
         * Answer every may-question the pending stack raises with [choice], resolving between
         * answers, and return how many questions were asked. That count is the number of ability
         * *instances* that triggered.
         */
        fun answerAllMayQuestions(game: TestGame, choice: Boolean): Int {
            var asked = 0
            var guard = 0
            while (game.hasPendingDecision() && guard++ < 20) {
                game.answerYesNo(choice)
                asked++
                game.resolveStack()
            }
            return asked
        }

        context("effectOncePerTurn — the effect cap") {

            test("every instance triggers, but only one applies its effect") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Effect Cap Warden")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardOnBattlefield(1, "Centaur Courser")
                    .withCardOnBattlefield(1, "Force of Nature")
                    .withCardInHand(1, "Pyroclasm")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withLifeTotal(1, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Pyroclasm").error shouldBe null
                game.resolveStack()

                // Say yes to every question: three creatures were dealt damage, so three instances
                // must have triggered — and the budget must still cap the payoff at one.
                val asked = answerAllMayQuestions(game, true)

                withClue("CR 603.2: one instance per damaged creature, all of them offered") {
                    asked shouldBe 3
                }
                withClue("only one instance may apply its effect this turn") {
                    game.getLifeTotal(1) shouldBe 22
                }
            }

            test("declining does not spend the budget — a later trigger the same turn still applies") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Effect Cap Warden")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardOnBattlefield(1, "Centaur Courser")
                    .withCardInHand(1, "Pyroclasm")
                    .withCardInHand(1, "Lightning Bolt")
                    .withLandsOnBattlefield(1, "Mountain", 5)
                    .withLifeTotal(1, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Pyroclasm").error shouldBe null
                game.resolveStack()
                val declined = answerAllMayQuestions(game, false)

                withClue("both damaged creatures offered their trigger") { declined shouldBe 2 }
                withClue("declining gains nothing") { game.getLifeTotal(1) shouldBe 20 }

                // A fresh damage event the same turn: the budget was never spent, so this one applies.
                // (Pyroclasm's 2 damage killed the 2/2 Bears; the 3/3 Courser survived it.)
                val courser = game.findPermanent("Centaur Courser")!!
                game.castSpell(1, "Lightning Bolt", targetId = courser).error shouldBe null
                game.resolveStack()
                val asked = answerAllMayQuestions(game, true)

                withClue("the declined instances left the budget intact") { asked shouldBe 1 }
                withClue("the later instance applies") { game.getLifeTotal(1) shouldBe 22 }
            }

            test("once spent, later triggers the same turn are not even offered") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Effect Cap Warden")
                    .withCardOnBattlefield(1, "Force of Nature")
                    .withCardsInHand(1, "Lightning Bolt", 2)
                    .withLandsOnBattlefield(1, "Mountain", 4)
                    .withLifeTotal(1, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val wall = game.findPermanent("Force of Nature")!!
                game.castSpell(1, "Lightning Bolt", targetId = wall).error shouldBe null
                game.resolveStack()
                answerAllMayQuestions(game, true) shouldBe 1
                game.getLifeTotal(1) shouldBe 22

                game.castSpell(1, "Lightning Bolt", targetId = wall).error shouldBe null
                game.resolveStack()

                withClue("the budget is spent, so no pointless may-question is raised") {
                    game.hasPendingDecision() shouldBe false
                }
                withClue("and nothing is applied") { game.getLifeTotal(1) shouldBe 22 }
            }

            test("the budget resets at end of turn") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Effect Cap Warden")
                    .withCardOnBattlefield(1, "Force of Nature")
                    .withCardsInHand(1, "Lightning Bolt", 2)
                    .withLandsOnBattlefield(1, "Mountain", 4)
                    // Both players need something to draw so crossing the turn boundary doesn't
                    // deck (and end) the game before the second half of the test runs.
                    .withCardInLibrary(1, "Mountain")
                    .withCardInLibrary(1, "Mountain")
                    .withCardInLibrary(2, "Mountain")
                    .withCardInLibrary(2, "Mountain")
                    .withLifeTotal(1, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val wall = game.findPermanent("Force of Nature")!!
                game.castSpell(1, "Lightning Bolt", targetId = wall).error shouldBe null
                game.resolveStack()
                answerAllMayQuestions(game, true) shouldBe 1
                game.getLifeTotal(1) shouldBe 22

                // Next turn — the per-turn tracker is cleared in cleanup, so it works again. The
                // second Bolt is cast at instant speed on the opponent's turn, after the cleanup
                // that clears the budget. (Two hops: `passUntilPhase` is a no-op when the target
                // phase/step is the current one, so leave this main phase before asking for the
                // next turn's.)
                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)
                game.passUntilPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                game.passPriority() // active player (Player2) passes; Player1 gets priority
                game.castSpell(1, "Lightning Bolt", targetId = wall).error shouldBe null
                game.resolveStack()

                withClue("a new turn restores the budget") {
                    answerAllMayQuestions(game, true) shouldBe 1
                    game.getLifeTotal(1) shouldBe 24
                }
            }

            test("two sources each keep their own budget") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Effect Cap Warden")
                    .withCardOnBattlefield(1, "Effect Cap Warden")
                    .withCardOnBattlefield(1, "Force of Nature")
                    .withCardInHand(1, "Lightning Bolt")
                    .withLandsOnBattlefield(1, "Mountain", 4)
                    .withLifeTotal(1, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val wall = game.findPermanent("Force of Nature")!!
                game.castSpell(1, "Lightning Bolt", targetId = wall).error shouldBe null
                game.resolveStack()

                withClue("one trigger per Warden — the budget is per (source, ability)") {
                    answerAllMayQuestions(game, true) shouldBe 2
                }
                withClue("both Wardens applied: budgets are not shared between permanents") {
                    game.getLifeTotal(1) shouldBe 24
                }
            }
        }

        context("oncePerTurn — the trigger cap, unchanged") {

            test("a trigger-capped ability still fires only once for a simultaneous batch") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Trigger Cap Warden")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardOnBattlefield(1, "Centaur Courser")
                    .withCardOnBattlefield(1, "Force of Nature")
                    .withCardInHand(1, "Pyroclasm")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withLifeTotal(1, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Pyroclasm").error shouldBe null
                game.resolveStack()

                withClue("the trigger cap collapses the batch to a single instance") {
                    answerAllMayQuestions(game, true) shouldBe 1
                }
                game.getLifeTotal(1) shouldBe 22
            }

            test("a trigger-capped ability does not trigger again later in the same turn") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Trigger Cap Warden")
                    .withCardOnBattlefield(1, "Force of Nature")
                    .withCardsInHand(1, "Lightning Bolt", 2)
                    .withLandsOnBattlefield(1, "Mountain", 4)
                    .withLifeTotal(1, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val wall = game.findPermanent("Force of Nature")!!
                game.castSpell(1, "Lightning Bolt", targetId = wall).error shouldBe null
                game.resolveStack()
                answerAllMayQuestions(game, true) shouldBe 1

                game.castSpell(1, "Lightning Bolt", targetId = wall).error shouldBe null
                game.resolveStack()

                withClue("the trigger cap blocks the second event entirely") {
                    game.hasPendingDecision() shouldBe false
                }
                game.getLifeTotal(1) shouldBe 22
            }

            test("declining a trigger-capped ability still consumes its one fire for the turn") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Trigger Cap Warden")
                    .withCardOnBattlefield(1, "Force of Nature")
                    .withCardsInHand(1, "Lightning Bolt", 2)
                    .withLandsOnBattlefield(1, "Mountain", 4)
                    .withLifeTotal(1, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val wall = game.findPermanent("Force of Nature")!!
                game.castSpell(1, "Lightning Bolt", targetId = wall).error shouldBe null
                game.resolveStack()
                answerAllMayQuestions(game, false) shouldBe 1

                game.castSpell(1, "Lightning Bolt", targetId = wall).error shouldBe null
                game.resolveStack()

                withClue("the trigger fired (and was declined), so it can't fire again this turn") {
                    game.hasPendingDecision() shouldBe false
                }
                withClue("this is exactly the defect that makes oncePerTurn wrong for the effect cap") {
                    game.getLifeTotal(1) shouldBe 20
                }
            }
        }
    }
}

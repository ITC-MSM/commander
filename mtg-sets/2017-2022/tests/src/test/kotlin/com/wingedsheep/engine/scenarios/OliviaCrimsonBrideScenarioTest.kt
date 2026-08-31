package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.combat.AttackingComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Scenario test for Olivia, Crimson Bride (VOW #245) — {4}{B}{R} Legendary Creature — Vampire Noble,
 * 3/4.
 *
 *   Flying, haste
 *   Whenever Olivia attacks, return target creature card from your graveyard to the battlefield
 *   tapped and attacking. It gains "When you don't control a legendary Vampire, exile this
 *   creature."
 *
 * Covers the new `GrantStateTriggeredAbilityEffect` primitive as much as the card: the rider is a
 * **state**-triggered ability (CR 603.8), so it has to survive on the reanimated creature and fire
 * on the moment its condition *becomes* true, with no event to hang a trigger off.
 *
 * The four cases are the ones that distinguish a correct implementation from the shortcuts:
 *  - the return really is tapped and attacking, and the rider is inert while Olivia is around;
 *  - Olivia leaving fires the rider (this is what a "whenever Olivia dies" trigger would fake);
 *  - *another* legendary Vampire keeps the creature alive (what a source-keyed rider would get
 *    wrong);
 *  - a reanimated legendary Vampire sustains itself (no `excludeSelf` on the condition).
 */
class OliviaCrimsonBrideScenarioTest : ScenarioTestBase() {

    init {
        context("Olivia, Crimson Bride") {

            test("attacking returns a creature card from your graveyard tapped and attacking") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Olivia, Crimson Bride", summoningSickness = false)
                    .withCardInGraveyard(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Olivia, Crimson Bride" to 2)).error shouldBe null
                game.resolveStack()

                withClue("the attack trigger asks for a creature card in your graveyard") {
                    game.getPendingDecision().shouldBeInstanceOf<ChooseTargetsDecision>()
                }
                val bears = game.findCardsInGraveyard(1, "Grizzly Bears").single()
                game.selectTargets(listOf(bears))
                game.resolveStack()

                val returned = game.findPermanent("Grizzly Bears")
                withClue("Grizzly Bears is back on the battlefield") {
                    returned shouldBe bears
                    game.isInGraveyard(1, "Grizzly Bears") shouldBe false
                }
                withClue("it returns tapped and attacking") {
                    game.state.getEntity(bears)?.has<TappedComponent>() shouldBe true
                    game.state.getEntity(bears)?.has<AttackingComponent>() shouldBe true
                }
                withClue("Olivia is a legendary Vampire, so the rider stays inert") {
                    game.isInExile(1, "Grizzly Bears") shouldBe false
                }
            }

            test("the reanimated creature is exiled once no legendary Vampire is controlled") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Olivia, Crimson Bride", summoningSickness = false)
                    .withCardInGraveyard(1, "Grizzly Bears")
                    .withCardInHand(1, "Murder")
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Olivia, Crimson Bride" to 2)).error shouldBe null
                game.resolveStack()
                game.selectTargets(listOf(game.findCardsInGraveyard(1, "Grizzly Bears").single()))
                game.resolveStack()

                game.isOnBattlefield("Grizzly Bears") shouldBe true

                // Kill Olivia. Nothing *happens* to the reanimated creature — its condition simply
                // becomes true, which is exactly why the rider has to be a state trigger.
                game.advanceToPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)
                game.castSpell(1, "Murder", game.findPermanent("Olivia, Crimson Bride")!!).error shouldBe null
                game.resolveStack()
                game.isOnBattlefield("Olivia, Crimson Bride") shouldBe false

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("the rider exiles the reanimated creature") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                    game.isInExile(1, "Grizzly Bears") shouldBe true
                }
            }

            test("another legendary Vampire keeps the reanimated creature on the battlefield") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Olivia, Crimson Bride", summoningSickness = false)
                    .withCardOnBattlefield(1, "Odric, Blood-Cursed", summoningSickness = false)
                    .withCardInGraveyard(1, "Grizzly Bears")
                    .withCardInHand(1, "Murder")
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Olivia, Crimson Bride" to 2)).error shouldBe null
                game.resolveStack()
                game.selectTargets(listOf(game.findCardsInGraveyard(1, "Grizzly Bears").single()))
                game.resolveStack()

                game.advanceToPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)
                game.castSpell(1, "Murder", game.findPermanent("Olivia, Crimson Bride")!!).error shouldBe null
                game.resolveStack()
                game.isOnBattlefield("Olivia, Crimson Bride") shouldBe false

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("Odric, Blood-Cursed is a legendary Vampire, so the condition stays false") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe true
                    game.isInExile(1, "Grizzly Bears") shouldBe false
                }
            }

            test("a reanimated legendary Vampire sustains itself") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Olivia, Crimson Bride", summoningSickness = false)
                    .withCardInGraveyard(1, "Odric, Blood-Cursed")
                    .withCardInHand(1, "Murder")
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Olivia, Crimson Bride" to 2)).error shouldBe null
                game.resolveStack()
                game.selectTargets(listOf(game.findCardsInGraveyard(1, "Odric, Blood-Cursed").single()))
                game.resolveStack()

                game.advanceToPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)
                game.castSpell(1, "Murder", game.findPermanent("Olivia, Crimson Bride")!!).error shouldBe null
                game.resolveStack()

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("the condition has no excludeSelf, so Odric sees himself and survives") {
                    game.isOnBattlefield("Odric, Blood-Cursed") shouldBe true
                    game.isInExile(1, "Odric, Blood-Cursed") shouldBe false
                }
            }
        }
    }
}

package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Scenario tests for Redtooth Vanguard (WOE #180) — {1}{G} 3/1 Elf Warrior.
 *
 * "Trample
 *  Whenever an enchantment you control enters, you may pay {2}. If you do, return this card from
 *  your graveyard to your hand."
 *
 * Exercises the graveyard-resident enters trigger (`triggerZone = GRAVEYARD`, same mechanism as
 * Dragon Shadow / Flamewake Phoenix), the optional {2} payment (`MayPayManaEffect`), and the
 * return-to-hand from the graveyard. All primitives already exist.
 */
class RedtoothVanguardScenarioTest : ScenarioTestBase() {

    init {
        context("Redtooth Vanguard") {

            test("enchantment entering + paying {2} returns Redtooth Vanguard from graveyard to hand") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInGraveyard(1, "Redtooth Vanguard")
                    .withCardInHand(1, "Angelic Shield") // {W}{U} Enchantment
                    .withLandsOnBattlefield(1, "Plains", 2)
                    .withLandsOnBattlefield(1, "Island", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                // Cast the enchantment; pay {W}{U}, resolve it onto the battlefield.
                game.castSpell(1, "Angelic Shield").error shouldBe null
                if (game.hasPendingDecision()) game.submitManaSourcesAutoPay()
                game.resolveStack()

                withClue("Angelic Shield entered, firing Redtooth's graveyard trigger") {
                    game.findPermanent("Angelic Shield") shouldNotBe null
                }

                // The reflexive may-pay is offered by the graveyard-resident trigger.
                game.getPendingDecision().shouldBeInstanceOf<YesNoDecision>()
                game.answerYesNo(true)
                game.getPendingDecision().shouldBeInstanceOf<SelectManaSourcesDecision>()
                game.submitManaSourcesAutoPay()
                game.resolveStack()

                withClue("Redtooth Vanguard is returned to its owner's hand") {
                    game.isInHand(1, "Redtooth Vanguard") shouldBe true
                }
                withClue("Redtooth Vanguard is no longer in the graveyard") {
                    game.isInGraveyard(1, "Redtooth Vanguard") shouldBe false
                }
            }

            test("declining the {2} payment leaves Redtooth Vanguard in the graveyard") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInGraveyard(1, "Redtooth Vanguard")
                    .withCardInHand(1, "Angelic Shield")
                    .withLandsOnBattlefield(1, "Plains", 2)
                    .withLandsOnBattlefield(1, "Island", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Angelic Shield").error shouldBe null
                if (game.hasPendingDecision()) game.submitManaSourcesAutoPay()
                game.resolveStack()

                game.getPendingDecision().shouldBeInstanceOf<YesNoDecision>()
                game.answerYesNo(false)
                game.resolveStack()

                withClue("Declining leaves Redtooth Vanguard in the graveyard") {
                    game.isInGraveyard(1, "Redtooth Vanguard") shouldBe true
                }
            }

            test("an opponent's enchantment entering does not trigger Redtooth Vanguard") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInGraveyard(1, "Redtooth Vanguard")
                    .withCardInHand(2, "Angelic Shield")
                    .withLandsOnBattlefield(2, "Plains", 2)
                    .withLandsOnBattlefield(2, "Island", 2)
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(2, "Angelic Shield").error shouldBe null
                if (game.hasPendingDecision()) game.submitManaSourcesAutoPay()
                game.resolveStack()

                withClue("The trigger checks 'an enchantment you control', so an opponent's enchantment is ignored") {
                    (game.getPendingDecision() is YesNoDecision) shouldBe false
                }
                withClue("Redtooth Vanguard stays in the graveyard") {
                    game.isInGraveyard(1, "Redtooth Vanguard") shouldBe true
                }
            }
        }
    }
}

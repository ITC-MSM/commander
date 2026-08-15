package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.state.components.identity.ControllerComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.chk.cards.KamiOfThePalaceFields
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.CardDefinition
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Soulshift 5 regression pack for Kami of the Palace Fields (CHK #24).
 *
 * It deliberately drives a real dies event through the stack, rather than
 * directly moving cards between zones: the important rules boundary is target
 * selection while the source has already left the battlefield.
 */
class KamiOfThePalaceFieldsScenarioTest : ScenarioTestBase() {

    init {
        cardRegistry.register(KamiOfThePalaceFields)
        cardRegistry.register(
            CardDefinition.creature(
                name = "Test Lesser Spirit",
                manaCost = ManaCost.parse("{4}{W}"),
                subtypes = setOf(Subtype.SPIRIT),
                power = 2,
                toughness = 2
            )
        )
        cardRegistry.register(
            CardDefinition.creature(
                name = "Test Greater Spirit",
                manaCost = ManaCost.parse("{5}{W}"),
                subtypes = setOf(Subtype.SPIRIT),
                power = 3,
                toughness = 3
            )
        )

        fun gameWith(targets: List<String>) = scenario()
            .withPlayers("Player1", "Player2")
            .withCardOnBattlefield(1, "Kami of the Palace Fields")
            .withCardInHand(2, "Murder")
            .withLandsOnBattlefield(2, "Swamp", 3)
            .withActivePlayer(2)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            .apply { targets.forEach { withCardInGraveyard(1, it) } }
            .build()

        fun TestGame.killKamiAndReachSoulshiftTargeting() {
            val kami = findPermanent("Kami of the Palace Fields")!!
            castSpell(2, "Murder", kami).error shouldBe null
            resolveStack()
        }

        context("Kami of the Palace Fields — Soulshift 5") {

            test("targets an eligible Spirit card and returns it after the optional choice") {
                val game = gameWith(listOf("Test Lesser Spirit", "Test Greater Spirit"))
                game.killKamiAndReachSoulshiftTargeting()

                // Soulshift's target is mandatory while the trigger is put on the stack; its
                // "may return" decision belongs to resolution, after priority passes.
                val lesser = game.findCardsInGraveyard(1, "Test Lesser Spirit").single()
                val greater = game.findCardsInGraveyard(1, "Test Greater Spirit").single()
                val kami = game.findCardsInGraveyard(1, "Kami of the Palace Fields").single()
                val targetDecision = game.getPendingDecision() as? ChooseTargetsDecision
                withClue("Soulshift pending decision after Kami dies: ${game.getPendingDecision()}") { (targetDecision != null) shouldBe true }
                val legalTargets = targetDecision!!.legalTargets.getValue(0)
                legalTargets shouldBe listOf(lesser)
                (legalTargets.contains(greater)) shouldBe false
                (legalTargets.contains(kami)) shouldBe false

                game.selectTargets(listOf(lesser)).error shouldBe null
                game.resolveStack()
                (game.getPendingDecision() is YesNoDecision) shouldBe true
                game.answerYesNo(true).error shouldBe null
                game.resolveStack()

                game.isInHand(1, "Test Lesser Spirit") shouldBe true
                game.isInGraveyard(1, "Kami of the Palace Fields") shouldBe true
            }

            test("declining the optional return leaves the legal target in the graveyard") {
                val game = gameWith(listOf("Test Lesser Spirit"))
                game.killKamiAndReachSoulshiftTargeting()
                val spirit = game.findCardsInGraveyard(1, "Test Lesser Spirit").single()
                game.selectTargets(listOf(spirit)).error shouldBe null
                game.resolveStack()
                (game.getPendingDecision() is YesNoDecision) shouldBe true
                game.answerYesNo(false).error shouldBe null
                game.resolveStack()

                game.isInGraveyard(1, "Test Lesser Spirit") shouldBe true
                game.isInHand(1, "Test Lesser Spirit") shouldBe false
            }

            test("does not create a targetless Soulshift trigger when no eligible Spirit card exists") {
                val game = gameWith(listOf("Test Greater Spirit", "Grizzly Bears"))
                game.killKamiAndReachSoulshiftTargeting()

                game.getPendingDecision() shouldBe null
                game.state.stack.isEmpty() shouldBe true
                game.isInGraveyard(1, "Test Greater Spirit") shouldBe true
                game.isInGraveyard(1, "Grizzly Bears") shouldBe true
            }

            test("uses the last controller's graveyard when a stolen Kami dies") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Kami of the Palace Fields")
                    .withCardInGraveyard(2, "Test Lesser Spirit")
                    .withCardInHand(1, "Murder")
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()
                val kami = game.findPermanent("Kami of the Palace Fields")!!
                game.state = game.state.updateEntity(kami) { it.with(ControllerComponent(game.player2Id)) }

                game.castSpell(1, "Murder", kami).error shouldBe null
                game.resolveStack()

                val decision = game.getPendingDecision() as? ChooseTargetsDecision
                withClue("the last controller, not the owner, controls Soulshift") {
                    decision?.playerId shouldBe game.player2Id
                }
                val spirit = game.findCardsInGraveyard(2, "Test Lesser Spirit").single()
                decision!!.legalTargets.getValue(0) shouldBe listOf(spirit)
            }

            test("simultaneous deaths use the stolen Kami's last controller and that controller's graveyard") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Kami of the Palace Fields")
                    .withCardOnBattlefield(2, "Test Lesser Spirit")
                    // This is otherwise eligible by subtype and mana value, but it belongs to the
                    // Kami owner's graveyard rather than the last controller's graveyard.
                    .withCardInGraveyard(1, "Test Lesser Spirit")
                    .withCardInHand(1, "Wrath of God")
                    .withLandsOnBattlefield(1, "Plains", 4)
                    .withActivePlayer(1)
                    .withPriorityPlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val kami = game.findPermanent("Kami of the Palace Fields")!!
                val controllerSpirit = game.findPermanent("Test Lesser Spirit")!!
                game.state = game.state.updateEntity(kami) {
                    it.with(ControllerComponent(game.player2Id))
                }

                // Wrath moves both creatures in one resolution wave. Soulshift looks back to the
                // Kami's controller immediately before that wave (CR 603.3a, 603.10a), while the
                // target is selected from that player's graveyard after both zone changes.
                game.castSpell(1, "Wrath of God").error shouldBe null
                game.resolveStack()

                game.isOnBattlefield("Kami of the Palace Fields") shouldBe false
                game.isOnBattlefield("Test Lesser Spirit") shouldBe false
                game.isInGraveyard(1, "Kami of the Palace Fields") shouldBe true

                val decision = game.getPendingDecision() as? ChooseTargetsDecision
                withClue("the stolen Kami's last controller owns the Soulshift target decision") {
                    decision?.playerId shouldBe game.player2Id
                }
                val controllerTarget = game.findCardsInGraveyard(2, "Test Lesser Spirit").single()
                val ownerTarget = game.findCardsInGraveyard(1, "Test Lesser Spirit").single()
                controllerTarget shouldBe controllerSpirit
                decision!!.legalTargets.getValue(0) shouldBe listOf(controllerTarget)
                (decision.legalTargets.getValue(0).contains(ownerTarget)) shouldBe false

                game.selectTargets(listOf(controllerTarget)).error shouldBe null
                game.resolveStack()
                (game.getPendingDecision() is YesNoDecision) shouldBe true
                game.answerYesNo(true).error shouldBe null
                game.resolveStack()

                game.isInHand(2, "Test Lesser Spirit") shouldBe true
                game.isInGraveyard(1, "Test Lesser Spirit") shouldBe true
            }
        }
    }
}

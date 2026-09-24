package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.PassPriority
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Gideon's Memorial (Reality Fracture #198) — {1}{W} Legendary Artifact:
 *   Creature tokens you control get +1/+0 and have vigilance.
 *   {T}: Add one mana of any color. Spend this mana only to cast a planeswalker spell.
 *   {1}{W}, Discard this card: It deals 4 damage to target attacking or blocking creature.
 */
class GideonsMemorialScenarioTest : ScenarioTestBase() {

    private fun handAbilityId() = cardRegistry.getCard("Gideon's Memorial")!!
        .activatedAbilities.first { it.activateFromZone == Zone.HAND }.id

    init {
        test("creature tokens you control get +1/+0 and vigilance; nontoken creatures don't") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardInHand(1, "Raise the Alarm")
                .withCardOnBattlefield(1, "Gideon's Memorial")
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withLandsOnBattlefield(1, "Plains", 2)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            game.castSpell(1, "Raise the Alarm").error shouldBe null
            game.resolveStack()

            val soldiers = game.findPermanents("Soldier Token")
            soldiers.size shouldBe 2
            val projected = game.state.projectedState
            for (soldier in soldiers) {
                projected.getPower(soldier) shouldBe 2
                projected.getToughness(soldier) shouldBe 1
                projected.hasKeyword(soldier, Keyword.VIGILANCE) shouldBe true
            }
            val bears = game.findPermanent("Grizzly Bears")!!
            projected.getPower(bears) shouldBe 2
            projected.hasKeyword(bears, Keyword.VIGILANCE) shouldBe false
        }

        test("discarding it from hand deals 4 damage to an attacking creature") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardInHand(1, "Gideon's Memorial")
                .withLandsOnBattlefield(1, "Plains", 2)
                .withCardOnBattlefield(2, "Centaur Courser")
                .withActivePlayer(2)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
            game.declareAttackers(mapOf("Centaur Courser" to 1)).error shouldBe null
            game.execute(PassPriority(game.player2Id))

            val courser = game.findPermanent("Centaur Courser")!!
            val handCard = game.findCardsInHand(1, "Gideon's Memorial").first()
            val result = game.execute(
                ActivateAbility(
                    playerId = game.player1Id,
                    sourceId = handCard,
                    abilityId = handAbilityId(),
                    targets = listOf(ChosenTarget.Permanent(courser))
                )
            )
            withClue("{1}{W} from two Plains, targeting an attacker: ${result.error}") {
                result.error shouldBe null
            }
            game.resolveStack()

            game.isOnBattlefield("Centaur Courser") shouldBe false
            game.isInGraveyard(1, "Gideon's Memorial") shouldBe true
        }

        test("a creature outside combat is not a legal target") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardInHand(1, "Gideon's Memorial")
                .withLandsOnBattlefield(1, "Plains", 2)
                .withCardOnBattlefield(2, "Centaur Courser")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val courser = game.findPermanent("Centaur Courser")!!
            val handCard = game.findCardsInHand(1, "Gideon's Memorial").first()
            val result = game.execute(
                ActivateAbility(
                    playerId = game.player1Id,
                    sourceId = handCard,
                    abilityId = handAbilityId(),
                    targets = listOf(ChosenTarget.Permanent(courser))
                )
            )
            (result.error != null) shouldBe true
        }
    }
}

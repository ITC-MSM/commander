package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.state.components.battlefield.DamageComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Wellgabber Apothecary (LRW #47) — "{1}{W}: Prevent all damage that would be dealt to target tapped
 * Merfolk or Kithkin creature this turn."
 *
 * "Tapped Merfolk or Kithkin creature" is one noun phrase and therefore one filter, so the tests pin
 * all three of its conjuncts: an untapped Merfolk is not a legal target, a tapped non-Merfolk /
 * non-Kithkin is not either, and a tapped one of each tribe is. Prodigal Sorcerer is the damage
 * source, which also proves the shield is not combat-only.
 */
class WellgabberApothecaryScenarioTest : ScenarioTestBase() {

    private val pingAbilityId by lazy {
        cardRegistry.getCard("Prodigal Sorcerer")!!.activatedAbilities[0].id
    }
    private val shieldAbilityId by lazy {
        cardRegistry.getCard("Wellgabber Apothecary")!!.activatedAbilities[0].id
    }

    init {
        fun board() = scenario()
            .withPlayers("Alice", "Bob")
            .withCardOnBattlefield(1, "Wellgabber Apothecary", summoningSickness = false)
            .withLandsOnBattlefield(1, "Plains", 4)
            .withCardOnBattlefield(1, "Fallowsage", tapped = true, summoningSickness = false)
            .withCardOnBattlefield(1, "Kinsbaile Skirmisher", tapped = true, summoningSickness = false)
            .withCardOnBattlefield(1, "Goldmeadow Harrier", summoningSickness = false)
            .withCardOnBattlefield(1, "Grizzly Bears", tapped = true, summoningSickness = false)
            .withCardOnBattlefield(1, "Prodigal Sorcerer", summoningSickness = false)
            .withActivePlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            .build()

        fun TestGame.autoPayIfAsked() {
            if (getPendingDecision() is SelectManaSourcesDecision) submitManaSourcesAutoPay()
        }

        fun TestGame.shield(targetName: String) = execute(
            ActivateAbility(
                playerId = player1Id,
                sourceId = findPermanent("Wellgabber Apothecary")!!,
                abilityId = shieldAbilityId,
                targets = listOf(ChosenTarget.Permanent(findPermanent(targetName)!!))
            )
        )

        fun TestGame.ping(targetName: String) {
            val result = execute(
                ActivateAbility(
                    playerId = player1Id,
                    sourceId = findPermanent("Prodigal Sorcerer")!!,
                    abilityId = pingAbilityId,
                    targets = listOf(ChosenTarget.Permanent(findPermanent(targetName)!!))
                )
            )
            withClue("ping activation failed: ${result.error}") { result.error shouldBe null }
            autoPayIfAsked()
            resolveStack()
        }

        fun TestGame.markedDamage(id: EntityId): Int =
            state.getEntity(id)?.get<DamageComponent>()?.amount ?: 0

        test("a tapped Merfolk is shielded from noncombat damage") {
            val game = board()
            val fallowsage = game.findPermanent("Fallowsage")!!

            withClue("a tapped Merfolk is a legal target") {
                game.shield("Fallowsage").error shouldBe null
            }
            game.autoPayIfAsked()
            game.resolveStack()

            game.ping("Fallowsage")
            withClue("the shield prevents all damage this turn") {
                game.markedDamage(fallowsage) shouldBe 0
            }
        }

        test("a tapped Kithkin is shielded too — the union has two halves") {
            val game = board()
            val skirmisher = game.findPermanent("Kinsbaile Skirmisher")!!

            game.shield("Kinsbaile Skirmisher").error shouldBe null
            game.autoPayIfAsked()
            game.resolveStack()

            game.ping("Kinsbaile Skirmisher")
            game.markedDamage(skirmisher) shouldBe 0
        }

        test("an untapped Kithkin is not a legal target") {
            val game = board()
            withClue("the Harrier is a Kithkin but is untapped") {
                game.shield("Goldmeadow Harrier").error shouldNotBe null
            }
        }

        test("a tapped creature of neither tribe is not a legal target") {
            val game = board()
            withClue("Grizzly Bears is tapped but is no Merfolk and no Kithkin") {
                game.shield("Grizzly Bears").error shouldNotBe null
            }
        }

        test("an unshielded tapped Merfolk still takes the damage") {
            val game = board()
            val fallowsage = game.findPermanent("Fallowsage")!!

            game.ping("Fallowsage")
            withClue("without the ability there is nothing to prevent the ping") {
                game.markedDamage(fallowsage) shouldBe 1
            }
        }
    }
}

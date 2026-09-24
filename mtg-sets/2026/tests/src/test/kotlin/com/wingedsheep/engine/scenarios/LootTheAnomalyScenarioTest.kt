package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.fra.cards.LootTheAnomaly
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Loot, the Anomaly (FRA #232) — {2}{B} Legendary Creature — Beast Horror, -2/4.
 *   If Loot's power is negative, he assigns combat damage as though his power were positive.
 *   Threshold — Sacrifice another creature or planeswalker: Loot gets -2/-0 until end of turn.
 *   Activate only if there are seven or more cards in your graveyard.
 *
 * Pins the printed negative base power, the absolute-value combat damage assignment (CR 510.1a
 * would otherwise have him assign nothing), that the characteristic itself stays negative, and the
 * threshold gate on the sacrifice ability.
 */
class LootTheAnomalyScenarioTest : ScenarioTestBase() {

    private val sacAbility = LootTheAnomaly.activatedAbilities.single().id

    private fun builder(graveyardCards: Int): ScenarioBuilder {
        var b = scenario().withPlayers()
            .withCardOnBattlefield(1, "Loot, the Anomaly")
            .withCardOnBattlefield(1, "Grizzly Bears")
            .withActivePlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
        repeat(graveyardCards) { b = b.withCardInGraveyard(1, "Swamp") }
        repeat(3) { b = b.withCardInLibrary(1, "Swamp") }
        repeat(3) { b = b.withCardInLibrary(2, "Swamp") }
        return b
    }

    private fun TestGame.activateSac() = execute(
        ActivateAbility(
            playerId = player1Id,
            sourceId = findPermanent("Loot, the Anomaly")!!,
            abilityId = sacAbility,
            costPayment = AdditionalCostPayment(sacrificedPermanents = listOf(findPermanent("Grizzly Bears")!!))
        )
    )

    private fun TestGame.attackUnblocked() {
        passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
        declareAttackers(mapOf("Loot, the Anomaly" to 2)).error shouldBe null
        passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
        declareNoBlockers().error shouldBe null
        passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)
    }

    init {
        test("printed base power is negative: Loot is a -2/4") {
            val game = builder(0).build()
            val loot = game.findPermanent("Loot, the Anomaly")!!
            game.state.projectedState.getPower(loot) shouldBe -2
            game.state.projectedState.getToughness(loot) shouldBe 4
        }

        test("an unblocked -2 power Loot deals 2 combat damage") {
            val game = builder(0).build()
            game.attackUnblocked()
            game.getLifeTotal(2) shouldBe 18
            withClue("the power characteristic itself is not rewritten") {
                game.state.projectedState.getPower(game.findPermanent("Loot, the Anomaly")!!) shouldBe -2
            }
        }

        test("a blocked Loot assigns 2 damage to the blocker") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, "Loot, the Anomaly")
                .withCardOnBattlefield(2, "Grizzly Bears")
                .withActivePlayer(1)
                .withCardInLibrary(1, "Swamp")
                .withCardInLibrary(2, "Swamp")
                .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                .build()
            game.declareAttackers(mapOf("Loot, the Anomaly" to 2)).error shouldBe null
            game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
            game.declareBlockers(mapOf("Grizzly Bears" to listOf("Loot, the Anomaly"))).error shouldBe null
            game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)

            game.isInGraveyard(2, "Grizzly Bears") shouldBe true
            game.isOnBattlefield("Loot, the Anomaly") shouldBe true
            game.getLifeTotal(2) shouldBe 20
        }

        test("threshold: sacrificing another creature makes Loot -4/4, and he deals 4") {
            val game = builder(7).build()
            val loot = game.findPermanent("Loot, the Anomaly")!!

            game.activateSac().error shouldBe null
            game.resolveStack()

            game.isInGraveyard(1, "Grizzly Bears") shouldBe true
            game.state.projectedState.getPower(loot) shouldBe -4
            game.state.projectedState.getToughness(loot) shouldBe 4

            game.attackUnblocked()
            game.getLifeTotal(2) shouldBe 16
        }

        test("without seven cards in the graveyard the ability can't be activated") {
            val game = builder(6).build()
            game.activateSac().error shouldNotBe null
            game.isOnBattlefield("Grizzly Bears") shouldBe true
        }
    }
}

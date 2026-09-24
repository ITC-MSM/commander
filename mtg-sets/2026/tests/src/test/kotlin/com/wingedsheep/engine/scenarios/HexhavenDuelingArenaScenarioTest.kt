package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.state.components.battlefield.PreparedComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.fra.cards.HexhavenDuelingArena
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.AbilityId
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Hexhaven Dueling Arena (FRA #181) — Land.
 * "{T}: Add {C}.
 *  {2}, {T}: Target creature that attacked this turn becomes prepared. Activate only as a sorcery.
 *  {4}, {T}: Target creature becomes prepared."
 */
class HexhavenDuelingArenaScenarioTest : ScenarioTestBase() {

    private val attackedAbility = HexhavenDuelingArena.activatedAbilities[1].id
    private val anyAbility = HexhavenDuelingArena.activatedAbilities[2].id

    private fun TestGame.activate(ability: AbilityId, target: EntityId) =
        execute(
            ActivateAbility(
                playerId = player1Id,
                sourceId = findPermanent("Hexhaven Dueling Arena")!!,
                abilityId = ability,
                targets = listOf(ChosenTarget.Permanent(target))
            )
        ).also { if (it.error == null && getPendingDecision() is SelectManaSourcesDecision) submitManaSourcesAutoPay() }

    private fun TestGame.isPrepared(id: EntityId) = state.getEntity(id)?.get<PreparedComponent>() != null

    private fun builder(): ScenarioBuilder {
        var b = scenario()
            .withPlayers("Player", "Opponent")
            .withCardOnBattlefield(1, "Hexhaven Dueling Arena")
            .withCardOnBattlefield(1, "Woodwork Prodigy")
            .withCardOnBattlefield(1, "Grizzly Bears")
            .withLandsOnBattlefield(1, "Forest", 4)
            .withActivePlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
        repeat(6) { b = b.withCardInLibrary(1, "Forest") }
        repeat(6) { b = b.withCardInLibrary(2, "Forest") }
        return b
    }

    init {
        context("Hexhaven Dueling Arena") {

            test("{4}: any creature with a prepare spell becomes prepared") {
                val game = builder().build()
                val prodigy = game.findPermanent("Woodwork Prodigy")!!

                game.activate(anyAbility, prodigy).error shouldBe null
                game.resolveStack()

                game.isPrepared(prodigy) shouldBe true
            }

            test("{4} on a creature without a prepare spell does nothing") {
                val game = builder().build()
                val bears = game.findPermanent("Grizzly Bears")!!

                game.activate(anyAbility, bears).error shouldBe null
                game.resolveStack()

                game.isPrepared(bears) shouldBe false
            }

            test("{2}: a creature that hasn't attacked this turn is not a legal target") {
                val game = builder().build()
                val prodigy = game.findPermanent("Woodwork Prodigy")!!

                game.activate(attackedAbility, prodigy).error.shouldNotBeNull()
                game.isPrepared(prodigy) shouldBe false
            }

            test("{2}: after attacking, the creature becomes prepared in the second main phase") {
                val game = builder().build()
                val prodigy = game.findPermanent("Woodwork Prodigy")!!

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Woodwork Prodigy" to 2)).error shouldBe null

                withClue("sorcery-speed: not during combat") {
                    game.activate(attackedAbility, prodigy).error shouldNotBe null
                }

                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)
                game.activate(attackedAbility, prodigy).error shouldBe null
                game.resolveStack()

                game.isPrepared(prodigy) shouldBe true
            }
        }
    }
}

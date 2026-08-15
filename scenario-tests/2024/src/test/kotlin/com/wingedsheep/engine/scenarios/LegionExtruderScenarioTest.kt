package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.ControllerComponent
import com.wingedsheep.engine.state.components.identity.TokenComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.big.cards.LegionExtruder
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Legion Extruder (BIG #12) — {1}{R} Artifact.
 *
 * "When this artifact enters, it deals 2 damage to any target.
 *  {2}, {T}, Sacrifice another artifact: Create a 3/3 colorless Golem artifact creature token."
 *
 * Covers the activated ability: paying {2}, tapping, and sacrificing *another* artifact yields a
 * 3/3 colorless Golem artifact creature token. A single fodder artifact on the battlefield makes
 * the sacrifice deterministic.
 */
class LegionExtruderScenarioTest : FunSpec({

    val activateAbilityId = LegionExtruder.activatedAbilities.first().id
    val projector = StateProjector()

    test("{2}, {T}, Sacrifice another artifact: create a 3/3 colorless Golem artifact creature token") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val extruder = driver.putPermanentOnBattlefield(player, "Legion Extruder")
        driver.removeSummoningSickness(extruder)
        // One other artifact to feed the "Sacrifice another artifact" cost.
        val fodder = driver.putPermanentOnBattlefield(player, "Legion Extruder")
        driver.removeSummoningSickness(fodder)
        driver.giveMana(player, Color.RED, 2)

        val before = artifactCreatureTokens(driver.state, player).toSet()
        driver.submit(
            ActivateAbility(playerId = player, sourceId = extruder, abilityId = activateAbilityId)
        ).isSuccess shouldBe true
        driver.bothPass()

        val newTokens = artifactCreatureTokens(driver.state, player) - before
        newTokens.size shouldBe 1

        val token = newTokens.first()
        val card = driver.state.getEntity(token)!!.get<CardComponent>()!!
        card.typeLine.isArtifact shouldBe true
        card.typeLine.isCreature shouldBe true
        card.typeLine.subtypes.map { it.value } shouldBe listOf("Golem")
        card.colors shouldBe emptySet()
        projector.getProjectedPower(driver.state, token) shouldBe 3
        projector.getProjectedToughness(driver.state, token) shouldBe 3

        // The fodder artifact was sacrificed; the source remains, now tapped.
        driver.state.getBattlefield().contains(fodder) shouldBe false
    }
})

private fun artifactCreatureTokens(state: GameState, player: EntityId): List<EntityId> =
    state.getBattlefield().filter {
        val e = state.getEntity(it) ?: return@filter false
        e.has<TokenComponent>() &&
            e.get<ControllerComponent>()?.playerId == player &&
            e.get<CardComponent>()?.typeLine?.isCreature == true
    }

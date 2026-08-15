package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.CardScript
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Tests for Excalibur II (FIN #257).
 *
 * Excalibur II {1} Legendary Artifact — Equipment
 * Whenever you gain life, put a charge counter on Excalibur II.
 * Equipped creature gets +1/+1 for each charge counter on Excalibur II.
 * Equip {3}
 *
 * Verifies the life-gain → charge-counter trigger and the dynamic equipped-creature buff
 * scaling with the source's charge-counter count (one charge counter per life-gain *event*,
 * regardless of amount — Scryfall ruling).
 */
class ExcaliburIIScenarioTest : ScenarioTestBase() {

    private val stateProjector = StateProjector()

    private val equipAbilityId by lazy {
        cardRegistry.requireCard("Excalibur II").activatedAbilities[0].id
    }

    init {
        // A simple "gain 3 life" instant to produce a single life-gain event.
        cardRegistry.register(
            CardDefinition.instant(
                name = "Test Healing Light",
                manaCost = ManaCost.parse("{W}"),
                oracleText = "You gain 3 life.",
                script = CardScript(spellEffect = Effects.GainLife(3))
            )
        )

        test("gaining life adds one charge counter and scales the equipped creature's stats") {
            val game = scenario()
                .withPlayers()
                .withCardOnBattlefield(1, "Grizzly Bears") // 2/2 base
                .withCardOnBattlefield(1, "Excalibur II")
                .withLandsOnBattlefield(1, "Plains", 4)     // Equip {3} + cast {W}
                .withCardInHand(1, "Test Healing Light")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val bears = game.findPermanent("Grizzly Bears")!!
            val excalibur = game.findPermanent("Excalibur II")!!

            // Equip Excalibur II onto Grizzly Bears.
            game.execute(
                ActivateAbility(
                    playerId = game.player1Id,
                    sourceId = excalibur,
                    abilityId = equipAbilityId,
                    targets = listOf(ChosenTarget.Permanent(bears))
                )
            ).error shouldBe null
            game.resolveStack()

            // Zero charge counters → no bonus yet.
            withClue("Equipped Bears with 0 charge counters should be a vanilla 2/2") {
                stateProjector.project(game.state).getPower(bears) shouldBe 2
                stateProjector.project(game.state).getToughness(bears) shouldBe 2
            }

            // Gain 3 life as a single event.
            game.castSpell(1, "Test Healing Light").error shouldBe null
            game.resolveStack()
            if (game.state.stack.isNotEmpty()) game.resolveStack()

            val charges = game.state.getEntity(excalibur)
                ?.get<CountersComponent>()?.getCount(CounterType.CHARGE) ?: 0
            withClue("One life-gain event adds exactly one charge counter regardless of amount") {
                charges shouldBe 1
            }
            withClue("Equipped Bears should now be 3/3 (2/2 + one charge counter)") {
                stateProjector.project(game.state).getPower(bears) shouldBe 3
                stateProjector.project(game.state).getToughness(bears) shouldBe 3
            }
        }
    }
}

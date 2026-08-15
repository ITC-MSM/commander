package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.battlefield.CastChoicesComponent
import com.wingedsheep.engine.state.components.battlefield.ChoiceValue
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.ControllerComponent
import com.wingedsheep.engine.state.components.identity.TokenComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.ons.cards.RiptideReplicator
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.ChoiceSlot
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Riptide Replicator (ONS #309) — {X}{4} Artifact.
 *
 * "As Riptide Replicator enters the battlefield, choose a color and a creature type. Riptide
 *  Replicator enters the battlefield with X charge counters on it. {4}, {T}: Create an X/X creature
 *  token of the chosen color and type, where X is the number of charge counters on it."
 *
 * Phase 2 proof: the one-off `CreateChosenTokenEffect` is gone — the activated ability is now a
 * generic `Effects.CreateTokenOfChosenColorAndType`, which reads `ChoiceSlot.COLOR` /
 * `ChoiceSlot.CREATURE_TYPE` off the unified [CastChoicesComponent] on the source. The chosen
 * color/type are written into the same bag by the `EntersWithChoice` resumer (covered by the
 * Shimmerwilds / Siege / Callous Oppressor tests); here we set the bag directly to isolate the
 * token-creation reading path, and vary the chosen values across the two cases to prove they are
 * actually read (not hardcoded).
 */
class RiptideReplicatorScenarioTest : FunSpec({

    val activateAbilityId = RiptideReplicator.activatedAbilities.first().id
    val projector = StateProjector()

    fun makeToken(charge: Int, color: Color, creatureType: String): Triple<GameState, EntityId, EntityId> {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), startingLife = 20)
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val replicator = driver.putPermanentOnBattlefield(player, "Riptide Replicator")
        driver.removeSummoningSickness(replicator)
        driver.replaceState(driver.state.updateEntity(replicator) { c ->
            var u = c.with(
                CastChoicesComponent(
                    chosen = mapOf(
                        ChoiceSlot.COLOR to ChoiceValue.ColorChoice(color),
                        ChoiceSlot.CREATURE_TYPE to ChoiceValue.TextChoice(creatureType)
                    )
                )
            )
            if (charge > 0) u = u.with(CountersComponent(mapOf(CounterType.CHARGE to charge)))
            u
        })
        driver.giveMana(player, Color.BLUE, 4)

        val before = creatureTokens(driver.state, player).toSet()
        driver.submit(
            ActivateAbility(playerId = player, sourceId = replicator, abilityId = activateAbilityId)
        ).isSuccess shouldBe true
        driver.bothPass()
        val newTokens = creatureTokens(driver.state, player) - before
        newTokens.size shouldBe 1
        return Triple(driver.state, newTokens.first(), player)
    }

    test("creates an X/X token of the chosen color and type (X = charge counters)") {
        val (state, token) = makeToken(charge = 3, color = Color.RED, creatureType = "Beast")
        projector.getProjectedPower(state, token) shouldBe 3
        projector.getProjectedToughness(state, token) shouldBe 3
        val card = state.getEntity(token)!!.get<CardComponent>()!!
        card.colors shouldBe setOf(Color.RED)
        card.typeLine.subtypes.map { it.value } shouldBe listOf("Beast")
    }

    test("reads the chosen color/type slots, not hardcoded values (different choice → different token)") {
        val (state, token) = makeToken(charge = 2, color = Color.GREEN, creatureType = "Elf")
        projector.getProjectedPower(state, token) shouldBe 2
        projector.getProjectedToughness(state, token) shouldBe 2
        val card = state.getEntity(token)!!.get<CardComponent>()!!
        card.colors shouldBe setOf(Color.GREEN)
        card.typeLine.subtypes.map { it.value } shouldBe listOf("Elf")
    }
})

private fun creatureTokens(state: GameState, player: EntityId): List<EntityId> =
    state.getBattlefield().filter {
        val e = state.getEntity(it)
        e?.has<TokenComponent>() == true &&
            e.get<ControllerComponent>()?.playerId == player
    }

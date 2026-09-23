package com.wingedsheep.engine.mechanics.sba

import com.wingedsheep.engine.core.Suspension
import com.wingedsheep.engine.core.BattleProtectorChoiceContinuation
import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.mechanics.battle.Battles
import com.wingedsheep.engine.mechanics.sba.permanent.BattleProtectorCheck
import com.wingedsheep.engine.state.ComponentContainer
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.ProtectorComponent
import com.wingedsheep.engine.state.components.combat.AttackingComponent
import com.wingedsheep.engine.state.components.identity.TeamComponent
import com.wingedsheep.engine.state.components.player.LossReason
import com.wingedsheep.engine.state.components.player.PlayerLostComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.ControllerComponent
import com.wingedsheep.engine.state.components.identity.OwnerComponent
import com.wingedsheep.engine.state.components.identity.PlayerComponent
import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.TypeLine
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import com.wingedsheep.engine.core.Outcome

/**
 * The CR 704.5x / 704.5y state-based actions that keep a battle's protector legal (CR 310.9),
 * exercised directly so the multiplayer branches — where the choice is *not* forced — are covered.
 * The two-player behaviour and the surrounding combat rules live in `BattleCardTypeScenarioTest`.
 */
class BattleProtectorCheckTest : FunSpec({

    val p1 = EntityId.of("player-1")
    val p2 = EntityId.of("player-2")
    val p3 = EntityId.of("player-3")
    val battleId = EntityId.of("battle-1")

    fun battleCard(siege: Boolean): ComponentContainer =
        ComponentContainer.of(
            CardComponent(
                cardDefinitionId = "Test Battle",
                name = "Test Battle",
                manaCost = ManaCost.parse("{2}{B}"),
                typeLine = TypeLine(
                    cardTypes = setOf(CardType.BATTLE),
                    subtypes = if (siege) setOf(Subtype.SIEGE) else emptySet(),
                ),
                oracleText = "",
                colors = setOf(Color.BLACK),
                ownerId = p1,
                spellEffect = null,
            ),
            OwnerComponent(p1),
            ControllerComponent(p1),
        )

    /** [players] seats in turn order; the battle is on the battlefield under p1's control. */
    fun stateWith(players: List<EntityId>, siege: Boolean, protector: EntityId? = null): GameState {
        var state = GameState().copy(turnOrder = players, activePlayerId = players.first())
        for (playerId in players) {
            state = state.withEntity(playerId, ComponentContainer.of(PlayerComponent("P${playerId.value}")))
        }
        var container = battleCard(siege)
        if (protector != null) container = container.with(ProtectorComponent(protector))
        return state
            .withEntity(battleId, container)
            .addToZone(ZoneKey(p1, Zone.BATTLEFIELD), battleId)
    }

    val check = BattleProtectorCheck()

    test("CR 310.12a — a Siege's eligible protectors are exactly its controller's opponents") {
        val state = stateWith(listOf(p1, p2, p3), siege = true)
        Battles.eligibleProtectors(state, battleId) shouldContainExactlyInAnyOrder listOf(p2, p3)
    }

    test("CR 310.9a — only its controller can protect a battle with no battle types") {
        val state = stateWith(listOf(p1, p2, p3), siege = false)
        Battles.eligibleProtectors(state, battleId) shouldContainExactlyInAnyOrder listOf(p1)
    }

    test("CR 704.5x — with two eligible protectors, the controller is prompted to choose") {
        val state = stateWith(listOf(p1, p2, p3), siege = true)

        val result = check.check(state)

        (result.outcome is Outcome.Paused) shouldBe true
        val decision = result.pendingDecision
        decision.shouldBeInstanceOf<ChooseOptionDecision>()
        withClue("the battle's controller makes the choice, not its would-be protector") {
            decision.playerId shouldBe p1
        }
        decision.options.size shouldBe 2

        val frame = result.state.continuationStack.last().shouldBeInstanceOf<Suspension>().answer
        frame.shouldBeInstanceOf<BattleProtectorChoiceContinuation>()
        frame.battleId shouldBe battleId
        frame.candidateIds shouldContainExactlyInAnyOrder listOf(p2, p3)
    }

    test("CR 704.5x — a single eligible protector is assigned silently, with no decision") {
        val state = stateWith(listOf(p1, p2), siege = true)

        val result = check.check(state)

        (result.outcome is Outcome.Paused) shouldBe false
        Battles.protectorOf(result.state, battleId) shouldBe p2
    }

    test("CR 704.5y — a Siege protected by a player who can't protect it gets a new one") {
        val state = stateWith(listOf(p1, p2), siege = true, protector = p1)

        val result = check.check(state)

        withClue("only an opponent may protect a Siege, so the illegal designation is replaced") {
            Battles.protectorOf(result.state, battleId) shouldBe p2
        }
    }

    test("CR 704.5x — a battle with no eligible protector is put into its owner's graveyard") {
        // A one-player game leaves a Siege with no opponent to protect it.
        val state = stateWith(listOf(p1), siege = true)

        val result = check.check(state)

        withClue("no player can be chosen, so the battle is put into its owner's graveyard") {
            (battleId in result.state.getBattlefield()) shouldBe false
            (battleId in result.state.getGraveyard(p1)) shouldBe true
        }
    }

    test("CR 310.12a — a player who has lost the game can't be chosen to protect a Siege") {
        val state = stateWith(listOf(p1, p2, p3), siege = true)
            .updateEntity(p3) { it.with(PlayerLostComponent(LossReason.LIFE_ZERO)) }
        Battles.eligibleProtectors(state, battleId) shouldContainExactlyInAnyOrder listOf(p2)
    }

    test("CR 102.3 / 310.12a — a teammate is not an opponent, so can't protect your Siege") {
        val state = stateWith(listOf(p1, p2, p3), siege = true)
            .updateEntity(p1) { it.with(TeamComponent(0)) }
            .updateEntity(p2) { it.with(TeamComponent(0)) }
            .updateEntity(p3) { it.with(TeamComponent(1)) }
        Battles.eligibleProtectors(state, battleId) shouldContainExactlyInAnyOrder listOf(p3)
    }

    test("CR 704.5x — a protector who left the game is replaced once the battle isn't being attacked") {
        val state = stateWith(listOf(p1, p2, p3), siege = true, protector = p2)
            .updateEntity(p2) { it.with(PlayerLostComponent(LossReason.LIFE_ZERO)) }

        val result = check.check(state)

        withClue("p3 is the only opponent still in the game, so the choice is forced") {
            Battles.protectorOf(result.state, battleId) shouldBe p3
        }
    }

    test("CR 704.5x — a protector who left the game is kept while creatures are still attacking the battle") {
        val attacker = EntityId.of("attacker-1")
        val state = stateWith(listOf(p1, p2, p3), siege = true, protector = p2)
            .updateEntity(p2) { it.with(PlayerLostComponent(LossReason.LIFE_ZERO)) }
            .withEntity(attacker, ComponentContainer.of(AttackingComponent(battleId)))
            .addToZone(ZoneKey(p3, Zone.BATTLEFIELD), attacker)

        val result = check.check(state)

        withClue("704.5x waits for the attack to end rather than handing the battle to someone new") {
            Battles.protectorOf(result.state, battleId) shouldBe p2
        }
    }

    test("a legal protector is left alone — the check is idempotent") {
        val state = stateWith(listOf(p1, p2, p3), siege = true, protector = p3)

        val result = check.check(state)

        (result.outcome is Outcome.Paused) shouldBe false
        Battles.protectorOf(result.state, battleId) shouldBe p3
    }
})

package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.handlers.continuations.entityIdToChosenTarget
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for Zack Fair (FIN #45).
 *
 * Zack Fair {W} Legendary Creature — Human Soldier 0/1
 *  - Zack Fair enters with a +1/+1 counter on it.
 *  - {1}, Sacrifice Zack Fair: Target creature you control gains indestructible until end of turn.
 *    Put Zack Fair's counters on that creature and attach an Equipment that was attached to Zack
 *    Fair to that creature.
 *
 * Because Zack is sacrificed as part of the activation *cost*, both halves of the resolution read
 * last-known information (CR 112.7a) captured before the cost was paid: the counter move falls back
 * to `lastKnownSourceCounters`, and the Equipment re-attach reads the new
 * `CardSource.LastKnownEquipmentAttachedToSource` (fed by `lastKnownSourceAttachments`). These tests
 * cover the full ability, the empty edge cases (no counters / no Equipment), and the player choice
 * when more than one Equipment was attached.
 */
class ZackFairScenarioTest : ScenarioTestBase() {

    private fun abilityId() = cardRegistry.getCard("Zack Fair")!!.script.activatedAbilities[0].id

    init {
        test("moves all counters, grants indestructible, and re-attaches the lone Equipment") {
            val game = scenario()
                .withPlayers()
                .withCardOnBattlefield(1, "Zack Fair", summoningSickness = false)
                .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                .withCardAttachedTo(1, "Buster Sword", "Zack Fair")
                .withLandsOnBattlefield(1, "Plains", 1)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val zack = game.findPermanent("Zack Fair")!!
            val bears = game.findPermanent("Grizzly Bears")!!
            val sword = game.findPermanent("Buster Sword")!!

            // Give Zack two +1/+1 counters (simulating its enters-with counter plus growth) so we
            // can prove the count moves, not just the existence of counters.
            game.state = game.state.updateEntity(zack) {
                it.with(CountersComponent().withAdded(CounterType.PLUS_ONE_PLUS_ONE, 2))
            }

            val activate = game.execute(
                ActivateAbility(
                    playerId = game.player1Id,
                    sourceId = zack,
                    abilityId = abilityId(),
                    targets = listOf(entityIdToChosenTarget(game.state, bears)),
                )
            )
            withClue("activation should succeed: ${activate.error}") { activate.error shouldBe null }
            if (game.getPendingDecision() is SelectManaSourcesDecision) game.submitManaSourcesAutoPay()
            game.resolveStack()

            withClue("only one Equipment qualified → no choice prompt") {
                game.getPendingDecision() shouldBe null
            }
            withClue("Zack Fair was sacrificed as a cost") {
                game.findPermanent("Zack Fair") shouldBe null
            }
            withClue("Grizzly Bears gained indestructible until end of turn") {
                game.state.projectedState.hasKeyword(bears, Keyword.INDESTRUCTIBLE) shouldBe true
            }
            withClue("Zack's two +1/+1 counters moved onto Grizzly Bears") {
                game.state.getEntity(bears)?.get<CountersComponent>()
                    ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 2
            }
            withClue("Buster Sword re-attached from Zack onto Grizzly Bears") {
                game.state.getEntity(sword)?.get<AttachedToComponent>()?.targetId shouldBe bears
            }
            withClue("Grizzly Bears is 2/2 base + 2/2 counters + 3/2 Buster Sword = 7/6") {
                game.state.projectedState.getPower(bears) shouldBe 7
                game.state.projectedState.getToughness(bears) shouldBe 6
            }
        }

        test("graceful no-op when Zack has no counters and no Equipment attached") {
            val game = scenario()
                .withPlayers()
                .withCardOnBattlefield(1, "Zack Fair", summoningSickness = false)
                .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                .withLandsOnBattlefield(1, "Plains", 1)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val zack = game.findPermanent("Zack Fair")!!
            val bears = game.findPermanent("Grizzly Bears")!!

            val activate = game.execute(
                ActivateAbility(
                    playerId = game.player1Id,
                    sourceId = zack,
                    abilityId = abilityId(),
                    targets = listOf(entityIdToChosenTarget(game.state, bears)),
                )
            )
            withClue("activation should succeed: ${activate.error}") { activate.error shouldBe null }
            if (game.getPendingDecision() is SelectManaSourcesDecision) game.submitManaSourcesAutoPay()
            game.resolveStack()

            withClue("no Equipment to attach → no decision, no error") {
                game.getPendingDecision() shouldBe null
            }
            withClue("Grizzly Bears still gains indestructible, gains no counters") {
                game.state.projectedState.hasKeyword(bears, Keyword.INDESTRUCTIBLE) shouldBe true
                game.state.getEntity(bears)?.get<CountersComponent>()
                    ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0 shouldBe 0
                game.state.projectedState.getPower(bears) shouldBe 2
            }
            game.findPermanent("Zack Fair") shouldBe null
        }

        test("player chooses which Equipment to re-attach when more than one qualifies") {
            val game = scenario()
                .withPlayers()
                .withCardOnBattlefield(1, "Zack Fair", summoningSickness = false)
                .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                .withCardAttachedTo(1, "Buster Sword", "Zack Fair")
                .withCardAttachedTo(1, "Coral Sword", "Zack Fair")
                .withLandsOnBattlefield(1, "Plains", 1)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val zack = game.findPermanent("Zack Fair")!!
            val bears = game.findPermanent("Grizzly Bears")!!
            val coral = game.findPermanent("Coral Sword")!!
            val buster = game.findPermanent("Buster Sword")!!

            val activate = game.execute(
                ActivateAbility(
                    playerId = game.player1Id,
                    sourceId = zack,
                    abilityId = abilityId(),
                    targets = listOf(entityIdToChosenTarget(game.state, bears)),
                )
            )
            withClue("activation should succeed: ${activate.error}") { activate.error shouldBe null }
            if (game.getPendingDecision() is SelectManaSourcesDecision) game.submitManaSourcesAutoPay()
            game.resolveStack()

            val decision = game.getPendingDecision()
            withClue("two Equipment qualified → prompt the controller to choose one") {
                (decision is SelectCardsDecision) shouldBe true
            }
            game.submitDecision(CardsSelectedResponse(decision!!.id, listOf(coral)))
            game.resolveStack()

            withClue("the chosen Coral Sword re-attached onto Grizzly Bears") {
                game.state.getEntity(coral)?.get<AttachedToComponent>()?.targetId shouldBe bears
            }
            withClue("the unchosen Buster Sword stays on the battlefield, no longer attached to Zack") {
                game.findPermanent("Buster Sword") shouldNotBe null
                game.state.getEntity(buster)?.get<AttachedToComponent>()?.targetId shouldNotBe bears
            }
        }
    }
}

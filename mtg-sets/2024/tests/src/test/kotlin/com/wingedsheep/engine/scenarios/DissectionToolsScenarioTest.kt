package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Dissection Tools (DSK #245) — {5} Artifact — Equipment.
 *
 * "When this Equipment enters, manifest dread, then attach this Equipment to that creature."
 * "Equipped creature gets +2/+2 and has deathtouch and lifelink." Equip—Sacrifice a creature.
 */
class DissectionToolsScenarioTest : ScenarioTestBase() {

    init {
        context("Dissection Tools") {
            test("ETB manifests dread, attaches, and grants +2/+2, deathtouch, lifelink") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Dissection Tools")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Forest")
                    .withLandsOnBattlefield(1, "Swamp", 5)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cast = game.castSpell(1, "Dissection Tools")
                withClue("Casting Dissection Tools should succeed: ${cast.error}") { cast.error shouldBe null }
                game.resolveStack()

                var guard = 0
                while (game.getPendingDecision() !is SelectCardsDecision && guard++ < 20) {
                    game.resolveStack()
                }
                val decision = game.getPendingDecision() as? SelectCardsDecision
                    ?: error("expected a SelectCardsDecision for manifest dread; got ${game.getPendingDecision()}")
                val manifestPick = decision.options.first()
                game.submitDecision(CardsSelectedResponse(decisionId = decision.id, selectedCards = listOf(manifestPick)))
                game.resolveStack()

                val toolsId = game.findPermanent("Dissection Tools")!!
                val attachedTo = game.state.getEntity(toolsId)?.get<AttachedToComponent>()
                attachedTo.shouldNotBeNull()
                attachedTo.targetId shouldBe manifestPick

                withClue("+2/+2 on a 2/2 manifest = 4/4, with deathtouch and lifelink") {
                    game.state.projectedState.getPower(manifestPick) shouldBe 4
                    game.state.projectedState.getToughness(manifestPick) shouldBe 4
                    game.state.projectedState.hasKeyword(manifestPick, Keyword.DEATHTOUCH) shouldBe true
                    game.state.projectedState.hasKeyword(manifestPick, Keyword.LIFELINK) shouldBe true
                }
            }

            test("Equip—Sacrifice a creature: paying the cost sacrifices a creature and re-attaches the Tools") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Dissection Tools")
                    .withCardOnBattlefield(1, "Centaur Courser", summoningSickness = false) // target to equip
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)   // fodder to sacrifice
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val toolsId = game.findPermanent("Dissection Tools")!!
                val courser = game.findPermanent("Centaur Courser")!!
                val bears = game.findPermanent("Grizzly Bears")!!
                val equipId = cardRegistry.getCard("Dissection Tools")!!.script.activatedAbilities
                    .first { it.isEquipAbility }.id

                val equip = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = toolsId,
                        abilityId = equipId,
                        targets = listOf(ChosenTarget.Permanent(courser)),
                        costPayment = AdditionalCostPayment(sacrificedPermanents = listOf(bears))
                    )
                )
                withClue("Equip—Sacrifice should succeed: ${equip.error}") { equip.error shouldBe null }
                game.resolveStack()

                withClue("The sacrificed Grizzly Bears left the battlefield") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                }
                game.state.getEntity(toolsId)?.get<AttachedToComponent>()?.targetId shouldBe courser
                withClue("+2/+2 on the equipped 3/3 Courser = 5/5") {
                    game.state.projectedState.getPower(courser) shouldBe 5
                    game.state.projectedState.getToughness(courser) shouldBe 5
                }
            }
        }
    }
}

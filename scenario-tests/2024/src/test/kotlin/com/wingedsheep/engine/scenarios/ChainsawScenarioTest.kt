package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.TargetsResponse
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Chainsaw (DSK #128) — {1}{R} Artifact — Equipment.
 *
 *  - "When this Equipment enters, it deals 3 damage to up to one target creature."
 *  - "Whenever one or more creatures die, put a rev counter on this Equipment." (any controller,
 *    once per death batch — [com.wingedsheep.sdk.dsl.Triggers.OneOrMoreCreaturesDie]).
 *  - "Equipped creature gets +X/+0, where X is the number of rev counters on this Equipment."
 *  - "Equip {3}".
 */
class ChainsawScenarioTest : ScenarioTestBase() {

    private fun revCounters(game: TestGame): Int {
        val id = game.findPermanent("Chainsaw")!!
        return game.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.REV) ?: 0
    }

    init {
        context("Chainsaw") {
            test("ETB deals 3 damage to up to one target creature") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Chainsaw")
                    .withCardOnBattlefield(2, "Centaur Courser") // 3/3
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val courser = game.findPermanent("Centaur Courser")!!
                val cast = game.castSpell(1, "Chainsaw")
                withClue("Casting Chainsaw should succeed: ${cast.error}") { cast.error shouldBe null }
                game.resolveStack()

                // The ETB "deals 3 damage to up to one target creature" trigger pauses for its target.
                val targetDecision = game.getPendingDecision() as? ChooseTargetsDecision
                    ?: error("expected a ChooseTargetsDecision for the ETB damage; got ${game.getPendingDecision()}")
                game.submitDecision(TargetsResponse(targetDecision.id, mapOf(0 to listOf(courser))))
                game.resolveStack()

                withClue("3 damage kills the 3/3 Centaur Courser") {
                    game.isOnBattlefield("Centaur Courser") shouldBe false
                }
            }

            test("a single creature dying adds one rev counter") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Chainsaw")
                    .withCardOnBattlefield(2, "Glory Seeker") // 2/2
                    .withCardInHand(1, "Doom Blade")
                    .withLandsOnBattlefield(1, "Swamp", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val seeker = game.findPermanent("Glory Seeker")!!
                game.castSpell(1, "Doom Blade", targetId = seeker)
                game.resolveStack()

                withClue("One creature died → one rev counter") { revCounters(game) shouldBe 1 }
            }

            test("a board wipe killing several creatures adds exactly one rev counter (once per batch)") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Chainsaw")
                    .withCardOnBattlefield(1, "Glory Seeker") // 2/2, yours
                    .withCardOnBattlefield(2, "Glory Seeker") // 2/2, opponent's
                    .withCardOnBattlefield(2, "Glory Seeker") // 2/2, opponent's
                    .withCardInHand(1, "Pyroclasm")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Pyroclasm")
                game.resolveStack()

                withClue("All three 2/2s die to 2 damage") {
                    game.findPermanents("Glory Seeker").size shouldBe 0
                }
                withClue("Three simultaneous deaths form one batch → exactly one rev counter") {
                    revCounters(game) shouldBe 1
                }
            }

            test("equipped creature gets +X/+0 where X is the rev counter count") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Chainsaw")
                    .withCardOnBattlefield(1, "Centaur Courser", summoningSickness = false) // 3/3
                    .withCardOnBattlefield(2, "Glory Seeker") // 2/2, dies to add a rev counter
                    .withCardInHand(1, "Doom Blade")
                    .withLandsOnBattlefield(1, "Mountain", 3)
                    .withLandsOnBattlefield(1, "Swamp", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val chainsaw = game.findPermanent("Chainsaw")!!
                val courser = game.findPermanent("Centaur Courser")!!
                val equipId = cardRegistry.getCard("Chainsaw")!!.script.activatedAbilities
                    .first { it.isEquipAbility }.id

                // Equip {3} → attach to the Courser.
                val equip = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = chainsaw,
                        abilityId = equipId,
                        targets = listOf(ChosenTarget.Permanent(courser))
                    )
                )
                withClue("Equip should succeed: ${equip.error}") { equip.error shouldBe null }
                game.resolveStack()

                game.state.getEntity(chainsaw)?.get<AttachedToComponent>()?.targetId shouldBe courser

                // No rev counters yet → +0/+0, base 3/3.
                withClue("With 0 rev counters the Courser is an unbuffed 3/3") {
                    game.state.projectedState.getPower(courser) shouldBe 3
                    game.state.projectedState.getToughness(courser) shouldBe 3
                }

                // Kill the opponent's Glory Seeker → one rev counter.
                val seeker = game.findPermanent("Glory Seeker")!!
                game.castSpell(1, "Doom Blade", targetId = seeker)
                game.resolveStack()
                withClue("One creature died → one rev counter") { revCounters(game) shouldBe 1 }

                withClue("+1/+0 from one rev counter → 4/3") {
                    game.state.projectedState.getPower(courser) shouldBe 4
                    game.state.projectedState.getToughness(courser) shouldBe 3
                }
            }
        }
    }
}

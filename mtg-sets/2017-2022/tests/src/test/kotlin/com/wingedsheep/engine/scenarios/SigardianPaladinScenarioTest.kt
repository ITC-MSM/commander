package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.handlers.continuations.entityIdToChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario test for Sigardian Paladin (VOW #247) — {2}{G}{W} Creature — Human Knight, 4/4.
 *
 *   As long as you've put one or more +1/+1 counters on a creature this turn, this creature has
 *   trample and lifelink.
 *   {1}{G}{W}: Target creature you control with a +1/+1 counter on it gains trample and lifelink
 *   until end of turn.
 *
 * The static ability is a claim about turn *history*, and the tests are shaped around the three
 * ways a board scan would get it wrong:
 *
 *  - it is off before anything has placed a counter;
 *  - it is on once one has been placed, on any creature — not only on the Paladin;
 *  - it **stays** on after that creature has left the battlefield entirely, which is the first
 *    ruling's own example and the one thing a board scan can never reproduce.
 *
 * The kind matters too: a `-1/-1` counter is a counter on a creature, but not a +1/+1 one, so it
 * must not switch the ability on.
 */
class SigardianPaladinScenarioTest : ScenarioTestBase() {

    init {
        context("Sigardian Paladin's counter-history static") {

            test("has neither keyword before any counter is placed this turn") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Sigardian Paladin", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Forest", 5)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val paladin = game.findPermanent("Sigardian Paladin")!!
                withClue("nothing has placed a +1/+1 counter this turn") {
                    game.state.projectedState.hasKeyword(paladin, Keyword.TRAMPLE.name) shouldBe false
                    game.state.projectedState.hasKeyword(paladin, Keyword.LIFELINK.name) shouldBe false
                }
            }

            test("gains trample and lifelink once a +1/+1 counter is put on any creature") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Sigardian Paladin", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardInHand(1, "Angelic Quartermaster")
                    .withLandsOnBattlefield(1, "Plains", 6)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val paladin = game.findPermanent("Sigardian Paladin")!!
                val bears = game.findPermanent("Grizzly Bears")!!

                val card = game.findCardsInHand(1, "Angelic Quartermaster").first()
                game.execute(CastSpell(game.player1Id, card, emptyList())).error shouldBe null
                game.resolveStack()
                game.selectTargets(listOf(bears)).error shouldBe null
                game.resolveStack()

                withClue("the counter went on the Bears, not the Paladin, and that is enough") {
                    game.state.projectedState.hasKeyword(paladin, Keyword.TRAMPLE.name) shouldBe true
                    game.state.projectedState.hasKeyword(paladin, Keyword.LIFELINK.name) shouldBe true
                }
            }

            test("stays on after the creature that received the counter has died") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Sigardian Paladin", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardInHand(1, "Angelic Quartermaster")
                    .withCardInHand(1, "Bleed Dry")
                    .withLandsOnBattlefield(1, "Plains", 6)
                    .withLandsOnBattlefield(1, "Swamp", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val paladin = game.findPermanent("Sigardian Paladin")!!
                val bears = game.findPermanent("Grizzly Bears")!!

                val quartermaster = game.findCardsInHand(1, "Angelic Quartermaster").first()
                game.execute(CastSpell(game.player1Id, quartermaster, emptyList())).error shouldBe null
                game.resolveStack()
                game.selectTargets(listOf(bears)).error shouldBe null
                game.resolveStack()

                val removal = game.findCardsInHand(1, "Bleed Dry").first()
                game.execute(
                    CastSpell(game.player1Id, removal, listOf(entityIdToChosenTarget(game.state, bears)))
                ).error shouldBe null
                game.resolveStack()
                game.checkStateBasedActions()

                withClue("the counter and its creature are both gone; the history is not") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                    game.state.projectedState.hasKeyword(paladin, Keyword.TRAMPLE.name) shouldBe true
                    game.state.projectedState.hasKeyword(paladin, Keyword.LIFELINK.name) shouldBe true
                }
            }

            test("a -1/-1 counter is a counter on a creature but not the right kind") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Sigardian Paladin", summoningSickness = false)
                    .withCardOnBattlefield(1, "Fevered Convulsions", summoningSickness = false)
                    .withCardOnBattlefield(2, "Hill Giant", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Swamp", 6)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val paladin = game.findPermanent("Sigardian Paladin")!!
                val giant = game.findPermanent("Hill Giant")!!
                val convulsions = game.findPermanent("Fevered Convulsions")!!
                val abilityId = cardRegistry.getCard("Fevered Convulsions")!!
                    .script.activatedAbilities[0].id

                game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = convulsions,
                        abilityId = abilityId,
                        targets = listOf(entityIdToChosenTarget(game.state, giant)),
                    )
                ).error shouldBe null
                game.resolveStack()

                withClue("this turn's only counter was a -1/-1, so the kind-scoped gate stays shut") {
                    game.state.projectedState.hasKeyword(paladin, Keyword.TRAMPLE.name) shouldBe false
                    game.state.projectedState.hasKeyword(paladin, Keyword.LIFELINK.name) shouldBe false
                }
            }
        }

        context("Sigardian Paladin's activated ability") {

            test("grants trample and lifelink to a creature that has a +1/+1 counter") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Sigardian Paladin", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardInHand(1, "Angelic Quartermaster")
                    .withLandsOnBattlefield(1, "Plains", 5)
                    .withLandsOnBattlefield(1, "Forest", 5)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!

                val quartermaster = game.findCardsInHand(1, "Angelic Quartermaster").first()
                game.execute(CastSpell(game.player1Id, quartermaster, emptyList())).error shouldBe null
                game.resolveStack()
                game.selectTargets(listOf(bears)).error shouldBe null
                game.resolveStack()

                val paladin = game.findPermanent("Sigardian Paladin")!!
                val abilityId = cardRegistry.getCard("Sigardian Paladin")!!
                    .script.activatedAbilities[0].id

                game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = paladin,
                        abilityId = abilityId,
                        targets = listOf(entityIdToChosenTarget(game.state, bears)),
                    )
                ).error shouldBe null
                game.resolveStack()

                withClue("the Bears now have both keywords until end of turn") {
                    game.state.projectedState.hasKeyword(bears, Keyword.TRAMPLE.name) shouldBe true
                    game.state.projectedState.hasKeyword(bears, Keyword.LIFELINK.name) shouldBe true
                }
            }

            test("cannot target a creature without a +1/+1 counter") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Sigardian Paladin", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Forest", 5)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val paladin = game.findPermanent("Sigardian Paladin")!!
                val bears = game.findPermanent("Grizzly Bears")!!
                val abilityId = cardRegistry.getCard("Sigardian Paladin")!!
                    .script.activatedAbilities[0].id

                withClue("the counter requirement lives in the target filter (second ruling)") {
                    game.execute(
                        ActivateAbility(
                            playerId = game.player1Id,
                            sourceId = paladin,
                            abilityId = abilityId,
                            targets = listOf(entityIdToChosenTarget(game.state, bears)),
                        )
                    ).error shouldNotBe null
                }
            }
        }
    }
}

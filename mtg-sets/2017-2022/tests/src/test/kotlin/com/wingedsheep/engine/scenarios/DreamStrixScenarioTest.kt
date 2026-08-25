package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.handlers.continuations.entityIdToChosenTarget
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Dream Strix (STX) — "Flying / When this creature becomes the target of a spell, sacrifice it. /
 * When this creature dies, learn."
 *
 * Two things the wording pins down that a lookalike would get wrong:
 *
 * 1. **Spells only.** An *ability* that targets it must not fire the sacrifice. That is the whole
 *    reason the trigger is a `BecomesTargetEvent(spellsOnly = true)` rather than the plain
 *    self-bound `Triggers.BecomesTarget`, which would also match abilities.
 * 2. **The drawback pays you.** Pointing removal at it makes it sacrifice itself, which *is* a
 *    death — so the second trigger fires and you Learn anyway, while the removal spell is left
 *    with no legal target and is countered on resolution (CR 608.2b).
 */
class DreamStrixScenarioTest : ScenarioTestBase() {

    init {
        context("becomes the target of a spell") {
            test("sacrifices itself, and the dies trigger still learns") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Dream Strix")
                    .withCardInHand(2, "Shock")
                    .withLandsOnBattlefield(2, "Mountain", 1)
                    .withCardInSideboard(1, "Boomerang Basics")
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val strix = game.findPermanent("Dream Strix")
                strix.shouldNotBeNull()

                game.castSpell(2, "Shock", strix).error shouldBe null
                // The becomes-target trigger goes on the stack above Shock and resolves first.
                game.resolveStack()

                withClue("sacrificed to its own trigger, so it is in the graveyard") {
                    game.findPermanent("Dream Strix") shouldBe null
                    game.isInGraveyard(1, "Dream Strix") shouldBe true
                }

                withClue("the dies trigger offers its controller the Learn") {
                    game.hasPendingDecision() shouldBe true
                }
                // Player 1's hand is empty, so the discard half auto-declines and this is the
                // Lesson prompt; take Boomerang Basics out of the sideboard.
                val lessonChoice = game.getPendingDecision() as? SelectCardsDecision
                lessonChoice.shouldNotBeNull()
                val lesson = lessonChoice.cardInfo!!.entries
                    .first { it.value.name == "Boomerang Basics" }.key
                game.selectCards(listOf(lesson))

                withClue("Learn found the Lesson") {
                    game.isInHand(1, "Boomerang Basics") shouldBe true
                }
            }
        }

        context("becomes the target of an ability") {
            test("does not sacrifice — the trigger is spells-only") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Dream Strix")
                    .withCardOnBattlefield(2, "Prodigal Sorcerer")
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val strix = game.findPermanent("Dream Strix")
                strix.shouldNotBeNull()
                val tim = game.findPermanent("Prodigal Sorcerer")!!
                val abilityId = cardRegistry.getCard("Prodigal Sorcerer")!!
                    .script.activatedAbilities[0].id

                game.execute(
                    ActivateAbility(
                        playerId = game.player2Id,
                        sourceId = tim,
                        abilityId = abilityId,
                        targets = listOf(entityIdToChosenTarget(game.state, strix))
                    )
                ).error shouldBe null
                game.resolveStack()

                withClue("an ability targeted it — no sacrifice, and 1 damage doesn't kill a 3/2") {
                    game.findPermanent("Dream Strix") shouldNotBe null
                    game.isInGraveyard(1, "Dream Strix") shouldBe false
                }
            }
        }
    }
}

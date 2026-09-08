package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.rav.cards.GriftersBlade
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Grifter's Blade (RAV) — "As this Equipment enters, choose a creature you control it could be
 * attached to. If you do, it enters attached to that creature."
 *
 * The card is two entry replacements standing in a row: `EntersWithChoice` records the host before
 * the Blade enters, then `OnEnterRunEffect` attaches to it inline with entry. Those two had never
 * met before — the `EntersWithChoice` resume path completed the permanent's entry itself and never
 * ran the `OnEnterRunEffect`, so the Blade would have made its choice and then entered unattached
 * anyway. The first test is the regression for that; it fails on `main` with the choice prompted
 * and nothing attached.
 *
 * The second test is the card's own second ruling — no creature, no choice, no attachment — which
 * also proves the pipeline does not error out when the choice is skipped.
 */
class GriftersBladeScenarioTest : ScenarioTestBase() {

    private val stateProjector = StateProjector()

    private fun TestGame.drainMana() {
        var guard = 0
        while (guard++ < 15 && getPendingDecision() is SelectManaSourcesDecision) {
            submitManaSourcesAutoPay()
        }
    }

    private fun TestGame.attachedHost(blade: EntityId): EntityId? =
        state.getEntity(blade)?.get<AttachedToComponent>()?.targetId

    private fun bladeGame(vararg creatures: String) = scenario()
        .withPlayers("Alice", "Bob")
        .withCardInHand(1, "Grifter's Blade")
        .withLandsOnBattlefield(1, "Plains", 3)
        .withActivePlayer(1)
        .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
        .also { builder -> creatures.forEach { builder.withCardOnBattlefield(1, it, summoningSickness = false) } }
        .build()

    init {
        cardRegistry.register(GriftersBlade)

        context("Grifter's Blade") {

            test("enters attached to the chosen creature, and equips it") {
                val game = bladeGame("Grizzly Bears")
                val bears = game.findPermanent("Grizzly Bears")!!

                withClue("a plain 2/2 before the Blade arrives") {
                    val before = stateProjector.project(game.state)
                    before.getPower(bears) shouldBe 2
                    before.getToughness(bears) shouldBe 2
                }

                game.castSpell(1, "Grifter's Blade").error shouldBe null
                game.drainMana()
                game.resolveStack()

                val decision = game.getPendingDecision()
                withClue("the host is chosen as the Blade enters, on the battlefield") {
                    (decision as? SelectCardsDecision)?.options shouldBe listOf(bears)
                }
                withClue("the Blade is not a creature, so the prompt must not say 'another'") {
                    decision!!.prompt shouldBe "Choose a creature you control"
                }

                game.selectCards(listOf(bears)).error shouldBe null

                val blade = game.findPermanent("Grifter's Blade")!!
                withClue("it enters ALREADY attached — the regression: the choice was made and then dropped") {
                    game.attachedHost(blade) shouldBe bears
                }
                withClue("and the equipped creature gets the +1/+1") {
                    val after = stateProjector.project(game.state)
                    after.getPower(bears) shouldBe 3
                    after.getToughness(bears) shouldBe 3
                }
            }

            test("with no creature to attach to, it simply enters unattached") {
                val game = bladeGame()

                game.castSpell(1, "Grifter's Blade").error shouldBe null
                game.drainMana()
                game.resolveStack()

                withClue("nothing to choose between, so no prompt is raised at all") {
                    game.getPendingDecision() shouldBe null
                }
                val blade = game.findPermanent("Grifter's Blade")
                withClue("the Blade still enters — the clause is 'if you do', not a failed entry") {
                    blade shouldNotBe null
                }
                withClue("its second ruling: unattached") {
                    game.attachedHost(blade!!) shouldBe null
                }
            }

            test("the chosen host is the one the player picked, not merely the first candidate") {
                val game = bladeGame("Grizzly Bears", "Hill Giant")
                val giant = game.findPermanent("Hill Giant")!!
                val bears = game.findPermanent("Grizzly Bears")!!

                game.castSpell(1, "Grifter's Blade").error shouldBe null
                game.drainMana()
                game.resolveStack()

                game.selectCards(listOf(giant)).error shouldBe null

                val blade = game.findPermanent("Grifter's Blade")!!
                withClue("the Giant was chosen") { game.attachedHost(blade) shouldBe giant }

                val after = stateProjector.project(game.state)
                withClue("3/3 Giant becomes 4/4") {
                    after.getPower(giant) shouldBe 4
                    after.getToughness(giant) shouldBe 4
                }
                withClue("the creature that wasn't chosen is untouched") {
                    after.getPower(bears) shouldBe 2
                    after.getToughness(bears) shouldBe 2
                }
            }

            test("an opponent's creature is never offered as a host") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardInHand(1, "Grifter's Blade")
                    .withLandsOnBattlefield(1, "Plains", 3)
                    .withCardOnBattlefield(2, "Grizzly Bears", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Grifter's Blade").error shouldBe null
                game.drainMana()
                game.resolveStack()

                withClue("'a creature YOU control' — the opponent's Bears is not a candidate") {
                    game.getPendingDecision() shouldBe null
                }
                val blade = game.findPermanent("Grifter's Blade")!!
                game.attachedHost(blade) shouldBe null
            }
        }
    }
}

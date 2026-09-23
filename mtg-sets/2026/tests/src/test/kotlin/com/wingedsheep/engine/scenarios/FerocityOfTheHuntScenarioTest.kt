package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Ferocity of the Hunt (FRA #134) — {1}{B/G} Enchantment — Aura.
 *
 * "Flash / Enchant creature / Enchanted creature gets +1/+0 and has deathtouch. / When enchanted
 *  creature dies, return that card to the battlefield tapped under its owner's control."
 *
 * The return is guarded on the card still being in the graveyard, so the test proves the guard
 * doesn't swallow the ordinary case: the creature comes back, tapped.
 */
class FerocityOfTheHuntScenarioTest : ScenarioTestBase() {

    private val projector = StateProjector()

    private val slay = card("Slay Test Spell") {
        manaCost = "{0}"
        typeLine = "Sorcery"
        oracleText = "Destroy target creature."
        spell {
            val c = target("target creature", Targets.Creature)
            effect = Effects.Destroy(c)
        }
    }

    init {
        cardRegistry.register(listOf(slay))

        context("Ferocity of the Hunt") {

            test("enchanted creature gets +1/+0 and has deathtouch") {
                val game = scenario()
                    .withPlayers()
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardAttachedTo(1, "Ferocity of the Hunt", "Grizzly Bears")
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val projected = projector.project(game.state)
                projected.getPower(bears) shouldBe 3
                projected.getToughness(bears) shouldBe 2
                projected.hasKeyword(bears, Keyword.DEATHTOUCH) shouldBe true
            }

            test("when enchanted creature dies it returns to the battlefield tapped") {
                val game = scenario()
                    .withPlayers()
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardAttachedTo(1, "Ferocity of the Hunt", "Grizzly Bears")
                    .withCardInHand(1, "Slay Test Spell")
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                game.castSpell(1, "Slay Test Spell", targetId = bears).error shouldBe null
                game.resolveStack()

                withClue("the Bears came back from the graveyard") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe true
                    game.isInGraveyard(1, "Grizzly Bears") shouldBe false
                }
                val returned = game.findPermanent("Grizzly Bears").shouldNotBeNull()
                withClue("returned tapped") {
                    game.state.getEntity(returned)!!.has<TappedComponent>() shouldBe true
                }
                withClue("the Aura itself went to the graveyard") {
                    game.isInGraveyard(1, "Ferocity of the Hunt") shouldBe true
                }
            }
        }
    }
}

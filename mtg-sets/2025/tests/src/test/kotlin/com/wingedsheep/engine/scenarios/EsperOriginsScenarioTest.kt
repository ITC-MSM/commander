package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ReorderLibraryDecision
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.battlefield.SagaComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for Esper Origins // Summon: Esper Maduin (FIN #185).
 *
 * Front — Esper Origins ({1}{G} Sorcery):
 *   "Surveil 2. You gain 2 life. If this spell was cast from a graveyard, exile it, then put it
 *    onto the battlefield transformed under its owner's control with a finality counter on it.
 *    Flashback {3}{G}"
 * Back — Summon: Esper Maduin (Enchantment Creature — Saga Elemental 4/4).
 *
 * Exercises [com.wingedsheep.sdk.model.CardScript.returnTransformedFromGraveyardOnResolve]: a spell
 * cast from a graveyard resolves onto the battlefield transformed (its back face up) with a finality
 * counter instead of going to the graveyard. Cast from hand, it is an ordinary sorcery.
 */
class EsperOriginsScenarioTest : ScenarioTestBase() {

    init {
        /**
         * Resolve the stack, auto-answering the Surveil prompts along the way: keep every card
         * (put none in the graveyard) and keep the library order. Surveil 2 emits a
         * [SelectCardsDecision] and then, when >1 card stays on top, a [ReorderLibraryDecision].
         */
        fun ScenarioTestBase.TestGame.resolveThroughSurveil() {
            resolveStack()
            var guard = 0
            while (getPendingDecision() != null && guard++ < 8) {
                when (getPendingDecision()) {
                    is SelectCardsDecision -> skipSelection()
                    is ReorderLibraryDecision -> keepLibraryOrder()
                    else -> break
                }
                resolveStack()
            }
        }

        test("cast from hand: surveils, gains 2 life, and goes to the graveyard (no transform)") {
            val game = scenario()
                .withPlayers()
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .withCardInHand(1, "Esper Origins")
                .withLandsOnBattlefield(1, "Forest", 2)
                .withCardInLibrary(1, "Grizzly Bears")
                .withCardInLibrary(1, "Grizzly Bears")
                .withCardInLibrary(1, "Grizzly Bears")
                .build()

            val lifeBefore = game.getLifeTotal(1)
            game.castSpell(1, "Esper Origins").error shouldBe null
            game.resolveThroughSurveil()

            withClue("gained 2 life") { game.getLifeTotal(1) shouldBe lifeBefore + 2 }
            withClue("resolved to the graveyard as a sorcery, not the battlefield") {
                game.isInGraveyard(1, "Esper Origins") shouldBe true
                game.isOnBattlefield("Esper Origins") shouldBe false
                game.isOnBattlefield("Summon: Esper Maduin") shouldBe false
            }
        }

        test("flashback from graveyard: transforms onto the battlefield as Summon: Esper Maduin with a finality counter") {
            val game = scenario()
                .withPlayers()
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .withCardInGraveyard(1, "Esper Origins")
                .withLandsOnBattlefield(1, "Forest", 4) // flashback {3}{G}
                // Enough permanent cards that Surveil and chapter I both have cards to look at.
                .withCardInLibrary(1, "Grizzly Bears")
                .withCardInLibrary(1, "Grizzly Bears")
                .withCardInLibrary(1, "Grizzly Bears")
                .build()

            val lifeBefore = game.getLifeTotal(1)
            game.castSpellFromGraveyard(1, "Esper Origins").error shouldBe null
            game.resolveThroughSurveil()

            withClue("gained 2 life on resolution") { game.getLifeTotal(1) shouldBe lifeBefore + 2 }

            val maduin = game.findPermanent("Summon: Esper Maduin")
            withClue("returned to the battlefield transformed as the Saga back face") {
                maduin shouldNotBe null
                game.isOnBattlefield("Esper Origins") shouldBe false
            }
            withClue("it is NOT in the graveyard or exile — the transform took precedence over flashback exile") {
                game.isInGraveyard(1, "Esper Origins") shouldBe false
                game.isInExile(1, "Esper Origins") shouldBe false
                game.isInGraveyard(1, "Summon: Esper Maduin") shouldBe false
                game.isInExile(1, "Summon: Esper Maduin") shouldBe false
            }
            withClue("entered with a finality counter") {
                game.state.getEntity(maduin!!)?.get<CountersComponent>()?.getCount(CounterType.FINALITY) shouldBe 1
            }
            withClue("it is a live Saga whose first chapter has triggered") {
                game.state.getEntity(maduin!!)?.get<SagaComponent>() shouldNotBe null
                game.state.getEntity(maduin)?.get<SagaComponent>()?.triggeredChapters?.contains(1) shouldBe true
            }
            withClue("chapter I put the revealed permanent card into hand") {
                game.isInHand(1, "Grizzly Bears") shouldBe true
            }
        }
    }
}

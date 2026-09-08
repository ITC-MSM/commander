package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import io.kotest.matchers.shouldBe

class SpellSelectionRemainderTest : ScenarioTestBase() {
    private val selectionSpell = card("Spell Selection Test") {
        manaCost = "{0}"
        typeLine = "Sorcery"
        spell {
            effect = Effects.Composite(
                GatherCardsEffect(CardSource.FromZone(Zone.HAND), "hand"),
                SelectFromCollectionEffect(
                    from = "hand",
                    selection = SelectionMode.ChooseSpell,
                    filter = GameObjectFilter.Nonland,
                    storeSelected = "selected",
                    storeRemainder = "rest"
                ),
                MoveCollectionEffect("rest", CardDestination.ToZone(Zone.EXILE))
            )
        }
    }

    init {
        cardRegistry.register(selectionSpell)

        fun setup() = scenario()
            .withPlayers("Player", "Opponent")
            .withCardInHand(1, "Spell Selection Test")
            .withCardInHand(1, "Grizzly Bears")
            .withCardInHand(1, "Island")
            .withActivePlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            .build()

        test("declining publishes all cards, including nonspells, to the remainder") {
            val game = setup()
            game.castSpell(1, "Spell Selection Test").error shouldBe null
            game.resolveStack()
            (game.state.pendingDecision is SelectCardsDecision) shouldBe true
            game.selectCards(emptyList()).error shouldBe null
            game.resolveStack()
            game.isInExile(1, "Grizzly Bears") shouldBe true
            game.isInExile(1, "Island") shouldBe true
            game.state.getHand(game.player1Id).size shouldBe 0
        }

        test("selecting a spell excludes only that card from the remainder") {
            val game = setup()
            game.castSpell(1, "Spell Selection Test").error shouldBe null
            game.resolveStack()
            val bear = game.findCardsInHand(1, "Grizzly Bears").single()
            game.selectCards(listOf(bear)).error shouldBe null
            game.resolveStack()
            game.isInHand(1, "Grizzly Bears") shouldBe true
            game.isInExile(1, "Island") shouldBe true
        }
    }
}

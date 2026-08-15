package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dsk.cards.HedgeShredder
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Hedge Shredder (DSK #183) — {2}{G}{G} Artifact — Vehicle 5/5.
 *
 * Second ability: "Whenever one or more land cards are put into your graveyard from your library,
 * put them onto the battlefield tapped." This exercises the new
 * [com.wingedsheep.sdk.dsl.Triggers.LandsPutIntoGraveyardFromLibrary] batching trigger plus its
 * `IterationSpace.TRIGGER_CAPTURED_COLLECTION` → `MoveCollectionEffect(... Tapped)` payoff.
 *
 * We drive a real library→graveyard mill with Hedge Shredder on the battlefield and assert that
 * milled lands land on the battlefield tapped under the controller, while milled nonlands stay in
 * the graveyard.
 */
class HedgeShredderScenarioTest : FunSpec({

    // A {0} sorcery that mills the caster's top two cards — produces real library→graveyard
    // ZoneChangeEvents that drive the batching trigger.
    val MillTwo = card("Mill Two Iso") {
        manaCost = "{0}"
        typeLine = "Sorcery"
        oracleText = "Mill two cards."
        spell { effect = Patterns.Library.mill(2) }
    }

    fun driver(): GameTestDriver = GameTestDriver().apply {
        registerCards(TestCards.all + HedgeShredder + MillTwo)
        initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20, skipMulligans = true)
    }

    test("milled land cards enter the battlefield tapped; nonlands stay in the graveyard") {
        val d = driver()
        val you = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        d.putPermanentOnBattlefield(you, "Hedge Shredder")

        // Stack the top two of the library: a nonland on top, a land beneath it (both milled).
        val land = d.putCardOnTopOfLibrary(you, "Forest")
        val nonland = d.putCardOnTopOfLibrary(you, "Grizzly Bears") // now top

        val mill = d.putCardInHand(you, "Mill Two Iso")
        d.castSpell(you, mill)
        var guard = 0
        while (d.stackSize > 0 && guard++ < 12) d.bothPass()

        // The land was put onto the battlefield (under your control) tapped.
        d.getPermanents(you).contains(land) shouldBe true
        d.state.getEntity(land)?.has<TappedComponent>() shouldBe true

        // The nonland stayed milled in the graveyard.
        d.getGraveyard(you).contains(nonland) shouldBe true
    }

    test("milling only nonlands puts nothing onto the battlefield") {
        val d = driver()
        val you = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        d.putPermanentOnBattlefield(you, "Hedge Shredder")

        val a = d.putCardOnTopOfLibrary(you, "Grizzly Bears")
        val b = d.putCardOnTopOfLibrary(you, "Grizzly Bears")

        val mill = d.putCardInHand(you, "Mill Two Iso")
        d.castSpell(you, mill)
        var guard = 0
        while (d.stackSize > 0 && guard++ < 12) d.bothPass()

        // Both nonlands stay in the graveyard — nothing enters the battlefield.
        d.getGraveyard(you).contains(a) shouldBe true
        d.getGraveyard(you).contains(b) shouldBe true
    }
})

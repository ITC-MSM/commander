package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fdn.cards.HighFaeTrickster
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * High Fae Trickster (FDN #40) — {3}{U} Creature — Faerie Wizard, 4/2.
 *
 * "Flash. Flying. You may cast spells as though they had flash."
 *
 * The third line is a `GrantFlashToSpellType` static over every spell its controller casts
 * (`GameObjectFilter.Any`, `controllerOnly = true`). This exercises the static-ability path
 * (the engine scans battlefield permanents for the grant when checking cast timing), distinct
 * from the one-shot `GrantFlashToSpellsEffect` component.
 */
class HighFaeTricksterScenarioTest : FunSpec({

    // A plain sorcery-speed creature to try to cast at instant speed.
    val testBeast = CardDefinition.creature(
        name = "Test Beast",
        manaCost = ManaCost.parse("{1}{G}"),
        subtypes = setOf(Subtype("Beast")),
        power = 2,
        toughness = 2
    )

    fun newDriver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + testBeast)
        d.registerCard(HighFaeTrickster)
        d.initMirrorMatch(deck = Deck.of("Forest" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    test("baseline: without the grant a creature can't be cast during the end step") {
        val d = newDriver()
        val p1 = d.player1

        val beast = d.putCardInHand(p1, "Test Beast")
        d.passPriorityUntil(Step.END)
        d.giveMana(p1, Color.GREEN, 1)
        d.giveColorlessMana(p1, 1)

        val result = d.submit(CastSpell(playerId = p1, cardId = beast, paymentStrategy = PaymentStrategy.FromPool))
        result.isSuccess shouldBe false
    }

    test("High Fae Trickster lets its controller cast a creature at instant speed") {
        val d = newDriver()
        val p1 = d.player1

        d.putCreatureOnBattlefield(p1, "High Fae Trickster")
        val beast = d.putCardInHand(p1, "Test Beast")
        d.passPriorityUntil(Step.END)
        d.giveMana(p1, Color.GREEN, 1)
        d.giveColorlessMana(p1, 1)

        val result = d.submit(CastSpell(playerId = p1, cardId = beast, paymentStrategy = PaymentStrategy.FromPool))
        result.isSuccess shouldBe true
    }

    test("opponents do not benefit from the grant") {
        val d = newDriver()
        val p1 = d.player1
        val p2 = d.player2

        d.putCreatureOnBattlefield(p1, "High Fae Trickster")
        val beast = d.putCardInHand(p2, "Test Beast")
        // Still p1's turn (end step) — p2 holds priority but has no flash permission.
        d.passPriorityUntil(Step.END)
        d.giveMana(p2, Color.GREEN, 1)
        d.giveColorlessMana(p2, 1)

        val result = d.submit(CastSpell(playerId = p2, cardId = beast, paymentStrategy = PaymentStrategy.FromPool))
        result.isSuccess shouldBe false
    }
})

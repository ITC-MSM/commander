package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dom.cards.Divination
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.Rarity
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for the [com.wingedsheep.sdk.scripting.EventPattern.NthCardDrawnEvent] trigger
 * primitive (draw analogue of NthSpellCast).
 *
 * CR 121.2: cards are drawn one at a time. A single multi-card draw fires the
 * "Nth card drawn" trigger at most once — only the one draw whose ordinal equals N.
 * CR 121.5: putting a card into a player's hand without the word "draw" does not
 * count as a draw and does not advance the per-turn count.
 *
 * The test card is a vanilla creature that gains 1 life on the trigger, used as
 * a witness for whether the trigger fired and how many times.
 */
class NthCardDrawnTriggerTest : FunSpec({

    val NthDrawWitness = card("Nth Draw Witness") {
        manaCost = "{W}"
        colorIdentity = "W"
        typeLine = "Creature — Test"
        oracleText = "Whenever you draw your second card each turn, you gain 1 life."
        power = 1
        toughness = 1

        triggeredAbility {
            trigger = Triggers.NthCardDrawn(2)
            effect = Effects.GainLife(1)
        }

        metadata {
            rarity = Rarity.COMMON
            collectorNumber = "T01"
        }
    }

    val DrawThreeTest = card("Draw Three Test") {
        manaCost = "{2}{U}"
        colorIdentity = "U"
        typeLine = "Sorcery"
        oracleText = "Draw three cards."

        spell {
            effect = Effects.DrawCards(3)
        }

        metadata {
            rarity = Rarity.COMMON
            collectorNumber = "T02"
        }
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(NthDrawWitness, Divination, DrawThreeTest))
        return driver
    }

    test("triggers once when a single 'draw two cards' spell crosses the threshold") {
        // Active player on turn 1 skips the draw step (CR 103.7a), so casting
        // Divination as the first effect of the turn draws cards 1 and 2. The 2nd
        // card crosses N=2 and the trigger fires exactly once.
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 20, "Plains" to 10, "Divination" to 10))

        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.putCreatureOnBattlefield(p1, "Nth Draw Witness")
        driver.setLifeTotal(p1, 20)

        val divination = driver.putCardInHand(p1, "Divination")
        driver.giveMana(p1, Color.BLUE, 1)
        driver.giveColorlessMana(p1, 2)
        driver.castSpell(p1, divination)

        // Stack: Divination → resolves and draws 2 cards → CardsDrawnEvent (count = 2)
        // → NthCardDrawn(2) trigger fires once → goes on top of stack → resolves and gains 1 life.
        driver.bothPass()
        driver.bothPass()

        driver.getLifeTotal(p1) shouldBe 21
    }

    test("triggers when the turn-based draw of turn 2 is the second card after a cantrip") {
        // Cast a one-card-draw spell on turn 1 (skipped draw step) → count = 1, no trigger.
        // Turn 2's untap/draw step draws card #2 → trigger fires, gain 1 life.
        // We approximate the one-card-draw via Divination's split: we use the inline
        // single-card-draw via Effects.DrawCards through a custom test card.
        val driver = createDriver()

        val DrawOneTest = card("Draw One Test") {
            manaCost = "{U}"
            colorIdentity = "U"
            typeLine = "Sorcery"
            oracleText = "Draw a card."
            spell {
                effect = Effects.DrawCards(1)
            }
            metadata {
                rarity = Rarity.COMMON
                collectorNumber = "T03"
            }
        }
        driver.registerCard(DrawOneTest)

        driver.initMirrorMatch(deck = Deck.of("Island" to 20, "Plains" to 10, "Draw One Test" to 10))

        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.putCreatureOnBattlefield(p1, "Nth Draw Witness")
        driver.setLifeTotal(p1, 20)

        // Turn 1: draw 1 card via cantrip. Total drawn = 1.
        val cantrip = driver.putCardInHand(p1, "Draw One Test")
        driver.giveMana(p1, Color.BLUE, 1)
        driver.castSpell(p1, cantrip)
        driver.bothPass()
        driver.bothPass()
        driver.getLifeTotal(p1) shouldBe 20 // No trigger yet.

        // Pass to opponent's turn and back.
        driver.passPriorityUntil(Step.END)
        driver.bothPass()
        driver.passPriorityUntil(Step.END)
        driver.bothPass()

        // Turn 2 for p1 — draw step is now active; this draws card #1 of the turn
        // (CardsDrawnThisTurnComponent is reset at turn start). No trigger.
        // Then we cast cantrip → card #2 → trigger fires.
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val cantrip2 = driver.putCardInHand(p1, "Draw One Test")
        driver.giveMana(p1, Color.BLUE, 1)
        driver.castSpell(p1, cantrip2)
        driver.bothPass()
        driver.bothPass()

        driver.getLifeTotal(p1) shouldBe 21
    }

    test("does not trigger when only one card is drawn this turn") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 20, "Plains" to 10))

        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.putCreatureOnBattlefield(p1, "Nth Draw Witness")
        driver.setLifeTotal(p1, 20)

        // No additional draw spells cast; turn 1 active player skipped draw step,
        // so cards drawn this turn = 0. No trigger.
        driver.passPriorityUntil(Step.END)
        driver.bothPass()

        driver.getLifeTotal(p1) shouldBe 20
    }

    test("triggers only once when a single 'draw three cards' spell spans the threshold") {
        // CR 121.2: cards drawn one at a time. The 1st card drawn doesn't fire the
        // trigger, the 2nd does, the 3rd doesn't. So a single Draw-3 effect fires the
        // trigger exactly once, not three times.
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 20, "Plains" to 10, "Draw Three Test" to 10))

        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.putCreatureOnBattlefield(p1, "Nth Draw Witness")
        driver.setLifeTotal(p1, 20)

        val draw3 = driver.putCardInHand(p1, "Draw Three Test")
        driver.giveMana(p1, Color.BLUE, 1)
        driver.giveColorlessMana(p1, 2)
        driver.castSpell(p1, draw3)
        driver.bothPass()
        driver.bothPass()

        // Exactly one life gain, not three.
        driver.getLifeTotal(p1) shouldBe 21
    }

    test("does not trigger when an opponent draws their second card") {
        // Trigger is bound to Player.You — p2 drawing two cards should not fire
        // the trigger on p1's witness.
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 20, "Plains" to 10, "Divination" to 10))

        val p1 = driver.activePlayer!!
        val p2 = driver.getOpponent(p1)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.putCreatureOnBattlefield(p1, "Nth Draw Witness")
        driver.setLifeTotal(p1, 20)

        // End p1's turn; p2 becomes active.
        driver.passPriorityUntil(Step.END)
        driver.bothPass()
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // p2 has drawn 1 card from their turn-2 draw step; casting Divination makes
        // cards 2 and 3 of the turn. The "your second card" trigger on p1's Witness
        // does not fire because p2 is not the controller of the Witness.
        val divination = driver.putCardInHand(p2, "Divination")
        driver.giveMana(p2, Color.BLUE, 1)
        driver.giveColorlessMana(p2, 2)
        driver.castSpell(p2, divination)
        driver.bothPass()
        driver.bothPass()

        driver.getLifeTotal(p1) shouldBe 20
    }

    test("the per-turn draw count resets between turns") {
        // Turn 1: Divination → 2nd card crosses → +1 life.
        // Turn 2 for the same player: Divination again → 2nd card crosses again → another +1 life.
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 30, "Plains" to 10, "Divination" to 10))

        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.putCreatureOnBattlefield(p1, "Nth Draw Witness")
        driver.setLifeTotal(p1, 20)

        val div1 = driver.putCardInHand(p1, "Divination")
        driver.giveMana(p1, Color.BLUE, 1)
        driver.giveColorlessMana(p1, 2)
        driver.castSpell(p1, div1)
        driver.bothPass()
        driver.bothPass()
        driver.getLifeTotal(p1) shouldBe 21

        // End p1's turn → p2 turn → back to p1.
        driver.passPriorityUntil(Step.END)
        driver.bothPass()
        driver.passPriorityUntil(Step.END)
        driver.bothPass()
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // p1's turn-2 draw step already drew card #1. Casting Divination draws cards
        // #2 and #3 — the 2nd crosses, so trigger fires once more.
        val div2 = driver.putCardInHand(p1, "Divination")
        driver.giveMana(p1, Color.BLUE, 1)
        driver.giveColorlessMana(p1, 2)
        driver.castSpell(p1, div2)
        driver.bothPass()
        driver.bothPass()

        driver.getLifeTotal(p1) shouldBe 22
    }
})

package com.wingedsheep.gameserver.scenario

import com.wingedsheep.assay.compile.CardCompiler
import com.wingedsheep.assay.compile.CompileResult
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * The claim the whole sandbox rests on: a card Argentum Assay compiled out of pasted Scryfall JSON
 * is a **card**, not a picture of one — it casts, it enters, its triggered ability goes on the
 * stack and resolves, and the layer system sees its keywords.
 *
 * Written against `GameTestDriver` rather than the HTTP endpoint because that is where the claim
 * lives. The wiring around it (dev gate, session-scoped overlay, refusal of a partially-read card)
 * is [AssayCardServiceTest]; this is the half that would make all of that pointless if it failed.
 *
 * It sits in `game-server` despite driving the engine, and that is not the usual exception being
 * bent: the subject is the **compiler**, not a card's rules. `:rules-engine` may not depend on
 * `:oracle-assay` (wrong direction — the engine knows nothing about parsers) and `:oracle-assay`
 * may not depend on the engine (SDK-only, by the rule that keeps it from becoming a loader), so
 * this module is the only place the two can meet.
 */
class CustomCardPlayableTest : FunSpec({

    fun compile(oracleText: String): CardDefinition {
        val result = CardCompiler.compile(
            """
            {
              "name": "Argentum Sentinel",
              "mana_cost": "{2}{W}",
              "type_line": "Creature — Bird Soldier",
              "oracle_text": "${oracleText.replace("\n", "\\n")}",
              "power": "2",
              "toughness": "3"
            }
            """.trimIndent()
        )
        return (result as? CompileResult.Compiled)?.definition
            ?: error("expected a compile, got $result")
    }

    test("a compiled custom card casts, enters with its printed stats, and its ETB trigger resolves") {
        val card = compile("Flying, vigilance\nWhen this creature enters, draw a card.")

        val driver = GameTestDriver()
        driver.registerCards(TestCards.all) // the harness's own basics — the deck below is Plains
        driver.registerCards(listOf(card))
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)

        driver.passPriorityUntil(Phase.PRECOMBAT_MAIN) // a creature is sorcery-speed
        val player = driver.activePlayer!!
        val handBefore = driver.getHandSize(player)
        val sentinel = driver.putCardInHand(player, "Argentum Sentinel")

        driver.giveMana(player, Color.WHITE, 1)
        driver.giveColorlessMana(player, 2)
        driver.castSpell(player, sentinel).isSuccess shouldBe true
        driver.bothPass() // the creature spell resolves

        val permanent = driver.findPermanent(player, "Argentum Sentinel")!!
        // Read through projection, not the base component: that is what the rest of the engine —
        // combat, filters, the AI — actually sees when it looks at this card.
        val projected = driver.state.projectedState
        projected.getPower(permanent) shouldBe 2
        projected.getToughness(permanent) shouldBe 3
        projected.hasKeyword(permanent, Keyword.FLYING) shouldBe true
        projected.hasKeyword(permanent, Keyword.VIGILANCE) shouldBe true

        // The ETB trigger went on the stack as the creature entered; resolving it draws. The cast
        // card left hand, so a hand one larger than before it was put there is the drawn card.
        driver.bothPass()
        driver.getHandSize(player) shouldBe handBefore + 1
    }
})

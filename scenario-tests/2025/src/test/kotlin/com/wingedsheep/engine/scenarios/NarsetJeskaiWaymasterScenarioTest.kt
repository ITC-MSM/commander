package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.tdm.cards.NarsetJeskaiWaymaster
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for Narset, Jeskai Waymaster (TDM #209).
 *
 *   At the beginning of your end step, you may discard your hand. If you do, draw cards equal to
 *   the number of spells you've cast this turn.
 *
 * Exercises the `MayEffect(IfYouDoEffect(discardHand, DrawCards(SpellsCastThisTurn)))` shape: the
 * draw count equals the number of spells cast that turn.
 */
class NarsetJeskaiWaymasterScenarioTest : FunSpec({

    val bolt = card("Test Bolt") {
        manaCost = "{R}"
        typeLine = "Instant"
        spell {
            effect = Effects.DealDamage(1, EffectTarget.PlayerRef(Player.EachOpponent))
        }
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(NarsetJeskaiWaymaster, bolt))
        return driver
    }

    fun GameTestDriver.resolveStack() {
        var guard = 0
        while (state.stack.isNotEmpty() && guard < 50) {
            if (state.pendingDecision != null) autoResolveDecision() else bothPass()
            guard++
        }
    }

    test("discarding hand draws cards equal to spells cast this turn") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val controller = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(controller, "Narset, Jeskai Waymaster")

        // Cast two spells this turn.
        repeat(2) {
            driver.giveMana(controller, Color.RED, 1)
            val boltCard = driver.putCardInHand(controller, "Test Bolt")
            driver.submit(CastSpell(playerId = controller, cardId = boltCard))
            driver.resolveStack()
        }

        driver.passPriorityUntil(Step.END)

        // Narset's end-step "may" triggers; answer yes. The whole hand is discarded, then we draw
        // cards equal to the number of spells cast this turn (2), so the hand ends at exactly 2.
        var guard = 0
        while (driver.state.pendingDecision == null && guard < 20) { driver.bothPass(); guard++ }
        driver.submitYesNo(controller, true)
        driver.resolveStack()

        driver.getHand(controller).size shouldBe 2
    }

    test("empty hand can still be discarded — the draw fires (ruling 2025-04-04)") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val controller = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(controller, "Narset, Jeskai Waymaster")

        // Cast one spell this turn, then empty the hand entirely.
        driver.giveMana(controller, Color.RED, 1)
        val boltCard = driver.putCardInHand(controller, "Test Bolt")
        driver.submit(CastSpell(playerId = controller, cardId = boltCard))
        driver.resolveStack()
        driver.getHand(controller).forEach { driver.moveToGraveyard(it) }
        driver.getHand(controller).size shouldBe 0

        driver.passPriorityUntil(Step.END)
        var guard = 0
        while (driver.state.pendingDecision == null && guard < 20) { driver.bothPass(); guard++ }
        driver.submitYesNo(controller, true)
        driver.resolveStack()

        // Discarding zero cards still counts as "if you do" — draw one (one spell cast).
        driver.getHand(controller).size shouldBe 1
    }

    test("declining the end-step ability keeps the hand and draws nothing") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val controller = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(controller, "Narset, Jeskai Waymaster")
        repeat(2) { driver.putCardInHand(controller, "Mountain") }
        val handBefore = driver.getHand(controller).size

        driver.passPriorityUntil(Step.END)
        var guard = 0
        while (driver.state.pendingDecision == null && guard < 20) { driver.bothPass(); guard++ }
        driver.submitYesNo(controller, false)
        driver.resolveStack()

        driver.getHand(controller).size shouldBe handBefore
    }
})

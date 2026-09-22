package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fra.cards.DenziloreFatehold
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class DenziloreFateholdScenarioTest : FunSpec({
    listOf(false, true).forEach { surveil ->
        listOf(false, true).forEach { opponent ->
            test("${if (opponent) "opponent" else "controller"} ${if (surveil) "surveil" else "scry"} triggers only for its controller and once for the whole action") {
                val lookSpell = card("Look at Three") {
                    manaCost = "{U}"
                    typeLine = "Instant"
                    spell { effect = if (surveil) Patterns.Library.surveil(3) else Patterns.Library.scry(3) }
                }
                val driver = GameTestDriver()
                driver.registerCards(TestCards.all)
                driver.registerCard(DenziloreFatehold)
                driver.registerCard(lookSpell)
                driver.initMirrorMatch(deck = Deck.of("Island" to 30))
                driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
                val active = driver.activePlayer!!
                val other = if (active == driver.player1) driver.player2 else driver.player1
                val denzilore = driver.putCreatureOnBattlefield(active, "Denzilore Fatehold")
                val ally = driver.putCreatureOnBattlefield(active, "Grizzly Bears")
                val enemy = driver.putCreatureOnBattlefield(other, "Grizzly Bears")
                val caster = if (opponent) other else active
                val spell = driver.putCardInHand(caster, "Look at Three")
                driver.giveMana(caster, Color.BLUE, 1)
                if (opponent) driver.passPriority(active)
                driver.castSpell(caster, spell).error shouldBe null
                var guard = 0
                while ((driver.stackSize > 0 || driver.state.pendingDecision != null) && guard++ < 40) {
                    if (driver.state.pendingDecision != null) driver.autoResolveDecision()
                    else driver.passPriority(driver.state.priorityPlayerId ?: active)
                }
                driver.stackSize shouldBe 0
                driver.state.pendingDecision shouldBe null
                val bonus = if (opponent) 0 else 1
                driver.state.projectedState.getPower(denzilore) shouldBe 3 + bonus
                driver.state.projectedState.getToughness(denzilore) shouldBe 4 + bonus
                driver.state.projectedState.getPower(ally) shouldBe 2 + bonus
                driver.state.projectedState.getPower(enemy) shouldBe 2
            }
        }
    }
})

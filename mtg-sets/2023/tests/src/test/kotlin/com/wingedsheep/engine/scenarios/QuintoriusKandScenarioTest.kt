package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.QuintoriusKand
import com.wingedsheep.mtg.sets.tokens.PredefinedTokens
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.effects.DiscoverEffect
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Quintorius Kand {3}{R}{W} — proves the signature loop: −3 discovers, and casting the discovered
 * card *from exile* triggers "Whenever you cast a spell from exile, ~ deals 2 damage to each
 * opponent and you gain 2 life." So −3 Discover 4 + a free cast should drain the opponent for 2 and
 * gain 2 life.
 */
class QuintoriusKandScenarioTest : FunSpec({

    // A mana-value-1 creature that is a legal Discover 4 hit and casts with no targets.
    val imp = card("Test Imp") {
        manaCost = "{R}"
        typeLine = "Creature — Imp"
        power = 1
        toughness = 1
    }

    test("−3 Discover, casting the discovered card from exile, triggers the drain") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCards(PredefinedTokens.allTokens)
        driver.registerCard(imp)
        driver.registerCard(QuintoriusKand)
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)

        val quintorius = driver.putPermanentOnBattlefield(me, "Quintorius Kand")
        // The battlefield helper doesn't seed loyalty counters; give Quintorius its starting loyalty.
        driver.replaceState(
            driver.state.updateEntity(quintorius) {
                it.with(
                    com.wingedsheep.engine.state.components.battlefield.CountersComponent(
                        mapOf(com.wingedsheep.sdk.core.CounterType.LOYALTY to 4)
                    )
                )
            }
        )
        driver.putCardOnTopOfLibrary(me, "Test Imp") // mana value 1 → discovered by Discover 4

        val minus3 = QuintoriusKand.script.activatedAbilities.first { it.effect is DiscoverEffect }
        driver.submit(ActivateAbility(me, quintorius, minus3.id)).isSuccess shouldBe true
        driver.bothPass() // −3 resolves → Discover 4 → cast-or-hand pause

        driver.submitYesNo(me, choice = true) // cast Test Imp for free, from exile

        // Resolve the cast-from-exile trigger + the Imp spell.
        repeat(6) { if (driver.stackSize > 0) driver.bothPass() }

        driver.getLifeTotal(opp) shouldBe 18 // 2 damage from the drain trigger
        driver.getLifeTotal(me) shouldBe 22  // 2 life gained
    }
})

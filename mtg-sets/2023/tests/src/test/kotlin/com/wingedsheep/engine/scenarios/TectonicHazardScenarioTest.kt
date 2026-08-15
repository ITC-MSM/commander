package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.TectonicHazard
import com.wingedsheep.mtg.sets.tokens.PredefinedTokens
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tectonic Hazard (LCI #169) — {R} Sorcery.
 *
 * "Tectonic Hazard deals 1 damage to each opponent and each creature they control."
 *
 * Two damage sub-effects composed: 1 damage to each opponent (the player) and 1 damage
 * to each creature that opponent controls. The caster and the caster's own creatures are
 * untouched.
 */
class TectonicHazardScenarioTest : FunSpec({

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCards(PredefinedTokens.allTokens)
        driver.registerCard(TectonicHazard)
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    test("deals 1 to the opponent and 1 to each of their creatures, sparing the caster's side") {
        val driver = newDriver()
        val me = driver.player1
        val opp = driver.player2

        // Opponent's board: a 2/2 (survives 1 damage) and a 1/1 (dies to 1 damage).
        driver.putCreatureOnBattlefield(opp, "Grizzly Bears")     // 2/2
        driver.putCreatureOnBattlefield(opp, "Llanowar Elves")    // 1/1
        // Caster's own creatures must be untouched.
        val myBears = driver.putCreatureOnBattlefield(me, "Grizzly Bears")   // 2/2

        val hazard = driver.putCardInHand(me, "Tectonic Hazard")
        driver.giveMana(me, Color.RED, 1)
        driver.castSpell(me, hazard).isSuccess shouldBe true
        driver.bothPass()

        // Opponent (the only opponent) took 1 damage; caster untouched.
        driver.getLifeTotal(opp) shouldBe 19
        driver.getLifeTotal(me) shouldBe 20

        // The opponent's 1/1 died; their 2/2 and the caster's 2/2 survived.
        driver.findPermanent(opp, "Llanowar Elves") shouldBe null
        (driver.findPermanent(opp, "Grizzly Bears") != null) shouldBe true
        (myBears == driver.findPermanent(me, "Grizzly Bears")) shouldBe true
        driver.getCreatures(me).size shouldBe 1
        driver.getCreatures(opp).size shouldBe 1
    }
})

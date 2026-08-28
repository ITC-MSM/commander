package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.DamageComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.DoranTheSiegeTower
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Doran, the Siege Tower (LRW #247) — {W}{B}{G} Legendary Creature — Treefolk Shaman 0/5
 *
 *   Each creature assigns combat damage equal to its toughness rather than its power.
 *
 * The static is unconditional and scoped to *every* creature, so the claims worth pinning are:
 * it turns Doran's own 0 power into 5 damage, it *lowers* a 2/1's damage to 1 rather than only
 * ever raising it, and it reaches creatures an opponent controls too.
 */
class DoranTheSiegeTowerScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + DoranTheSiegeTower)
        return driver
    }

    test("Doran itself assigns 5 combat damage despite 0 power") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40))
        val p1 = driver.activePlayer!!
        val p2 = driver.getOpponent(p1)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val doran = driver.putCreatureOnBattlefield(p1, "Doran, the Siege Tower")
        driver.removeSummoningSickness(doran)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(p1, listOf(doran), p2)
        driver.declareNoBlockers(p2)
        driver.passPriorityUntil(Step.END_COMBAT)

        driver.getLifeTotal(p2) shouldBe 15
    }

    test("a 2/1 assigns 1 instead of 2 — the effect lowers damage as readily as it raises it") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))
        val p1 = driver.activePlayer!!
        val p2 = driver.getOpponent(p1)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val guide = driver.putCreatureOnBattlefield(p1, "Goblin Guide")
        driver.removeSummoningSickness(guide)
        driver.putCreatureOnBattlefield(p1, "Doran, the Siege Tower")

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(p1, listOf(guide), p2)
        driver.declareNoBlockers(p2)
        driver.passPriorityUntil(Step.END_COMBAT)

        withClue("Goblin Guide is 2/1, so with Doran out it deals its toughness: 1") {
            driver.getLifeTotal(p2) shouldBe 19
        }
    }

    test("without Doran the same 2/1 deals its printed power") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))
        val p1 = driver.activePlayer!!
        val p2 = driver.getOpponent(p1)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val guide = driver.putCreatureOnBattlefield(p1, "Goblin Guide")
        driver.removeSummoningSickness(guide)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(p1, listOf(guide), p2)
        driver.declareNoBlockers(p2)
        driver.passPriorityUntil(Step.END_COMBAT)

        driver.getLifeTotal(p2) shouldBe 18
    }

    test("the effect reaches creatures an opponent controls") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40))
        val p1 = driver.activePlayer!!
        val p2 = driver.getOpponent(p1)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Doran belongs to the active player; the 2/1 blocker belongs to the opponent, and it is
        // *its* damage that has to be re-read off its toughness. A 2/2 attacker survives 1 damage
        // and dies to 2, so the blocker's controller is exactly what the assertion measures.
        driver.putCreatureOnBattlefield(p1, "Doran, the Siege Tower")
        val frogmite = driver.putCreatureOnBattlefield(p1, "Frogmite")
        driver.removeSummoningSickness(frogmite)
        val guide = driver.putCreatureOnBattlefield(p2, "Goblin Guide")

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(p1, listOf(frogmite), p2)
        driver.bothPass()
        driver.declareBlockers(p2, mapOf(guide to listOf(frogmite)))
        driver.bothPass() // end of declare blockers
        driver.bothPass() // first strike damage (nobody has it)
        driver.bothPass() // combat damage

        withClue("the opponent's 2/1 assigned its toughness — 1, not its printed 2") {
            driver.state.getEntity(frogmite)?.get<DamageComponent>()?.amount shouldBe 1
        }
        withClue("the 2/2 attacker assigned its toughness (2), killing the 2/1 blocker") {
            driver.getCreatures(p2).contains(guide) shouldBe false
        }
    }
})

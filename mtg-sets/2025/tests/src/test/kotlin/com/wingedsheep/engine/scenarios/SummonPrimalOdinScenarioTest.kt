package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.SummonPrimalOdin
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Summon: Primal Odin — {4}{B}{B} Enchantment Creature — Saga Knight, 5/3 (FIN).
 *
 *   I — Gungnir — Destroy target creature an opponent controls.
 *   II — Zantetsuken — This creature gains "Whenever this creature deals combat damage to a
 *        player, that player loses the game."
 *   III — Hall of Sorrow — Draw two cards. Each player loses 2 life.
 *
 * Generic saga machinery is covered by CreatureSagaTest; this pins chapter I's targeted destroy and
 * chapter III's draw-two / drain-each-player.
 */
class SummonPrimalOdinScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(SummonPrimalOdin))
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.castOdin(me: EntityId): EntityId {
        val spell = putCardInHand(me, "Summon: Primal Odin")
        giveColorlessMana(me, 4)
        giveMana(me, com.wingedsheep.sdk.core.Color.BLACK, 2)
        castSpell(me, spell)
        return spell
    }

    /** Resolve the whole stack, supplying [chooseTargets] for any target prompt. */
    fun GameTestDriver.resolveStack(chooseTargets: () -> List<EntityId> = { emptyList() }) {
        var guard = 0
        while ((state.stack.isNotEmpty() || state.pendingDecision != null) && guard++ < 100) {
            val pd = state.pendingDecision
            when {
                pd is ChooseTargetsDecision -> submitTargetSelection(pd.playerId, chooseTargets())
                pd != null -> autoResolveDecision()
                else -> bothPass()
            }
        }
    }

    test("chapter I destroys the targeted opponent creature") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)

        val courser = driver.putCreatureOnBattlefield(opp, "Centaur Courser") // 3/3

        driver.castOdin(me)
        driver.resolveStack(chooseTargets = { listOf(courser) })

        driver.findPermanent(opp, "Centaur Courser") shouldBe null
        // The Saga itself is still on the battlefield at lore I of III.
        (driver.findPermanent(me, "Summon: Primal Odin") != null) shouldBe true
    }

    test("chapter III draws two and drains each player for two") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)

        // No opponent creatures, so chapter I has no legal target and simply does nothing —
        // advancing never pauses on a mandatory target it cannot satisfy.
        driver.castOdin(me)
        driver.resolveStack()

        val saga = driver.findPermanent(me, "Summon: Primal Odin")!!

        // Advance turns until chapter III has resolved (both players drained to 18).
        var guard = 0
        while (guard++ < 1000) {
            if (driver.getLifeTotal(me) <= 18 && driver.getLifeTotal(opp) <= 18) break
            if (driver.state.gameOver) throw AssertionError("Game ended before chapter III resolved")
            val pd = driver.state.pendingDecision
            when {
                pd is ChooseTargetsDecision -> driver.submitTargetSelection(pd.playerId, emptyList())
                pd != null -> driver.autoResolveDecision()
                driver.state.priorityPlayerId != null -> {
                    driver.autoSubmitCombatDeclarationIfNeeded()
                    driver.passPriority(driver.state.priorityPlayerId!!)
                }
            }
        }

        driver.getLifeTotal(me) shouldBe 18
        driver.getLifeTotal(opp) shouldBe 18
        // After its final chapter, the Saga is sacrificed.
        driver.findPermanent(me, "Summon: Primal Odin") shouldBe null
    }
})

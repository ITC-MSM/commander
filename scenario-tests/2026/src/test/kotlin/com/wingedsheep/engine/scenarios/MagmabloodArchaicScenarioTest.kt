package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.sos.cards.MagmabloodArchaic
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Magmablood Archaic (Secrets of Strixhaven #123).
 *
 * Magmablood Archaic ({2/R}{2/R}{2/R}, 2/2 Avatar):
 *   Trample, reach
 *   Converge — This creature enters with a +1/+1 counter on it for each color of mana spent to cast it.
 *   Whenever you cast an instant or sorcery spell, creatures you control get +1/+0 until end of turn
 *   for each color of mana spent to cast that spell.
 *
 * The Converge enters-with-counters part reuses `DistinctColorsManaSpent` (covered for the family by
 * ConvergeMechanicTest); this file also pins the *spell-cast pump*, which scales by the new
 * `ContextPropertyKey.COLORS_SPENT_ON_TRIGGERING_SPELL` — the colours spent on the **triggering**
 * spell, not on the Archaic's own cast.
 */
class MagmabloodArchaicScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCards(listOf(MagmabloodArchaic))
        return driver
    }

    fun startTurn(driver: GameTestDriver): EntityId {
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver.activePlayer!!
    }

    fun plusCounters(driver: GameTestDriver, id: EntityId): Int =
        driver.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    fun projectedPower(driver: GameTestDriver, id: EntityId): Int =
        StateProjector().getProjectedPower(driver.state, id)

    // ── Converge enters-with-counters (the colors of the Archaic's OWN cast) ──

    test("enters with a +1/+1 counter for each color of mana spent to cast it (2 colors → 4/4)") {
        val driver = createDriver()
        val p = startTurn(driver)
        val archaic = driver.putCardInHand(p, "Magmablood Archaic")
        // {2/R}{2/R}{2/R}: two pips paid {R} (red), the third's {2} generic side paid with two
        // green → mana spent is R, R, G, G → 2 distinct colors.
        driver.giveMana(p, Color.RED, 2)
        driver.giveMana(p, Color.GREEN, 2)

        driver.castSpell(p, archaic).isSuccess shouldBe true
        driver.bothPass()

        plusCounters(driver, archaic) shouldBe 2
    }

    // ── Spell-cast pump scales by the colors spent on the TRIGGERING spell ──

    test("a 1-color spell pumps creatures you control +1/+0") {
        val driver = createDriver()
        val p = startTurn(driver)
        val archaic = driver.putCreatureOnBattlefield(p, "Magmablood Archaic") // 2/2
        val lion = driver.putCreatureOnBattlefield(p, "Savannah Lions") // 1/1
        val opp = driver.getOpponent(p)
        val victim = driver.putCreatureOnBattlefield(opp, "Centaur Courser")

        val bolt = driver.putCardInHand(p, "Lightning Bolt") // {R} → 1 color
        driver.giveMana(p, Color.RED, 1)

        driver.castSpell(p, bolt, targets = listOf(victim)).isSuccess shouldBe true
        driver.bothPass() // bolt resolves
        driver.bothPass() // Magmablood cast-trigger resolves

        // 1 color spent → +1/+0. Lion 1/1 → 2/1; Magmablood 2/2 → 3/2.
        projectedPower(driver, lion) shouldBe 2
        projectedPower(driver, archaic) shouldBe 3
    }

    test("a 2-color spell pumps creatures you control +2/+0") {
        val driver = createDriver()
        val p = startTurn(driver)
        val archaic = driver.putCreatureOnBattlefield(p, "Magmablood Archaic") // 2/2
        val lion = driver.putCreatureOnBattlefield(p, "Savannah Lions") // 1/1
        val opp = driver.getOpponent(p)
        val victim = driver.putCreatureOnBattlefield(opp, "Centaur Courser") // nonblack target

        // Doom Blade {1}{B}: pay {B} with black, the {1} generic with red → 2 distinct colors.
        val doomBlade = driver.putCardInHand(p, "Doom Blade")
        driver.giveMana(p, Color.BLACK, 1)
        driver.giveMana(p, Color.RED, 1)

        driver.castSpell(p, doomBlade, targets = listOf(victim)).isSuccess shouldBe true
        driver.bothPass() // Doom Blade resolves
        driver.bothPass() // Magmablood cast-trigger resolves

        // 2 colors spent → +2/+0. Lion 1/1 → 3/1; Magmablood 2/2 → 4/2.
        projectedPower(driver, lion) shouldBe 3
        projectedPower(driver, archaic) shouldBe 4
    }
})

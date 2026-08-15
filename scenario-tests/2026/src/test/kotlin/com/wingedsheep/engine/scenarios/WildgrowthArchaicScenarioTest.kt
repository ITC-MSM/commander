package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.sos.cards.WildgrowthArchaic
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Wildgrowth Archaic ({2/G}{2/G}, 0/0 Avatar, Trample/Reach):
 *   Converge — This creature enters with a +1/+1 counter for each color of mana spent to cast it.
 *   Whenever you cast a creature spell, that creature enters with X additional +1/+1 counters on it,
 *   where X is the number of colors of mana spent to cast it.
 *
 * Pins both `EntersWithDynamicCounters` halves, plus the engine fix that lets the *global* (other-
 * creatures) enters-with path read `DistinctColorsManaSpent` off the **entering** creature's cast
 * rather than off Wildgrowth's own cast:
 *  - Wildgrowth's own converge counters (self path).
 *  - A second creature cast with N distinct colors → N extra +1/+1 counters (the count is about the
 *    entering creature, NOT about Wildgrowth's payment).
 *  - All-colourless cast of the second creature → 0 colours → no extra counters.
 *  - A creature that enters WITHOUT being cast (e.g. put onto the battlefield) → 0 mana spent → 0
 *    colours → no extra counters, matching the "whenever you **cast**" wording.
 */
class WildgrowthArchaicScenarioTest : FunSpec({

    // A plain {3} colourless vanilla creature so the test fully controls how many distinct colors
    // are spent on its cast.
    val genericBear = CardDefinition.creature(
        name = "Generic Test Bear",
        manaCost = ManaCost.parse("{3}"),
        subtypes = setOf(Subtype("Bear")),
        power = 2,
        toughness = 2,
    )

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCards(listOf(WildgrowthArchaic, genericBear))
        return driver
    }

    fun startTurn(driver: GameTestDriver): EntityId {
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver.activePlayer!!
    }

    fun plusCounters(driver: GameTestDriver, id: EntityId): Int =
        driver.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    /**
     * Cast Wildgrowth Archaic and resolve it onto the battlefield, paying its {2/G}{2/G} with the
     * supplied mana (give EXACTLY what should be spent — the auto-payer otherwise minimizes colors).
     */
    fun resolveWildgrowth(driver: GameTestDriver, p: EntityId, mana: Map<Color, Int> = mapOf(Color.GREEN to 2)): EntityId {
        val wild = driver.putCardInHand(p, "Wildgrowth Archaic")
        mana.forEach { (color, amount) -> driver.giveMana(p, color, amount) }
        driver.castSpell(p, wild).isSuccess shouldBe true
        driver.bothPass()
        return wild
    }

    test("Wildgrowth's own converge: 2 colors spent → enters as a 2/2 (two +1/+1 counters)") {
        val driver = createDriver()
        val p = startTurn(driver)
        // {2/G}{2/G}: one pip paid {G} (green), the other pip's {2} paid with two red → 2 colors.
        // Give exactly that pool so the solver can't collapse it to a single color.
        val wild = resolveWildgrowth(driver, p, mapOf(Color.GREEN to 1, Color.RED to 2))
        plusCounters(driver, wild) shouldBe 2
    }

    test("another creature cast with 3 colors enters with 3 extra +1/+1 counters") {
        val driver = createDriver()
        val p = startTurn(driver)
        resolveWildgrowth(driver, p) // Wildgrowth itself only matters as the source

        val bear = driver.putCardInHand(p, "Generic Test Bear")
        // {3} paid with three distinct colors → X = 3 for THIS creature's cast.
        driver.giveMana(p, Color.WHITE, 1)
        driver.giveMana(p, Color.BLUE, 1)
        driver.giveMana(p, Color.BLACK, 1)
        driver.castSpell(p, bear).isSuccess shouldBe true
        driver.bothPass()

        // Base 2/2, no own enters-with counters → exactly the 3 granted by Wildgrowth.
        plusCounters(driver, bear) shouldBe 3
    }

    test("another creature cast with all-colorless mana → 0 colors → no extra counters") {
        val driver = createDriver()
        val p = startTurn(driver)
        resolveWildgrowth(driver, p)

        val bear = driver.putCardInHand(p, "Generic Test Bear")
        driver.giveColorlessMana(p, 3) // 0 distinct colors
        driver.castSpell(p, bear).isSuccess shouldBe true
        driver.bothPass()

        plusCounters(driver, bear) shouldBe 0
    }

    test("a creature that enters without being cast gets no extra counters (whenever you CAST)") {
        val driver = createDriver()
        val p = startTurn(driver)
        resolveWildgrowth(driver, p)

        // Put a creature directly onto the battlefield (no cast → no mana spent → 0 colors).
        val bear = driver.putCreatureOnBattlefield(p, "Generic Test Bear")
        plusCounters(driver, bear) shouldBe 0
    }
})

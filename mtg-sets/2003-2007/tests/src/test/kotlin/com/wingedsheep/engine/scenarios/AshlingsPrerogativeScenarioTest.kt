package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CastChoicesComponent
import com.wingedsheep.engine.state.components.battlefield.ChoiceValue
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.AshlingsPrerogative
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.ChoiceSlot
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Ashling's Prerogative (LRW #150).
 *
 * "As this enchantment enters, choose odd or even. Each creature with mana value of the chosen
 *  quality has haste. Each creature without mana value of the chosen quality enters tapped."
 *
 * The card is two mirrored pairs of effects behind one mode choice, and *every* wrong wiring of it
 * still compiles and still reads right on the card: swapping the two parities, or gating the tap
 * clause on the wrong mode, produces an enchantment that does exactly the opposite thing. So each
 * assertion below is run on **both** modes with the **same** two creatures — the only shape where
 * an inverted wiring disagrees with a correct one.
 *
 * The tap half is also the reason this card carries a test at all: it needed a `condition` gate on
 * [com.wingedsheep.sdk.scripting.PermanentsEnterTapped], because a replacement stamped into the
 * source's replacement component and consulted from the battlefield can't be wrapped in a
 * `ConditionalStaticAbility` the way the haste halves are. An ungated version would tap creatures
 * of *both* parities.
 *
 * Creatures are **cast**, never placed: `putCreatureOnBattlefield` inserts a permanent directly
 * and never consults an entry replacement, so it would report every creature untapped regardless
 * of what the enchantment says.
 */
class AshlingsPrerogativeScenarioTest : FunSpec({

    /** Centaur Courser is {2}{G} — mana value 3, odd. Forest Walker is {1}{G} — mana value 2, even. */
    val oddCreature = "Centaur Courser"
    val evenCreature = "Forest Walker"

    fun setUp(mode: String): Triple<GameTestDriver, EntityId, EntityId> {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(AshlingsPrerogative))
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val prerogative = driver.putPermanentOnBattlefield(player, "Ashling's Prerogative")
        driver.addComponent(
            prerogative,
            CastChoicesComponent(chosen = mapOf(ChoiceSlot.MODE to ChoiceValue.TextChoice(mode)))
        )
        return Triple(driver, player, prerogative)
    }

    /** Cast [cardName] from hand and let it resolve onto the battlefield. */
    fun castCreature(driver: GameTestDriver, player: EntityId, cardName: String): EntityId {
        val card = driver.putCardInHand(player, cardName)
        driver.giveMana(player, Color.GREEN, 5)
        driver.castSpell(player, card)
        driver.bothPass()
        return driver.findPermanent(player, cardName)!!
    }

    test("odd chosen: odd-cost creatures enter untapped with haste, even-cost creatures enter tapped") {
        val (driver, player, _) = setUp("odd")

        val odd = castCreature(driver, player, oddCreature)
        driver.isTapped(odd) shouldBe false
        driver.state.projectedState.hasKeyword(odd, Keyword.HASTE) shouldBe true

        val even = castCreature(driver, player, evenCreature)
        driver.isTapped(even) shouldBe true
        driver.state.projectedState.hasKeyword(even, Keyword.HASTE) shouldBe false
    }

    test("even chosen: the same two creatures swap roles") {
        val (driver, player, _) = setUp("even")

        val even = castCreature(driver, player, evenCreature)
        driver.isTapped(even) shouldBe false
        driver.state.projectedState.hasKeyword(even, Keyword.HASTE) shouldBe true

        val odd = castCreature(driver, player, oddCreature)
        driver.isTapped(odd) shouldBe true
        driver.state.projectedState.hasKeyword(odd, Keyword.HASTE) shouldBe false
    }

    test("haste is granted to every creature of the chosen parity, not just the controller's") {
        val (driver, player, _) = setUp("odd")
        val opponent = driver.getOpponent(player)

        // Already on the battlefield under the opponent's control: "each creature" is unqualified,
        // so `youControl()` on the grant filter would silently drop this one.
        val theirs = driver.putCreatureOnBattlefield(opponent, oddCreature)
        driver.state.projectedState.hasKeyword(theirs, Keyword.HASTE) shouldBe true
    }
})

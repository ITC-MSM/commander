package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.big.cards.PestControl
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Tests for Pest Control.
 *
 * Pest Control: {W}{B}
 * Sorcery
 * Destroy all nonland permanents with mana value 1 or less.
 * Cycling {2}
 *
 * Verifies the filtered board wipe: only nonland permanents whose mana value is 1 or
 * less are destroyed. Higher-mana-value permanents and lands survive.
 */
class PestControlScenarioTest : FunSpec({

    // MV 1 — should be destroyed
    val TinyBird = CardDefinition.creature(
        name = "Tiny Bird",
        manaCost = ManaCost.parse("{W}"),
        subtypes = setOf(Subtype("Bird")),
        power = 1,
        toughness = 1
    )

    // MV 0 — should be destroyed
    val FreeBlob = CardDefinition.creature(
        name = "Free Blob",
        manaCost = ManaCost.parse("{0}"),
        subtypes = setOf(Subtype("Ooze")),
        power = 0,
        toughness = 1
    )

    // MV 3 — should survive
    val BigBeast = CardDefinition.creature(
        name = "Big Beast",
        manaCost = ManaCost.parse("{2}{G}"),
        subtypes = setOf(Subtype("Beast")),
        power = 3,
        toughness = 3
    )

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(
            TestCards.all + listOf(PestControl, TinyBird, FreeBlob, BigBeast)
        )
        return driver
    }

    test("Pest Control destroys nonland permanents with mana value 1 or less and spares the rest") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Plains" to 20, "Swamp" to 20),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // MV 1 and MV 0 nonland permanents — destroyed.
        driver.putCreatureOnBattlefield(activePlayer, "Tiny Bird")
        driver.putCreatureOnBattlefield(opponent, "Free Blob")
        // MV 3 nonland permanent — survives.
        driver.putCreatureOnBattlefield(opponent, "Big Beast")
        // Land — never matches "nonland", survives.
        val survivingLand = driver.putLandOnBattlefield(opponent, "Plains")

        val pestControl = driver.putCardInHand(activePlayer, "Pest Control")
        driver.giveMana(activePlayer, Color.WHITE, 1)
        driver.giveMana(activePlayer, Color.BLACK, 1)

        val result = driver.castSpell(activePlayer, pestControl)
        result.isSuccess shouldBe true

        driver.bothPass()

        // MV <= 1 nonland permanents destroyed.
        driver.findPermanent(activePlayer, "Tiny Bird") shouldBe null
        driver.findPermanent(opponent, "Free Blob") shouldBe null

        // MV 3 creature survives.
        driver.findPermanent(opponent, "Big Beast") shouldNotBe null

        // Land survives (it's a land, regardless of mana value).
        driver.findPermanent(opponent, "Plains") shouldNotBe null
        survivingLand shouldNotBe null
    }
})

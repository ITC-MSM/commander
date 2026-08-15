package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dom.cards.SporecrownThallid
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for Sporecrown Thallid — "Each other creature you control that's a Fungus or Saproling gets +1/+1."
 */
class SporecrownThallidTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(SporecrownThallid)
        driver.registerCard(
            CardDefinition.creature(
                name = "Thallid",
                manaCost = ManaCost.parse("{G}"),
                subtypes = setOf(Subtype("Fungus")),
                power = 1,
                toughness = 1
            )
        )
        driver.registerCard(
            CardDefinition.creature(
                name = "Saproling Warrior",
                manaCost = ManaCost.parse("{1}{G}"),
                subtypes = setOf(Subtype("Saproling")),
                power = 2,
                toughness = 2
            )
        )
        return driver
    }

    test("Sporecrown Thallid buffs other Fungus creatures you control") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Forest" to 40),
            skipMulligans = true
        )

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val thallid = driver.putPermanentOnBattlefield(activePlayer, "Thallid")
        val sporecrown = driver.putPermanentOnBattlefield(activePlayer, "Sporecrown Thallid")

        // Thallid (Fungus) should get +1/+1
        projector.getProjectedPower(driver.state, thallid) shouldBe 2
        projector.getProjectedToughness(driver.state, thallid) shouldBe 2

        // Sporecrown itself should NOT be buffed (excludeSelf)
        projector.getProjectedPower(driver.state, sporecrown) shouldBe 2
        projector.getProjectedToughness(driver.state, sporecrown) shouldBe 2
    }

    test("Sporecrown Thallid buffs other Saproling creatures you control") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Forest" to 40),
            skipMulligans = true
        )

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val saproling = driver.putPermanentOnBattlefield(activePlayer, "Saproling Warrior")
        driver.putPermanentOnBattlefield(activePlayer, "Sporecrown Thallid")

        // Saproling should get +1/+1
        projector.getProjectedPower(driver.state, saproling) shouldBe 3
        projector.getProjectedToughness(driver.state, saproling) shouldBe 3
    }

    test("Sporecrown Thallid does NOT buff non-Fungus non-Saproling creatures") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Forest" to 40),
            skipMulligans = true
        )

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val bears = driver.putCreatureOnBattlefield(activePlayer, "Grizzly Bears")
        driver.putPermanentOnBattlefield(activePlayer, "Sporecrown Thallid")

        // Grizzly Bears (not Fungus or Saproling) should NOT be buffed
        projector.getProjectedPower(driver.state, bears) shouldBe 2
        projector.getProjectedToughness(driver.state, bears) shouldBe 2
    }
})

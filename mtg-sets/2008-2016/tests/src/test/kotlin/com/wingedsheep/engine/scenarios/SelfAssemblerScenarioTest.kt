package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.kld.cards.SelfAssembler
import com.wingedsheep.mtg.sets.definitions.mh2.cards.ArcboundPrototype
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Self-Assembler (KLD #232) — {5} Artifact Creature — Assembly-Worker, 4/4.
 *
 * "When this creature enters, you may search your library for an Assembly-Worker creature card,
 *  reveal it, put it into your hand, then shuffle."
 *
 * The search filters on the *subtype*, which only works if `Assembly-Worker` survives type-line
 * parsing intact — the hyphen used to be read as the type/subtype separator, leaving every printed
 * Assembly-Worker in the corpus with the subtype `Assembly`, so this ability could never find
 * anything. The negative case pins the filter to the subtype rather than the artifact-creature
 * type: a Golem artifact creature is not offered.
 */
class SelfAssemblerScenarioTest : FunSpec({

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(SelfAssembler, ArcboundPrototype))
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    /**
     * Cast Self-Assembler, accept its optional search, and take [pick] when the library offers it.
     * Returns the names the search actually offered, so a test can assert on what was *not* findable.
     */
    fun GameTestDriver.castSelfAssembler(player: EntityId, pick: String? = null): List<String> {
        val card = putCardInHand(player, "Self-Assembler")
        giveMana(player, Color.WHITE, 5)
        castSpell(player, card).isSuccess shouldBe true

        val offered = mutableListOf<String>()
        var guard = 0
        while ((stackSize > 0 || isPaused) && guard++ < 40) {
            when (val decision = pendingDecision) {
                is YesNoDecision -> submitYesNo(player, true)
                is SelectCardsDecision -> {
                    offered += decision.options.mapNotNull { getCardName(it) }
                    val chosen = decision.options.firstOrNull { getCardName(it) == pick }
                    if (chosen != null) submitCardSelection(player, listOf(chosen))
                    else submitCardSelection(player, emptyList())
                }
                null -> bothPass()
                else -> autoResolveDecision()
            }
        }
        return offered
    }

    test("the enters trigger finds an Assembly-Worker in the library and puts it into hand") {
        val driver = newDriver()
        val me = driver.activePlayer!!
        driver.putCardOnTopOfLibrary(me, "Arcbound Prototype")

        val offered = driver.castSelfAssembler(me, pick = "Arcbound Prototype")

        offered.contains("Arcbound Prototype") shouldBe true
        (driver.findCardInHand(me, "Arcbound Prototype") != null) shouldBe true
    }

    test("an artifact creature that is not an Assembly-Worker is not offered") {
        val driver = newDriver()
        val me = driver.activePlayer!!
        driver.putCardOnTopOfLibrary(me, "Artifact Creature")

        val offered = driver.castSelfAssembler(me, pick = "Artifact Creature")

        offered.contains("Artifact Creature") shouldBe false
        driver.findCardInHand(me, "Artifact Creature") shouldBe null
    }
})

package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.eoe.cards.ScourForScrap
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe

/**
 * Scour for Scrap — {3}{U} Instant (EOE).
 *
 * Choose one or both —
 * • Search your library for an artifact card, reveal it, put it into your hand, then shuffle.
 * • Return target artifact card from your graveyard to your hand.
 *
 * "Choose one or both" is `chooseCount = 2, minChooseCount = 1` (CR 700.2). It used to be written
 * as three modes with `chooseCount = 1` — search, return, and a third that did both — which is a
 * mode the card does not print and reports one chosen mode where the player chose two. Winterflame
 * carried the identical mistake.
 */
class ScourForScrapScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(ScourForScrap))
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.castScour(
        caster: EntityId,
        modes: List<Int>,
        modeTargets: List<List<ChosenTarget>>,
    ) {
        giveMana(caster, Color.BLUE, 4)
        val spell = putCardInHand(caster, "Scour for Scrap")
        submit(
            CastSpell(
                playerId = caster,
                cardId = spell,
                targets = modeTargets.flatten(),
                chosenModes = modes,
                modeTargetsOrdered = modeTargets,
            )
        ).error shouldBe null
        bothPass()
    }

    test("both modes — the search finds a card and the graveyard artifact comes back") {
        val driver = createDriver()
        val me = driver.activePlayer!!

        val inLibrary = driver.putCardOnTopOfLibrary(me, "Artifact Creature")
        val inGraveyard = driver.putCardInGraveyard(me, "Artifact Creature")

        driver.castScour(
            me,
            modes = listOf(0, 1),
            modeTargets = listOf(emptyList(), listOf(ChosenTarget.Card(inGraveyard, me, Zone.GRAVEYARD))),
        )

        val search = driver.pendingDecision as SelectCardsDecision
        search.options shouldContain inLibrary
        driver.submitCardSelection(me, listOf(inLibrary))

        var guard = 0
        while ((driver.stackSize > 0 || driver.isPaused) && guard++ < 30) {
            if (driver.isPaused) driver.autoResolveDecision() else driver.bothPass()
        }

        val hand = driver.getHand(me)
        hand shouldContain inLibrary
        hand shouldContain inGraveyard
    }

    test("one mode is enough — returning from the graveyard alone leaves the library untouched") {
        val driver = createDriver()
        val me = driver.activePlayer!!

        val inGraveyard = driver.putCardInGraveyard(me, "Artifact Creature")
        val librarySizeBefore = driver.state.getLibrary(me).size

        driver.castScour(
            me,
            modes = listOf(1),
            modeTargets = listOf(listOf(ChosenTarget.Card(inGraveyard, me, Zone.GRAVEYARD))),
        )

        driver.getHand(me) shouldContain inGraveyard
        driver.state.getLibrary(me).size shouldBe librarySizeBefore
    }

    test("the card offers exactly the two modes it prints") {
        val modal = ScourForScrap.script.spellEffect
            as com.wingedsheep.sdk.scripting.effects.ModalEffect

        modal.modes.size shouldBe 2
        modal.chooseCount shouldBe 2
        modal.minChooseCount shouldBe 1
    }
})

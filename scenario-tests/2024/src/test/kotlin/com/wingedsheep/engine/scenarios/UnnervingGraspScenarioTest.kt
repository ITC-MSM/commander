package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.handlers.continuations.entityIdToChosenTarget
import com.wingedsheep.engine.state.components.identity.FaceDownComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Unnerving Grasp (DSK #80) — {2}{U} Sorcery.
 *
 *  - Return up to one target nonland permanent to its owner's hand.
 *  - Manifest dread.
 *
 * Exercises both the bounce (with a chosen target) and the manifest dread that follows, plus the
 * "up to one" optional path where no target is chosen and only the manifest dread happens.
 */
class UnnervingGraspScenarioTest : FunSpec({

    fun driver(): GameTestDriver = GameTestDriver().apply {
        registerCards(TestCards.all)
        initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
    }

    test("bounces a target nonland permanent, then manifests dread") {
        val d = driver()
        val you = d.activePlayer!!
        val opp = d.player2
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Top two: a creature to manifest, a land beneath.
        d.putCardOnTopOfLibrary(you, "Forest")
        val toManifest = d.putCardOnTopOfLibrary(you, "Centaur Courser")

        val victim = d.putCreatureOnBattlefield(opp, "Centaur Courser")
        val spell = d.putCardInHand(you, "Unnerving Grasp")
        d.giveMana(you, Color.BLUE, 1)
        d.giveColorlessMana(you, 2)
        d.castSpellWithTargets(you, spell, listOf(entityIdToChosenTarget(d.state, victim)))
        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()

        // Bounce resolved: victim is back in its owner's hand.
        d.state.getZone(com.wingedsheep.engine.state.ZoneKey(opp, Zone.HAND)).contains(victim) shouldBe true
        d.getPermanents(opp).contains(victim) shouldBe false

        // Manifest dread paused on the pick — choose the creature to manifest.
        val pick = d.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
        d.submitDecision(you, CardsSelectedResponse(decisionId = pick.id, selectedCards = listOf(toManifest)))
        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()

        d.state.getEntity(toManifest)?.get<FaceDownComponent>() shouldBe FaceDownComponent
    }

    test("with no target chosen, only manifest dread happens") {
        val d = driver()
        val you = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        d.putCardOnTopOfLibrary(you, "Forest")
        val toManifest = d.putCardOnTopOfLibrary(you, "Centaur Courser")

        val spell = d.putCardInHand(you, "Unnerving Grasp")
        d.giveMana(you, Color.BLUE, 1)
        d.giveColorlessMana(you, 2)
        // Cast with no targets (the bounce is "up to one").
        d.castSpellWithTargets(you, spell, emptyList())
        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()

        val pick = d.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
        d.submitDecision(you, CardsSelectedResponse(decisionId = pick.id, selectedCards = listOf(toManifest)))
        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()

        d.state.getEntity(toManifest)?.get<FaceDownComponent>() shouldBe FaceDownComponent
    }
})

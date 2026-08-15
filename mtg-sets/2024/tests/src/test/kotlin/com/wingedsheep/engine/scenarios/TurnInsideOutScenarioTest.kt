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
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Turn Inside Out (DSK #160) — {R} Instant.
 *
 *  - Target creature gets +3/+0 until end of turn.
 *  - When it dies this turn, manifest dread.
 *
 * Exercises the +3/+0 buff and the watched-entity delayed dies trigger: killing the buffed
 * creature later this turn fires the manifest dread. Also covers the case where the creature
 * survives — no manifest dread.
 */
class TurnInsideOutScenarioTest : FunSpec({

    fun driver(): GameTestDriver = GameTestDriver().apply {
        registerCards(TestCards.all)
        initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
    }

    test("buffs +3/+0; when the creature dies this turn, manifest dread happens") {
        val d = driver()
        val you = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Top two for manifest dread later: a creature to manifest, a land beneath.
        d.putCardOnTopOfLibrary(you, "Forest")
        val toManifest = d.putCardOnTopOfLibrary(you, "Centaur Courser")

        val creature = d.putCreatureOnBattlefield(you, "Centaur Courser") // 3/3
        val spell = d.putCardInHand(you, "Turn Inside Out")
        d.giveMana(you, Color.RED, 1)
        d.castSpellWithTargets(you, spell, listOf(entityIdToChosenTarget(d.state, creature)))
        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()

        // +3/+0 applied → 6/3.
        d.state.projectedState.getPower(creature) shouldBe 6
        d.state.projectedState.getToughness(creature) shouldBe 3

        // Now kill it with Doom Blade — the watched dies trigger fires manifest dread.
        val doomBlade = d.putCardInHand(you, "Doom Blade")
        d.giveMana(you, Color.BLACK, 1)
        d.giveColorlessMana(you, 1)
        d.castSpellWithTargets(you, doomBlade, listOf(entityIdToChosenTarget(d.state, creature)))
        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()

        // Creature died.
        d.state.getZone(com.wingedsheep.engine.state.ZoneKey(you, Zone.GRAVEYARD)).contains(creature) shouldBe true

        // Delayed trigger paused on the manifest-dread pick.
        val pick = d.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
        d.submitDecision(you, CardsSelectedResponse(decisionId = pick.id, selectedCards = listOf(toManifest)))
        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()

        d.state.getEntity(toManifest)?.get<FaceDownComponent>() shouldBe FaceDownComponent
    }

    test("if the creature survives, no manifest dread") {
        val d = driver()
        val you = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        d.putCardOnTopOfLibrary(you, "Forest")
        d.putCardOnTopOfLibrary(you, "Centaur Courser")

        val creature = d.putCreatureOnBattlefield(you, "Centaur Courser")
        val spell = d.putCardInHand(you, "Turn Inside Out")
        d.giveMana(you, Color.RED, 1)
        d.castSpellWithTargets(you, spell, listOf(entityIdToChosenTarget(d.state, creature)))
        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()

        // No death this turn → no manifest dread decision is pending.
        d.pendingDecision.shouldBeNull()
    }
})

package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.atq.cards.ColossusOfSardia
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Colossus of Sardia — mandatory "doesn't untap" plus a pay-{9}-to-untap ability restricted to
 * the controller's upkeep.
 *
 * Trample. This creature doesn't untap during your untap step.
 * {9}: Untap this creature. Activate only during your upkeep.
 */
class ColossusOfSardiaScenarioTest : FunSpec({

    val abilityId = ColossusOfSardia.activatedAbilities.first().id
    val projector = StateProjector()

    test("9/9 trample; stays tapped through the untap step; {9} during upkeep untaps it") {
        val d = GameTestDriver()
        d.registerCards(TestCards.all)
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        val p = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val colossus = d.putCreatureOnBattlefield(p, "Colossus of Sardia")
        d.removeSummoningSickness(colossus)

        projector.getProjectedPower(d.state, colossus) shouldBe 9
        projector.getProjectedToughness(d.state, colossus) shouldBe 9
        projector.project(d.state).hasKeyword(colossus, Keyword.TRAMPLE) shouldBe true

        // Tap it (as if it attacked) and run to OUR next upkeep (turn 2). DOESNT_UNTAP filters it
        // out of every untap step along the way, so it never gets the keep-tapped choice and
        // remains tapped.
        d.tapPermanent(colossus)
        d.passPriorityUntil(Step.UPKEEP)        // opponent's upkeep
        d.passPriorityUntil(Step.PRECOMBAT_MAIN) // opponent's main
        d.passPriorityUntil(Step.UPKEEP)        // our next upkeep
        d.activePlayer shouldBe p
        d.state.getEntity(colossus)?.has<TappedComponent>() shouldBe true

        // Pay {9} during our upkeep to untap it.
        d.giveColorlessMana(p, 9)
        val result = d.submit(
            ActivateAbility(playerId = p, sourceId = colossus, abilityId = abilityId)
        )
        result.isSuccess shouldBe true
        d.bothPass()
        d.state.getEntity(colossus)?.has<TappedComponent>() shouldBe false
    }

    test("the {9} untap ability cannot be activated outside the controller's upkeep") {
        val d = GameTestDriver()
        d.registerCards(TestCards.all)
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        val p = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val colossus = d.putCreatureOnBattlefield(p, "Colossus of Sardia")
        d.removeSummoningSickness(colossus)
        d.tapPermanent(colossus)

        // In the main phase (not upkeep), the restriction must reject the activation.
        d.giveColorlessMana(p, 9)
        val result = d.submit(
            ActivateAbility(playerId = p, sourceId = colossus, abilityId = abilityId)
        )
        result.isSuccess shouldBe false
        d.state.getEntity(colossus)?.has<TappedComponent>() shouldBe true
    }
})

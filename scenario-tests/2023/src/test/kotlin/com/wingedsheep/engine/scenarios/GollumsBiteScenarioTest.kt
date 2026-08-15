package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.player.TheRingComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.ltr.cards.GollumsBite
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Gollum's Bite — "{B}: Target creature gets -2/-2 until end of turn." plus the graveyard ability
 * "{3}{B}, Exile this card from your graveyard: The Ring tempts you. Activate only as a sorcery."
 * Exercises both the instant's stat modification and the sorcery-speed exile-from-graveyard Ring tempt.
 */
class GollumsBiteScenarioTest : FunSpec({

    val projector = StateProjector()
    val graveyardAbilityId = GollumsBite.activatedAbilities.first().id

    fun driver(): GameTestDriver = GameTestDriver().apply {
        registerCards(TestCards.all)
        initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
    }

    test("the instant gives target creature -2/-2 until end of turn") {
        val d = driver()
        val you = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val courser = d.putCreatureOnBattlefield(you, "Centaur Courser") // 3/3
        val bite = d.putCardInHand(you, "Gollum's Bite")
        d.giveMana(you, Color.BLACK, 1)

        d.castSpell(you, bite, listOf(courser)).isSuccess shouldBe true
        d.bothPass()

        // 3/3 - 2/2 = 1/1: still alive, proving the -2/-2 applied.
        val projected = projector.project(d.state)
        d.findPermanent(you, "Centaur Courser") shouldBe courser
        projected.getPower(courser) shouldBe 1
        projected.getToughness(courser) shouldBe 1
    }

    test("from the graveyard, exile it to make the Ring tempt you at sorcery speed") {
        val d = driver()
        val you = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val bite = d.putCardInGraveyard(you, "Gollum's Bite")
        val bear = d.putCreatureOnBattlefield(you, "Grizzly Bears") // a Ring-bearer candidate
        d.giveMana(you, Color.BLACK, 4) // {3}{B}

        val result = d.submit(
            ActivateAbility(playerId = you, sourceId = bite, abilityId = graveyardAbilityId)
        )
        result.isSuccess shouldBe true
        d.bothPass() // resolve → pause to choose a Ring-bearer

        val decision = d.pendingDecision as SelectCardsDecision
        d.submitDecision(you, CardsSelectedResponse(decision.id, listOf(bear)))

        d.state.getEntity(you)?.get<TheRingComponent>()?.temptCount shouldBe 1
        // The exile-from-graveyard cost removed it from the graveyard.
        d.getGraveyard(you).contains(bite) shouldBe false
    }

    test("the graveyard ability cannot be activated at instant speed") {
        val d = driver()
        val you = d.activePlayer!!
        d.passPriorityUntil(Step.UPKEEP, maxPasses = 200) // upkeep is not a sorcery-speed window

        val bite = d.putCardInGraveyard(you, "Gollum's Bite")
        d.giveMana(you, Color.BLACK, 4)

        val result = d.submit(
            ActivateAbility(playerId = you, sourceId = bite, abilityId = graveyardAbilityId)
        )
        result.isSuccess shouldBe false
    }
})

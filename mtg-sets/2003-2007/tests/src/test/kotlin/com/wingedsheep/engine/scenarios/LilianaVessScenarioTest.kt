package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.LilianaVess
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.engine.state.ZoneKey
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Liliana Vess {3}{B}{B} — Legendary Planeswalker — Liliana (loyalty 5)
 *   +1: Target player discards a card.
 *   −2: Search your library for a card, then shuffle and put that card on top.
 *   −8: Put all creature cards from all graveyards onto the battlefield under your control.
 *
 * The +1 is aimed at the opponent, who picks the card. The −2 plants a lone non-land in a
 * library of Swamps and checks it ends up on *top* after the shuffle. The −8 seeds both
 * graveyards with a creature and a non-creature each; the creatures — including the opponent's —
 * must come back under Liliana's controller, and the instants must stay put.
 */
class LilianaVessScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(LilianaVess))
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun putLiliana(driver: GameTestDriver, playerId: EntityId, loyalty: Int): EntityId {
        val liliana = driver.putPermanentOnBattlefield(playerId, "Liliana Vess")
        driver.addComponent(liliana, CountersComponent(mapOf(CounterType.LOYALTY to loyalty)))
        return liliana
    }

    fun loyalty(driver: GameTestDriver, entityId: EntityId): Int =
        driver.state.getEntity(entityId)?.get<CountersComponent>()?.getCount(CounterType.LOYALTY) ?: 0

    val abilities = LilianaVess.script.activatedAbilities

    test("+1 makes the targeted opponent discard a card of their choice") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)
        val liliana = putLiliana(driver, me, 5)
        val myHand = driver.getHandSize(me)
        val theirHand = driver.getHandSize(opp)

        driver.submitSuccess(
            ActivateAbility(
                playerId = me,
                sourceId = liliana,
                abilityId = abilities[0].id,
                targets = listOf(ChosenTarget.Player(opp))
            )
        )
        driver.bothPass()

        val decision = driver.pendingDecision
        if (decision is SelectCardsDecision) {
            withClue("the discarding player chooses") { decision.playerId shouldBe opp }
            driver.submitDecision(opp, CardsSelectedResponse(decision.id, listOf(decision.options.first())))
        }

        withClue("the opponent discarded") { driver.getHandSize(opp) shouldBe theirHand - 1 }
        withClue("Liliana's controller did not") { driver.getHandSize(me) shouldBe myHand }
        driver.getGraveyard(opp).size shouldBe 1
        loyalty(driver, liliana) shouldBe 6
    }

    test("−2 finds a card and puts it on top of the library after shuffling") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val liliana = putLiliana(driver, me, 5)
        val bears = driver.putCardOnTopOfLibrary(me, "Grizzly Bears")
        // Bury it: a handful of Swamps above it so "on top" is not where it already was.
        repeat(5) { driver.putCardOnTopOfLibrary(me, "Swamp") }
        val librarySize = driver.state.getZone(ZoneKey(me, Zone.LIBRARY)).size

        driver.submitSuccess(ActivateAbility(playerId = me, sourceId = liliana, abilityId = abilities[1].id))
        driver.bothPass()

        val decision = driver.pendingDecision
        decision.shouldBeInstanceOf<SelectCardsDecision>()
        withClue("any card may be chosen") { decision.options.size shouldBe librarySize }
        driver.submitDecision(me, CardsSelectedResponse(decision.id, listOf(bears)))

        val library = driver.state.getZone(ZoneKey(me, Zone.LIBRARY))
        withClue("the found card is on top") { library.first() shouldBe bears }
        withClue("nothing left the library") { library.size shouldBe librarySize }
        loyalty(driver, liliana) shouldBe 3
    }

    test("−8 puts every creature card from every graveyard onto the battlefield under your control") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)
        val liliana = putLiliana(driver, me, 8)
        val myBears = driver.putCardInGraveyard(me, "Grizzly Bears")
        val myBolt = driver.putCardInGraveyard(me, "Lightning Bolt")
        val theirCourser = driver.putCardInGraveyard(opp, "Centaur Courser")
        val theirBolt = driver.putCardInGraveyard(opp, "Lightning Bolt")

        driver.submitSuccess(ActivateAbility(playerId = me, sourceId = liliana, abilityId = abilities[2].id))
        driver.bothPass()

        val projected = driver.state.projectedState
        withClue("both creatures are on the battlefield under Liliana's controller") {
            driver.getCreatures(me).toSet() shouldBe setOf(myBears, theirCourser)
            projected.getController(theirCourser) shouldBe me
            driver.getCreatures(opp) shouldBe emptyList()
        }
        withClue("the non-creature cards stayed in their graveyards") {
            driver.getGraveyard(me) shouldContain myBolt
            driver.getGraveyard(opp) shouldBe listOf(theirBolt)
        }
        withClue("Liliana went to 0 loyalty and was put into the graveyard by state-based actions") {
            driver.getGraveyard(me) shouldContain liliana
        }
    }
})

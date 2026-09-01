package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.GarrukWildspeaker
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Garruk Wildspeaker {2}{G}{G} — Legendary Planeswalker — Garruk (loyalty 3)
 *   +1: Untap two target lands.
 *   −1: Create a 3/3 green Beast creature token.
 *   −4: Creatures you control get +3/+3 and gain trample until end of turn.
 *
 * The +1 untaps two tapped lands and leaves a third tapped, so a "untap all lands" slip would
 * show. The −4 is aimed at an opponent's creature too, the one shape where a dropped "you
 * control" disagrees with the card.
 */
class GarrukWildspeakerScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(GarrukWildspeaker))
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun putGarruk(driver: GameTestDriver, playerId: EntityId, loyalty: Int): EntityId {
        val garruk = driver.putPermanentOnBattlefield(playerId, "Garruk Wildspeaker")
        driver.addComponent(garruk, CountersComponent(mapOf(CounterType.LOYALTY to loyalty)))
        return garruk
    }

    fun loyalty(driver: GameTestDriver, entityId: EntityId): Int =
        driver.state.getEntity(entityId)?.get<CountersComponent>()?.getCount(CounterType.LOYALTY) ?: 0

    val abilities = GarrukWildspeaker.script.activatedAbilities

    test("+1 untaps the two targeted lands and no other") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val garruk = putGarruk(driver, me, 3)
        val forest1 = driver.putLandOnBattlefield(me, "Forest")
        val forest2 = driver.putLandOnBattlefield(me, "Forest")
        val forest3 = driver.putLandOnBattlefield(me, "Forest")
        listOf(forest1, forest2, forest3).forEach { driver.tapPermanent(it) }

        driver.submitSuccess(
            ActivateAbility(
                playerId = me,
                sourceId = garruk,
                abilityId = abilities[0].id,
                targets = listOf(ChosenTarget.Permanent(forest1), ChosenTarget.Permanent(forest2))
            )
        )
        driver.bothPass()

        withClue("the two targets untapped") {
            driver.isTapped(forest1) shouldBe false
            driver.isTapped(forest2) shouldBe false
        }
        withClue("the third land is still tapped") { driver.isTapped(forest3) shouldBe true }
        loyalty(driver, garruk) shouldBe 4
    }

    test("−1 creates a 3/3 green Beast token") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val garruk = putGarruk(driver, me, 3)

        driver.submitSuccess(ActivateAbility(playerId = me, sourceId = garruk, abilityId = abilities[1].id))
        driver.bothPass()

        val beasts = driver.getCreatures(me).filter { driver.getCardName(it)?.contains("Beast") == true }
        beasts.size shouldBe 1
        val beast = beasts.single()
        val projected = driver.state.projectedState
        projected.getPower(beast) shouldBe 3
        projected.getToughness(beast) shouldBe 3
        projected.getColors(beast) shouldBe setOf(Color.GREEN.name)
        loyalty(driver, garruk) shouldBe 2
    }

    test("−4 gives creatures you control +3/+3 and trample until end of turn — not the opponent's") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)
        val garruk = putGarruk(driver, me, 4)
        val mine = driver.putCreatureOnBattlefield(me, "Grizzly Bears")
        val theirs = driver.putCreatureOnBattlefield(opp, "Grizzly Bears")

        driver.submitSuccess(ActivateAbility(playerId = me, sourceId = garruk, abilityId = abilities[2].id))
        driver.bothPass()

        val projected = driver.state.projectedState
        withClue("my creature is pumped and tramples") {
            projected.getPower(mine) shouldBe 5
            projected.getToughness(mine) shouldBe 5
            projected.hasKeyword(mine, Keyword.TRAMPLE) shouldBe true
        }
        withClue("the opponent's creature is untouched") {
            projected.getPower(theirs) shouldBe 2
            projected.hasKeyword(theirs, Keyword.TRAMPLE) shouldBe false
        }
        loyalty(driver, garruk) shouldBe 0

        driver.passPriorityUntil(Step.UPKEEP)
        withClue("the pump wore off at end of turn") {
            driver.state.projectedState.getPower(mine) shouldBe 2
            driver.state.projectedState.hasKeyword(mine, Keyword.TRAMPLE) shouldBe false
        }
    }
})

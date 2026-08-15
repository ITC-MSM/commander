package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CastChoicesComponent
import com.wingedsheep.engine.state.components.battlefield.ChoiceValue
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.big.cards.TarnationVista
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.ChoiceSlot
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Tarnation Vista (BIG #30).
 *
 * Tarnation Vista — Land.
 *   "This land enters tapped. As it enters, choose a color.
 *    {T}: Add one mana of the chosen color.
 *    {1}, {T}: For each color among monocolored permanents you control, add one mana of that color."
 */
class TarnationVistaScenarioTest : FunSpec({

    val chosenColorAbilityId = TarnationVista.activatedAbilities[0].id
    val eachColorAbilityId = TarnationVista.activatedAbilities[1].id

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(TarnationVista))
        return driver
    }

    fun manaPool(driver: GameTestDriver, playerId: com.wingedsheep.sdk.model.EntityId): ManaPoolComponent =
        driver.state.getEntity(playerId)?.get<ManaPoolComponent>() ?: ManaPoolComponent()

    test("{T}: adds one mana of the chosen color") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val vista = driver.putPermanentOnBattlefield(player, "Tarnation Vista")
        // Simulate the "as it enters, choose a color" result: chose red.
        driver.replaceState(driver.state.updateEntity(vista) { c ->
            c.with(CastChoicesComponent(chosen = mapOf(ChoiceSlot.COLOR to ChoiceValue.ColorChoice(Color.RED))))
        })

        driver.submit(
            ActivateAbility(playerId = player, sourceId = vista, abilityId = chosenColorAbilityId)
        ).isSuccess shouldBe true

        manaPool(driver, player).red shouldBe 1
    }

    test("{1},{T}: adds one mana for each color among monocolored permanents you control") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val vista = driver.putPermanentOnBattlefield(player, "Tarnation Vista")
        driver.replaceState(driver.state.updateEntity(vista) { c ->
            c.with(CastChoicesComponent(chosen = mapOf(ChoiceSlot.COLOR to ChoiceValue.ColorChoice(Color.RED))))
        })

        // Control three monocolored creatures: white, red, blue, plus a colorless
        // artifact creature that contributes no color (and so is excluded by "monocolored").
        driver.putCreatureOnBattlefield(player, "Savannah Lions")  // white
        driver.putCreatureOnBattlefield(player, "Goblin Guide")    // red
        driver.putCreatureOnBattlefield(player, "Phantom Warrior") // blue
        driver.putCreatureOnBattlefield(player, "Artifact Creature") // colorless → excluded

        // Pay the {1} from pool.
        driver.giveColorlessMana(player, 1)

        driver.submit(
            ActivateAbility(playerId = player, sourceId = vista, abilityId = eachColorAbilityId)
        ).isSuccess shouldBe true

        val pool = manaPool(driver, player)
        pool.white shouldBe 1
        pool.blue shouldBe 1
        pool.red shouldBe 1
        pool.black shouldBe 0
        pool.green shouldBe 0
    }
})

package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fdn.cards.LootExuberantExplorer
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Loot, Exuberant Explorer (FDN #106) — "{4}{G}{G}, {T}: Look at the top six cards of your library.
 * You may reveal a creature card with mana value less than or equal to the number of lands you
 * control from among them and put it onto the battlefield. Put the rest on the bottom in a random
 * order."
 *
 * Proves the dynamic mana-value cap is read live off the number of lands the controller has:
 *   - With 3 lands, a mana-value-3 creature (Centaur Courser) is eligible → it can be put onto the
 *     battlefield.
 *   - With 2 lands, the same creature is over the cap (3 > 2) → it is not even offered as a choice.
 */
class LootExuberantExplorerScenarioTest : FunSpec({

    val activateAbilityId = LootExuberantExplorer.activatedAbilities.first().id

    fun setup(lands: Int): Triple<GameTestDriver, EntityId, EntityId> {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val loot = driver.putPermanentOnBattlefield(player, "Loot, Exuberant Explorer")
        driver.removeSummoningSickness(loot)
        repeat(lands) { driver.putLandOnBattlefield(player, "Forest") }

        // Centaur Courser is a {2}{G} 3/3 — mana value 3.
        val courser = driver.putCardOnTopOfLibrary(player, "Centaur Courser")

        // {4}{G}{G} — six green mana covers the two {G} pips and the four generic.
        driver.giveMana(player, Color.GREEN, 6)
        return Triple(driver, loot, courser)
    }

    test("puts a creature with mana value <= lands you control onto the battlefield") {
        val (driver, loot, courser) = setup(lands = 3)
        val player = driver.activePlayer!!

        driver.submit(
            ActivateAbility(playerId = player, sourceId = loot, abilityId = activateAbilityId)
        ).isSuccess shouldBe true
        while (!driver.isPaused && driver.state.stack.isNotEmpty()) driver.bothPass()

        val pick = driver.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
        pick.options shouldContain courser
        driver.submitDecision(player, CardsSelectedResponse(decisionId = pick.id, selectedCards = listOf(courser)))
        while (!driver.isPaused && driver.state.stack.isNotEmpty()) driver.bothPass()

        driver.getPermanents(player) shouldContain courser
    }

    test("does not offer a creature whose mana value exceeds the number of lands you control") {
        val (driver, loot, courser) = setup(lands = 2)
        val player = driver.activePlayer!!

        driver.submit(
            ActivateAbility(playerId = player, sourceId = loot, abilityId = activateAbilityId)
        ).isSuccess shouldBe true
        while (!driver.isPaused && driver.state.stack.isNotEmpty()) driver.bothPass()

        // The "look at" pipeline still pauses (showAllCards), but the over-cap creature is not a
        // legal choice, so it is absent from the selectable options.
        if (driver.isPaused) {
            val pick = driver.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
            pick.options shouldNotContain courser
            driver.submitDecision(player, CardsSelectedResponse(decisionId = pick.id, selectedCards = emptyList()))
            while (!driver.isPaused && driver.state.stack.isNotEmpty()) driver.bothPass()
        }

        driver.getPermanents(player) shouldNotContain courser
    }
})

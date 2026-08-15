package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.CastChoicesComponent
import com.wingedsheep.engine.state.components.battlefield.ChoiceValue
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.ControllerComponent
import com.wingedsheep.engine.state.components.identity.TokenComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.hob.cards.AnUnexpectedParty
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.ChoiceSlot
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe

/**
 * An Unexpected Party // At the Door (HOB #29) — {2}{W}{W} Enchantment with an
 * `{X}{2}{W} Sorcery — Adventure` face ("Create X 2/2 red Dwarf creature tokens").
 *
 * The Adventure's {X} lives in the *face's* mana cost, not the card's top-level one. The
 * enumerated cast action therefore has to carry `hasXCost` / `maxAffordableX` off the face's
 * effective cost — without them the client never opens the X picker, the face casts at X = 0, and
 * "create X tokens" creates nothing (the reported bug).
 */
class AnUnexpectedPartyScenarioTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(AnUnexpectedParty)
        return driver
    }

    fun startAtMain(driver: GameTestDriver): EntityId {
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return player
    }

    test("the Adventure face is offered with an X picker, and the ceiling is what the board can pay") {
        val driver = createDriver()
        val player = startAtMain(driver)

        driver.putCardInHand(player, "An Unexpected Party")
        repeat(6) { driver.putPermanentOnBattlefield(player, "Plains") }

        val adventure = driver.legalActions(player)
            .single { (it.action as? CastSpell)?.faceIndex == 0 }

        adventure.hasXCost shouldBe true
        // Six Plains, {X}{2}{W} — three mana is spoken for by the fixed part, so X tops out at 3.
        adventure.maxAffordableX shouldBe 3
        adventure.manaCostString shouldBe "{X}{2}{W}"
    }

    test("the enchantment face carries no X picker") {
        val driver = createDriver()
        val player = startAtMain(driver)

        driver.putCardInHand(player, "An Unexpected Party")
        repeat(6) { driver.putPermanentOnBattlefield(player, "Plains") }

        val enchantment = driver.legalActions(player)
            .single { (it.action as? CastSpell)?.faceIndex == null && it.description.contains("An Unexpected Party") }

        enchantment.hasXCost shouldBe false
        enchantment.maxAffordableX shouldBe null
    }

    test("casting At the Door for X = 3 creates three 2/2 red Dwarves and exiles the card") {
        val driver = createDriver()
        val player = startAtMain(driver)

        val party = driver.putCardInHand(player, "An Unexpected Party")
        driver.giveMana(player, Color.WHITE, 6) // {X=3}{2}{W}

        driver.submit(
            CastSpell(playerId = player, cardId = party, faceIndex = 0, xValue = 3)
        ).isSuccess shouldBe true
        driver.bothPass()
        driver.isPaused shouldBe false

        val dwarves = dwarfTokens(driver, player)
        dwarves.size shouldBe 3
        dwarves.forEach { token ->
            projector.getProjectedPower(driver.state, token) shouldBe 2
            projector.getProjectedToughness(driver.state, token) shouldBe 2
            driver.state.getEntity(token)!!.get<CardComponent>()!!.colors shouldBe setOf(Color.RED)
        }

        // CR 715.3d — the Adventure exiles itself, castable as the enchantment later.
        driver.getExile(player) shouldContain party
        driver.state.mayPlayPermissions.any { party in it.cardIds && it.controllerId == player } shouldBe true
    }

    test("X is read from the cast, not hardcoded: X = 1 creates one Dwarf") {
        val driver = createDriver()
        val player = startAtMain(driver)

        val party = driver.putCardInHand(player, "An Unexpected Party")
        driver.giveMana(player, Color.WHITE, 4) // {X=1}{2}{W}

        driver.submit(
            CastSpell(playerId = player, cardId = party, faceIndex = 0, xValue = 1)
        ).isSuccess shouldBe true
        driver.bothPass()

        dwarfTokens(driver, player).size shouldBe 1
    }

    test("the enchantment's chosen type gets +2/+2 — Dwarves the Adventure made included") {
        val driver = createDriver()
        val player = startAtMain(driver)

        val party = driver.putCardInHand(player, "An Unexpected Party")
        driver.giveMana(player, Color.WHITE, 5) // {X=2}{2}{W}
        driver.submit(
            CastSpell(playerId = player, cardId = party, faceIndex = 0, xValue = 2)
        ).isSuccess shouldBe true
        driver.bothPass()
        val dwarves = dwarfTokens(driver, player)
        dwarves.size shouldBe 2

        // The enchantment face, with "Dwarf" already chosen (the EntersWithChoice resumer's own
        // path is covered by the shared choose-a-type tests; this isolates the lord).
        val enchantment = driver.putPermanentOnBattlefield(player, "An Unexpected Party")
        driver.replaceState(
            driver.state.updateEntity(enchantment) { c ->
                c.with(
                    CastChoicesComponent(
                        chosen = mapOf(ChoiceSlot.CREATURE_TYPE to ChoiceValue.TextChoice("Dwarf"))
                    )
                )
            }
        )

        dwarves.forEach { token ->
            projector.getProjectedPower(driver.state, token) shouldBe 4
            projector.getProjectedToughness(driver.state, token) shouldBe 4
        }
    }
})

private fun dwarfTokens(driver: GameTestDriver, player: EntityId): List<EntityId> =
    driver.state.getZone(ZoneKey(player, Zone.BATTLEFIELD)).filter { id ->
        val entity = driver.state.getEntity(id) ?: return@filter false
        entity.has<TokenComponent>() &&
            entity.get<ControllerComponent>()?.playerId == player &&
            entity.get<CardComponent>()?.typeLine?.subtypes?.any { it.value == "Dwarf" } == true
    }

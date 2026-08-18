package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.LinkedExileComponent
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.mh3.cards.UginsLabyrinth
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.TypeLine
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Ugin's Labyrinth (MH3 #233) — Land.
 *
 *   "Imprint — When this land enters, you may exile a colorless card with mana value 7 or
 *    greater from your hand.
 *    {T}: Add {C}. If a card is exiled with this land, add {C}{C} instead.
 *    {T}: Return the exiled card to its owner's hand."
 *
 * Three pieces of behaviour to pin down:
 *  - The ETB offers the choice only for eligible (colorless, mana value >= 7) hand cards, and
 *    links the exiled card to this land.
 *  - The mana ability reads the link: {C} normally, {C}{C} once something is imprinted.
 *  - The third ability returns the linked card to its owner's hand.
 */
class UginsLabyrinthScenarioTest : FunSpec({

    // A colorless {7} artifact — eligible for the imprint ability. Not a real printed card;
    // just a minimal fixture so the test doesn't depend on another set's corpus.
    val ColorlessRelic = CardDefinition(
        name = "Colorless Relic",
        manaCost = ManaCost.parse("{7}"),
        typeLine = TypeLine.artifact()
    )

    val manaAbilityId = UginsLabyrinth.activatedAbilities[0].id
    val returnAbilityId = UginsLabyrinth.activatedAbilities[1].id

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(UginsLabyrinth)
        driver.registerCard(ColorlessRelic)
        return driver
    }

    test("playing the land offers to exile an eligible card, linking it to the land") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val me = driver.activePlayer!!
        val land = driver.putCardInHand(me, "Ugin's Labyrinth")
        val relic = driver.putCardInHand(me, "Colorless Relic")

        driver.playLand(me, land).error shouldBe null
        // The ETB trigger needs a priority round to resolve off the stack.
        driver.bothPass()

        driver.submitCardSelection(me, listOf(relic)).error shouldBe null

        driver.getExile(me).contains(relic) shouldBe true
        val linked = driver.state.getEntity(land)?.get<LinkedExileComponent>()
        linked?.exiledIds shouldBe listOf(relic)
    }

    test("declining the imprint choice leaves the hand untouched") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val me = driver.activePlayer!!
        val land = driver.putCardInHand(me, "Ugin's Labyrinth")
        val relic = driver.putCardInHand(me, "Colorless Relic")

        driver.playLand(me, land).error shouldBe null
        driver.bothPass()

        driver.submitCardSelection(me, emptyList()).error shouldBe null

        driver.getExile(me).contains(relic) shouldBe false
        driver.getHand(me).contains(relic) shouldBe true
        driver.state.getEntity(land)?.get<LinkedExileComponent>() shouldBe null
    }

    test("mana ability adds {C} with nothing imprinted, {C}{C} once a card is linked") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val me = driver.activePlayer!!
        val plainLand = driver.putPermanentOnBattlefield(me, "Ugin's Labyrinth")

        driver.submit(ActivateAbility(playerId = me, sourceId = plainLand, abilityId = manaAbilityId))
            .isSuccess shouldBe true
        driver.state.getEntity(me)?.get<ManaPoolComponent>()?.colorless shouldBe 1

        val linkedLand = driver.putPermanentOnBattlefield(me, "Ugin's Labyrinth")
        val exiledRelic = driver.putCardInExile(me, "Colorless Relic")
        driver.replaceState(
            driver.state.updateEntity(linkedLand) { c ->
                c.with(LinkedExileComponent(listOf(exiledRelic)))
            }
        )

        driver.submit(ActivateAbility(playerId = me, sourceId = linkedLand, abilityId = manaAbilityId))
            .isSuccess shouldBe true
        driver.state.getEntity(me)?.get<ManaPoolComponent>()?.colorless shouldBe (1 + 2)
    }

    test("third ability returns the linked card to its owner's hand") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val me = driver.activePlayer!!
        val land = driver.putPermanentOnBattlefield(me, "Ugin's Labyrinth")
        val exiledRelic = driver.putCardInExile(me, "Colorless Relic")
        driver.replaceState(
            driver.state.updateEntity(land) { c ->
                c.with(LinkedExileComponent(listOf(exiledRelic)))
            }
        )

        driver.submit(ActivateAbility(playerId = me, sourceId = land, abilityId = returnAbilityId))
            .isSuccess shouldBe true

        // Unlike the mana ability, this one isn't a mana ability — it goes on the stack and needs
        // resolving before the card actually moves.
        driver.bothPass()

        driver.getHand(me).contains(exiledRelic) shouldBe true
        driver.getExile(me).contains(exiledRelic) shouldBe false
    }
})

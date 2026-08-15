package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.TurnFaceUp
import com.wingedsheep.engine.state.components.identity.FaceDownComponent
import com.wingedsheep.engine.state.components.identity.MorphDataComponent
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dsk.cards.UnableToScream
import com.wingedsheep.mtg.sets.definitions.scg.cards.ZombieCutthroat
import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.KeywordAbility
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for Unable to Scream
 * {U} Enchantment — Aura
 * Enchant creature
 * Enchanted creature loses all abilities and is a Toy artifact creature with base power and
 * toughness 0/2 in addition to its other types.
 * As long as enchanted creature is face down, it can't be turned face up.
 */
class UnableToScreamScenarioTest : FunSpec({

    // A flying blue Bird with an activated ability — proves abilities are removed but the original
    // color/creature type are kept (added to, not replaced).
    val flyer = CardDefinition.creature(
        name = "Test Flyer",
        manaCost = ManaCost.parse("{1}{U}"),
        subtypes = setOf(Subtype("Bird")),
        power = 3,
        toughness = 3,
        keywords = setOf(Keyword.FLYING),
    )

    val projector = StateProjector()

    val allCards = TestCards.all + listOf(flyer, ZombieCutthroat, UnableToScream)

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(allCards)
        return driver
    }

    fun GameTestDriver.putFaceDownCreature(playerId: EntityId, cardName: String): EntityId {
        val creatureId = putCreatureOnBattlefield(playerId, cardName)
        val cardDef = allCards.first { it.name == cardName }
        val morphAbility = cardDef.keywordAbilities.filterIsInstance<KeywordAbility.Morph>().firstOrNull()
        replaceState(state.updateEntity(creatureId) { container ->
            var c = container.with(FaceDownComponent)
            if (morphAbility != null) {
                c = c.with(MorphDataComponent(morphAbility.morphCost, cardDef.name))
            }
            c
        })
        removeSummoningSickness(creatureId)
        return creatureId
    }

    test("enchanted creature is a 0/2 Toy artifact creature with no abilities, keeping its other types/colors") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), startingLife = 20)

        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val creature = driver.putCreatureOnBattlefield(player, "Test Flyer")
        val aura = driver.putCardInHand(player, "Unable to Scream")
        driver.giveMana(player, Color.BLUE, 1)
        driver.castSpell(player, aura, listOf(creature))
        driver.bothPass()

        val projected = driver.state.projectedState
        // Base P/T 0/2.
        projector.getProjectedPower(driver.state, creature) shouldBe 0
        projector.getProjectedToughness(driver.state, creature) shouldBe 2
        // Loses all abilities (flying gone).
        projected.hasLostAllAbilities(creature) shouldBe true
        projected.hasKeyword(creature, Keyword.FLYING) shouldBe false
        // Toy artifact creature, in addition to its other types (still a Bird, still blue).
        projected.hasType(creature, CardType.ARTIFACT.name) shouldBe true
        projected.hasType(creature, CardType.CREATURE.name) shouldBe true
        projected.hasSubtype(creature, "Toy") shouldBe true
        projected.hasSubtype(creature, "Bird") shouldBe true
        projected.hasColor(creature, Color.BLUE) shouldBe true
    }

    test("a face-down enchanted creature can't be turned face up") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40, "Swamp" to 40), startingLife = 20)

        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // A face-down morph creature (Zombie Cutthroat) that could normally be turned face up.
        val cutthroat = driver.putFaceDownCreature(player, "Zombie Cutthroat")

        // Enchant it with Unable to Scream.
        val aura = driver.putCardInHand(player, "Unable to Scream")
        driver.giveMana(player, Color.BLUE, 1)
        driver.castSpell(player, aura, listOf(cutthroat))
        driver.bothPass()

        // The flag is projected, and the turn-face-up special action is rejected.
        driver.state.projectedState.cantBeTurnedFaceUp(cutthroat) shouldBe true
        val result = driver.submit(TurnFaceUp(playerId = player, sourceId = cutthroat))
        result.isSuccess shouldBe false
        driver.state.getEntity(cutthroat)?.get<FaceDownComponent>() shouldBe FaceDownComponent
    }
})

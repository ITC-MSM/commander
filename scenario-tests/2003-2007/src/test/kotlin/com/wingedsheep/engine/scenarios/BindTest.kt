package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.inv.cards.Bind
import com.wingedsheep.mtg.sets.definitions.scg.cards.CarrionFeeder
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for Bind.
 *
 * Bind: {1}{G}
 * Instant
 * Counter target activated ability. (Mana abilities can't be targeted.)
 * Draw a card.
 *
 * Exercises the new `CardPredicate.IsActivatedAbility` / `Targets.ActivatedAbility`
 * target: it counters an activated ability, draws a card, and refuses a triggered ability
 * as a target.
 */
class BindTest : FunSpec({

    // Simple test creature with an ETB "gain 3 life" trigger (for the negative case)
    val LifeGainCreature = card("Life Gain Creature") {
        manaCost = "{1}{G}"
        typeLine = "Creature — Beast"
        power = 2
        toughness = 2

        triggeredAbility {
            trigger = Triggers.EntersBattlefield
            effect = Effects.GainLife(3)
        }
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(Bind, LifeGainCreature))
        return driver
    }

    test("counters an activated ability and draws a card") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Forest" to 20, "Swamp" to 20),
            startingLife = 20
        )

        val player1 = driver.activePlayer!!
        val player2 = driver.getOpponent(player1)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val feeder = driver.putCreatureOnBattlefield(player2, "Carrion Feeder")
        driver.removeSummoningSickness(feeder)
        val fodder = driver.putCreatureOnBattlefield(player2, "Grizzly Bears")

        val bind = driver.putCardInHand(player1, "Bind")
        driver.giveMana(player1, Color.GREEN, 1)
        driver.giveMana(player1, Color.GREEN, 1)
        val handBefore = driver.getHandSize(player1)

        driver.passPriority(player1)

        // Player 2 activates Carrion Feeder, sacrificing Grizzly Bears
        val abilityId = CarrionFeeder.activatedAbilities[0].id
        val activateResult = driver.submit(
            ActivateAbility(
                playerId = player2,
                sourceId = feeder,
                abilityId = abilityId,
                costPayment = AdditionalCostPayment(sacrificedPermanents = listOf(fodder))
            )
        )
        activateResult.isSuccess shouldBe true

        val abilityOnStack = driver.getTopOfStack()!!

        // Player 2 passes; Player 1 responds with Bind targeting the activated ability
        driver.passPriority(player2)
        val castResult = driver.castSpellWithTargets(player1, bind, listOf(ChosenTarget.Spell(abilityOnStack)))
        castResult.isSuccess shouldBe true

        driver.bothPass()

        // The activated ability was countered — no +1/+1 counter on Carrion Feeder
        driver.stackSize shouldBe 0
        driver.state.getEntity(feeder)?.get<CountersComponent>() shouldBe null

        // Bind drew a card (hand: -1 Bind cast, +1 drawn = net unchanged from pre-cast count)
        driver.getHandSize(player1) shouldBe handBefore
    }

    test("cannot target a triggered ability") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Forest" to 20, "Swamp" to 20),
            startingLife = 20
        )

        val player1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Player 1 casts Life Gain Creature (ETB: gain 3 life)
        val creature = driver.putCardInHand(player1, "Life Gain Creature")
        driver.giveMana(player1, Color.GREEN, 2)
        driver.castSpell(player1, creature)
        driver.bothPass()

        // Triggered ability is on the stack
        driver.stackSize shouldBe 1
        val triggeredAbilityOnStack = driver.getTopOfStack()!!

        val bind = driver.putCardInHand(player1, "Bind")
        driver.giveMana(player1, Color.GREEN, 1)
        driver.giveMana(player1, Color.GREEN, 1)

        // Targeting the triggered ability with Bind must be rejected
        val castResult = driver.castSpellWithTargets(
            player1, bind, listOf(ChosenTarget.Spell(triggeredAbilityOnStack))
        )
        castResult.isSuccess shouldBe false

        // Trigger still resolves normally
        driver.bothPass()
        driver.getLifeTotal(player1) shouldBe 23
    }
})

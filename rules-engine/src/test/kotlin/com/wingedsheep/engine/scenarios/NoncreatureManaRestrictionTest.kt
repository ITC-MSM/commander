package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.effects.ManaRestriction
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import com.wingedsheep.engine.core.Outcome
import io.kotest.matchers.shouldNotBe

/**
 * Tests for the negated card-type mana restriction —
 * [ManaRestriction.CardTypeSpellsOrAbilitiesOnly] with `negated = true`
 * ("Spend this mana only to cast a noncreature spell", The Emperor of Palamecia).
 */
class NoncreatureManaRestrictionTest : FunSpec({

    val noncreatureOnly = ManaRestriction.CardTypeSpellsOrAbilitiesOnly(
        cardType = CardType.CREATURE,
        negated = true,
    )

    val testInstant = card("Test Noncreature Instant") {
        manaCost = "{U}"
        typeLine = "Instant"
        oracleText = "Draw a card."
        spell {
            effect = Effects.DrawCards(1)
        }
    }

    val testCreature = card("Test Plain Creature") {
        manaCost = "{U}"
        typeLine = "Creature — Human"
        power = 1
        toughness = 1
    }

    fun createDriver(extraCards: List<CardDefinition> = emptyList()): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + extraCards)
        return driver
    }

    test("negated restriction renders a non-prefixed description") {
        noncreatureOnly.description shouldContain "noncreature"
    }

    test("noncreature-only mana CAN pay for an instant") {
        val driver = createDriver(listOf(testInstant))
        driver.initMirrorMatch(deck = Deck.of("Island" to 20), skipMulligans = true)

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.giveRestrictedMana(activePlayer, Color.BLUE, 1, noncreatureOnly)

        val instantId = driver.putCardInHand(activePlayer, "Test Noncreature Instant")
        val castResult = driver.submit(
            CastSpell(
                playerId = activePlayer,
                cardId = instantId,
                paymentStrategy = PaymentStrategy.FromPool,
            )
        )
        castResult.outcome shouldBe Outcome.Done

        val pool = driver.state.getEntity(activePlayer)?.get<ManaPoolComponent>()
        pool!!.restrictedMana.size shouldBe 0
    }

    test("noncreature-only mana CANNOT pay for a creature spell") {
        val driver = createDriver(listOf(testCreature))
        driver.initMirrorMatch(deck = Deck.of("Island" to 20), skipMulligans = true)

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.giveRestrictedMana(activePlayer, Color.BLUE, 1, noncreatureOnly)

        val creatureId = driver.putCardInHand(activePlayer, "Test Plain Creature")
        val castResult = driver.submit(
            CastSpell(
                playerId = activePlayer,
                cardId = creatureId,
                paymentStrategy = PaymentStrategy.FromPool,
            )
        )
        castResult.outcome shouldNotBe Outcome.Done
    }

    test("non-negated restriction is unchanged: creature-type-only mana pays creature spells") {
        val creatureOnly = ManaRestriction.CardTypeSpellsOrAbilitiesOnly(
            cardType = CardType.CREATURE,
            negated = false,
        )
        val driver = createDriver(listOf(testCreature))
        driver.initMirrorMatch(deck = Deck.of("Island" to 20), skipMulligans = true)

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.giveRestrictedMana(activePlayer, Color.BLUE, 1, creatureOnly)

        val creatureId = driver.putCardInHand(activePlayer, "Test Plain Creature")
        val castResult = driver.submit(
            CastSpell(
                playerId = activePlayer,
                cardId = creatureId,
                paymentStrategy = PaymentStrategy.FromPool,
            )
        )
        castResult.outcome shouldBe Outcome.Done
    }
})

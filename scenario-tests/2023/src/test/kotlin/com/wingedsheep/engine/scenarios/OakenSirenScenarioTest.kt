package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.OakenSiren
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Oaken Siren (LCI #66) — {1}{U} Artifact Creature — Siren Pirate 1/2
 *
 *   Flying, vigilance
 *   {T}: Add {U}. Spend this mana only to cast an artifact spell or activate an ability of an
 *        artifact source.
 *
 * Exercises [ManaRestriction.CardTypeSpellsOrAbilitiesOnly](CardType.ARTIFACT) with both spells
 * and abilities allowed:
 *  1. Tapping adds exactly one restricted {U} to the pool.
 *  2. The restricted mana can pay for an artifact spell.
 *  3. The restricted mana cannot pay for a non-artifact spell.
 */
class OakenSirenScenarioTest : FunSpec({

    val testArtifact = CardDefinition.artifact(
        name = "Test Artifact",
        manaCost = ManaCost.parse("{U}")
    )
    val testCreature = CardDefinition.creature(
        name = "Test Creature",
        manaCost = ManaCost.parse("{U}"),
        subtypes = setOf(Subtype("Human")),
        power = 1,
        toughness = 1
    )

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + OakenSiren + testArtifact + testCreature)
        return driver
    }

    val manaAbilityId = OakenSiren.activatedAbilities[0].id

    fun GameTestDriver.tapForArtifactMana(playerId: EntityId) {
        val siren = putPermanentOnBattlefield(playerId, "Oaken Siren")
        submit(ActivateAbility(playerId, siren, manaAbilityId))
    }

    test("restricted mana is placed in pool after tapping") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40))
        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.tapForArtifactMana(p1)

        val pool = driver.state.getEntity(p1)?.get<ManaPoolComponent>()
        pool!!.restrictedMana.size shouldBe 1
    }

    test("restricted mana can pay for an artifact spell") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40))
        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.tapForArtifactMana(p1)

        val artifact = driver.putCardInHand(p1, "Test Artifact")
        val result = driver.submit(
            CastSpell(playerId = p1, cardId = artifact, paymentStrategy = PaymentStrategy.FromPool)
        )
        result.isSuccess shouldBe true
    }

    test("restricted mana cannot pay for a non-artifact spell") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40))
        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.tapForArtifactMana(p1)

        val creature = driver.putCardInHand(p1, "Test Creature")
        val result = driver.submit(
            CastSpell(playerId = p1, cardId = creature, paymentStrategy = PaymentStrategy.FromPool)
        )
        result.isSuccess shouldBe false
    }
})

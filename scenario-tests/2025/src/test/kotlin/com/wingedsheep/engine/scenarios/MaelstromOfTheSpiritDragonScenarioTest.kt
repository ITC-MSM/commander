package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.tdm.cards.MaelstromOfTheSpiritDragon
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Maelstrom of the Spirit Dragon (TDM #260) — Land.
 *
 *   {T}: Add {C}.
 *   {T}: Add one mana of any color. Spend this mana only to cast a Dragon spell or an Omen spell.
 *   {4}, {T}, Sacrifice this land: Search your library for a Dragon card, ...
 *
 * Exercises the new [com.wingedsheep.sdk.scripting.effects.ManaRestriction.SubtypeSpellsOnly]:
 * the any-color mana may pay for a Dragon creature spell, but not for a non-Dragon spell.
 */
class MaelstromOfTheSpiritDragonScenarioTest : FunSpec({

    val testDragon = CardDefinition.creature(
        name = "Test Dragon",
        manaCost = ManaCost.parse("{R}"),
        subtypes = setOf(Subtype.DRAGON),
        power = 4,
        toughness = 4
    )
    val testOgre = CardDefinition.creature(
        name = "Test Ogre",
        manaCost = ManaCost.parse("{R}"),
        subtypes = setOf(Subtype("Ogre")),
        power = 2,
        toughness = 2
    )

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + MaelstromOfTheSpiritDragon + testDragon + testOgre)
        return driver
    }

    val anyColorDragonOmenAbility = MaelstromOfTheSpiritDragon.activatedAbilities[1].id

    test("any-color mana tagged for Dragon/Omen can pay for a Dragon spell") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))
        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val maelstrom = driver.putPermanentOnBattlefield(p1, "Maelstrom of the Spirit Dragon")
        // The mana ability pauses for a color choice; pick red.
        driver.submit(ActivateAbility(p1, maelstrom, anyColorDragonOmenAbility))
        driver.state.pendingDecision?.let { decision ->
            driver.submitDecision(
                p1,
                com.wingedsheep.engine.core.ColorChosenResponse(decision.id, com.wingedsheep.sdk.core.Color.RED)
            )
        }

        val pool = driver.state.getEntity(p1)?.get<ManaPoolComponent>()
        pool!!.restrictedMana.size shouldBe 1

        val dragon = driver.putCardInHand(p1, "Test Dragon")
        val result = driver.submit(
            CastSpell(playerId = p1, cardId = dragon, paymentStrategy = PaymentStrategy.FromPool)
        )
        result.isSuccess shouldBe true
    }

    test("any-color mana tagged for Dragon/Omen cannot pay for a non-Dragon, non-Omen spell") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))
        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val maelstrom = driver.putPermanentOnBattlefield(p1, "Maelstrom of the Spirit Dragon")
        driver.submit(ActivateAbility(p1, maelstrom, anyColorDragonOmenAbility))
        driver.state.pendingDecision?.let { decision ->
            driver.submitDecision(
                p1,
                com.wingedsheep.engine.core.ColorChosenResponse(decision.id, com.wingedsheep.sdk.core.Color.RED)
            )
        }

        val ogre = driver.putCardInHand(p1, "Test Ogre")
        val result = driver.submit(
            CastSpell(playerId = p1, cardId = ogre, paymentStrategy = PaymentStrategy.FromPool)
        )
        // The restricted mana can't pay for an Ogre and there's no other mana available.
        result.isSuccess shouldBe false
    }
})

package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.ColorChosenResponse
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.IxallisLorekeeper
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Ixalli's Lorekeeper (LCI #194) — {G} Creature — Human Druid 1/1
 *
 *   {T}: Add one mana of any color. Spend this mana only to cast a Dinosaur spell or
 *        activate an ability of a Dinosaur source.
 *
 * Exercises [ManaRestriction.SubtypeSpellsOrAbilitiesOnly]("Dinosaur") with `creatureOnly = false`:
 *  1. The one restricted mana can pay for a Dinosaur creature spell.
 *  2. The one restricted mana cannot pay for a non-Dinosaur creature spell.
 *  3. After tapping, exactly one restricted mana entry is present in the pool.
 */
class IxallisLorekeeperScenarioTest : FunSpec({

    val testDinosaur = CardDefinition.creature(
        name = "Test Dinosaur",
        manaCost = ManaCost.parse("{G}"),
        subtypes = setOf(Subtype("Dinosaur")),
        power = 2,
        toughness = 2
    )
    val testElf = CardDefinition.creature(
        name = "Test Elf",
        manaCost = ManaCost.parse("{G}"),
        subtypes = setOf(Subtype("Elf")),
        power = 1,
        toughness = 1
    )

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + IxallisLorekeeper + testDinosaur + testElf)
        return driver
    }

    val manaAbilityId = IxallisLorekeeper.activatedAbilities[0].id

    fun GameTestDriver.tapForDinoMana(playerId: EntityId) {
        val lorekeeper = putPermanentOnBattlefield(playerId, "Ixalli's Lorekeeper")
        submit(ActivateAbility(playerId, lorekeeper, manaAbilityId))
        state.pendingDecision?.let { decision ->
            submitDecision(playerId, ColorChosenResponse(decision.id, Color.GREEN))
        }
    }

    test("restricted mana is placed in pool after tapping") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40))
        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.tapForDinoMana(p1)

        val pool = driver.state.getEntity(p1)?.get<ManaPoolComponent>()
        pool!!.restrictedMana.size shouldBe 1
    }

    test("restricted mana can pay for a Dinosaur spell") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40))
        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.tapForDinoMana(p1)

        val dino = driver.putCardInHand(p1, "Test Dinosaur")
        val result = driver.submit(
            CastSpell(playerId = p1, cardId = dino, paymentStrategy = PaymentStrategy.FromPool)
        )
        result.isSuccess shouldBe true
    }

    test("restricted mana cannot pay for a non-Dinosaur spell") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40))
        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.tapForDinoMana(p1)

        val elf = driver.putCardInHand(p1, "Test Elf")
        val result = driver.submit(
            CastSpell(playerId = p1, cardId = elf, paymentStrategy = PaymentStrategy.FromPool)
        )
        result.isSuccess shouldBe false
    }
})

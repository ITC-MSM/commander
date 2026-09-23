package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.battlefield.SagaComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import com.wingedsheep.engine.core.Outcome

/**
 * Engine coverage for **Summon Sagas** — "Enchantment Creature — Saga" permanents (FIN), which are
 * a creature (power/toughness, keywords, combat) *and* a Saga (lore counters + chapter abilities) at
 * the same time (CR 714.1a). Proves the saga machinery (lore accrual CR 714.3c, chapter triggers
 * CR 714.2b, final-chapter sacrifice CR 714.4) co-exists with the permanent being a live creature.
 */
class CreatureSagaTest : FunSpec({

    val projector = StateProjector()

    // A three-chapter Saga that is ALSO a 7/7 creature with reach. Chapters I and II gain life;
    // chapter III (the final chapter) is self-referential: "This creature deals damage equal to its
    // power to each opponent" — EffectTarget.Self must resolve to the saga-creature.
    val SummonTestTitan = card("Summon Test Titan") {
        manaCost = "{3}{G}{G}"
        colorIdentity = "G"
        typeLine = "Enchantment Creature — Saga Giant"
        oracleText = "(As this Saga enters and after your draw step, add a lore counter. " +
            "Sacrifice after III.)\n" +
            "I, II — You gain 2 life.\n" +
            "III — This creature deals damage equal to its power to each opponent.\n" +
            "Reach"
        power = 7
        toughness = 7
        keywords(Keyword.REACH)

        sagaChapter(1) { effect = Effects.GainLife(2) }
        sagaChapter(2) { effect = Effects.GainLife(2) }
        sagaChapter(3) {
            effect = Effects.DealDamage(
                amount = DynamicAmount.EntityProperty(EntityReference.Source, EntityNumericProperty.Power),
                target = EffectTarget.PlayerRef(Player.EachOpponent),
                damageSource = EffectTarget.Self,
            )
        }
    }

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(SummonTestTitan))
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40))
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    /** Cast the saga from hand and resolve it (plus its chapter I ability) onto the battlefield. */
    fun castSummonTitan(driver: GameTestDriver, controller: com.wingedsheep.sdk.model.EntityId) {
        val spell = driver.putCardInHand(controller, "Summon Test Titan")
        driver.giveMana(controller, com.wingedsheep.sdk.core.Color.GREEN, 2)
        driver.giveColorlessMana(controller, 3)
        driver.castSpell(controller, spell)
        driver.bothPass()
        var guard = 0
        while (guard++ < 20 && driver.state.stack.isNotEmpty()) driver.bothPass()
    }

    /** Advance to the active player's *next* precombat main (stepping out via END first). */
    fun advanceToNextTurnMain(driver: GameTestDriver) {
        driver.passPriorityUntil(Step.END, maxPasses = 300)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN, maxPasses = 300)
    }

    test("enters as a creature AND a Saga: lore counter, chapter I, projected P/T + keyword") {
        val driver = newDriver()
        val active = driver.activePlayer!!
        val startLife = driver.getLifeTotal(active)

        castSummonTitan(driver, active)

        val saga = driver.findPermanent(active, "Summon Test Titan")
        saga shouldNotBe null

        val container = driver.state.getEntity(saga!!)!!
        // Saga machinery applied to a creature: SagaComponent present, chapter 1 marked triggered,
        // one lore counter (CR 714.3a entry replacement).
        val sagaComp = container.get<SagaComponent>()
        sagaComp shouldNotBe null
        sagaComp!!.triggeredChapters.contains(1) shouldBe true
        container.get<CountersComponent>()!!.getCount(CounterType.LORE) shouldBe 1

        // Chapter I fired on entry → +2 life.
        driver.getLifeTotal(active) shouldBe startLife + 2

        // It is simultaneously a creature and a Saga in projection, with its printed P/T + keyword.
        val projected = projector.project(driver.state)
        projected.isCreature(saga) shouldBe true
        projected.hasType(saga, "ENCHANTMENT") shouldBe true
        // Subtypes keep their printed casing in projection (card types are uppercase enum names).
        projected.hasType(saga, "Saga") shouldBe true
        projected.hasType(saga, "Giant") shouldBe true
        projected.getPower(saga) shouldBe 7
        projected.getToughness(saga) shouldBe 7
        projected.hasKeyword(saga, Keyword.REACH) shouldBe true
    }

    test("lore accrues at the controller's precombat main and fires chapter II") {
        val driver = newDriver()
        val active = driver.activePlayer!!
        val startLife = driver.getLifeTotal(active)

        castSummonTitan(driver, active)
        driver.getLifeTotal(active) shouldBe startLife + 2 // chapter I

        // Back to the controller's next precombat main → turn-based lore accrual (CR 714.3c).
        advanceToNextTurnMain(driver) // opponent's turn
        advanceToNextTurnMain(driver) // controller's next turn
        driver.state.activePlayerId shouldBe active

        var guard = 0
        while (guard++ < 20 && driver.state.stack.isNotEmpty()) driver.bothPass()

        val saga = driver.findPermanent(active, "Summon Test Titan")!!
        driver.state.getEntity(saga)!!.get<CountersComponent>()!!.getCount(CounterType.LORE) shouldBe 2
        // Chapter II resolved → another +2 life (life may also have changed from the draw of the
        // turn, but life-gain is the only life change here).
        driver.getLifeTotal(active) shouldBe startLife + 4
    }

    test("final chapter is self-referential (Self = the saga-creature) and then it is sacrificed") {
        val driver = newDriver()
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)
        val oppStartLife = driver.getLifeTotal(opponent)

        castSummonTitan(driver, active)
        advanceToNextTurnMain(driver) // opp turn 2
        advanceToNextTurnMain(driver) // active turn 3 → lore 2, chapter II
        var g = 0
        while (g++ < 20 && driver.state.stack.isNotEmpty()) driver.bothPass()
        advanceToNextTurnMain(driver) // opp turn 4
        advanceToNextTurnMain(driver) // active turn 5 → lore 3, chapter III (final)
        g = 0
        while (g++ < 20 && driver.state.stack.isNotEmpty()) driver.bothPass()

        // Self resolved to the 7/7 saga → 7 damage to the opponent.
        driver.getLifeTotal(opponent) shouldBe oppStartLife - 7

        // CR 714.4 — lore >= final chapter and no chapter on the stack → controller sacrifices it.
        driver.findPermanent(active, "Summon Test Titan") shouldBe null
        driver.getGraveyardCardNames(active).contains("Summon Test Titan") shouldBe true
    }

    test("the saga-creature can attack as a normal creature") {
        val driver = newDriver()
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)

        castSummonTitan(driver, active)
        val saga = driver.findPermanent(active, "Summon Test Titan")!!
        driver.removeSummoningSickness(saga)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS, maxPasses = 100)
        driver.declareAttackers(active, listOf(saga), opponent).outcome shouldBe Outcome.Done
    }
})

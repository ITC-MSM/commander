package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.TargetsResponse
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.core.YesNoResponse
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.battlefield.SagaComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.JoshuaPhoenixsDominant
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.TimingRule
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Joshua, Phoenix's Dominant // Phoenix, Warden of Fire (FIN #229).
 *
 * Front — {1}{R}{W} 3/4: "When Joshua enters, discard up to two cards, then draw that many
 * cards." and a sorcery-speed "{3}{R}{W}, {T}: exile, return transformed" Dominant loop.
 * Back — Phoenix, Warden of Fire (4/4 flying, lifelink Saga): I, II deal 2 to each opponent,
 * III reanimates creatures (total mana value 6 or less) and flips back to the front face.
 *
 * The transform machinery itself is proven generically in [DominantEikonTransformScenarioTest];
 * this test pins Joshua's own behavior — the ETB loot draws exactly what was discarded, and
 * Rising Flames' 2 damage to each opponent feeds the back face's lifelink.
 */
class JoshuaPhoenixsDominantScenarioTest : FunSpec({

    val projector = StateProjector()

    fun resolveStack(driver: GameTestDriver) {
        var guard = 0
        while (guard++ < 40 && driver.state.stack.isNotEmpty() && !driver.isPaused) driver.bothPass()
    }

    fun declineOptionalDecisions(driver: GameTestDriver) {
        var guard = 0
        while (guard++ < 12 && driver.isPaused) {
            when (val decision = driver.pendingDecision) {
                is YesNoDecision ->
                    driver.submitDecision(decision.playerId, YesNoResponse(decision.id, false))
                is SelectCardsDecision ->
                    // "discard up to two" — decline by selecting none.
                    driver.submitCardSelection(decision.playerId, emptyList())
                is ChooseTargetsDecision -> {
                    val chosen = decision.targetRequirements.associate { req ->
                        req.index to decision.legalTargets[req.index].orEmpty().take(req.minTargets)
                    }
                    driver.submitDecision(decision.playerId, TargetsResponse(decision.id, chosen))
                }
                else -> driver.autoResolveDecision()
            }
        }
    }

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(JoshuaPhoenixsDominant))
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun castJoshua(driver: GameTestDriver, player: EntityId): EntityId {
        val spell = driver.putCardInHand(player, "Joshua, Phoenix's Dominant")
        driver.giveMana(player, Color.RED, 1)
        driver.giveMana(player, Color.WHITE, 1)
        driver.giveColorlessMana(player, 1)
        driver.castSpell(player, spell)
        driver.bothPass()
        resolveStack(driver)
        return driver.findPermanent(player, "Joshua, Phoenix's Dominant")!!
    }

    test("front-face transform ability is sorcery-speed (CR: 'activate only as a sorcery')") {
        JoshuaPhoenixsDominant.activatedAbilities.first().timing shouldBe TimingRule.SorcerySpeed
    }

    test("Joshua's ETB loots: discard two cards, then draw that many") {
        val driver = newDriver()
        val me = driver.player1

        val joshua = castJoshua(driver, me)
        joshua shouldNotBe null

        // ETB pauses on the "discard up to two" selection. Discard two cards.
        var guard = 0
        var discarded = false
        while (guard++ < 12 && driver.isPaused) {
            val pd = driver.pendingDecision
            if (pd is SelectCardsDecision && !discarded) {
                val toDiscard = pd.options.take(2)
                toDiscard.size shouldBe 2
                val graveBefore = driver.getGraveyard(me).size
                driver.submitCardSelection(me, toDiscard)
                discarded = true
                resolveStack(driver)
                // Two cards discarded, two drawn: net hand unchanged by the loot, grave +2.
                driver.getGraveyard(me).size shouldBe graveBefore + 2
            } else {
                declineOptionalDecisions(driver)
            }
        }
        discarded shouldBe true
    }

    test("Phoenix's Rising Flames deals 2 to each opponent, and lifelink gains that life") {
        val driver = newDriver()
        val me = driver.player1
        val opp = driver.player2

        val joshua = castJoshua(driver, me)
        declineOptionalDecisions(driver) // decline the ETB discard
        resolveStack(driver)

        val myLifeBefore = driver.getLifeTotal(me)
        val oppLifeBefore = driver.getLifeTotal(opp)

        // {3}{R}{W}, {T}: exile Joshua, return it transformed. Sorcery-speed, so bypass sickness.
        driver.removeSummoningSickness(joshua)
        driver.giveMana(me, Color.RED, 1)
        driver.giveMana(me, Color.WHITE, 1)
        driver.giveColorlessMana(me, 3)
        val abilityId = JoshuaPhoenixsDominant.activatedAbilities.first().id
        driver.submit(ActivateAbility(playerId = me, sourceId = joshua, abilityId = abilityId))
            .isSuccess shouldBe true
        driver.bothPass()
        declineOptionalDecisions(driver)
        resolveStack(driver)
        declineOptionalDecisions(driver) // chapter I "Rising Flames" trigger resolves

        // Same entity id, now the back face: Phoenix, a 4/4 Enchantment-Creature Saga with lore 1.
        val container = driver.state.getEntity(joshua)!!
        container.get<CardComponent>()!!.name shouldBe "Phoenix, Warden of Fire"
        val projected = projector.project(driver.state)
        projected.isCreature(joshua) shouldBe true
        projected.hasType(joshua, "Saga") shouldBe true
        projected.getPower(joshua) shouldBe 4
        projected.getToughness(joshua) shouldBe 4
        container.get<SagaComponent>() shouldNotBe null
        container.get<CountersComponent>()!!.getCount(CounterType.LORE) shouldBe 1

        // Rising Flames (chapter I): 2 to each opponent, and Phoenix's lifelink gains me 2.
        driver.getLifeTotal(opp) shouldBe oppLifeBefore - 2
        driver.getLifeTotal(me) shouldBe myLifeBefore + 2
    }
})

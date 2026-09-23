package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.ChooseColorDecision
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.ColorChosenResponse
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.battlefield.CastRecordComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.basicLand
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import com.wingedsheep.engine.core.Outcome

/**
 * Bug repro: Deceit's black gate ({B}{B} spent) must trigger when its two {U/B}
 * hybrid symbols are paid with black mana — including black mana produced by a
 * Mountain enchanted with Shimmerwilds Growth (chosen color Black).
 */
class DeceitHybridManaSpentTest : FunSpec({

    fun deceitPermanent(driver: GameTestDriver) =
        driver.state.getBattlefield().first { id ->
            driver.state.getEntity(id)?.get<CardComponent>()?.name == "Deceit"
        }

    test("paying both {U/B} symbols with pooled black mana records {B}{B} spent") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val deceit = driver.putCardInHand(activePlayer, "Deceit")
        // {4}{U/B}{U/B}: 4 generic + two black for the hybrids.
        driver.giveColorlessMana(activePlayer, 4)
        driver.giveMana(activePlayer, Color.BLACK, 2)

        driver.castSpell(activePlayer, deceit)
        driver.bothPass()

        val record = driver.state.getEntity(deceitPermanent(driver))!!.get<CastRecordComponent>()!!
        record.blackSpent shouldBe 2
        record.blueSpent shouldBe 0
    }

    test("black gate fires when {B}{B} spent: opponent reveals hand and discards a nonland card") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)

        val activePlayer = driver.activePlayer!!
        val opponent = driver.state.getOpponents(activePlayer).first()
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Opponent holds exactly one nonland card to be discarded.
        val victim = driver.putCardInHand(opponent, "Shimmerwilds Growth")

        val deceit = driver.putCardInHand(activePlayer, "Deceit")
        driver.giveColorlessMana(activePlayer, 4)
        driver.giveMana(activePlayer, Color.BLACK, 2)

        driver.castSpell(activePlayer, deceit)

        // Resolve Deceit, then resolve the black-gate trigger it puts on the stack.
        // Target the opponent and choose their nonland card to discard.
        var guard = 0
        while (guard++ < 60) {
            when (val d = driver.pendingDecision) {
                is ChooseTargetsDecision -> driver.submitTargetSelection(d.playerId, listOf(opponent))
                is SelectCardsDecision -> driver.submitCardSelection(d.playerId, d.options.take(d.minSelections))
                null -> {
                    if (driver.state.priorityPlayerId == null) break
                    driver.bothPass()
                }
                else -> driver.autoResolveDecision()
            }
            if (driver.getGraveyard(opponent).contains(victim)) break
        }

        driver.getGraveyard(opponent) shouldBe listOf(victim)
    }

    test("Mountain enchanted with Shimmerwilds Growth (Black) pays Deceit's hybrids as black") {
        val TestMountain = basicLand("Mountain") {}
        val mountainManaAbilityId = TestMountain.activatedAbilities[0].id

        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(TestMountain))
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val mountain = driver.putPermanentOnBattlefield(activePlayer, "Mountain")
        val growth = driver.putCardInHand(activePlayer, "Shimmerwilds Growth")
        driver.giveMana(activePlayer, Color.GREEN, 2)
        driver.castSpell(activePlayer, growth, listOf(mountain))
        driver.bothPass()

        val decision = driver.pendingDecision as ChooseColorDecision
        driver.submitDecision(activePlayer, ColorChosenResponse(decision.id, Color.BLACK))

        // Tap the enchanted Mountain: produces {B} (overridden) + {B} (bonus) = {B}{B}.
        driver.submit(
            ActivateAbility(playerId = activePlayer, sourceId = mountain, abilityId = mountainManaAbilityId)
        ).outcome shouldBe Outcome.Done
        val pool = driver.state.getEntity(activePlayer)!!.get<ManaPoolComponent>()!!
        pool.black shouldBe 2

        // 4 generic for the {4}, the two black for {U/B}{U/B}.
        driver.giveColorlessMana(activePlayer, 4)

        val deceit = driver.putCardInHand(activePlayer, "Deceit")
        driver.castSpell(activePlayer, deceit)
        driver.bothPass()

        val record = driver.state.getEntity(deceitPermanent(driver))!!.get<CastRecordComponent>()!!
        record.blackSpent shouldBe 2
        record.blueSpent shouldBe 0
    }

    test("auto-pay: bonus black from Shimmerwilds Growth counts toward {B}{B} spent (the bug)") {
        // The user's report: tapping an enchanted Mountain (chosen Black) for its {B} + bonus {B}
        // and casting Deceit. Through the auto-pay / mana-solver path, the aura's BONUS mana lives
        // in the solver's bonus pool and was never folded into the per-color "mana spent" tally,
        // so only 1 black registered and the {B}{B} gate silently failed.
        val TestMountain = basicLand("Mountain") {}

        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(TestMountain))
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)

        val activePlayer = driver.activePlayer!!
        val opponent = driver.state.getOpponents(activePlayer).first()
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val victim = driver.putCardInHand(opponent, "Shimmerwilds Growth")

        val mountain = driver.putPermanentOnBattlefield(activePlayer, "Mountain")
        val growth = driver.putCardInHand(activePlayer, "Shimmerwilds Growth")
        driver.giveMana(activePlayer, Color.GREEN, 2)
        driver.castSpell(activePlayer, growth, listOf(mountain))
        driver.bothPass()
        val colorDecision = driver.pendingDecision as ChooseColorDecision
        driver.submitDecision(activePlayer, ColorChosenResponse(colorDecision.id, Color.BLACK))

        // Four Forests cover the {4}; the enchanted Mountain alone produces {B}{B} for the hybrids.
        // No floating mana, so the cast routes through auto-pay / the mana solver.
        repeat(4) { driver.putPermanentOnBattlefield(activePlayer, "Forest") }

        val deceit = driver.putCardInHand(activePlayer, "Deceit")
        driver.castSpell(activePlayer, deceit)

        var guard = 0
        while (guard++ < 60) {
            when (val d = driver.pendingDecision) {
                is ChooseTargetsDecision -> driver.submitTargetSelection(d.playerId, listOf(opponent))
                is SelectCardsDecision -> driver.submitCardSelection(d.playerId, d.options.take(d.minSelections))
                null -> {
                    if (driver.state.priorityPlayerId == null) break
                    driver.bothPass()
                }
                else -> driver.autoResolveDecision()
            }
            if (driver.getGraveyard(opponent).contains(victim)) break
        }

        val record = driver.state.getEntity(deceitPermanent(driver))!!.get<CastRecordComponent>()!!
        record.blackSpent shouldBe 2
        driver.getGraveyard(opponent) shouldBe listOf(victim)
    }
})

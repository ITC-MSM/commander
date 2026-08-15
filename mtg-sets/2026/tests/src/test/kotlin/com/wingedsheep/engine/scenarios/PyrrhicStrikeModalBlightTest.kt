package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.core.TargetsResponse
import com.wingedsheep.engine.legalactions.support.EnumerationTestDriver
import com.wingedsheep.engine.legalactions.support.setupP1
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.mtg.sets.definitions.ecl.cards.PyrrhicStrike
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Pyrrhic Strike (ECL #30) — {2}{W} Instant.
 *
 *   "As an additional cost to cast this spell, you may blight 2.
 *    Choose one. If this spell's additional cost was paid, choose both instead.
 *    • Destroy target artifact or enchantment.
 *    • Destroy target creature with mana value 3 or greater."
 *
 * Regression for a "can only choose one mode" bug on the blight path: the engine surfaces the
 * blight path as a distinct `CastSpellModal` variant whose `modalEnumeration` forces every mode,
 * but it only unlocks those extra modes once the submitted action actually carries `blightTargets`
 * (and it reads the same field to apply the −1/−1 counters). The web client's modal pipeline used
 * to submit `chosenModes` without ever collecting the blight target, so the engine rejected the
 * two-mode cast with "Too many modes chosen". These tests pin the engine contract the client must
 * satisfy.
 */
class PyrrhicStrikeModalBlightTest : FunSpec({

    fun setup(): EnumerationTestDriver = setupP1(
        hand = listOf("Pyrrhic Strike"),
        battlefield = listOf(
            "Force of Nature",  // 5/5 blight fodder
            "Centaur Courser",  // 3/3, mana value 3 → "creature with MV 3 or greater"
            "Test Enchantment", // enchantment → "artifact or enchantment"
            "Plains", "Plains", "Plains",
        ),
        extraSetCards = listOf(PyrrhicStrike),
    )

    fun EnumerationTestDriver.bf(name: String): EntityId =
        game.state.getZone(ZoneKey(player1, Zone.BATTLEFIELD))
            .first { game.state.getEntity(it)?.get<CardComponent>()?.name == name }

    fun EnumerationTestDriver.handCard(name: String): EntityId =
        game.state.getZone(ZoneKey(player1, Zone.HAND))
            .first { game.state.getEntity(it)?.get<CardComponent>()?.name == name }

    test("blight path is a distinct modal variant that forces choosing both modes") {
        val driver = setup()
        val casts = driver.enumerateFor(driver.player1).castActionsFor("Pyrrhic Strike")

        // Pay path: "choose one" → one CastSpellMode per mode (the player picks exactly one).
        val payModes = casts.filter { it.actionType == "CastSpellMode" }
        payModes shouldHaveSize 2

        // Blight path: a single CastSpellModal whose enumeration locks the player into both modes.
        val blightVariant = casts.single { it.actionType == "CastSpellModal" }
        val enumeration = blightVariant.modalEnumeration.shouldNotBeNull()
        enumeration.chooseCount shouldBe 2
        enumeration.minChooseCount shouldBe 2
    }

    test("casting via the blight path destroys both targets and blights the fodder") {
        val driver = setup()
        val p1 = driver.player1
        val fodder = driver.bf("Force of Nature")
        val enchantment = driver.bf("Test Enchantment")
        val creature = driver.bf("Centaur Courser")
        val pyrrhic = driver.handCard("Pyrrhic Strike")

        // The payload the (fixed) client submits to take the blight path: both modes chosen and
        // the blight target that commits the player to "choose both". Per-mode targeting is then
        // driven server-side (CR 601.2c), exactly as in-game — the client supplies no flat targets.
        val result = driver.game.submit(
            CastSpell(
                playerId = p1,
                cardId = pyrrhic,
                paymentStrategy = PaymentStrategy.AutoPay,
                chosenModes = listOf(0, 1),
                additionalCostPayment = AdditionalCostPayment(
                    blightAmount = 2,
                    blightTargets = listOf(fodder),
                ),
            )
        )
        result.error shouldBe null

        // Answer the per-mode target prompts: mode 0 → the enchantment, mode 1 → Centaur Courser.
        val desiredPerMode = mapOf(0 to enchantment, 1 to creature)
        var safety = 0
        while (driver.game.pendingDecision is ChooseTargetsDecision && safety < 10) {
            val decision = driver.game.pendingDecision as ChooseTargetsDecision
            val req = decision.targetRequirements.first()
            val legal = decision.legalTargets[req.index].orEmpty()
            // Pick whichever of the two intended targets is legal for the mode being resolved.
            val pick = desiredPerMode.values.first { it in legal }
            driver.game.submitDecision(
                p1, TargetsResponse(decision.id, mapOf(req.index to listOf(pick)))
            )
            safety++
        }

        // Resolve the spell off the stack.
        safety = 0
        while (driver.game.stackSize > 0 && safety < 20) { driver.game.bothPass(); safety++ }

        // Both targets destroyed.
        driver.game.findPermanent(p1, "Test Enchantment") shouldBe null
        driver.game.findPermanent(p1, "Centaur Courser") shouldBe null

        // Fodder took blight 2 (and survives as a 3/3).
        driver.game.findPermanent(p1, "Force of Nature").shouldNotBeNull()
        driver.game.state.getEntity(fodder)?.get<CountersComponent>()
            ?.getCount(CounterType.MINUS_ONE_MINUS_ONE) shouldBe 2
    }

    test("choosing both modes without paying blight is rejected") {
        // The "choose one" floor without blight is exactly one mode (CR 700.2 / the printed
        // "Choose one"). The two-mode cast is only legal on the blight path, so an action that
        // claims both modes but carries no blight payment must be refused.
        val driver = setup()
        val p1 = driver.player1
        val pyrrhic = driver.handCard("Pyrrhic Strike")

        val result = driver.game.submit(
            CastSpell(
                playerId = p1,
                cardId = pyrrhic,
                paymentStrategy = PaymentStrategy.AutoPay,
                chosenModes = listOf(0, 1),
            )
        )
        result.isSuccess shouldBe false
    }
})

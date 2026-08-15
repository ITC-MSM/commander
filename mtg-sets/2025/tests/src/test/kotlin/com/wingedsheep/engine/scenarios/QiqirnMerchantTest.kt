package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.QiqirnMerchant
import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.TypeLine
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe

/**
 * Tests for Qiqirn Merchant (FIN) — proves activated-ability cost reduction *by permanent count*.
 *
 * "{7}, {T}, Sacrifice this creature: Draw three cards. This ability costs {1} less to activate for
 * each Town you control." rides the engine's per-activation
 * [com.wingedsheep.sdk.scripting.ActivatedAbility.genericCostReduction] field fed
 * `DynamicAmounts.battlefield(You, Land — Town).count()`. The same field already powers
 * The Dominion Bracelet (X = creature's power) and Dragonfire Blade (X = target's color count);
 * here the [com.wingedsheep.sdk.scripting.values.DynamicAmount] is a battlefield count, so no new
 * engine vocabulary is required — these tests pin that the reduction lowers the displayed/affordable
 * and paid generic cost, floors at {0}, and never goes negative.
 *
 * A Town here is an inert "Land — Town" with no mana ability, so the only mana available is what the
 * test grants — exact-cost assertions stay clean.
 */
class QiqirnMerchantTest : FunSpec({

    // The cost-reduced "draw three" ability is the one carrying genericCostReduction; the looter is the other.
    val drawThreeAbilityId = QiqirnMerchant.activatedAbilities.first { it.genericCostReduction != null }.id
    val looterAbilityId = QiqirnMerchant.activatedAbilities.first { it.genericCostReduction == null }.id

    // Inert Land — Town (no mana ability) used purely as a "Town you control" for the count.
    val town = CardDefinition(
        name = "Test Town",
        manaCost = ManaCost.ZERO,
        typeLine = TypeLine(cardTypes = setOf(CardType.LAND), subtypes = setOf(Subtype("Town"))),
    )

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(QiqirnMerchant)
        driver.registerCard(town)
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    /** Put a ready (no summoning sickness) Qiqirn Merchant onto [player]'s battlefield. */
    fun GameTestDriver.readyMerchant(player: com.wingedsheep.sdk.model.EntityId): com.wingedsheep.sdk.model.EntityId {
        val merchant = putCreatureOnBattlefield(player, "Qiqirn Merchant")
        removeSummoningSickness(merchant)
        return merchant
    }

    /**
     * Add [count] Towns to [player]'s battlefield, tapped. The engine grants every land an intrinsic
     * mana ability, so untapped Towns would help pay the very cost we're metering. Tapped Towns still
     * satisfy the "Town you control" count (the filter has no untapped requirement), so the only mana
     * the test affords is what it explicitly grants.
     */
    fun GameTestDriver.addTowns(player: com.wingedsheep.sdk.model.EntityId, count: Int) {
        repeat(count) { tapPermanent(putLandOnBattlefield(player, "Test Town")) }
    }

    test("draw-three costs the full {7} with no Towns in play") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        val merchant = driver.readyMerchant(player)
        val handBefore = driver.getHandSize(player)

        // {6} is one short of the unreduced {7}.
        driver.giveColorlessMana(player, 6)
        driver.submitExpectFailure(ActivateAbility(player, merchant, drawThreeAbilityId))
        driver.state.getBattlefield(player) shouldContain merchant // not yet sacrificed

        // The seventh mana covers it; the ability resolves and draws three.
        driver.giveColorlessMana(player, 1)
        driver.submit(ActivateAbility(player, merchant, drawThreeAbilityId)).isSuccess shouldBe true
        driver.bothPass()

        driver.getHandSize(player) shouldBe handBefore + 3
        driver.state.getBattlefield(player) shouldNotContain merchant // sacrificed as a cost
        driver.getGraveyard(player) shouldContain merchant
    }

    test("each Town you control reduces the cost by {1} — three Towns make it {4}") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        val merchant = driver.readyMerchant(player)
        driver.addTowns(player, 3)
        val handBefore = driver.getHandSize(player)

        // {7} - 3 Towns = {4}. Three mana is not enough...
        driver.giveColorlessMana(player, 3)
        driver.submitExpectFailure(ActivateAbility(player, merchant, drawThreeAbilityId))
        driver.state.getBattlefield(player) shouldContain merchant

        // ...the fourth mana is.
        driver.giveColorlessMana(player, 1)
        driver.submit(ActivateAbility(player, merchant, drawThreeAbilityId)).isSuccess shouldBe true
        driver.bothPass()

        driver.getHandSize(player) shouldBe handBefore + 3
        driver.getGraveyard(player) shouldContain merchant
    }

    test("the reduction floors at {0} and never goes negative with more Towns than generic mana") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        val merchant = driver.readyMerchant(player)
        driver.addTowns(player, 8) // 8 > the {7} generic; tapped, so they afford no mana themselves
        val handBefore = driver.getHandSize(player)

        // {7} - 8 Towns floors at {0}: activatable with no mana at all (CR 118.9a — never below zero).
        driver.submit(ActivateAbility(player, merchant, drawThreeAbilityId)).isSuccess shouldBe true
        driver.bothPass()

        driver.getHandSize(player) shouldBe handBefore + 3
        driver.getGraveyard(player) shouldContain merchant
    }

    test("the looter ability is unaffected by Towns and loots one card") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        val merchant = driver.readyMerchant(player)
        driver.addTowns(player, 5) // must not discount this ability
        val toDiscard = driver.putCardInHand(player, "Mountain")
        val handBefore = driver.getHandSize(player)

        // The Town count must not reduce the looter's {1} — zero mana fails, one mana succeeds.
        driver.submitExpectFailure(ActivateAbility(player, merchant, looterAbilityId))
        driver.giveColorlessMana(player, 1)
        driver.submit(ActivateAbility(player, merchant, looterAbilityId)).isSuccess shouldBe true
        driver.bothPass()

        // Drew one, then discard the card we planted: net hand unchanged, Merchant still in play.
        driver.submitCardSelection(player, listOf(toDiscard))
        driver.getHandSize(player) shouldBe handBefore
        driver.getGraveyard(player) shouldContain toDiscard
        driver.state.getBattlefield(player) shouldContain merchant
    }
})

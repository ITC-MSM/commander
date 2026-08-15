package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.legalactions.EnumerationMode
import com.wingedsheep.engine.legalactions.LegalActionEnumerator
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.SummoningSicknessComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.blb.cards.BonebindOrator
import com.wingedsheep.mtg.sets.definitions.blb.cards.CamelliaTheSeedmiser
import com.wingedsheep.mtg.sets.definitions.blb.cards.FeedTheCycle
import com.wingedsheep.mtg.sets.definitions.blb.cards.OsteomancerAdept
import com.wingedsheep.mtg.sets.tokens.PredefinedTokens
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.collections.shouldNotContainAnyOf
import io.kotest.matchers.shouldBe

/**
 * Forage is "exile three cards from your graveyard or sacrifice a Food" — a *choice* cost. These
 * tests pin the unified cost-based forage behaviour (see
 * [com.wingedsheep.engine.handlers.costs.ForageCostResolver]): when both modes are payable the
 * player gets to choose the mode, and within a mode the player picks which cards / Food — the
 * engine never silently picks for them.
 *
 * Covers the activated-ability path ([com.wingedsheep.sdk.scripting.AbilityCost.Forage], Camellia)
 * and the graveyard-cast path (Osteomancer Adept). The auto-pay fallback (no choice supplied) is
 * covered by [CamelliaTheSeedmiserForageTest] and [OsteomancerAdeptForageCastTest].
 */
class ForageCostUnificationTest : FunSpec({

    val camelliaAbilityId = CamelliaTheSeedmiser.activatedAbilities.first().id
    val osteomancerAbilityId = OsteomancerAdept.activatedAbilities.first().id

    // ---------------------------------------------------------------------------------------------
    // Activated-ability forage (Camellia, the Seedmiser): {2}, Forage
    // ---------------------------------------------------------------------------------------------

    fun camelliaDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(CamelliaTheSeedmiser, PredefinedTokens.Food))
        return driver
    }

    test("Forage offers both modes (exile 3 / sacrifice a Food) as separate actions when both are payable") {
        val driver = camelliaDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 20, "Swamp" to 20))
        val active = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val camellia = driver.putCreatureOnBattlefield(active, "Camellia, the Seedmiser")
        driver.replaceState(driver.state.updateEntity(camellia) { it.without<SummoningSicknessComponent>() })
        val graveCards = (1..5).map { driver.putCardInGraveyard(active, "Forest") }
        val food = driver.putPermanentOnBattlefield(active, "Food")
        repeat(3) { driver.putLandOnBattlefield(active, "Forest") }

        val enumerator = LegalActionEnumerator.create(driver.cardRegistry)
        val forageActions = enumerator.enumerate(driver.state, active, EnumerationMode.FULL).filter {
            val a = it.action
            a is ActivateAbility && a.sourceId == camellia && a.abilityId == camelliaAbilityId
        }

        // One legal action per available forage mode — the player picks in the action menu.
        forageActions.size shouldBe 2
        val exileMode = forageActions.single { it.additionalCostInfo?.costType == "ExileFromGraveyard" }.additionalCostInfo!!
        val sacrificeMode = forageActions.single { it.additionalCostInfo?.costType == "SacrificePermanent" }.additionalCostInfo!!

        exileMode.exileMinCount shouldBe 3
        exileMode.exileMaxCount shouldBe 3
        exileMode.validExileTargets shouldContainAll graveCards

        sacrificeMode.sacrificeCount shouldBe 1
        sacrificeMode.validSacrificeTargets shouldContain food
    }

    test("Choosing the sacrifice mode sacrifices the chosen Food and leaves the graveyard untouched") {
        val driver = camelliaDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 20, "Swamp" to 20))
        val active = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val camellia = driver.putCreatureOnBattlefield(active, "Camellia, the Seedmiser")
        driver.replaceState(driver.state.updateEntity(camellia) { it.without<SummoningSicknessComponent>() })
        val graveCards = (1..5).map { driver.putCardInGraveyard(active, "Forest") }
        val food = driver.putPermanentOnBattlefield(active, "Food")
        repeat(3) { driver.putLandOnBattlefield(active, "Forest") }

        val result = driver.submit(
            ActivateAbility(
                playerId = active,
                sourceId = camellia,
                abilityId = camelliaAbilityId,
                costPayment = AdditionalCostPayment(sacrificedPermanents = listOf(food))
            )
        )
        result.isSuccess shouldBe true

        // The Food was sacrificed; not a single graveyard card was exiled.
        driver.state.getBattlefield(active) shouldNotContain food
        driver.state.getGraveyard(active) shouldContain food
        driver.state.getExile(active) shouldNotContainAnyOf graveCards
        driver.state.getGraveyard(active) shouldContainAll graveCards
    }

    // ---------------------------------------------------------------------------------------------
    // Graveyard-cast forage (Osteomancer Adept) — the cast card is excluded from the exile pool.
    // ---------------------------------------------------------------------------------------------

    fun osteomancerDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(OsteomancerAdept, BonebindOrator, PredefinedTokens.Food))
        return driver
    }

    fun grantForageAndAdvanceToCast(driver: GameTestDriver): Pair<com.wingedsheep.sdk.model.EntityId, com.wingedsheep.sdk.model.EntityId> {
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 20))
        val active = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val adept = driver.putCreatureOnBattlefield(active, "Osteomancer Adept")
        driver.removeSummoningSickness(adept)
        driver.submitSuccess(ActivateAbility(playerId = active, sourceId = adept, abilityId = osteomancerAbilityId))
        driver.bothPass()
        driver.giveMana(active, Color.BLACK, 2)
        return active to adept
    }

    test("Graveyard-cast forage advertises which graveyard cards can be exiled, excluding the cast card") {
        val driver = osteomancerDriver()
        val (active, _) = grantForageAndAdvanceToCast(driver)
        val orator = driver.putCardInGraveyard(active, "Bonebind Orator")
        val filler = (1..5).map { driver.putCardInGraveyard(active, "Swamp") }

        val enumerator = LegalActionEnumerator.create(driver.cardRegistry)
        val forageCast = enumerator.enumerate(driver.state, active, EnumerationMode.FULL).single {
            val a = it.action
            a is CastSpell && a.cardId == orator && it.requiresForage
        }

        val cost = forageCast.additionalCostInfo!!
        cost.costType shouldBe "ExileFromGraveyard"
        cost.exileMinCount shouldBe 3
        cost.exileMaxCount shouldBe 3
        cost.validExileTargets shouldContainAll filler
        // The creature being cast can't be one of the three cards it exiles to pay for itself.
        cost.validExileTargets shouldNotContain orator
    }

    test("Graveyard-cast forage exiles exactly the player-chosen three cards, not the first three") {
        val driver = osteomancerDriver()
        val (active, _) = grantForageAndAdvanceToCast(driver)
        val orator = driver.putCardInGraveyard(active, "Bonebind Orator")
        val filler = (1..5).map { driver.putCardInGraveyard(active, "Swamp") }

        val chosen = listOf(filler[1], filler[3], filler[4])
        val result = driver.submit(
            CastSpell(
                playerId = active,
                cardId = orator,
                additionalCostPayment = AdditionalCostPayment(exiledCards = chosen)
            )
        )
        result.isSuccess shouldBe true

        val exile = driver.state.getZone(ZoneKey(active, Zone.EXILE))
        exile.size shouldBe 3
        exile shouldContainAll chosen
        exile shouldNotContain filler[0]
        exile shouldNotContain filler[2]
        // The creature being cast went to the stack, not exile.
        exile shouldNotContain orator
        driver.state.stack shouldContain orator
    }

    test("Graveyard-cast forage offers both modes when a Food is also available") {
        val driver = osteomancerDriver()
        val (active, _) = grantForageAndAdvanceToCast(driver)
        val orator = driver.putCardInGraveyard(active, "Bonebind Orator")
        (1..3).map { driver.putCardInGraveyard(active, "Swamp") }
        driver.putPermanentOnBattlefield(active, "Food")

        val enumerator = LegalActionEnumerator.create(driver.cardRegistry)
        val forageCasts = enumerator.enumerate(driver.state, active, EnumerationMode.FULL).filter {
            val a = it.action
            a is CastSpell && a.cardId == orator && it.requiresForage
        }

        forageCasts.size shouldBe 2
        forageCasts.count { it.additionalCostInfo?.costType == "ExileFromGraveyard" } shouldBe 1
        forageCasts.count { it.additionalCostInfo?.costType == "SacrificePermanent" } shouldBe 1
    }

    test("Graveyard-cast forage with a chosen Food sacrifices that Food and exiles nothing") {
        val driver = osteomancerDriver()
        val (active, _) = grantForageAndAdvanceToCast(driver)
        val orator = driver.putCardInGraveyard(active, "Bonebind Orator")
        val filler = (1..3).map { driver.putCardInGraveyard(active, "Swamp") }
        val food = driver.putPermanentOnBattlefield(active, "Food")

        val result = driver.submit(
            CastSpell(
                playerId = active,
                cardId = orator,
                additionalCostPayment = AdditionalCostPayment(sacrificedPermanents = listOf(food))
            )
        )
        result.isSuccess shouldBe true

        driver.state.getBattlefield(active) shouldNotContain food
        driver.state.getZone(ZoneKey(active, Zone.EXILE)) shouldNotContainAnyOf filler
        driver.state.getZone(ZoneKey(active, Zone.GRAVEYARD)) shouldContainAll filler
        driver.state.stack shouldContain orator
    }

    // ---------------------------------------------------------------------------------------------
    // Modal additional-cost forage (Feed the Cycle — "forage or pay {B}"). Regression: paying the
    // forage mode used to be a silent no-op (no forage case in the cast cost-payment loop).
    // ---------------------------------------------------------------------------------------------

    test("Casting Feed the Cycle's forage mode actually pays forage by exiling the chosen three cards") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(FeedTheCycle, BonebindOrator, PredefinedTokens.Food))
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 20))
        val active = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val feed = driver.putCardInHand(active, "Feed the Cycle")
        val victim = driver.putCreatureOnBattlefield(active, "Bonebind Orator")
        val filler = (1..5).map { driver.putCardInGraveyard(active, "Swamp") }
        driver.giveMana(active, Color.BLACK, 2) // {1}{B}, forage covers the rest of the cost

        val chosen = listOf(filler[0], filler[2], filler[4])
        val result = driver.submit(
            CastSpell(
                playerId = active,
                cardId = feed,
                targets = listOf(com.wingedsheep.engine.state.components.stack.ChosenTarget.Permanent(victim)),
                chosenModes = listOf(1), // mode index 1 = the forage mode
                modeTargetsOrdered = listOf(
                    listOf(com.wingedsheep.engine.state.components.stack.ChosenTarget.Permanent(victim))
                ),
                additionalCostPayment = AdditionalCostPayment(exiledCards = chosen)
            )
        )
        result.isSuccess shouldBe true

        // Forage was paid: exactly the three chosen cards were exiled (not silently skipped).
        val exile = driver.state.getZone(ZoneKey(active, Zone.EXILE))
        exile shouldContainAll chosen
        exile.size shouldBe 3
        driver.state.stack shouldContain feed
    }
})

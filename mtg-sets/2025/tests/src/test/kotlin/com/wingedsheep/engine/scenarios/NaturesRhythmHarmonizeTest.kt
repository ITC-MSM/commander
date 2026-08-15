package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.legalactions.EnumerationMode
import com.wingedsheep.engine.legalactions.LegalActionEnumerator
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.tdm.cards.NaturesRhythm
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.AlternativePaymentChoice
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Nature's Rhythm ({X}{G}{G}, Harmonize {X}{G}{G}{G}{G}) cast from the graveyard.
 *
 * Verifies the X-cost Harmonize UX/engine support:
 *  - the enumerator advertises `hasXCost`/`maxAffordableX` so the client prompts for X,
 *    folding in the best single-creature tap reduction;
 *  - tapping a creature reduces the GENERIC mana paid — and {X} is generic, so it reduces
 *    the mana paid for X (TDM release notes), while the chosen X (the search's "mana value
 *    X or less") is unchanged.
 */
class NaturesRhythmHarmonizeTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(NaturesRhythm)
        // Library is all Grizzly Bears (2/2, MV 2) — eligible search targets for X >= 2.
        driver.initMirrorMatch(deck = Deck.of("Grizzly Bears" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    test("enumeration advertises X for the Harmonize cast, with max X folding in a tap") {
        val driver = createDriver()
        val player = driver.activePlayer!!

        driver.putCardInGraveyard(player, "Nature's Rhythm")
        driver.putCreatureOnBattlefield(player, "Grizzly Bears") // 2/2 — tappable for Harmonize
        repeat(4) { driver.putLandOnBattlefield(player, "Forest") } // {G}{G}{G}{G} affordable at X=0

        val enumerator = LegalActionEnumerator.create(driver.cardRegistry)
        val actions = enumerator.enumerate(driver.state, player, EnumerationMode.FULL)

        val harmonize = actions.firstOrNull { it.actionType == "CastWithHarmonize" }
        harmonize shouldNotBe null
        harmonize!!.hasHarmonize shouldBe true
        harmonize.hasXCost shouldBe true
        // No-tap max X = (4 mana − 4 fixed)/1 = 0; tapping the 2-power bear raises it to 2.
        harmonize.maxAffordableX shouldBe 2
    }

    test("tapping a creature reduces the mana paid for X, but not the X used by the search") {
        val driver = createDriver()
        val player = driver.activePlayer!!

        val spell = driver.putCardInGraveyard(player, "Nature's Rhythm")
        val bears = driver.putCreatureOnBattlefield(player, "Grizzly Bears") // 2/2
        // Only {G}{G}{G}{G} = 4 green. The full harmonize cost at X=2 is {2}{G}{G}{G}{G} = 6,
        // so this is affordable ONLY because the 2-power tap reduces the {X}=2 generic to 0.
        driver.giveMana(player, Color.GREEN, 4)

        driver.submit(
            CastSpell(
                player, spell,
                xValue = 2,
                useAlternativeCost = true,
                paymentStrategy = PaymentStrategy.FromPool,
                alternativePayment = AlternativePaymentChoice(harmonizeCreature = bears),
            )
        ).isSuccess shouldBe true
        driver.isTapped(bears).shouldBeTrue()
        driver.bothPass()

        // The spell resolves and pauses to search for a creature with mana value X(=2) or less.
        // If the tap had wrongly reduced the chosen X to 0, no MV-2 Grizzly Bears would be
        // offered — so a non-empty choice proves X stayed 2 for the effect.
        val decision = driver.pendingDecision as? SelectCardsDecision
        decision shouldNotBe null
        decision!!.options.size shouldBeGreaterThan 0
        val bearsCard = decision.options.first()
        driver.submitCardSelection(player, listOf(bearsCard))

        // A Grizzly Bears was put onto the battlefield and the spell was exiled.
        val battlefieldBears = driver.state.getBattlefield().count {
            driver.state.getEntity(it)?.get<CardComponent>()?.name == "Grizzly Bears"
        }
        battlefieldBears shouldBe 2 // the tapped one + the fetched one
        driver.state.getZone(ZoneKey(player, Zone.EXILE)).contains(spell) shouldBe true
    }

    test("without tapping a creature, X=2 is unaffordable on only four green mana") {
        val driver = createDriver()
        val player = driver.activePlayer!!

        val spell = driver.putCardInGraveyard(player, "Nature's Rhythm")
        driver.giveMana(player, Color.GREEN, 4) // {2}{G}{G}{G}{G} = 6 needed, no tap

        driver.submitExpectFailure(
            CastSpell(
                player, spell,
                xValue = 2,
                useAlternativeCost = true,
                paymentStrategy = PaymentStrategy.FromPool,
            )
        )
        driver.state.getZone(ZoneKey(player, Zone.GRAVEYARD)).contains(spell) shouldBe true
    }
})

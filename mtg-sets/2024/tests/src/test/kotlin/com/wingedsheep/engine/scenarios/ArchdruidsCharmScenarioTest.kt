package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Archdruid's Charm (MKM #151) — {G}{G}{G} instant, three modes.
 *
 * Mode 1 is the reason this file exists. It is a *single* search whose destination branches on the
 * found card's type — "onto the battlefield tapped if it's a land card. Otherwise, put it into your
 * hand" — which no `SearchDestination` can express, so it is an inline pipeline that splits the
 * one-card result on `GameObjectFilter.Land`. Both arms are exercised from the same board, because
 * a `filterSplit` wired backwards (or a `Creature`-vs-`Land` split instead of land-vs-rest) passes
 * whichever arm you test alone.
 *
 * Mode 2's damage must read the *boosted* power: the counter resolves first, so a 2/2 hits for 3,
 * not 2. Asserting the victim merely died would pass at either amount, so the survivor case pins it
 * — a 2/4 blocker takes exactly 3 and lives with 3 damage marked.
 */
class ArchdruidsCharmScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(
            deck = Deck.of("Forest" to 30, "Grizzly Bears" to 10),
            skipMulligans = true,
        )
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.castCharm(
        player: EntityId,
        modeIndex: Int,
        modeTargets: List<ChosenTarget> = emptyList(),
    ) = run {
        val card = putCardInHand(player, "Archdruid's Charm")
        giveMana(player, Color.GREEN, 3)
        submit(
            CastSpell(
                playerId = player,
                cardId = card,
                targets = modeTargets,
                chosenModes = listOf(modeIndex),
                modeTargetsOrdered = listOf(modeTargets),
                paymentStrategy = PaymentStrategy.FromPool,
            )
        )
    }

    test("mode 1 puts a found land onto the battlefield tapped, and shuffles") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        val landsBefore = driver.getLands(player).size

        driver.castCharm(player, 0).error shouldBe null
        driver.bothPass()

        val decision = driver.pendingDecision
        decision.shouldBeInstanceOf<SelectCardsDecision>()
        val forest = decision.options.first { driver.getCardName(it) == "Forest" }
        driver.submitCardSelection(player, listOf(forest)).error shouldBe null

        withClue("the land arm ran: onto the battlefield, and tapped") {
            driver.getLands(player).size shouldBe landsBefore + 1
            driver.getLands(player) shouldContain forest
            driver.isTapped(forest) shouldBe true
        }
        withClue("the land arm ran, so the hand arm must not have") {
            driver.getHand(player) shouldNotContain forest
        }
    }

    test("mode 1 puts a found nonland card into hand instead") {
        val driver = createDriver()
        val player = driver.activePlayer!!

        driver.castCharm(player, 0).error shouldBe null
        driver.bothPass()

        val decision = driver.pendingDecision
        decision.shouldBeInstanceOf<SelectCardsDecision>()
        val bears = decision.options.first { driver.getCardName(it) == "Grizzly Bears" }
        driver.submitCardSelection(player, listOf(bears)).error shouldBe null

        withClue("a creature card takes the 'otherwise' arm — hand, not battlefield") {
            driver.getHand(player) shouldContain bears
            driver.findPermanent(player, "Grizzly Bears") shouldBe null
        }
    }

    test("mode 1 resolves with nothing found — the search is 'up to one'") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        val handBefore = driver.getHandSize(player)
        val landsBefore = driver.getLands(player).size

        driver.castCharm(player, 0).error shouldBe null
        driver.bothPass()

        driver.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
        driver.submitCardSelection(player, emptyList()).error shouldBe null

        withClue("declining the search moves nothing, and doesn't error out downstream") {
            driver.getHandSize(player) shouldBe handBefore
            driver.getLands(player).size shouldBe landsBefore
        }
    }

    test("mode 2 counters first, then deals the boosted power as damage") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)
        val mine = driver.putCreatureOnBattlefield(player, "Grizzly Bears")
        val theirs = driver.putCreatureOnBattlefield(opponent, "Centaur Courser")

        driver.castCharm(
            player,
            1,
            listOf(ChosenTarget.Permanent(mine), ChosenTarget.Permanent(theirs)),
        ).error shouldBe null
        driver.bothPass()

        withClue("the +1/+1 counter landed on my creature") {
            driver.state.getEntity(mine)?.get<CountersComponent>()
                ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 1
            driver.state.projectedState.getPower(mine) shouldBe 3
        }
        withClue("a 3/3 died, so the damage was 3 — the counter resolved before power was read") {
            driver.findPermanent(opponent, "Centaur Courser") shouldBe null
            driver.getGraveyardCardNames(opponent) shouldContain "Centaur Courser"
        }
        withClue("my creature survived — the damage went one way only") {
            driver.findPermanent(player, "Grizzly Bears").shouldNotBeNull()
        }
    }

    test("mode 3 exiles an enchantment") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)
        val enchantment = driver.putPermanentOnBattlefield(opponent, "Chalk Outline")

        driver.castCharm(player, 2, listOf(ChosenTarget.Permanent(enchantment))).error shouldBe null
        driver.bothPass()

        withClue("'exile target artifact or enchantment' accepted the enchantment half") {
            driver.findPermanent(opponent, "Chalk Outline") shouldBe null
            driver.getExileCardNames(opponent) shouldContain "Chalk Outline"
        }
    }
})

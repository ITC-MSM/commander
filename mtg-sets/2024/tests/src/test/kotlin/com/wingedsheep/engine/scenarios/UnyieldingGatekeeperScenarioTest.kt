package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.core.TurnFaceUp
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.identity.FaceDownComponent
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
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Unyielding Gatekeeper (MKM #35) — {1}{W} 3/2 with Disguise {1}{W}.
 *
 * "When this creature is turned face up, exile another target nonland permanent. If you controlled
 *  it, return it to the battlefield tapped. Otherwise, its controller creates a 2/2 white and blue
 *  Detective creature token."
 *
 * One trigger, two opposite outcomes, and the branch is chosen by a state test that must be read
 * **before** the exile — a naive implementation that exiles first and then asks "did you control
 * it?" always takes the else arm, because an exiled card has no controller. So both arms are driven
 * from the same trigger, and each asserts the *other* arm didn't fire:
 *
 *  - my own permanent comes back tapped and **no** Detective appears;
 *  - the opponent's stays exiled and exactly one Detective appears **under their control**.
 *
 * The whole thing only happens on a flip, so each case goes through the real disguise loop: cast
 * face down for {3}, then pay {1}{W} to turn it face up.
 */
class UnyieldingGatekeeperScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    /** Cast the Gatekeeper face down for {3} and return the resulting face-down permanent. */
    fun GameTestDriver.castFaceDown(player: EntityId): EntityId {
        val card = putCardInHand(player, "Unyielding Gatekeeper")
        giveColorlessMana(player, 3)
        submit(
            CastSpell(
                playerId = player,
                cardId = card,
                castFaceDown = true,
                paymentStrategy = PaymentStrategy.FromPool,
            )
        ).error shouldBe null
        bothPass()
        return getPermanents(player).single {
            state.getEntity(it)?.has<FaceDownComponent>() == true
        }
    }

    /** Pay the {1}{W} disguise cost, then let the turned-face-up trigger resolve. */
    fun GameTestDriver.flipUp(player: EntityId, gatekeeper: EntityId) {
        giveColorlessMana(player, 1)
        giveMana(player, Color.WHITE, 1)
        submit(
            TurnFaceUp(
                playerId = player,
                sourceId = gatekeeper,
                paymentStrategy = PaymentStrategy.FromPool,
            )
        ).error shouldBe null
    }

    fun GameTestDriver.detectivesOf(player: EntityId): List<EntityId> =
        getCreatures(player).filter { getCardName(it) == "Detective Token" }

    test("a permanent you control is blinked back tapped, with no consolation Detective") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)
        val mine = driver.putCreatureOnBattlefield(player, "Grizzly Bears")
        driver.untapPermanent(mine)
        // A counter is the observable "same object or not?" probe: a permanent that genuinely left
        // the battlefield and came back is a new object and keeps nothing (CR 400.7).
        driver.addComponent(mine, CountersComponent(mapOf(CounterType.PLUS_ONE_PLUS_ONE to 2)))

        val gatekeeper = driver.castFaceDown(player)
        driver.flipUp(player, gatekeeper)
        driver.submitTargetSelection(player, listOf(mine)).error shouldBe null
        driver.bothPass()

        val returned = driver.findPermanent(player, "Grizzly Bears")
        withClue("it came back under my control rather than staying in exile") {
            returned.shouldNotBeNull()
            driver.getExileCardNames(player) shouldBe emptyList()
        }
        withClue("it came back *tapped*") {
            driver.isTapped(returned!!) shouldBe true
        }
        withClue("it really left and came back — a new object keeps none of its counters") {
            driver.state.getEntity(returned!!)?.get<CountersComponent>()
                ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0 shouldBe 0
        }
        withClue("the else arm must not have run for a permanent I controlled") {
            driver.detectivesOf(player) shouldBe emptyList()
            driver.detectivesOf(opponent) shouldBe emptyList()
        }
    }

    test("an opponent's permanent stays exiled and hands them one Detective") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)
        driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")
        val theirs = driver.findPermanent(opponent, "Grizzly Bears")!!

        val gatekeeper = driver.castFaceDown(player)
        driver.flipUp(player, gatekeeper)
        driver.submitTargetSelection(player, listOf(theirs)).error shouldBe null
        driver.bothPass()

        withClue("the exile stuck — nothing returned it") {
            driver.findPermanent(opponent, "Grizzly Bears") shouldBe null
            driver.getExileCardNames(opponent) shouldContain "Grizzly Bears"
        }
        withClue("'its controller' is the opponent, not me (CR 111.1)") {
            driver.detectivesOf(opponent).size shouldBe 1
            driver.detectivesOf(player) shouldBe emptyList()
        }
        withClue("the Gatekeeper itself is face up and on the battlefield") {
            driver.findPermanent(player, "Unyielding Gatekeeper").shouldNotBeNull()
        }
    }
})

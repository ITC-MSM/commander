package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Insidious Roots (MKM #208) — {B}{G} enchantment.
 *
 * "Creature tokens you control have '{T}: Add one mana of any color.'"
 * "Whenever one or more creature cards leave your graveyard, create a 0/1 green Plant creature
 *  token, then put a +1/+1 counter on each Plant you control."
 *
 * Two things worth pinning down:
 *
 *  - the payoff is **ordered**, not simultaneous. The token is created first and the counters land
 *    second, so the brand-new Plant gets one too and arrives as a 1/2. An implementation that
 *    snapshots the group before creating the token leaves it a 0/1.
 *  - the counters go on **Plants**, not on all creature tokens — a Detective token sitting next to
 *    them must stay untouched, which a filter copied from the first ability would get wrong.
 *
 * The second trigger fires exactly once per batch (CR 603.2c) by construction — that is
 * `Triggers.CardsLeaveYourGraveyard`, shared with Chalk Outline and covered by its test — so this
 * file drives it once and spends its assertions on the payoff instead.
 */
class InsidiousRootsScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), skipMulligans = true)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.plants(player: EntityId): List<EntityId> =
        getCreatures(player).filter { getCardName(it) == "Plant Token" }

    fun GameTestDriver.counters(id: EntityId): Int =
        state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    /** Raise Dead a creature card out of the graveyard, which is what fires the trigger. */
    fun GameTestDriver.raiseDead(player: EntityId, target: EntityId) {
        val spell = putCardInHand(player, "Raise Dead")
        giveMana(player, com.wingedsheep.sdk.core.Color.BLACK, 1)
        submit(
            CastSpell(
                playerId = player,
                cardId = spell,
                targets = listOf(ChosenTarget.Card(target, player, Zone.GRAVEYARD)),
                paymentStrategy = PaymentStrategy.FromPool,
            )
        ).error shouldBe null
        bothPass()
    }

    test("the new Plant is included in its own counter sweep, arriving as a 1/2") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        driver.putPermanentOnBattlefield(player, "Insidious Roots")
        val corpse = driver.putCardInGraveyard(player, "Grizzly Bears")

        driver.plants(player) shouldBe emptyList()
        driver.raiseDead(player, corpse)
        driver.bothPass()

        val plants = driver.plants(player)
        withClue("one creature card left the graveyard, so one Plant was created") {
            plants.size shouldBe 1
        }
        withClue("the counter sweep ran after the token existed — 0/1 plus a counter is 1/2") {
            driver.counters(plants.single()) shouldBe 1
            driver.state.projectedState.getPower(plants.single()) shouldBe 1
            driver.state.projectedState.getToughness(plants.single()) shouldBe 2
        }
    }

    test("a second trigger grows every Plant, and leaves non-Plant tokens alone") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        driver.putPermanentOnBattlefield(player, "Insidious Roots")
        // Chalk Outline shares the trigger and makes a Detective — a creature token that is not a
        // Plant, so it proves the counter filter is narrower than the mana-grant filter.
        driver.putPermanentOnBattlefield(player, "Chalk Outline")

        val first = driver.putCardInGraveyard(player, "Grizzly Bears")
        driver.raiseDead(player, first)
        while (driver.stackSize > 0) driver.bothPass()

        val second = driver.putCardInGraveyard(player, "Centaur Courser")
        driver.raiseDead(player, second)
        while (driver.stackSize > 0) driver.bothPass()

        val plants = driver.plants(player)
        withClue("two triggers, two Plants") {
            plants.size shouldBe 2
        }
        withClue("the first Plant caught both sweeps; the second caught only its own") {
            plants.map { driver.counters(it) }.sorted() shouldBe listOf(1, 2)
        }
        withClue("Detective tokens are creature tokens but not Plants — no counters") {
            driver.getCreatures(player)
                .filter { driver.getCardName(it) == "Detective Token" }
                .map { driver.counters(it) } shouldBe listOf(0, 0)
        }
    }
})

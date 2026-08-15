package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.eld.cards.WishclawTalisman
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Wishclaw Talisman (ELD #110, canonical printing) — {1}{B} Artifact.
 *
 * "This artifact enters with three wish counters on it.
 *  {1}, {T}, Remove a wish counter from this artifact: Search your library for a card, put it into
 *  your hand, then shuffle. An opponent gains control of this artifact. Activate only during your
 *  turn."
 *
 * Proven here:
 *  - the enters-with-three-wish-counters replacement (CR 614.1c) fires when it resolves from a cast;
 *  - activating tutors a card to hand, spends one wish counter, and hands the artifact to the
 *    opponent (the "an opponent gains control" clause, chosen on resolution — forced in a two-player
 *    game);
 *  - the ability is unactivatable on an opponent's turn (`ActivationRestriction.OnlyDuringYourTurn`);
 *  - with no wish counters left the ability can't be activated at all, matching the printed ruling
 *    that the Talisman simply sits there once the counters run out.
 */
class WishclawTalismanScenarioTest : FunSpec({

    val abilityId = WishclawTalisman.activatedAbilities.first().id

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    /** Drain the stack, stopping at decisions or errors. */
    fun GameTestDriver.drainStack(maxIterations: Int = 20) {
        var guard = 0
        while (state.stack.isNotEmpty() && !isPaused && guard++ < maxIterations) {
            bothPass()
        }
    }

    fun GameTestDriver.wishCounters(id: EntityId): Int =
        state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.WISH) ?: 0

    fun GameTestDriver.setWishCounters(id: EntityId, count: Int) {
        replaceState(
            state.updateEntity(id) { c ->
                c.with(CountersComponent(mapOf(CounterType.WISH to count)))
            }
        )
    }

    test("enters with three wish counters when cast") {
        val d = newDriver()
        val you = d.player1

        val talisman = d.putCardInHand(you, "Wishclaw Talisman")
        d.giveMana(you, Color.BLACK, 2)
        d.castSpell(you, talisman)
        d.bothPass()

        val onBattlefield = d.findPermanent(you, "Wishclaw Talisman")
        withClue("Wishclaw Talisman should resolve onto the battlefield") {
            (onBattlefield != null) shouldBe true
        }
        withClue("It enters with three wish counters") {
            d.wishCounters(onBattlefield!!) shouldBe 3
        }
    }

    test("activating tutors a card to hand, spends a wish counter, and gives the artifact to the opponent") {
        val d = newDriver()
        val you = d.player1
        val opponent = d.player2

        val talisman = d.putPermanentOnBattlefield(you, "Wishclaw Talisman")
        d.setWishCounters(talisman, 3)
        d.giveColorlessMana(you, 1)

        val handBefore = d.getHandSize(you)

        val result = d.submit(
            ActivateAbility(playerId = you, sourceId = talisman, abilityId = abilityId)
        )
        withClue("Activating the tutor ability during your own turn should succeed") {
            result.isSuccess shouldBe true
        }

        // Resolve until the library-search selection pauses.
        d.drainStack()
        withClue("The library search should present a SelectCardsDecision") {
            (d.pendingDecision is SelectCardsDecision) shouldBe true
        }
        val decision = d.pendingDecision as SelectCardsDecision
        withClue("The search should offer the whole library (no filter)") {
            decision.options.isEmpty() shouldBe false
        }
        val fetched = decision.options.first()
        d.submitCardSelection(you, listOf(fetched))
        d.drainStack()

        withClue("The searched card should be in your hand") {
            d.state.getZone(ZoneKey(you, Zone.HAND)).contains(fetched) shouldBe true
        }
        withClue("Your hand grew by exactly the tutored card") {
            d.getHandSize(you) shouldBe handBefore + 1
        }
        withClue("Paying the cost removed one wish counter (3 -> 2)") {
            d.wishCounters(talisman) shouldBe 2
        }
        withClue("Tapping was part of the cost") {
            d.isTapped(talisman) shouldBe true
        }
        withClue("The opponent now controls the Talisman") {
            d.state.projectedState.getController(talisman) shouldBe opponent
        }
    }

    test("cannot be activated during an opponent's turn") {
        val d = newDriver()
        val you = d.player1

        val talisman = d.putPermanentOnBattlefield(you, "Wishclaw Talisman")
        d.setWishCounters(talisman, 3)

        // Advance into the opponent's turn.
        d.passPriorityUntil(Step.UPKEEP)
        withClue("The opponent should now be the active player") {
            (d.activePlayer != you) shouldBe true
        }
        d.giveColorlessMana(you, 1)

        val activatable = d.legalActions(you).any {
            (it.action as? ActivateAbility)?.let { a -> a.sourceId == talisman && a.abilityId == abilityId } == true
        }
        withClue("OnlyDuringYourTurn should keep the ability out of the legal actions") {
            activatable shouldBe false
        }
        d.submitExpectFailure(
            ActivateAbility(playerId = you, sourceId = talisman, abilityId = abilityId)
        )
    }

    test("cannot be activated once the wish counters run out") {
        val d = newDriver()
        val you = d.player1

        val talisman = d.putPermanentOnBattlefield(you, "Wishclaw Talisman")
        d.setWishCounters(talisman, 0)
        d.giveColorlessMana(you, 1)

        val activatable = d.legalActions(you).any {
            val a = it.action as? ActivateAbility
            a != null && a.sourceId == talisman && a.abilityId == abilityId && it.affordable
        }
        withClue("With zero wish counters the removal cost is unpayable") {
            activatable shouldBe false
        }
        d.submitExpectFailure(
            ActivateAbility(playerId = you, sourceId = talisman, abilityId = abilityId)
        )
    }
})

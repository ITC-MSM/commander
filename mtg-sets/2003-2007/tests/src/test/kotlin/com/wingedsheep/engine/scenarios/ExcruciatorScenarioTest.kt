package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.p02.cards.GoblinPiker
import com.wingedsheep.mtg.sets.definitions.rav.cards.Excruciator
import com.wingedsheep.mtg.sets.definitions.usg.cards.DiscipleOfLaw
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Excruciator (RAV #121) — "Damage that would be dealt by this creature can't be prevented."
 *
 * Protection prevents damage (CR 702.16e), so Excruciator's combat damage kills a blocker with
 * protection from red. The half that is easy to get wrong is the *scope*: the shutoff names its
 * own source, so any other red creature in the same combat must still be walled off by the same
 * protection. Both attackers swing into an identical blocker in one combat so the two answers are
 * read from one board — a global shutoff would kill both Disciples, and a shutoff that never fired
 * would kill neither.
 */
class ExcruciatorScenarioTest : FunSpec({

    test("Excruciator's damage ignores protection from red, and only Excruciator's does") {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + Excruciator + DiscipleOfLaw + GoblinPiker)
        d.initMirrorMatch(deck = Deck.of("Mountain" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val attacker = d.player1
        val defender = d.player2

        val excruciator = d.putCreatureOnBattlefield(attacker, "Excruciator")   // 7/7 red
        val piker = d.putCreatureOnBattlefield(attacker, "Goblin Piker")        // 2/1 red
        d.removeSummoningSickness(excruciator)
        d.removeSummoningSickness(piker)

        // Two identical 1/2 blockers, each with protection from red.
        val blockingExcruciator = d.putCreatureOnBattlefield(defender, "Disciple of Law")
        val blockingPiker = d.putCreatureOnBattlefield(defender, "Disciple of Law")

        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(attacker, listOf(excruciator, piker), defender)
        d.passPriority(attacker)

        d.passPriorityUntil(Step.DECLARE_BLOCKERS)
        d.declareBlockers(
            defender,
            mapOf(
                blockingExcruciator to listOf(excruciator),
                blockingPiker to listOf(piker)
            )
        )
        // The defender may hold priority after blockers are declared.
        if (d.state.priorityPlayerId != attacker) d.passPriority(defender)

        d.passPriorityUntil(Step.END)

        withClue("protection couldn't prevent Excruciator's 7 damage, so its blocker died") {
            d.getGraveyardCardNames(defender) shouldBe listOf("Disciple of Law")
            d.getCreatures(defender).contains(blockingExcruciator) shouldBe false
        }
        withClue("the Goblin Piker's damage was still prevented by the same protection") {
            d.getCreatures(defender) shouldBe listOf(blockingPiker)
        }
        withClue("both blockers still dealt their damage back") {
            // Excruciator (7/7) took 1 from its blocker, Goblin Piker (2/1) took 1 and died.
            d.findPermanent(attacker, "Excruciator").shouldNotBeNull()
            d.findPermanent(attacker, "Goblin Piker").shouldBeNull()
        }
        withClue("neither attacker got through — the defender took no damage") {
            d.getLifeTotal(defender) shouldBe 20
        }
    }
})

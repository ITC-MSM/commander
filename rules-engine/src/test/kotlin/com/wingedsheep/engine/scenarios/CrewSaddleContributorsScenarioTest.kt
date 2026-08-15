package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.CrewVehicle
import com.wingedsheep.engine.core.SaddleMount
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.battlefield.CrewSaddleContributorsComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe

/**
 * End-to-end scenario tests for the shared "creatures that crewed/saddled this permanent this
 * turn" tracker (OTJ engine gap #2). Exercises the two read surfaces:
 *
 *  - membership, via [StatePredicate.CrewedOrSaddledSourceThisTurn] used as a target filter —
 *    "put a +1/+1 counter on target creature that saddled it this turn" (Giant Beaver shape).
 *  - count, via [DynamicAmount.CreaturesThatCrewedOrSaddledThisTurn] — "draw a card for each
 *    creature that crewed it this turn" (Luxurious Locomotive shape; faithful enough using a
 *    dynamic DrawCards as the observable payoff).
 *
 * Both tracked off a per-permanent [CrewSaddleContributorsComponent] recorded by the Crew and
 * Saddle handlers and cleared at end of turn.
 */
class CrewSaddleContributorsScenarioTest : FunSpec({

    // Mount that pays off the saddlers: "Whenever it attacks while saddled, put a +1/+1 counter
    // on target creature that saddled it this turn." 2/2 with Saddle 2.
    val saddlePayoffMount = card("Saddler's Reward") {
        manaCost = "{2}{G}"
        typeLine = "Creature — Horse Mount"
        power = 2
        toughness = 2
        oracleText = "Saddle 2\nWhenever Saddler's Reward attacks while saddled, put a +1/+1 " +
            "counter on target creature that saddled it this turn."
        keywordAbility(KeywordAbility.saddle(2))
        triggeredAbility {
            trigger = Triggers.Attacks
            triggerRestriction = Conditions.SourceIsSaddled
            val saddler = target(
                "target creature that saddled it this turn",
                TargetCreature(filter = TargetFilter(GameObjectFilter.Creature.crewedOrSaddledSourceThisTurn()))
            )
            effect = Effects.AddCounters("+1/+1", 1, saddler)
        }
    }

    // Vehicle that pays off the crew: "Whenever it attacks, draw a card for each creature that
    // crewed it this turn." 4/4 with Crew 1.
    val crewPayoffVehicle = card("Prospector's Wagon") {
        manaCost = "{3}"
        typeLine = "Artifact — Vehicle"
        power = 4
        toughness = 4
        oracleText = "Whenever Prospector's Wagon attacks, draw a card for each creature that " +
            "crewed it this turn.\nCrew 1"
        keywordAbility(KeywordAbility.crew(1))
        triggeredAbility {
            trigger = Triggers.Attacks
            effect = Effects.DrawCards(DynamicAmounts.creaturesThatCrewedOrSaddledThisTurn())
        }
    }

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(saddlePayoffMount)
        driver.registerCard(crewPayoffVehicle)
        driver.initMirrorMatch(Deck.of("Forest" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.contributors(id: EntityId): Set<EntityId> =
        state.getEntity(id)?.get<CrewSaddleContributorsComponent>()?.creatureIds ?: emptySet()

    fun GameTestDriver.plusOneCounters(id: EntityId): Int =
        state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    // --- Recording -----------------------------------------------------------------------------

    test("saddling records the saddlers on the Mount") {
        val driver = newDriver()
        val mount = driver.putCreatureOnBattlefield(driver.player1, "Saddler's Reward")
        val bear = driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears") // power 2

        driver.contributors(mount) shouldBe emptySet()

        driver.submitSuccess(SaddleMount(driver.player1, mount, listOf(bear)))
        driver.bothPass()

        driver.contributors(mount) shouldBe setOf(bear)
    }

    test("crewing records the crew on the Vehicle (union across creatures)") {
        val driver = newDriver()
        val wagon = driver.putPermanentOnBattlefield(driver.player1, "Prospector's Wagon")
        val bear1 = driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")
        val bear2 = driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")

        driver.submitSuccess(CrewVehicle(driver.player1, wagon, listOf(bear1, bear2)))
        driver.bothPass()

        driver.contributors(wagon) shouldBe setOf(bear1, bear2)
    }

    test("the record is source-relative: saddlers of one Mount don't count for another") {
        val driver = newDriver()
        val mountA = driver.putCreatureOnBattlefield(driver.player1, "Saddler's Reward")
        val mountB = driver.putCreatureOnBattlefield(driver.player1, "Saddler's Reward")
        val bearA = driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")
        val bearB = driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")

        driver.submitSuccess(SaddleMount(driver.player1, mountA, listOf(bearA)))
        driver.bothPass()
        driver.submitSuccess(SaddleMount(driver.player1, mountB, listOf(bearB)))
        driver.bothPass()

        driver.contributors(mountA) shouldBe setOf(bearA)
        driver.contributors(mountB) shouldBe setOf(bearB)
    }

    // --- Membership predicate (target filter) --------------------------------------------------

    test("only creatures that saddled it this turn are legal targets, and the counter lands") {
        val driver = newDriver()
        val mount = driver.putCreatureOnBattlefield(driver.player1, "Saddler's Reward")
        val saddler = driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")
        val bystander = driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")
        driver.removeSummoningSickness(mount)

        driver.submitSuccess(SaddleMount(driver.player1, mount, listOf(saddler)))
        driver.bothPass()

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(driver.player1, listOf(mount), driver.player2)

        // The attack-while-saddled trigger asks for a target; only the saddler qualifies.
        val decision = driver.state.pendingDecision as ChooseTargetsDecision
        decision.legalTargets.getValue(0) shouldContain saddler
        decision.legalTargets.getValue(0) shouldNotContain bystander

        driver.submitTargetSelection(driver.player1, listOf(saddler))
        driver.bothPass()

        driver.plusOneCounters(saddler) shouldBe 1
        driver.plusOneCounters(bystander) shouldBe 0
    }

    // --- Count DynamicAmount -------------------------------------------------------------------

    test("the attack payoff counts each creature that crewed it this turn") {
        val driver = newDriver()
        val wagon = driver.putPermanentOnBattlefield(driver.player1, "Prospector's Wagon")
        val bear1 = driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")
        val bear2 = driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")
        driver.removeSummoningSickness(wagon)

        driver.submitSuccess(CrewVehicle(driver.player1, wagon, listOf(bear1, bear2)))
        driver.bothPass()

        val handBefore = driver.getHandSize(driver.player1)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(driver.player1, listOf(wagon), driver.player2)
        driver.bothPass()

        // Two creatures crewed it → drew two cards.
        driver.getHandSize(driver.player1) shouldBe handBefore + 2
    }

    test("the count includes a crewer that has since left the battlefield (Luxurious Locomotive ruling)") {
        val driver = newDriver()
        val wagon = driver.putPermanentOnBattlefield(driver.player1, "Prospector's Wagon")
        val bear1 = driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")
        val bear2 = driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")
        driver.removeSummoningSickness(wagon)

        driver.submitSuccess(CrewVehicle(driver.player1, wagon, listOf(bear1, bear2)))
        driver.bothPass()

        // One crewer leaves the battlefield before the attack trigger resolves; its id remains
        // recorded, so it is still counted.
        driver.moveToGraveyard(bear2)
        driver.contributors(wagon) shouldContain bear2

        val handBefore = driver.getHandSize(driver.player1)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(driver.player1, listOf(wagon), driver.player2)
        driver.bothPass()

        driver.getHandSize(driver.player1) shouldBe handBefore + 2
    }

    // --- Cleanup -------------------------------------------------------------------------------

    test("the record is cleared at end of turn") {
        val driver = newDriver()
        val mount = driver.putCreatureOnBattlefield(driver.player1, "Saddler's Reward")
        val bear = driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")

        driver.submitSuccess(SaddleMount(driver.player1, mount, listOf(bear)))
        driver.bothPass()
        driver.contributors(mount) shouldBe setOf(bear)

        // Advance through this turn's cleanup into the next turn.
        driver.passPriorityUntil(Step.POSTCOMBAT_MAIN)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.contributors(mount) shouldBe emptySet()
    }
})

package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CombatResolutionDecision
import com.wingedsheep.engine.core.CombatResolutionResponse
import com.wingedsheep.engine.core.DamageEdgeAmount
import com.wingedsheep.engine.core.OrderObjectsDecision
import com.wingedsheep.engine.core.OrderedResponse
import com.wingedsheep.engine.core.OrderTriggeredAbilitiesDecision
import com.wingedsheep.engine.core.PassPriority
import com.wingedsheep.engine.core.TriggeredAbilitiesOrderedResponse
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Tests for the Flanking keyword (CR 702.25).
 *
 * Flanking is a triggered ability (CR 702.25b): "Whenever this creature becomes blocked by a
 * creature without flanking, that blocking creature gets -1/-1 until end of turn." The engine
 * synthesizes the trigger for any creature that has [Keyword.FLANKING] (see
 * [com.wingedsheep.engine.event.TriggerAbilityResolver.getFlankingTriggeredAbilities] and
 * [com.wingedsheep.sdk.scripting.Flanking]).
 *
 * Each test isolates the flanking effect by giving the flanking attacker **0 power**, so the
 * attacker never deals combat damage — any blocker death is attributable to the -1/-1 alone, not
 * to ordinary combat. This lets us prove the keyword actually does something.
 */
class FlankingCombatTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        return driver
    }

    fun flanker(name: String, power: Int, toughness: Int): CardDefinition =
        CardDefinition.creature(
            name = name,
            manaCost = ManaCost.parse("{1}{W}"),
            subtypes = setOf(Subtype("Knight")),
            power = power,
            toughness = toughness,
            keywords = setOf(Keyword.FLANKING),
        )

    fun vanilla(name: String, power: Int, toughness: Int): CardDefinition =
        CardDefinition.creature(
            name = name,
            manaCost = ManaCost.parse("{1}{G}"),
            subtypes = setOf(Subtype("Soldier")),
            power = power,
            toughness = toughness,
        )

    /**
     * Pass priority (auto-ordering any simultaneous flanking triggers and confirming combat
     * damage with the engine defaults) until the postcombat main phase is reached. Unlike
     * [GameTestDriver.passPriorityUntil] this also answers [OrderObjectsDecision], which the
     * multi-blocker case produces when several flanking triggers go on the stack at once.
     */
    fun resolveThroughCombat(driver: GameTestDriver) {
        var guard = 0
        while (driver.currentStep != Step.POSTCOMBAT_MAIN && guard++ < 300) {
            when (val decision = driver.state.pendingDecision) {
                is OrderObjectsDecision ->
                    driver.submitDecision(decision.playerId, OrderedResponse(decision.id, decision.objects))
                is OrderTriggeredAbilitiesDecision ->
                    driver.submitDecision(
                        decision.playerId,
                        TriggeredAbilitiesOrderedResponse(decision.id, decision.abilities.map { it.id }),
                    )
                is CombatResolutionDecision -> {
                    val edges = decision.edges.map { DamageEdgeAmount(it.id, it.amount) }
                    driver.submitDecision(decision.playerId, CombatResolutionResponse(decision.id, edges))
                }
                null -> {
                    val priority = driver.state.priorityPlayerId ?: break
                    driver.submit(PassPriority(priority))
                }
                else -> error("Unexpected decision during combat: ${decision::class.simpleName}")
            }
            if (driver.state.gameOver) break
        }
    }

    test("flanking weakens a non-flanking blocker enough to kill it") {
        // 0/3 flanking attacker, blocked by a 1/1 without flanking.
        // Flanking gives the blocker -1/-1 -> 0/0 -> dies to state-based actions.
        // The attacker has 0 power, so it deals no combat damage: the death is pure flanking.
        val driver = createDriver()
        driver.registerCard(flanker("Test Flanking Sentinel", power = 0, toughness = 3))
        driver.registerCard(vanilla("Test Footsoldier", power = 1, toughness = 1))
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40))

        val attacker = driver.activePlayer!!
        val defender = driver.getOpponent(attacker)

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val sentinel = driver.putCreatureOnBattlefield(attacker, "Test Flanking Sentinel")
        val footsoldier = driver.putCreatureOnBattlefield(defender, "Test Footsoldier")
        driver.removeSummoningSickness(sentinel)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(attacker, listOf(sentinel), defender).isSuccess shouldBe true

        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)
        driver.declareBlockers(defender, mapOf(footsoldier to listOf(sentinel))).isSuccess shouldBe true

        resolveThroughCombat(driver)

        // Blocker died to the -1/-1; the 0-power attacker survives.
        driver.findPermanent(defender, "Test Footsoldier") shouldBe null
        driver.getGraveyardCardNames(defender) shouldContain "Test Footsoldier"
        driver.findPermanent(attacker, "Test Flanking Sentinel") shouldNotBe null
    }

    test("a blocker that also has flanking is not weakened (CR 702.25c)") {
        // 0/3 flanking attacker, blocked by a 1/1 that ALSO has flanking.
        // The "becomes blocked by a creature without flanking" filter excludes it, so no -1/-1.
        // With 1 toughness intact and the attacker dealing 0, the blocker survives.
        val driver = createDriver()
        driver.registerCard(flanker("Test Flanking Sentinel", power = 0, toughness = 3))
        driver.registerCard(flanker("Test Flanking Skirmisher", power = 1, toughness = 1))
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40))

        val attacker = driver.activePlayer!!
        val defender = driver.getOpponent(attacker)

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val sentinel = driver.putCreatureOnBattlefield(attacker, "Test Flanking Sentinel")
        val skirmisher = driver.putCreatureOnBattlefield(defender, "Test Flanking Skirmisher")
        driver.removeSummoningSickness(sentinel)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(attacker, listOf(sentinel), defender).isSuccess shouldBe true

        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)
        driver.declareBlockers(defender, mapOf(skirmisher to listOf(sentinel))).isSuccess shouldBe true

        resolveThroughCombat(driver)

        // Flanking blocker keeps its 1 toughness and survives; so does the attacker.
        driver.findPermanent(defender, "Test Flanking Skirmisher") shouldNotBe null
        driver.findPermanent(attacker, "Test Flanking Sentinel") shouldNotBe null
    }

    test("flanking applies -1/-1 to each non-flanking blocker independently") {
        // 0/5 flanking attacker gang-blocked by two 1/1s without flanking.
        // Each blocker triggers flanking separately and becomes 0/0, so both die.
        val driver = createDriver()
        driver.registerCard(flanker("Test Flanking Sentinel", power = 0, toughness = 5))
        driver.registerCard(vanilla("Test Footsoldier", power = 1, toughness = 1))
        driver.registerCard(vanilla("Test Militia", power = 1, toughness = 1))
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40))

        val attacker = driver.activePlayer!!
        val defender = driver.getOpponent(attacker)

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val sentinel = driver.putCreatureOnBattlefield(attacker, "Test Flanking Sentinel")
        val footsoldier = driver.putCreatureOnBattlefield(defender, "Test Footsoldier")
        val militia = driver.putCreatureOnBattlefield(defender, "Test Militia")
        driver.removeSummoningSickness(sentinel)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(attacker, listOf(sentinel), defender).isSuccess shouldBe true

        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)
        val declaration = driver.declareBlockers(
            defender,
            mapOf(footsoldier to listOf(sentinel), militia to listOf(sentinel)),
        )
        // Two simultaneous Flanking triggers correctly pause the declaration for the attacking
        // player to order them.  A paused action is a valid result here; `resolveThroughCombat`
        // makes that order explicitly below.
        declaration.error shouldBe null

        resolveThroughCombat(driver)

        // Both non-flanking blockers were reduced to 0/0 and died; the attacker survives.
        driver.findPermanent(defender, "Test Footsoldier") shouldBe null
        driver.findPermanent(defender, "Test Militia") shouldBe null
        driver.getGraveyardCardNames(defender) shouldContain "Test Footsoldier"
        driver.getGraveyardCardNames(defender) shouldContain "Test Militia"
        driver.findPermanent(attacker, "Test Flanking Sentinel") shouldNotBe null
    }

    test("flanking does not trigger when the flanking creature is unblocked") {
        // An unblocked flanking attacker deals its normal combat damage and weakens nothing.
        // A non-blocking bystander on the defending side is left untouched.
        val driver = createDriver()
        driver.registerCard(flanker("Test Flanking Charger", power = 2, toughness = 2))
        driver.registerCard(vanilla("Test Bystander", power = 1, toughness = 1))
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)

        val attacker = driver.activePlayer!!
        val defender = driver.getOpponent(attacker)

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val charger = driver.putCreatureOnBattlefield(attacker, "Test Flanking Charger")
        driver.putCreatureOnBattlefield(defender, "Test Bystander")
        driver.removeSummoningSickness(charger)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(attacker, listOf(charger), defender).isSuccess shouldBe true

        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)
        driver.declareNoBlockers(defender)

        resolveThroughCombat(driver)

        // Defender took 2 combat damage; the non-blocking bystander is unaffected by flanking.
        driver.assertLifeTotal(defender, 18)
        driver.findPermanent(defender, "Test Bystander") shouldNotBe null
        driver.findPermanent(attacker, "Test Flanking Charger") shouldNotBe null
    }
})

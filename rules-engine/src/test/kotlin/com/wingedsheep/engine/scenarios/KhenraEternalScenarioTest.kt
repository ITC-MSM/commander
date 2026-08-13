package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.OrderTriggeredAbilitiesDecision
import com.wingedsheep.engine.core.TriggeredAbilitiesOrderedResponse
import com.wingedsheep.engine.core.LifeChangedEvent
import com.wingedsheep.engine.core.LifeChangeReason
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.mechanics.layers.Layer
import com.wingedsheep.engine.mechanics.layers.SerializableModification
import com.wingedsheep.engine.mechanics.layers.addFloatingEffect
import com.wingedsheep.engine.state.components.battlefield.ProtectorComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.hou.cards.KhenraEternal
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.effects.MoveToZoneEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Khenra Eternal / Afflict (CR 702.130) regression scenarios.
 *
 * Authority: official CR effective 2026-08-07, SHA-256
 * 2ED5F1BBB4F8771C84A6F2944A218428C6C5CCBA21110A5D101B9A51A4C062B3;
 * 702.130a-b, 509.1g-h, 509.3c, 509.2/.2a, 603.3b, 405.3, 117.5, 704.3,
 * and 119.3/119.8. Oracle: Khenra Eternal (91b46b22-8062-434b-8432-5ed1062ccaf9),
 * "Afflict 1 (Whenever this creature becomes blocked, defending player loses 1 life.)"
 */
class KhenraEternalScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver = GameTestDriver().also {
        it.registerCards(TestCards.all)
        it.registerCard(KhenraEternal)
    }

    fun doubleAfflictCreature(): CardDefinition = CardDefinition.creature(
        name = "Test Double Afflict",
        manaCost = ManaCost.parse("{1}{B}"),
        subtypes = setOf(Subtype("Zombie")),
        power = 0,
        toughness = 3,
        keywords = setOf(Keyword.AFFLICT),
    ).copy(
        keywordAbilities = listOf(
            KeywordAbility.Numeric(Keyword.AFFLICT, 1),
            KeywordAbility.Numeric(Keyword.AFFLICT, 1),
        ),
    )

    fun testRemovalSpell(): CardDefinition = CardDefinition.instant(
        name = "Test Instant Removal",
        manaCost = ManaCost.parse("{B}"),
        oracleText = "Destroy target creature.",
        script = CardScript.spell(
            MoveToZoneEffect(
                target = EffectTarget.BoundVariable("target"),
                destination = Zone.GRAVEYARD,
                byDestruction = true,
            ),
            TargetCreature(id = "target"),
        ),
    )

    fun testSiege(): CardDefinition = card("Test Afflict Siege") {
        manaCost = "{2}{B}{B}"
        typeLine = "Battle — Siege"
        startingDefense = 5
    }

    fun orderTriggeredAbilitiesIfNeeded(driver: GameTestDriver) {
        when (val decision = driver.state.pendingDecision) {
            is OrderTriggeredAbilitiesDecision -> driver.submitDecision(
                decision.playerId,
                TriggeredAbilitiesOrderedResponse(decision.id, decision.abilities.map { it.id }),
            )
            null -> Unit
            else -> error("Unexpected decision while putting afflict on stack: ${decision::class.simpleName}")
        }
    }

    fun removeAfflictUntilEndOfTurn(driver: GameTestDriver, target: com.wingedsheep.sdk.model.EntityId) {
        driver.replaceState(
            driver.state.addFloatingEffect(
                layer = Layer.ABILITY,
                modification = SerializableModification.RemoveKeyword(Keyword.AFFLICT.name),
                affectedEntities = setOf(target),
                duration = Duration.EndOfTurn,
                context = EffectContext(sourceId = null, controllerId = driver.player2),
            )
        )
    }

    fun resolveStack(driver: GameTestDriver) {
        var guard = 0
        while (driver.stackSize > 0 && guard++ < 20) {
            driver.passPriority(driver.priorityPlayer ?: error("No priority while afflict is on stack"))
        }
        driver.stackSize shouldBe 0
    }

    fun setUpBlockedKhenra(driver: GameTestDriver): Triple<com.wingedsheep.sdk.model.EntityId, com.wingedsheep.sdk.model.EntityId, com.wingedsheep.sdk.model.EntityId> {
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val attacker = driver.activePlayer!!
        val defender = driver.getOpponent(attacker)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val khenra = driver.putCreatureOnBattlefield(attacker, "Khenra Eternal")
        val blocker = driver.putCreatureOnBattlefield(defender, "Grizzly Bears")
        driver.removeSummoningSickness(khenra)
        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(attacker, listOf(khenra), defender).isSuccess shouldBe true
        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)
        return Triple(attacker, defender, blocker)
    }

    test("Khenra Eternal becoming blocked puts one life-loss trigger on the stack and makes its defender lose 1") {
        val driver = createDriver()
        val (attacker, defender, blocker) = setUpBlockedKhenra(driver)

        driver.declareBlockers(defender, mapOf(blocker to listOf(driver.findPermanent(attacker, "Khenra Eternal")!!))).isSuccess shouldBe true
        orderTriggeredAbilitiesIfNeeded(driver)
        driver.stackSize shouldBe 1

        resolveStack(driver)
        driver.assertLifeTotal(defender, 19)
        driver.assertLifeTotal(attacker, 20)
    }

    test("Khenra Eternal being unblocked creates no Afflict trigger") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val attacker = driver.activePlayer!!
        val defender = driver.getOpponent(attacker)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val khenra = driver.putCreatureOnBattlefield(attacker, "Khenra Eternal")
        driver.removeSummoningSickness(khenra)
        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(attacker, listOf(khenra), defender).isSuccess shouldBe true
        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)
        driver.declareNoBlockers(defender).isSuccess shouldBe true

        driver.stackSize shouldBe 0
        driver.assertLifeTotal(defender, 20)
    }

    test("removing Afflict in projected state before blockers suppresses its trigger") {
        val driver = createDriver()
        val (attacker, defender, blocker) = setUpBlockedKhenra(driver)
        val khenra = driver.findPermanent(attacker, "Khenra Eternal")!!
        removeAfflictUntilEndOfTurn(driver, khenra)

        driver.declareBlockers(defender, mapOf(blocker to listOf(khenra))).isSuccess shouldBe true
        driver.stackSize shouldBe 0
        driver.assertLifeTotal(defender, 20)
    }

    test("removing Afflict after its trigger is on the stack does not stop the life loss") {
        val driver = createDriver()
        val (attacker, defender, blocker) = setUpBlockedKhenra(driver)
        val khenra = driver.findPermanent(attacker, "Khenra Eternal")!!
        driver.declareBlockers(defender, mapOf(blocker to listOf(khenra))).isSuccess shouldBe true
        orderTriggeredAbilitiesIfNeeded(driver)
        driver.stackSize shouldBe 1
        removeAfflictUntilEndOfTurn(driver, khenra)

        resolveStack(driver)
        driver.assertLifeTotal(defender, 19)
    }

    test("Afflict retains its defending-player snapshot after its source leaves before resolution") {
        val driver = createDriver()
        driver.registerCard(testRemovalSpell())
        val (attacker, defender, blocker) = setUpBlockedKhenra(driver)
        val khenra = driver.findPermanent(attacker, "Khenra Eternal")!!
        driver.declareBlockers(defender, mapOf(blocker to listOf(khenra))).isSuccess shouldBe true
        orderTriggeredAbilitiesIfNeeded(driver)
        driver.stackSize shouldBe 1

        val removal = driver.putCardInHand(defender, "Test Instant Removal")
        driver.giveMana(defender, com.wingedsheep.sdk.core.Color.BLACK)
        // Attacker passes; defender legally removes Khenra while its non-targeted Afflict ability
        // waits on the stack. The defender must remain captured even after the source is gone.
        driver.passPriority(attacker)
        driver.castSpell(defender, removal, listOf(khenra)).isSuccess shouldBe true
        driver.bothPass()
        driver.findPermanent(attacker, "Khenra Eternal") shouldBe null
        driver.stackSize shouldBe 1

        resolveStack(driver)
        driver.assertLifeTotal(defender, 19)
    }

    test("multiple blockers still cause each Afflict instance to trigger only once") {
        val driver = createDriver()
        val (attacker, defender, firstBlocker) = setUpBlockedKhenra(driver)
        val secondBlocker = driver.putCreatureOnBattlefield(defender, "Grizzly Bears")

        driver.declareBlockers(
            defender,
            mapOf(firstBlocker to listOf(driver.findPermanent(attacker, "Khenra Eternal")!!), secondBlocker to listOf(driver.findPermanent(attacker, "Khenra Eternal")!!)),
        ).isSuccess shouldBe true
        orderTriggeredAbilitiesIfNeeded(driver)
        driver.stackSize shouldBe 1
        resolveStack(driver)

        driver.assertLifeTotal(defender, 19)
    }

    test("separate Afflict instances create separate stack objects and life-loss events") {
        val driver = createDriver()
        driver.registerCard(doubleAfflictCreature())
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val attacker = driver.activePlayer!!
        val defender = driver.getOpponent(attacker)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val doubleAfflict = driver.putCreatureOnBattlefield(attacker, "Test Double Afflict")
        val blocker = driver.putCreatureOnBattlefield(defender, "Grizzly Bears")
        driver.removeSummoningSickness(doubleAfflict)
        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(attacker, listOf(doubleAfflict), defender).isSuccess shouldBe true
        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)
        // Two simultaneous Afflict instances correctly pause for CR 603.3b ordering.
        // The declaration itself is legal; the next helper submits that order.
        driver.declareBlockers(defender, mapOf(blocker to listOf(doubleAfflict))).error shouldBe null

        orderTriggeredAbilitiesIfNeeded(driver)
        driver.stackSize shouldBe 2
        val lossesBeforeResolution = driver.events.filterIsInstance<LifeChangedEvent>()
            .count { it.playerId == defender && it.reason == LifeChangeReason.LIFE_LOSS }
        resolveStack(driver)
        driver.assertLifeTotal(defender, 18)
        val lossesAfterResolution = driver.events.filterIsInstance<LifeChangedEvent>()
            .count { it.playerId == defender && it.reason == LifeChangeReason.LIFE_LOSS }
        (lossesAfterResolution - lossesBeforeResolution) shouldBe 2
    }

    test("Afflict uses the actual multiplayer defender, not an arbitrary opponent") {
        val driver = createDriver()
        val players = driver.initMultiplayer(List(3) { Deck.of("Forest" to 40) }, startingLife = 20)
        val attacker = players[0]
        val uninvolvedOpponent = players[1]
        val defender = players[2]
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val khenra = driver.putCreatureOnBattlefield(attacker, "Khenra Eternal")
        val blocker = driver.putCreatureOnBattlefield(defender, "Grizzly Bears")
        driver.removeSummoningSickness(khenra)
        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(attacker, listOf(khenra), defender).isSuccess shouldBe true
        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)
        driver.declareBlockers(defender, mapOf(blocker to listOf(khenra))).isSuccess shouldBe true

        orderTriggeredAbilitiesIfNeeded(driver)
        driver.stackSize shouldBe 1
        resolveStack(driver)
        driver.assertLifeTotal(defender, 19)
        driver.assertLifeTotal(uninvolvedOpponent, 20)
    }

    test("Afflict uses a battle's protector, rather than its controller, as the defending player") {
        val driver = createDriver()
        driver.registerCard(testSiege())
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val attacker = driver.activePlayer!!
        val protector = driver.getOpponent(attacker)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val khenra = driver.putCreatureOnBattlefield(attacker, "Khenra Eternal")
        val siege = driver.putPermanentOnBattlefield(attacker, "Test Afflict Siege")
        val blocker = driver.putCreatureOnBattlefield(protector, "Grizzly Bears")
        driver.replaceState(driver.state.updateEntity(siege) { it.with(ProtectorComponent(protector)) })
        driver.removeSummoningSickness(khenra)
        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(attacker, mapOf(khenra to siege)).isSuccess shouldBe true
        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)
        driver.declareBlockers(protector, mapOf(blocker to listOf(khenra))).isSuccess shouldBe true

        orderTriggeredAbilitiesIfNeeded(driver)
        driver.stackSize shouldBe 1
        resolveStack(driver)
        driver.assertLifeTotal(protector, 19)
        driver.assertLifeTotal(attacker, 20)
    }
})

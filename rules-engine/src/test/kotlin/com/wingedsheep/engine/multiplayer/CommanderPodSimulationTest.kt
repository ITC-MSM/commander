package com.wingedsheep.engine.multiplayer

import com.wingedsheep.engine.core.InvariantCheckingActionObserver
import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.DamageDealtEvent
import com.wingedsheep.engine.core.SpellFizzledEvent
import com.wingedsheep.engine.core.OrderTriggeredAbilitiesDecision
import com.wingedsheep.engine.core.Concede
import com.wingedsheep.engine.core.PlayerLeftGameEvent
import com.wingedsheep.engine.core.PlayerLostEvent
import com.wingedsheep.engine.core.TriggeredAbilitiesOrderedResponse
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.core.ZoneChangeEvent
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.identity.CommanderComponent
import com.wingedsheep.engine.state.components.battlefield.DamageComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.state.components.stack.TriggeredAbilityOnStackComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Format
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Supertype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.mtg.sets.definitions.ori.cards.ArchangelOfTithes
import com.wingedsheep.mtg.sets.definitions.inv.cards.PhyrexianAltar
import com.wingedsheep.mtg.sets.definitions.avr.cards.BloodArtist
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Deterministic four-seat Commander integration traces.
 *
 * These are deliberately scripted rather than random self-play: each submitted
 * action crosses [InvariantCheckingActionObserver], while the trace pins stack
 * and priority boundaries that a seeded smoke simulation cannot guarantee.
 */
class CommanderPodSimulationTest : FunSpec({
    val podCommander = CardDefinition.creature(
        name = "Pod Simulation Commander",
        manaCost = ManaCost.parse("{2}{G}"),
        supertypes = setOf(Supertype.LEGENDARY),
        subtypes = emptySet(),
        power = 21,
        toughness = 21,
        keywords = setOf(Keyword.TRAMPLE),
    )
    val wardedSentinel = card("Pod Warded Sentinel") {
        manaCost = "{1}{G}"
        typeLine = "Creature — Bear"
        power = 2
        toughness = 2
        keywordAbility(KeywordAbility.ward("{2}"))
    }
    val doubleEtb = card("Pod Double ETB") {
        manaCost = "{W}"
        typeLine = "Creature — Human Wizard"
        power = 1
        toughness = 1
        triggeredAbility {
            trigger = Triggers.EntersBattlefield
            effect = Effects.GainLife(1)
        }
        triggeredAbility {
            trigger = Triggers.EntersBattlefield
            effect = Effects.GainLife(2)
        }
    }
    val blockingWitness = card("Pod Blocking Witness") {
        manaCost = "{1}{G}"
        typeLine = "Creature — Bear"
        power = 2
        toughness = 2
        keywords(Keyword.FLYING)
        triggeredAbility {
            trigger = Triggers.Blocks
            effect = Effects.GainLife(1)
        }
    }
    val doubleBlockingWitness = card("Pod Double Blocking Witness") {
        manaCost = "{1}{G}"
        typeLine = "Creature — Bear"
        power = 2
        toughness = 2
        triggeredAbility {
            trigger = Triggers.Blocks
            effect = Effects.GainLife(1)
        }
        triggeredAbility {
            trigger = Triggers.Blocks
            effect = Effects.GainLife(2)
        }
    }
    // Test-local: Deathgreeter has no registered definition yet.
    val deathgreeter = card("Deathgreeter") {
        manaCost = "{B}"
        typeLine = "Creature — Human Shaman"
        power = 1
        toughness = 1
        oracleText = "Whenever another creature dies, you may gain 1 life."
        triggeredAbility {
            trigger = Triggers.AnyCreatureDies.copy(binding = TriggerBinding.OTHER)
            effect = MayEffect(Effects.GainLife(1))
        }
    }

    fun pod(): Pair<GameTestDriver, List<com.wingedsheep.sdk.model.EntityId>> {
        val driver = GameTestDriver(InvariantCheckingActionObserver())
        driver.registerCards(TestCards.all + listOf(
            podCommander, wardedSentinel, doubleEtb, blockingWitness, doubleBlockingWitness,
            ArchangelOfTithes, PhyrexianAltar, BloodArtist, deathgreeter,
        ))
        val players = driver.initMultiplayer(
            decks = List(4) { Deck(cards = List(99) { "Mountain" }) },
            format = Format.Commander(),
            commanders = List(4) { podCommander.name },
            skipMulligans = true,
            startingPlayer = 0,
        )
        return driver to players
    }

    fun passRound(driver: GameTestDriver, players: List<com.wingedsheep.sdk.model.EntityId>) {
        players.forEach { player ->
            driver.priorityPlayer shouldBe player
            driver.passPriority(player).error shouldBe null
        }
    }

    /** Setup only: exercise a real Commander entity rather than minting a similar creature. */
    fun commanderOnBattlefield(driver: GameTestDriver, owner: com.wingedsheep.sdk.model.EntityId): com.wingedsheep.sdk.model.EntityId {
        val commander = driver.state.getZone(ZoneKey(owner, Zone.COMMAND)).single()
        driver.state.getEntity(commander)?.has<CommanderComponent>() shouldBe true
        driver.replaceState(
            driver.state
                .removeFromZone(ZoneKey(owner, Zone.COMMAND), commander)
                .addToZone(ZoneKey(owner, Zone.BATTLEFIELD), commander)
        )
        return commander
    }

    test("four-seat Commander pod counters Ward, then resolves the original spell through SBA") {
        val (driver, players) = pod()
        val (a, b, c, d) = players
        driver.passPriorityUntil(com.wingedsheep.sdk.core.Step.PRECOMBAT_MAIN)
        driver.activePlayer shouldBe a
        driver.priorityPlayer shouldBe a

        val sentinel = driver.putCreatureOnBattlefield(c, wardedSentinel.name)
        driver.giveMana(a, Color.RED, 1)
        driver.giveMana(b, Color.BLUE, 1)
        val bolt = driver.putCardInHand(a, "Lightning Bolt")
        val stifle = driver.putCardInHand(b, "Stifle")

        driver.castSpellWithTargets(a, bolt, listOf(ChosenTarget.Permanent(sentinel))).error shouldBe null
        driver.priorityPlayer shouldBe a

        // B cannot respond until A has passed; the observer also proves this rejected input is inert.
        driver.castSpellWithTargets(b, stifle, listOf(ChosenTarget.Spell(driver.getTopOfStack()!!))).error shouldNotBe null
        driver.passPriority(a).error shouldBe null
        driver.priorityPlayer shouldBe b

        val ward = driver.getTopOfStack()!!
        driver.castSpellWithTargets(b, stifle, listOf(ChosenTarget.Spell(ward))).error shouldBe null
        driver.priorityPlayer shouldBe b
        passRound(driver, listOf(b, c, d, a))

        driver.stackSize shouldBe 1
        driver.priorityPlayer shouldBe a
        passRound(driver, players)

        driver.stackSize shouldBe 0
        driver.getGraveyardCardNames(a).contains("Lightning Bolt") shouldBe true
        driver.getGraveyardCardNames(b).contains("Stifle") shouldBe true
        driver.state.getZone(ZoneKey(c, Zone.BATTLEFIELD)).contains(sentinel) shouldBe false
        driver.getGraveyardCardNames(c).contains(wardedSentinel.name) shouldBe true
        driver.priorityPlayer shouldBe a
    }

    test("four-seat Commander pod lets the caster decline Ward and counters only the targeting spell") {
        val (driver, players) = pod()
        val (a, _, c, _) = players
        driver.passPriorityUntil(com.wingedsheep.sdk.core.Step.PRECOMBAT_MAIN)
        val sentinel = driver.putCreatureOnBattlefield(c, wardedSentinel.name)
        // The decision exists only when A could pay {2}; otherwise Ward counters immediately.
        driver.putLandOnBattlefield(a, "Mountain")
        driver.putLandOnBattlefield(a, "Mountain")
        driver.giveMana(a, Color.RED, 1)
        val bolt = driver.putCardInHand(a, "Lightning Bolt")

        driver.castSpellWithTargets(a, bolt, listOf(ChosenTarget.Permanent(sentinel))).error shouldBe null
        passRound(driver, players)
        driver.pendingDecision?.playerId shouldBe a
        driver.submitManaAutoPayOrDecline(a, autoPay = false).error shouldBe null

        driver.stackSize shouldBe 0
        driver.state.getZone(ZoneKey(c, Zone.BATTLEFIELD)).shouldContainExactly(sentinel)
        driver.getGraveyardCardNames(a).contains("Lightning Bolt") shouldBe true
        driver.priorityPlayer shouldBe a
    }

    test("four-seat Commander pod pays Ward, then resolves the original spell through SBA") {
        val (driver, players) = pod()
        val (a, _, c, _) = players
        driver.passPriorityUntil(com.wingedsheep.sdk.core.Step.PRECOMBAT_MAIN)
        val sentinel = driver.putCreatureOnBattlefield(c, wardedSentinel.name)
        driver.putLandOnBattlefield(a, "Mountain")
        driver.putLandOnBattlefield(a, "Mountain")
        driver.giveMana(a, Color.RED, 1)
        val bolt = driver.putCardInHand(a, "Lightning Bolt")

        driver.castSpellWithTargets(a, bolt, listOf(ChosenTarget.Permanent(sentinel))).error shouldBe null
        passRound(driver, players)
        driver.pendingDecision?.playerId shouldBe a
        driver.submitManaAutoPayOrDecline(a, autoPay = true).error shouldBe null

        driver.stackSize shouldBe 1
        passRound(driver, players)
        driver.stackSize shouldBe 0
        driver.state.getZone(ZoneKey(c, Zone.BATTLEFIELD)).shouldContainExactly()
        driver.getGraveyardCardNames(c).contains(wardedSentinel.name) shouldBe true
        driver.priorityPlayer shouldBe a
    }

    test("four-seat Commander pod offers the owner the pre-move command-zone choice for a bounce") {
        val (driver, players) = pod()
        val (a, _, c, _) = players
        driver.passPriorityUntil(com.wingedsheep.sdk.core.Step.PRECOMBAT_MAIN)
        val commander = commanderOnBattlefield(driver, c)
        driver.giveMana(a, Color.BLUE, 1)
        val unsummon = driver.putCardInHand(a, "Unsummon")
        val eventStart = driver.events.size

        driver.castSpellWithTargets(a, unsummon, listOf(ChosenTarget.Permanent(commander))).error shouldBe null
        passRound(driver, players)

        driver.pendingDecision?.playerId shouldBe c
        driver.state.getZone(ZoneKey(c, Zone.BATTLEFIELD)).shouldContainExactly(commander)
        driver.submitYesNo(c, choice = true).error shouldBe null
        driver.state.getZone(ZoneKey(c, Zone.COMMAND)).shouldContainExactly(commander)
        driver.state.getZone(ZoneKey(c, Zone.HAND)).contains(commander) shouldBe false
        driver.events.drop(eventStart).filterIsInstance<ZoneChangeEvent>()
            .filter { it.entityId == commander }
            .map { it.fromZone to it.toZone }.shouldContainExactly(Zone.BATTLEFIELD to Zone.COMMAND)
        driver.getGraveyardCardNames(a).contains("Unsummon") shouldBe true
        driver.priorityPlayer shouldBe a
    }

    test("four-seat Commander pod honors the owner declining a bounce replacement") {
        val (driver, players) = pod()
        val (a, _, c, _) = players
        driver.passPriorityUntil(com.wingedsheep.sdk.core.Step.PRECOMBAT_MAIN)
        val commander = commanderOnBattlefield(driver, c)
        driver.giveMana(a, Color.BLUE, 1)
        val unsummon = driver.putCardInHand(a, "Unsummon")
        val eventStart = driver.events.size

        driver.castSpellWithTargets(a, unsummon, listOf(ChosenTarget.Permanent(commander))).error shouldBe null
        passRound(driver, players)
        driver.pendingDecision?.playerId shouldBe c
        driver.submitYesNo(c, choice = false).error shouldBe null

        driver.state.getZone(ZoneKey(c, Zone.HAND)).contains(commander) shouldBe true
        driver.state.getZone(ZoneKey(c, Zone.COMMAND)).shouldContainExactly()
        driver.events.drop(eventStart).filterIsInstance<ZoneChangeEvent>()
            .filter { it.entityId == commander }
            .map { it.fromZone to it.toZone }.shouldContainExactly(Zone.BATTLEFIELD to Zone.HAND)
        driver.getGraveyardCardNames(a).contains("Unsummon") shouldBe true
        driver.priorityPlayer shouldBe a
    }

    test("four-seat Commander pod removes a conceding spell target and skips that seat") {
        val (driver, players) = pod()
        val (a, b, c, d) = players
        driver.passPriorityUntil(com.wingedsheep.sdk.core.Step.PRECOMBAT_MAIN)
        val cCommander = driver.state.getZone(ZoneKey(c, Zone.COMMAND)).single()
        driver.giveMana(a, Color.RED, 1)
        val bolt = driver.putCardInHand(a, "Lightning Bolt")
        val eventStart = driver.events.size

        driver.castSpellWithTargets(a, bolt, listOf(ChosenTarget.Player(c))).error shouldBe null
        driver.passPriority(a).error shouldBe null
        driver.passPriority(b).error shouldBe null
        driver.priorityPlayer shouldBe c
        driver.submit(Concede(c)).error shouldBe null

        driver.state.activePlayers.shouldContainExactly(a, b, d)
        driver.state.getEntity(cCommander) shouldBe null
        driver.events.drop(eventStart).filterIsInstance<PlayerLostEvent>().single().playerId shouldBe c
        driver.events.drop(eventStart).filterIsInstance<PlayerLeftGameEvent>().single().playerId shouldBe c
        driver.priorityPlayer shouldBe d

        // The historical player entity remains, but cannot be newly targeted after leaving.
        driver.giveMana(a, Color.RED, 1)
        val invalidBolt = driver.putCardInHand(a, "Lightning Bolt")
        driver.passPriority(d).error shouldBe null
        driver.priorityPlayer shouldBe a
        driver.castSpellWithTargets(a, invalidBolt, listOf(ChosenTarget.Player(c))).error shouldNotBe null
        driver.passPriority(a).error shouldBe null
        driver.passPriority(b).error shouldBe null
        driver.stackSize shouldBe 0
        driver.getGraveyardCardNames(a).contains("Lightning Bolt") shouldBe true
        driver.events.drop(eventStart).filterIsInstance<SpellFizzledEvent>()
            .filter { it.spellEntityId == bolt }.single().reason shouldBe "All targets are invalid"
        driver.events.drop(eventStart).filterIsInstance<DamageDealtEvent>()
            .filter { it.sourceId == bolt }.shouldContainExactly()
        driver.priorityPlayer shouldBe a
    }

    test("four-seat Commander pod counts a countered command-zone cast and charges tax on recast") {
        val (driver, players) = pod()
        val (a, b, c, d) = players
        driver.passPriorityUntil(com.wingedsheep.sdk.core.Step.PRECOMBAT_MAIN)
        val commander = driver.state.getZone(ZoneKey(a, Zone.COMMAND)).single()
        val eventStart = driver.events.size
        driver.giveMana(a, Color.GREEN, 1)
        driver.giveMana(a, Color.RED, 2)
        driver.giveMana(b, Color.BLUE, 2)
        val counterspell = driver.putCardInHand(b, "Counterspell")

        driver.castSpell(a, commander).error shouldBe null
        driver.state.getEntity(commander)!!.get<CommanderComponent>()!!.castsFromCommandZone shouldBe 1
        driver.passPriority(a).error shouldBe null
        driver.castSpellWithTargets(b, counterspell, listOf(ChosenTarget.Spell(driver.getTopOfStack()!!))).error shouldBe null
        passRound(driver, listOf(b, c, d, a))

        // CR 903.9a is an SBA choice after the counter has made STACK -> GRAVEYARD.
        driver.pendingDecision?.playerId shouldBe a
        driver.submitYesNo(a, choice = true).error shouldBe null
        driver.state.getZone(ZoneKey(a, Zone.COMMAND)).shouldContainExactly(commander)
        driver.events.drop(eventStart).filterIsInstance<ZoneChangeEvent>()
            .filter { it.entityId == commander }
            .map { it.fromZone to it.toZone }
            .shouldContainExactly(Zone.STACK to Zone.GRAVEYARD, Zone.GRAVEYARD to Zone.COMMAND)
        driver.getGraveyardCardNames(b).contains("Counterspell") shouldBe true
        driver.priorityPlayer shouldBe a

        // {2}{G} is insufficient now that the next command-zone cast has the {2} tax.
        driver.giveMana(a, Color.GREEN, 1)
        driver.giveMana(a, Color.RED, 2)
        driver.castSpell(a, commander).error shouldNotBe null
        driver.giveMana(a, Color.RED, 2)
        driver.castSpell(a, commander).error shouldBe null
        driver.state.getEntity(commander)!!.get<CommanderComponent>()!!.castsFromCommandZone shouldBe 2
    }

    test("four-seat Commander pod exposes controller-local ordering before simultaneous triggers enter stack") {
        val (driver, players) = pod()
        val a = players.first()
        driver.passPriorityUntil(com.wingedsheep.sdk.core.Step.PRECOMBAT_MAIN)
        driver.giveMana(a, Color.WHITE, 1)
        val creature = driver.putCardInHand(a, doubleEtb.name)

        driver.castSpell(a, creature).error shouldBe null
        passRound(driver, players)

        val decision = driver.pendingDecision.shouldBeInstanceOf<OrderTriggeredAbilitiesDecision>()
        decision.playerId shouldBe a
        decision.abilities.size shouldBe 2
        driver.submitDecision(
            a,
            TriggeredAbilitiesOrderedResponse(decision.id, decision.abilities.map { it.id }),
        ).error shouldBe null
        driver.stackSize shouldBe 2
        passRound(driver, players)
        driver.stackSize shouldBe 1
        passRound(driver, players)
        driver.stackSize shouldBe 0
        driver.getLifeTotal(a) shouldBe 43
        driver.priorityPlayer shouldBe a
    }

    test("APNAP-4P-TARGET-MAY-001: four-seat Commander orders death triggers locally, targets them, and asks Deathgreeter only on resolution") {
        val (driver, players) = pod()
        val (a, b, c, d) = players
        driver.passPriorityUntil(com.wingedsheep.sdk.core.Step.PRECOMBAT_MAIN)
        val altar = driver.putPermanentOnBattlefield(a, PhyrexianAltar.name)
        driver.putCreatureOnBattlefield(a, BloodArtist.name)
        driver.putCreatureOnBattlefield(a, deathgreeter.name)
        driver.putCreatureOnBattlefield(b, BloodArtist.name)
        driver.putCreatureOnBattlefield(c, BloodArtist.name)
        driver.putCreatureOnBattlefield(d, BloodArtist.name)
        val bear = driver.putCreatureOnBattlefield(a, wardedSentinel.name)
        val lifeBefore = players.associateWith(driver::getLifeTotal)

        driver.submit(
            ActivateAbility(
                playerId = a,
                sourceId = altar,
                abilityId = PhyrexianAltar.activatedAbilities.single().id,
                costPayment = AdditionalCostPayment(sacrificedPermanents = listOf(bear)),
                manaColorChoice = Color.BLACK,
            )
        ).error shouldBe null
        driver.stackSize shouldBe 0
        driver.getGraveyardCardNames(a).contains(wardedSentinel.name) shouldBe true

        // Active A orders its two triggers first. A forged duplicate/omitted permutation is inert.
        val aOrder = driver.pendingDecision.shouldBeInstanceOf<OrderTriggeredAbilitiesDecision>()
        aOrder.playerId shouldBe a
        val aAbilityIds = aOrder.abilities.associateBy { it.sourceName }
        aAbilityIds.keys.sorted().shouldContainExactly(BloodArtist.name, deathgreeter.name)
        val beforeForgedOrder = driver.state
        driver.submitDecision(
            a,
            TriggeredAbilitiesOrderedResponse(aOrder.id, listOf(aOrder.abilities.first().id, aOrder.abilities.first().id)),
        ).error shouldNotBe null
        driver.state shouldBe beforeForgedOrder
        driver.pendingDecision shouldBe aOrder

        // A's Blood Artist, then Deathgreeter, are placed before B/C/D in APNAP order. Each Artist
        // gets its target while being put on the stack; Deathgreeter's May remains for resolution.
        val orderResult = driver.submitDecision(
            a,
            TriggeredAbilitiesOrderedResponse(
                aOrder.id,
                listOf(aAbilityIds.getValue(BloodArtist.name).id, aAbilityIds.getValue(deathgreeter.name).id),
            ),
        )
        orderResult.error shouldBe null
        driver.pendingDecision?.playerId shouldBe a
        val aTargetResult = driver.submitTargetSelection(a, listOf(b))
        aTargetResult.error shouldBe null
        driver.pendingDecision?.playerId shouldBe b
        driver.submitTargetSelection(b, listOf(a)).error shouldBe null
        driver.pendingDecision?.playerId shouldBe c
        driver.submitTargetSelection(c, listOf(a)).error shouldBe null
        driver.pendingDecision?.playerId shouldBe d
        driver.submitTargetSelection(d, listOf(a)).error shouldBe null
        driver.pendingDecision shouldBe null
        driver.stackSize shouldBe 5
        driver.state.stack.map { stackObjectId ->
            driver.state.getEntity(stackObjectId)?.get<TriggeredAbilityOnStackComponent>()?.sourceName
        }.shouldContainExactly(
            BloodArtist.name, deathgreeter.name, BloodArtist.name, BloodArtist.name, BloodArtist.name,
        )

        // D, C, B resolve first; each full pass round returns priority to active A.
        repeat(3) {
            passRound(driver, players)
            driver.priorityPlayer shouldBe a
        }
        // Deathgreeter resolves next and only now exposes its optional choice. Decline it.
        passRound(driver, players)
        driver.pendingDecision?.playerId shouldBe a
        driver.submitYesNo(a, choice = false).error shouldBe null
        driver.priorityPlayer shouldBe a
        passRound(driver, players)

        driver.stackSize shouldBe 0
        driver.priorityPlayer shouldBe a
        driver.getLifeTotal(a) shouldBe lifeBefore.getValue(a) - 3 + 1
        driver.getLifeTotal(b) shouldBe lifeBefore.getValue(b) - 1 + 1
        driver.getLifeTotal(c) shouldBe lifeBefore.getValue(c) + 1
        driver.getLifeTotal(d) shouldBe lifeBefore.getValue(d) + 1
    }

    test("Deathgreeter does not trigger when it dies itself") {
        val (driver, players) = pod()
        val (a, _, _, _) = players
        driver.passPriorityUntil(com.wingedsheep.sdk.core.Step.PRECOMBAT_MAIN)
        val greeter = driver.putCreatureOnBattlefield(a, deathgreeter.name)
        driver.giveMana(a, Color.RED, 1)
        val bolt = driver.putCardInHand(a, "Lightning Bolt")

        driver.castSpellWithTargets(a, bolt, listOf(ChosenTarget.Permanent(greeter))).error shouldBe null
        passRound(driver, players)

        driver.stackSize shouldBe 0
        driver.getGraveyardCardNames(a).contains(deathgreeter.name) shouldBe true
        driver.pendingDecision shouldBe null
        driver.priorityPlayer shouldBe a
    }

    test("four-seat Commander combat assigns simultaneous damage to separate defenders before commander SBA") {
        val (driver, players) = pod()
        val (a, b, c, d) = players
        val commander = commanderOnBattlefield(driver, a)
        val ordinaryAttacker = driver.putCreatureOnBattlefield(a, wardedSentinel.name)
        val blocker = driver.putCreatureOnBattlefield(b, blockingWitness.name)
        driver.removeSummoningSickness(commander)
        driver.removeSummoningSickness(ordinaryAttacker)

        // The commander has dealt 2 to B in an earlier combat; this combat's 19 trample damage
        // reaches the 21-damage Commander-loss threshold only after combat damage is dealt.
        driver.replaceState(driver.state.recordCommanderDamage(commander, b, 2))
        val eventStart = driver.events.size

        driver.passPriorityUntil(com.wingedsheep.sdk.core.Step.DECLARE_ATTACKERS)
        driver.declareAttackers(a, mapOf(commander to b, ordinaryAttacker to d)).error shouldBe null
        driver.passPriorityUntil(com.wingedsheep.sdk.core.Step.DECLARE_BLOCKERS)
        driver.priorityPlayer shouldBe b
        driver.declareBlockers(b, mapOf(blocker to listOf(commander))).error shouldBe null
        driver.priorityPlayer shouldBe c
        driver.declareNoBlockers(c).error shouldBe null
        driver.priorityPlayer shouldBe d
        driver.declareNoBlockers(d).error shouldBe null
        // Entering COMBAT_DAMAGE is a priority window. Continue through its pass round so
        // the combat-damage turn-based action and following SBAs have actually occurred.
        driver.passPriorityUntil(com.wingedsheep.sdk.core.Step.POSTCOMBAT_MAIN)

        // 2 is lethal to B's blocker, so trample sends the remaining 19 to B; D's unblocked
        // ordinary attacker also deals its 2 at the same combat-damage boundary.
        driver.state.commanderDamageOf(commander, b) shouldBe 21
        driver.getLifeTotal(d) shouldBe 38
        driver.state.activePlayers.shouldContainExactly(a, c, d)
        val combatEvents = driver.events.drop(eventStart)
        combatEvents.filterIsInstance<DamageDealtEvent>().any {
            it.sourceId == commander && it.targetId == b && it.amount == 19 && it.isCombatDamage
        } shouldBe true
        combatEvents.filterIsInstance<DamageDealtEvent>().any {
            it.sourceId == ordinaryAttacker && it.targetId == d && it.amount == 2 && it.isCombatDamage
        } shouldBe true
        val bLoss = combatEvents.indexOfFirst { it is PlayerLostEvent && it.playerId == b }
        val dDamage = combatEvents.indexOfFirst {
            it is DamageDealtEvent && it.sourceId == ordinaryAttacker && it.targetId == d
        }
        bLoss shouldNotBe -1
        dDamage shouldNotBe -1
        (bLoss > dDamage) shouldBe true
        driver.priorityPlayer shouldBe a
    }

    test("four-seat Commander pod waits for every defender before placing block triggers") {
        val (driver, players) = pod()
        val (a, b, c, d) = players
        val commander = commanderOnBattlefield(driver, a)
        val ordinaryAttacker = driver.putCreatureOnBattlefield(a, wardedSentinel.name)
        val blocker = driver.putCreatureOnBattlefield(b, blockingWitness.name)
        driver.removeSummoningSickness(commander)
        driver.removeSummoningSickness(ordinaryAttacker)

        driver.passPriorityUntil(com.wingedsheep.sdk.core.Step.DECLARE_ATTACKERS)
        driver.declareAttackers(a, mapOf(commander to b, ordinaryAttacker to d)).error shouldBe null
        driver.passPriorityUntil(com.wingedsheep.sdk.core.Step.DECLARE_BLOCKERS)

        // C and D are defenders too, but must wait for B's earlier APNAP declaration turn.
        driver.declareNoBlockers(d).error shouldNotBe null
        driver.priorityPlayer shouldBe b

        // B's "when this blocks" trigger is not placed while D still has the mandatory
        // declare-blockers turn. D can complete an empty declaration without a decision/stack pause.
        driver.declareBlockers(b, mapOf(blocker to listOf(commander))).error shouldBe null
        driver.priorityPlayer shouldBe c
        driver.stackSize shouldBe 0
        driver.pendingDecision shouldBe null

        driver.declareNoBlockers(c).error shouldBe null
        driver.priorityPlayer shouldBe d
        driver.declareNoBlockers(d).error shouldBe null
        driver.priorityPlayer shouldBe a
        driver.stackSize shouldBe 1
        passRound(driver, players)
        driver.stackSize shouldBe 0
        driver.getLifeTotal(b) shouldBe 41
    }

    test("four-seat Commander rechecks SBAs after a paused block-trigger placement wave") {
        val (driver, players) = pod()
        val (a, b, c, _) = players
        val attacker = commanderOnBattlefield(driver, a)
        val blocker = driver.putCreatureOnBattlefield(b, doubleBlockingWitness.name)
        val sbaVictim = driver.putCreatureOnBattlefield(c, wardedSentinel.name)
        driver.removeSummoningSickness(attacker)

        driver.passPriorityUntil(com.wingedsheep.sdk.core.Step.DECLARE_ATTACKERS)
        driver.declareAttackers(a, listOf(attacker), b).error shouldBe null
        driver.passPriorityUntil(com.wingedsheep.sdk.core.Step.DECLARE_BLOCKERS)
        driver.declareBlockers(b, mapOf(blocker to listOf(attacker))).error shouldBe null

        val order = driver.pendingDecision.shouldBeInstanceOf<OrderTriggeredAbilitiesDecision>()
        order.playerId shouldBe b
        order.abilities.size shouldBe 2

        // Corrupted-negative control: emulate an SBA becoming applicable while trigger placement
        // is paused. The placement continuation must finish the complete wave, then run the
        // second SBA checkpoint before assigning priority; the old one-pass boundary left this
        // lethally damaged creature on the battlefield.
        driver.replaceState(
            driver.state.updateEntity(sbaVictim) { it.with(DamageComponent(amount = 2)) }
        )
        driver.submitDecision(
            b,
            TriggeredAbilitiesOrderedResponse(order.id, order.abilities.map { it.id }),
        ).error shouldBe null

        driver.pendingDecision shouldBe null
        driver.priorityPlayer shouldBe a
        driver.stackSize shouldBe 2
        driver.state.getZone(ZoneKey(c, Zone.BATTLEFIELD)).contains(sbaVictim) shouldBe false
        driver.getGraveyardCardNames(c).contains(wardedSentinel.name) shouldBe true
    }

    test("four-seat Commander pod keeps paid intermediate block tax off the stack and advances the defender cursor") {
        val (driver, players) = pod()
        val (a, b, c, d) = players
        val archangel = driver.putCreatureOnBattlefield(a, ArchangelOfTithes.name)
        val otherAttacker = driver.putCreatureOnBattlefield(a, wardedSentinel.name)
        val blocker = driver.putCreatureOnBattlefield(b, blockingWitness.name)
        driver.removeSummoningSickness(archangel)
        driver.removeSummoningSickness(otherAttacker)
        driver.putLandOnBattlefield(b, "Plains")

        driver.passPriorityUntil(com.wingedsheep.sdk.core.Step.DECLARE_ATTACKERS)
        driver.declareAttackers(a, mapOf(archangel to b, otherAttacker to d)).error shouldBe null
        driver.passPriorityUntil(com.wingedsheep.sdk.core.Step.DECLARE_BLOCKERS)
        driver.priorityPlayer shouldBe b

        driver.declareBlockers(b, mapOf(blocker to listOf(archangel))).error shouldBe null
        driver.pendingDecision.shouldBeInstanceOf<SelectManaSourcesDecision>().playerId shouldBe b
        driver.submitManaAutoPayOrDecline(b, autoPay = true).error shouldBe null

        // B's trigger remains deferred: C owns the next mandatory declaration turn.
        driver.priorityPlayer shouldBe c
        driver.stackSize shouldBe 0
        driver.pendingDecision shouldBe null
    }

    test("four-seat Commander pod lets a declining blocker retry with an empty declaration") {
        val (driver, players) = pod()
        val (a, b, _, _) = players
        val archangel = driver.putCreatureOnBattlefield(a, ArchangelOfTithes.name)
        val blocker = driver.putCreatureOnBattlefield(b, blockingWitness.name)
        driver.removeSummoningSickness(archangel)
        driver.putLandOnBattlefield(b, "Plains")

        driver.passPriorityUntil(com.wingedsheep.sdk.core.Step.DECLARE_ATTACKERS)
        driver.declareAttackers(a, listOf(archangel), b).error shouldBe null
        driver.passPriorityUntil(com.wingedsheep.sdk.core.Step.DECLARE_BLOCKERS)
        driver.declareBlockers(b, mapOf(blocker to listOf(archangel))).error shouldBe null
        driver.pendingDecision.shouldBeInstanceOf<SelectManaSourcesDecision>().playerId shouldBe b
        driver.submitManaAutoPayOrDecline(b, autoPay = false).error shouldBe null

        // Declining the proposed taxed block has not consumed B's declaration opportunity.
        driver.priorityPlayer shouldBe b
        driver.pendingDecision shouldBe null
        driver.declareNoBlockers(b).error shouldBe null
        driver.priorityPlayer shouldBe a
    }

    test("four-seat Commander pod completes a paid final block tax before placing deferred triggers") {
        val (driver, players) = pod()
        val (a, b, _, _) = players
        val archangel = driver.putCreatureOnBattlefield(a, ArchangelOfTithes.name)
        val blocker = driver.putCreatureOnBattlefield(b, blockingWitness.name)
        driver.removeSummoningSickness(archangel)
        driver.putLandOnBattlefield(b, "Plains")

        driver.passPriorityUntil(com.wingedsheep.sdk.core.Step.DECLARE_ATTACKERS)
        // With only B directly attacked, B is the sole defender in this FFA combat.
        driver.declareAttackers(a, listOf(archangel), b).error shouldBe null
        driver.passPriorityUntil(com.wingedsheep.sdk.core.Step.DECLARE_BLOCKERS)
        driver.declareBlockers(b, mapOf(blocker to listOf(archangel))).error shouldBe null
        driver.pendingDecision.shouldBeInstanceOf<SelectManaSourcesDecision>().playerId shouldBe b
        driver.submitManaAutoPayOrDecline(b, autoPay = true).error shouldBe null

        driver.priorityPlayer shouldBe a
        driver.pendingDecision shouldBe null
        driver.stackSize shouldBe 1
        passRound(driver, players)
        driver.stackSize shouldBe 0
        driver.getLifeTotal(b) shouldBe 41
    }

    test("four-seat Commander drains a deferred block trigger once when the final defender concedes") {
        val (driver, players) = pod()
        val (a, b, c, d) = players
        val commander = commanderOnBattlefield(driver, a)
        val ordinaryAttacker = driver.putCreatureOnBattlefield(a, wardedSentinel.name)
        val blocker = driver.putCreatureOnBattlefield(b, blockingWitness.name)
        driver.removeSummoningSickness(commander)
        driver.removeSummoningSickness(ordinaryAttacker)
        val eventStart = driver.events.size

        driver.passPriorityUntil(com.wingedsheep.sdk.core.Step.DECLARE_ATTACKERS)
        driver.declareAttackers(a, mapOf(commander to b, ordinaryAttacker to d)).error shouldBe null
        driver.passPriorityUntil(com.wingedsheep.sdk.core.Step.DECLARE_BLOCKERS)

        // B's trigger must remain deferred while C and D still own declaration turns.
        driver.declareBlockers(b, mapOf(blocker to listOf(commander))).error shouldBe null
        driver.priorityPlayer shouldBe c
        driver.declareNoBlockers(c).error shouldBe null
        driver.priorityPlayer shouldBe d
        driver.stackSize shouldBe 0
        driver.pendingDecision shouldBe null

        // D leaves instead of declaring. Its skipped declaration completes the defender sequence;
        // only then may B's already-detected trigger enter the stack, exactly once.
        driver.submit(Concede(d)).error shouldBe null
        driver.state.activePlayers.shouldContainExactly(a, b, c)
        driver.events.drop(eventStart).filterIsInstance<PlayerLostEvent>().single().playerId shouldBe d
        driver.events.drop(eventStart).filterIsInstance<PlayerLeftGameEvent>().single().playerId shouldBe d
        driver.priorityPlayer shouldBe a
        driver.stackSize shouldBe 1
        driver.pendingDecision shouldBe null

        passRound(driver, listOf(a, b, c))
        driver.stackSize shouldBe 0
        driver.getLifeTotal(b) shouldBe 41
        driver.priorityPlayer shouldBe a
    }

    test("four-seat Commander rejects every later defender before the APNAP blocker cursor") {
        val (driver, players) = pod()
        val (a, b, c, d) = players
        val commander = commanderOnBattlefield(driver, a)
        val ordinaryAttacker = driver.putCreatureOnBattlefield(a, wardedSentinel.name)
        driver.removeSummoningSickness(commander)
        driver.removeSummoningSickness(ordinaryAttacker)

        driver.passPriorityUntil(com.wingedsheep.sdk.core.Step.DECLARE_ATTACKERS)
        driver.declareAttackers(a, mapOf(commander to b, ordinaryAttacker to d)).error shouldBe null
        driver.passPriorityUntil(com.wingedsheep.sdk.core.Step.DECLARE_BLOCKERS)
        driver.priorityPlayer shouldBe b

        driver.declareNoBlockers(c).error shouldNotBe null
        driver.declareNoBlockers(d).error shouldNotBe null
        driver.priorityPlayer shouldBe b

        driver.declareNoBlockers(b).error shouldBe null
        driver.priorityPlayer shouldBe c
        driver.declareNoBlockers(d).error shouldNotBe null
        driver.priorityPlayer shouldBe c

        driver.declareNoBlockers(c).error shouldBe null
        driver.priorityPlayer shouldBe d
        driver.declareNoBlockers(d).error shouldBe null
        driver.priorityPlayer shouldBe a
    }
})

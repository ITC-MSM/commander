package com.wingedsheep.gym.simulation

import com.wingedsheep.engine.core.GameConfig
import com.wingedsheep.engine.core.ActionProcessorObserver
import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.InvariantViolation
import com.wingedsheep.engine.core.InvariantViolationException
import com.wingedsheep.engine.core.PlayerConfig
import com.wingedsheep.engine.core.SubmitDecision
import com.wingedsheep.engine.core.TriggeredAbilitiesOrderedResponse
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.gym.GameEnvironment
import com.wingedsheep.mtg.sets.definitions.por.PortalSet
import com.wingedsheep.sdk.core.Format
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Supertype
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.costs.CostAtom
import com.wingedsheep.sdk.scripting.effects.DrawCardsEffect
import com.wingedsheep.sdk.scripting.effects.Gate
import com.wingedsheep.sdk.scripting.effects.GatedEffect
import com.wingedsheep.sdk.dsl.Triggers
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe

class SeededSimulationRunnerTest : FunSpec({
    fun registry() = CardRegistry().apply {
        register(PortalSet.cards)
        register(PortalSet.basicLands)
    }

    val config = GameConfig(
        players = listOf(
            PlayerConfig("Alice", Deck.of("Mountain" to 17, "Raging Goblin" to 3)),
            PlayerConfig("Bob", Deck.of("Mountain" to 17, "Raging Goblin" to 3)),
        ),
        skipMulligans = true,
        startingPlayerIndex = 0,
        seed = 9898L,
    )

    test("same game and policy seeds reproduce an invariant-checked action trace") {
        val runner = SeededSimulationRunner(registry())
        val first = runner.run(config, policySeed = 77L, maxSteps = 48)
        val second = runner.run(config, policySeed = 77L, maxSteps = 48)

        first.outcome shouldBe SimulationOutcome.STEP_LIMIT
        first.entries.shouldNotBeEmpty()
        first.gameSeed shouldBe 9898L
        first.entries shouldBe second.entries

        val imported = SimulationTranscript.fromJson(first.toJson())
        imported shouldBe first
        runner.replay(config, imported).valid shouldBe true

        val corrupted = imported.copy(entries = imported.entries.mapIndexed { index, entry ->
            if (index == 0) entry.copy(postDigest = "tampered") else entry
        })
        runner.replay(config, corrupted).valid shouldBe false
    }

    test("a transcript replays with its resolved seed when the original config used entropy") {
        val entropyConfig = config.copy(seed = null)
        val runner = SeededSimulationRunner(registry())

        val transcript = runner.run(entropyConfig, policySeed = 77L, maxSteps = 16)

        runner.replay(entropyConfig, SimulationTranscript.fromJson(transcript.toJson())).valid shouldBe true
    }

    test("replay rejects a supplied configuration that differs from transcript evidence") {
        val runner = SeededSimulationRunner(registry())
        val transcript = runner.run(config, policySeed = 77L, maxSteps = 16)
        val mismatchedConfig = config.copy(players = config.players.mapIndexed { index, player ->
            if (index == 0) player.copy(playerId = EntityId.of("different-replay-player")) else player
        })

        val replay = runner.replay(mismatchedConfig, transcript)
        replay.valid shouldBe false
        replay.failure.orEmpty().contains("config differs") shouldBe true
    }

    test("simulation evidence retains explicit PlayerConfig player IDs") {
        val explicitId = EntityId.of("simulation-evidence-player")
        val identifiedConfig = config.copy(players = config.players.mapIndexed { index, player ->
            if (index == 0) player.copy(playerId = explicitId) else player
        })
        val runner = SeededSimulationRunner(registry())
        val transcript = runner.run(identifiedConfig, policySeed = 77L, maxSteps = 16)

        transcript.config.players.first().playerId shouldBe explicitId
        runner.replay(identifiedConfig, SimulationTranscript.fromJson(transcript.toJson())).valid shouldBe true
    }

    test("replay rejects a freshly enumerated but unaffordable priority action") {
        val abilityId = AbilityId("replay_unaffordable_ability")
        val abilityPermanent = CardDefinition.creature(
            name = "Replay Unaffordable Ability Permanent",
            manaCost = ManaCost.parse("{0}"),
            subtypes = setOf(Subtype("Wizard")),
            power = 1,
            toughness = 1,
            script = CardScript(
                activatedAbilities = listOf(
                    com.wingedsheep.sdk.scripting.ActivatedAbility(
                        id = abilityId,
                        cost = AbilityCost.Atom(CostAtom.Mana(ManaCost.parse("{1}"))),
                        effect = DrawCardsEffect(1),
                    )
                )
            )
        )
        val unaffordableConfig = GameConfig(
            players = listOf(
                PlayerConfig("Alice", Deck.of(abilityPermanent.name to 20)),
                PlayerConfig("Bob", Deck.of("Raging Goblin" to 20)),
            ),
            skipMulligans = true,
            startingPlayerIndex = 0,
            seed = 1717L,
        )
        val cardRegistry = registry().apply { register(listOf(abilityPermanent)) }
        val runner = SeededSimulationRunner(cardRegistry)
        val transcript = runner.run(unaffordableConfig, policySeed = 3L, maxSteps = 48)
        transcript.entries.shouldNotBeEmpty()
        runner.replay(unaffordableConfig, SimulationTranscript.fromJson(transcript.toJson())).valid shouldBe true

        // Normal unaffordable hand casts are deliberately omitted by CastSpellEnumerator. In
        // contrast, ActivatedAbilityEnumerator deliberately emits a real greyed-out action so the
        // client can show the ability. Find that genuine action at its recorded priority boundary.
        val environment = GameEnvironment.create(cardRegistry)
        environment.reset(unaffordableConfig)
        var unaffordableEntryIndex = -1
        var unaffordableAction: ActivateAbility? = null
        for ((index, entry) in transcript.entries.withIndex()) {
            val candidate = environment.legalActions().firstOrNull { legal ->
                !legal.affordable && (legal.action as? ActivateAbility)?.abilityId == abilityId
            }
            if (candidate != null) {
                candidate.affordable shouldBe false
                candidate.actionType shouldBe "ActivateAbility"
                entry.inputKind shouldBe SimulationInputKind.LEGAL_ACTION
                unaffordableAction = candidate.action as ActivateAbility
                unaffordableEntryIndex = index
                break
            }
            environment.step(entry.action)
        }
        val freshlyEnumeratedUnaffordableAction = requireNotNull(unaffordableAction) {
            "Expected the recorded priority windows to expose the unaffordable test ability"
        }
        require(unaffordableEntryIndex >= 0)

        // Keep every pre/post expectation from the valid trace; replay must reject this input
        // before it can use those post-state fields because the freshly enumerated action is not affordable.
        val corrupted = transcript.copy(entries = transcript.entries.mapIndexed { index, entry ->
            if (index == unaffordableEntryIndex) entry.copy(action = freshlyEnumeratedUnaffordableAction) else entry
        })
        val replay = runner.replay(unaffordableConfig, SimulationTranscript.fromJson(corrupted.toJson()))
        replay.valid shouldBe false
        replay.failure.orEmpty().contains("legal affordable action") shouldBe true
    }

    test("an invariant failure preserves and replays the attempted input as JSON evidence") {
        val runner = SeededSimulationRunner(
            registry = registry(),
            observerFactory = {
                ActionProcessorObserver { _, _, _ ->
                    throw InvariantViolationException(
                        "injected",
                        listOf(InvariantViolation.RejectedActionEmittedEvents),
                    )
                }
            },
        )

        val transcript = runner.run(config, policySeed = 77L, maxSteps = 48)

        transcript.outcome shouldBe SimulationOutcome.INVARIANT_FAILURE
        transcript.entries shouldBe emptyList()
        val failure = requireNotNull(transcript.invariantFailure)
        failure.attemptedStepIndex shouldBe 1
        failure.preDigest shouldBe transcript.initialStateDigest
        failure.canonicalViolations shouldBe listOf("RejectedActionEmittedEvents")
        failure.message.contains("injected") shouldBe true

        val imported = SimulationTranscript.fromJson(transcript.toJson())
        imported shouldBe transcript
        runner.replay(config, imported).valid shouldBe true
        runner.replay(config, imported.copy(invariantFailure = failure.copy(message = "tampered"))).valid shouldBe false
        runner.replay(config, imported.copy(invariantFailure = failure.copy(exceptionType = "tampered.Exception"))).valid shouldBe false
        runner.replay(config, imported.copy(invariantFailure = failure.copy(canonicalViolations = emptyList()))).valid shouldBe false
    }

    test("a four-seat Commander pod produces a reproducible invariant-checked smoke transcript") {
        val commander = CardDefinition.creature(
            name = "Gym Simulation Commander",
            manaCost = ManaCost.parse("{2}{G}"),
            supertypes = setOf(Supertype.LEGENDARY),
            subtypes = emptySet(),
            power = 3,
            toughness = 3,
        )
        val registry = registry().apply { register(listOf(commander)) }
        val podConfig = GameConfig(
            players = List(4) { seat ->
                PlayerConfig(
                    name = "Pod player ${seat + 1}",
                    // Commander is configured separately and must not be in the library.  Ninety-nine
                    // Forests plus the green commander is a legal 100-card Commander deck and has
                    // color identity wholly within the commander's identity.
                    deck = Deck(cards = List(99) { "Forest" }),
                    commanderCardName = commander.name,
                )
            },
            format = Format.Commander(),
            skipMulligans = true,
            startingPlayerIndex = 0,
            seed = 7070L,
        )

        val runner = SeededSimulationRunner(registry)
        val first = runner.run(podConfig, policySeed = 9L, maxSteps = 48)
        val second = runner.run(podConfig, policySeed = 9L, maxSteps = 48)

        first.outcome shouldBe SimulationOutcome.STEP_LIMIT
        first.entries.size shouldBe 48
        first.entries shouldBe second.entries

        val imported = SimulationTranscript.fromJson(first.toJson())
        imported shouldBe first
        runner.replay(podConfig, imported).valid shouldBe true
    }

    test("JSON decision transcripts replay after rebinding fresh decision routing IDs") {
        val decisionCard = CardDefinition.creature(
            name = "Replay Decision Seer",
            manaCost = ManaCost.parse("{0}"),
            subtypes = setOf(Subtype("Wizard")),
            power = 1,
            toughness = 1,
            script = CardScript(
                triggeredAbilities = listOf(
                    TriggeredAbility(
                        id = AbilityId("replay_decision_seer_may"),
                        trigger = Triggers.EntersBattlefield.event,
                        binding = Triggers.EntersBattlefield.binding,
                        effect = GatedEffect(gate = Gate.MayDecide(), then = DrawCardsEffect(1)),
                    )
                )
            )
        )
        val decisionConfig = GameConfig(
            players = listOf(
                PlayerConfig("Alice", Deck.of(decisionCard.name to 20)),
                PlayerConfig("Bob", Deck.of(decisionCard.name to 20)),
            ),
            skipMulligans = true,
            startingPlayerIndex = 0,
            seed = 3333L,
        )
        val runner = SeededSimulationRunner(registry().apply { register(listOf(decisionCard)) })
        val recorded = runner.run(decisionConfig, policySeed = 88L, maxSteps = 96)
        recorded.entries.any { it.inputKind == SimulationInputKind.PENDING_DECISION } shouldBe true

        // Deliberately make the transport ID stale. The selected mulligan choice remains legal,
        // while replay must bind it to the new UUID generated by the fresh initialization.
        val staleIdTranscript = recorded.copy(entries = recorded.entries.map { entry ->
            if (entry.inputKind == SimulationInputKind.PENDING_DECISION) {
                val action = entry.action as SubmitDecision
                entry.copy(action = action.copy(response = action.response.withDecisionId("stale-transcript-id")))
            } else {
                entry
            }
        })
        val imported = SimulationTranscript.fromJson(staleIdTranscript.toJson())
        val replay = runner.replay(decisionConfig, imported)
        check(replay.valid) { replay.failure.orEmpty() }
    }

    test("JSON trigger-order transcripts rebind fresh trigger instance IDs semantically") {
        val triggerCard = CardDefinition.creature(
            name = "Replay Trigger Pair",
            manaCost = ManaCost.parse("{0}"),
            subtypes = setOf(Subtype("Wizard")),
            power = 1,
            toughness = 1,
            script = CardScript(
                triggeredAbilities = List(2) { index ->
                    TriggeredAbility(
                        id = AbilityId("replay_trigger_pair_$index"),
                        trigger = Triggers.EntersBattlefield.event,
                        binding = Triggers.EntersBattlefield.binding,
                        effect = DrawCardsEffect(1),
                    )
                }
            )
        )
        val triggerConfig = GameConfig(
            players = listOf(
                PlayerConfig("Alice", Deck.of(triggerCard.name to 20)),
                PlayerConfig("Bob", Deck.of("Mountain" to 20)),
            ),
            skipMulligans = true,
            startingPlayerIndex = 0,
            seed = 4444L,
        )
        val runner = SeededSimulationRunner(registry().apply { register(listOf(triggerCard)) })
        val transcript = runner.run(triggerConfig, policySeed = 1L, maxSteps = 96)
        val orderingEntry = transcript.entries.firstOrNull { entry ->
            (entry.action as? SubmitDecision)?.response is TriggeredAbilitiesOrderedResponse
        } ?: error("Expected a trigger-ordering decision in deterministic transcript")
        requireNotNull(orderingEntry.triggerOrderItems).size shouldBe 2

        val staleIdTranscript = transcript.copy(entries = transcript.entries.map { entry ->
            if (entry === orderingEntry) {
                val action = entry.action as SubmitDecision
                entry.copy(action = action.copy(response = action.response.withDecisionId("stale-trigger-order-id")))
            } else entry
        })
        val replay = runner.replay(triggerConfig, SimulationTranscript.fromJson(staleIdTranscript.toJson()))
        check(replay.valid) { replay.failure.orEmpty() }
    }
})

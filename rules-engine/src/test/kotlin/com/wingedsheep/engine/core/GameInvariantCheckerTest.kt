package com.wingedsheep.engine.core

import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.player.LossReason
import com.wingedsheep.engine.state.components.player.PlayerLostComponent
import com.wingedsheep.engine.state.components.stack.SpellOnStackComponent
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Format
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Supertype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class GameInvariantCheckerTest : FunSpec({
    val checker = GameInvariantChecker()

    fun validState() = GameInitializer(CardRegistry().apply { register(TestCards.all) })
        .initializeGame(
            GameConfig(
                players = listOf(
                    PlayerConfig("Alice", Deck.of("Forest" to 20)),
                    PlayerConfig("Bob", Deck.of("Forest" to 20)),
                ),
                skipMulligans = true,
                seed = 17L,
            )
        ).state

    test("a freshly initialized game satisfies structural invariants") {
        checker.check(validState()) shouldBe emptyList()
    }

    test("checker reports dangling and duplicated zone entities") {
        val state = validState()
        val alice = state.turnOrder.first()
        val libraryKey = ZoneKey(alice, Zone.LIBRARY)
        val handKey = ZoneKey(alice, Zone.HAND)
        val card = state.getZone(libraryKey).first()

        val corrupted = state.copy(
            zones = state.zones +
                (handKey to listOf(card, EntityId("missing")))
        )

        val violations = checker.check(corrupted)
        violations shouldContain InvariantViolation.EntityInMultipleLocations(card, libraryKey.toString(), handKey.toString())
        violations shouldContain InvariantViolation.DanglingZoneEntity(handKey.toString(), EntityId("missing"))
    }

    test("checker reports a zone card incorrectly placed on stack") {
        val state = validState()
        val card = state.getZone(ZoneKey(state.turnOrder.first(), Zone.LIBRARY)).first()

        checker.check(state.copy(stack = listOf(card))) shouldContain
            InvariantViolation.EntityInMultipleLocations(card, ZoneKey(state.turnOrder.first(), Zone.LIBRARY).toString(), "stack")
    }

    test("checker accepts legal controls and rejects corrupted structural routing") {
        data class Control(
            val name: String,
            val legal: () -> com.wingedsheep.engine.state.GameState,
            val corrupted: () -> com.wingedsheep.engine.state.GameState,
            val expected: (com.wingedsheep.engine.state.GameState) -> InvariantViolation,
        )

        fun legalStackState(): com.wingedsheep.engine.state.GameState {
            val state = validState()
            val caster = state.turnOrder.first()
            val libraryKey = ZoneKey(caster, Zone.LIBRARY)
            val card = state.getZone(libraryKey).first()
            return state.updateEntity(card) { it.with(SpellOnStackComponent(caster)) }.copy(
                zones = state.zones + (libraryKey to state.getZone(libraryKey).filterNot { it == card }),
                stack = listOf(card),
            )
        }

        val controls = listOf(
            Control(
                name = "stack object has a stack component",
                legal = ::legalStackState,
                corrupted = {
                    val state = validState()
                    val card = state.getZone(ZoneKey(state.turnOrder.first(), Zone.LIBRARY)).first()
                    state.copy(
                        zones = state.zones + (ZoneKey(state.turnOrder.first(), Zone.LIBRARY) to
                            state.getZone(ZoneKey(state.turnOrder.first(), Zone.LIBRARY)).filterNot { it == card }),
                        stack = listOf(card),
                    )
                },
                expected = { it.stack.single().let(InvariantViolation::StackEntityWithoutStackComponent) },
            ),
            Control(
                name = "turn order has no duplicate players",
                legal = ::validState,
                corrupted = {
                    val state = validState()
                    state.copy(turnOrder = state.turnOrder + state.turnOrder.first())
                },
                expected = { InvariantViolation.DuplicateTurnOrderPlayer },
            ),
            Control(
                name = "turn order contains only player entities",
                legal = ::validState,
                corrupted = {
                    val state = validState()
                    val card = state.getZone(ZoneKey(state.turnOrder.first(), Zone.LIBRARY)).first()
                    state.copy(turnOrder = state.turnOrder + card)
                },
                expected = { it.turnOrder.last().let(InvariantViolation::TurnOrderEntityIsNotPlayer) },
            ),
            Control(
                name = "active player is active",
                legal = ::validState,
                corrupted = { validState().copy(activePlayerId = EntityId("unknown-active")) },
                expected = { InvariantViolation.UnknownActivePlayer(EntityId("unknown-active")) },
            ),
            Control(
                name = "pending decision belongs to an active player",
                legal = {
                    val state = validState()
                    state.withPendingDecision(
                        YesNoDecision("legal-decision", state.turnOrder.first(), "Legal", DecisionContext())
                    )
                },
                corrupted = {
                    validState().withPendingDecision(
                        YesNoDecision("corrupt-decision", EntityId("unknown-decision"), "Corrupt", DecisionContext())
                    )
                },
                expected = { InvariantViolation.UnknownDecisionPlayer(EntityId("unknown-decision")) },
            ),
        )

        controls.forEach { control ->
            checker.check(control.legal()) shouldBe emptyList()
            val corrupted = control.corrupted()
            checker.check(corrupted) shouldContain control.expected(corrupted)
        }
    }

    test("checker rejects priority and pending input owned by a player who has left") {
        val state = validState()
        val departed = state.turnOrder.last()
        val corrupted = state.updateEntity(departed) { it.with(PlayerLostComponent(LossReason.CONCESSION)) }.copy(
            priorityPlayerId = departed,
            priorityPassedBy = setOf(departed),
        )

        val violations = checker.check(corrupted)
        violations shouldContain InvariantViolation.UnknownPriorityPlayer(departed)
        violations shouldContain InvariantViolation.UnknownPriorityPasser(departed)
    }

    test("observer accepts rejected actions only when they are side-effect free") {
        val state = validState()
        val observer = InvariantCheckingActionObserver(checker)
        val processor = ActionProcessor(
            EngineServices(CardRegistry().apply { register(TestCards.all) }),
            computeUndo = false,
            observer = observer,
        )

        val rejected = processor.process(state, PassPriority(EntityId("unknown"))).result
        rejected.error shouldBe "Unknown player: unknown"
        rejected.state shouldBe state
        rejected.events shouldHaveSize 0

        data class RejectedActionControl(
            val name: String,
            val rejected: ExecutionResult,
            val expected: InvariantViolation,
        )
        listOf(
            RejectedActionControl(
                "state changes",
                ExecutionResult.error(state.copy(turnNumber = state.turnNumber + 1), "synthetic rejection"),
                InvariantViolation.RejectedActionChangedState,
            ),
            RejectedActionControl(
                "events are emitted",
                ExecutionResult.error(state, "synthetic rejection").copy(
                    events = listOf(LifeChangedEvent(state.turnOrder.first(), 20, 19, LifeChangeReason.LIFE_LOSS))
                ),
                InvariantViolation.RejectedActionEmittedEvents,
            ),
        ).forEach { control ->
            val exception = shouldThrow<InvariantViolationException> {
                observer.afterProcess(state, PassPriority(state.turnOrder.first()), control.rejected)
            }
            exception.violations shouldContain control.expected
        }
    }

    test("a seeded four-seat Commander priority trace stays valid and deterministic") {
        val commander = CardDefinition.creature(
            name = "Invariant Pod Commander",
            manaCost = ManaCost.parse("{2}{G}"),
            supertypes = setOf(Supertype.LEGENDARY),
            subtypes = setOf(Subtype("Elf")),
            power = 3,
            toughness = 3,
        )
        val bear = CardDefinition.creature(
            name = "Invariant Pod Bear",
            manaCost = ManaCost.parse("{1}{G}"),
            subtypes = emptySet(),
            power = 2,
            toughness = 2,
        )
        val registry = CardRegistry().apply { register(listOf(commander, bear)) }
        val config = GameConfig(
            players = List(4) { seat ->
                PlayerConfig(
                    name = "Player ${seat + 1}",
                    deck = Deck(cards = List(99) { bear.name } + commander.name),
                    commanderCardName = commander.name,
                )
            },
            format = Format.Commander(),
            skipMulligans = true,
            seed = 4242L,
        )

        fun trace(): List<com.wingedsheep.engine.state.GameState> {
            var state = GameInitializer(registry).initializeGame(config).state
            val processor = ActionProcessor(
                EngineServices(registry),
                computeUndo = false,
                observer = InvariantCheckingActionObserver(checker),
            )
            return buildList {
                // Stop before cleanup's discard prompt: decision IDs are transport handles
                // minted independently of the seeded game RNG, while this smoke trace is
                // deliberately a deterministic priority/turn-transition trace.
                repeat(31) {
                    val priorityPlayer = state.priorityPlayerId ?: error("priority unexpectedly absent")
                    val result = processor.process(state, PassPriority(priorityPlayer)).result
                    result.error shouldBe null
                    state = result.state
                    add(state)
                }
            }
        }

        trace() shouldBe trace()
    }
})

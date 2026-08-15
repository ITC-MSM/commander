package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CrewVehicle
import com.wingedsheep.engine.core.GameEvent
import com.wingedsheep.engine.core.TappedEvent
import com.wingedsheep.engine.core.engineSerializersModule
import com.wingedsheep.engine.state.components.battlefield.HasBecomeTappedComponent
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlinx.serialization.json.Json

/**
 * The **per-permanent first-time-tapped** primitive: `TappedEvent.firstThisTurn`, backed by
 * [HasBecomeTappedComponent] and matched by [EventPattern.TapEvent.firstTimeEachTurn].
 *
 * Named for the mechanic rather than for a card because it is engine vocabulary — Captain America,
 * Living Legend (MSH #210) is its first reader and his own behaviour lives in
 * `CaptainAmericaLivingLegendScenarioTest`. What these tests pin is the *axis*.
 *
 * The claim that matters is that the window is per-**permanent**, not per-**ability**. `oncePerTurn`
 * already existed and looks like the same thing; it isn't, and the two are run head-to-head below on
 * the same two taps — `firstTimeEachTurn` fires twice, `oncePerTurn` once.
 *
 * Enumerating the tapping paths is cheap here because nearly all of them are one: `TapEventEnforcementTest`
 * bans open-coded `with(TappedComponent)` outside its enters-tapped/cleanup allowlist, so tap
 * transitions go through the `tap()` atom, which is where the flag is computed. The path tests below
 * (attack declaration, an `Effects.Tap` effect, crew, a teamwork additional cost, a spell's mana
 * payment) exercise a representative caller of each shape rather than a list the feature depends on
 * being complete.
 *
 * Two qualifications on that argument, both deliberate:
 * - **Regeneration is a genuine exception**, not an enters-tapped one. `ZoneMovementUtils`
 *   open-codes regeneration's tap (CR 701.19a — "its controller taps it") while sitting on the
 *   allowlist, so a creature regenerated while untapped is never stamped and its next tap that turn
 *   still reports `firstThisTurn = true`. Routing regeneration through `tap()` is the fix and is its
 *   own change (it also restores the "becomes tapped" trigger regeneration silently skips today).
 * - **The guard is a text scan with two holes**: its regex matches `.with(TappedComponent)` /
 *   `.without<TappedComponent>()` but not `components.add(TappedComponent)`, and it scans only
 *   `rules-engine/src/main/kotlin`, so `game-server`'s scenario builder is invisible to it. Every
 *   current hit of both kinds is a legitimate enters-tapped site.
 *
 * Trigger firing is measured as a **library** delta, not a hand delta: the payoff is "draw a card",
 * casting the trigger's cause also moves cards out of hand, and the turn-boundary case crosses a draw
 * step. Only the library moves for exactly one reason.
 */
class FirstTimeTappedThisTurnScenarioTest : ScenarioTestBase() {

    private fun List<GameEvent>.tapsOf(entityId: EntityId): List<TappedEvent> =
        filterIsInstance<TappedEvent>().filter { it.entityId == entityId }

    /** "Tap target creature." The repeatable tap handle for the same-turn re-tap cases. */
    private val tapPulse = card("Tap Pulse") {
        manaCost = "{1}"
        typeLine = "Instant"
        oracleText = "Tap target creature."
        spell {
            val t = target("target", TargetCreature(filter = TargetFilter.Creature))
            effect = Effects.Tap(t)
        }
    }

    /** "Untap target creature." Lets a creature be tapped twice in one turn. */
    private val untapPulse = card("Untap Pulse") {
        manaCost = "{1}"
        typeLine = "Instant"
        oracleText = "Untap target creature."
        spell {
            val t = target("target", TargetCreature(filter = TargetFilter.Creature))
            effect = Effects.Untap(t)
        }
    }

    /**
     * The reader under test: "Whenever a creature you control becomes tapped for the first time this
     * turn, draw a card." Per-permanent — it fires once for *each* creature's first tap.
     *
     * Deliberately ungated by `Conditions.IsYourTurn` so these tests measure the window and nothing
     * else; Captain America's "during your turn" rider is his own test's business.
     */
    private val firstTapLedger = card("First Tap Ledger") {
        manaCost = "{1}"
        typeLine = "Enchantment"
        oracleText = "Whenever a creature you control becomes tapped for the first time this turn, " +
            "draw a card."
        triggeredAbility {
            trigger = Triggers.becomesTapped(
                binding = TriggerBinding.ANY,
                filter = GameObjectFilter.Creature.youControl(),
                firstTimeEachTurn = true,
            )
            effect = Effects.DrawCards(1)
        }
    }

    /**
     * The near-miss it must not collapse into: the same observer capped with `oncePerTurn`, which is
     * a limit on the *ability* (Interface Ace's printed "This ability triggers only once each turn").
     */
    private val oncePerTurnLedger = card("Once Per Turn Ledger") {
        manaCost = "{1}"
        typeLine = "Enchantment"
        oracleText = "Whenever a creature you control becomes tapped, draw a card. " +
            "This ability triggers only once each turn."
        triggeredAbility {
            trigger = Triggers.becomesTapped(
                binding = TriggerBinding.ANY,
                filter = GameObjectFilter.Creature.youControl(),
            )
            oncePerTurn = true
            effect = Effects.DrawCards(1)
        }
    }

    /**
     * The batch match site (`TriggerDetector.detectTapBatchTriggers`) is separate from the per-event
     * one, so the window has to narrow there too: "Whenever one or more creatures you control become
     * tapped for the first time this turn, draw a card."
     */
    private val firstTapRollCall = card("First Tap Roll Call") {
        manaCost = "{1}"
        typeLine = "Enchantment"
        oracleText = "Whenever one or more creatures you control become tapped for the first time " +
            "this turn, draw a card."
        triggeredAbility {
            trigger = Triggers.OneOrMoreBecomeTapped(
                GameObjectFilter.Creature.youControl(),
                firstTimeEachTurn = true,
            )
            effect = Effects.DrawCards(1)
        }
    }

    init {
        cardRegistry.register(tapPulse)
        cardRegistry.register(untapPulse)
        cardRegistry.register(firstTapLedger)
        cardRegistry.register(oncePerTurnLedger)
        cardRegistry.register(firstTapRollCall)

        /** Two creatures, two Tap Pulses, plenty of mana and library — the shared head-to-head board. */
        fun twoCreatureBoard(ledgerName: String) = scenario()
            .withPlayers("Player", "Opponent")
            .withCardOnBattlefield(1, ledgerName)
            .withCardOnBattlefield(1, "Grizzly Bears")
            .withCardOnBattlefield(1, "Craw Wurm")
            .withCardsInHand(1, "Tap Pulse", 2)
            .withLandsOnBattlefield(1, "Plains", 6)
            .withCardInLibrary(1, "Island")
            .withCardInLibrary(1, "Island")
            .withCardInLibrary(1, "Island")
            .withCardInLibrary(1, "Island")
            .withActivePlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            .build()

        context("first time each turn a permanent becomes tapped") {

            test("two different creatures each tapping once both fire the per-permanent window") {
                val game = twoCreatureBoard("First Tap Ledger")
                val bears = game.findPermanent("Grizzly Bears").shouldNotBeNull()
                val wurm = game.findPermanent("Craw Wurm").shouldNotBeNull()
                val libraryBefore = game.librarySize(1)

                game.castSpell(1, "Tap Pulse", targetId = bears).error shouldBe null
                game.resolveStack()
                game.castSpell(1, "Tap Pulse", targetId = wurm).error shouldBe null
                game.resolveStack()

                withClue("both creatures really are tapped") {
                    game.state.getEntity(bears)?.has<TappedComponent>() shouldBe true
                    game.state.getEntity(wurm)?.has<TappedComponent>() shouldBe true
                }
                withClue("each creature's own first tap fires the ability — two draws") {
                    game.librarySize(1) shouldBe libraryBefore - 2
                }
            }

            test("oncePerTurn on the same observer answers only the first creature") {
                val game = twoCreatureBoard("Once Per Turn Ledger")
                val bears = game.findPermanent("Grizzly Bears").shouldNotBeNull()
                val wurm = game.findPermanent("Craw Wurm").shouldNotBeNull()
                val libraryBefore = game.librarySize(1)

                game.castSpell(1, "Tap Pulse", targetId = bears).error shouldBe null
                game.resolveStack()
                game.castSpell(1, "Tap Pulse", targetId = wurm).error shouldBe null
                game.resolveStack()

                game.state.getEntity(wurm)?.has<TappedComponent>() shouldBe true
                withClue(
                    "the per-ability cap fires once for the turn no matter how many creatures " +
                        "took their first tap — which is why it cannot express this card"
                ) {
                    game.librarySize(1) shouldBe libraryBefore - 1
                }
            }

            test("a creature tapped, untapped and tapped again in the same turn fires only once") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "First Tap Ledger")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardsInHand(1, "Tap Pulse", 2)
                    .withCardInHand(1, "Untap Pulse")
                    .withLandsOnBattlefield(1, "Plains", 8)
                    .withCardInLibrary(1, "Island")
                    .withCardInLibrary(1, "Island")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears").shouldNotBeNull()
                val libraryBefore = game.librarySize(1)

                game.castSpell(1, "Tap Pulse", targetId = bears).error shouldBe null
                game.resolveStack()
                withClue("the first tap drew a card") {
                    game.librarySize(1) shouldBe libraryBefore - 1
                }

                game.castSpell(1, "Untap Pulse", targetId = bears).error shouldBe null
                game.resolveStack()
                game.state.getEntity(bears)?.has<TappedComponent>() shouldBe false

                game.castSpell(1, "Tap Pulse", targetId = bears).error shouldBe null
                val retapEvents = game.resolveStack().flatMap { it.events }
                withClue("the second tap really happened — it is not that nothing was tapped") {
                    retapEvents.tapsOf(bears).single().firstThisTurn shouldBe false
                    game.state.getEntity(bears)?.has<TappedComponent>() shouldBe true
                }
                withClue("but it was not the first tap this turn, so no second draw") {
                    game.librarySize(1) shouldBe libraryBefore - 1
                }
            }

            test("the window reopens on the controller's next turn") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "First Tap Ledger")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardsInHand(1, "Tap Pulse", 2)
                    .withLandsOnBattlefield(1, "Plains", 8)
                    .withCardInLibrary(1, "Island")
                    .withCardInLibrary(1, "Island")
                    .withCardInLibrary(1, "Island")
                    .withCardInLibrary(1, "Island")
                    .withCardInLibrary(1, "Island")
                    .withCardInLibrary(1, "Island")
                    .withCardInLibrary(2, "Mountain")
                    .withCardInLibrary(2, "Mountain")
                    .withCardInLibrary(2, "Mountain")
                    .withCardInLibrary(2, "Mountain")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears").shouldNotBeNull()

                game.castSpell(1, "Tap Pulse", targetId = bears).error shouldBe null
                game.resolveStack()
                val firstTurnStamp = game.state.getEntity(bears)
                    ?.get<HasBecomeTappedComponent>().shouldNotBeNull()
                firstTurnStamp.lastBecameTappedTurn shouldBe game.state.turnNumber

                // Roll around to this player's next turn — through this turn's postcombat main, the
                // opponent's whole turn, and back. (`passUntilPhase` returns immediately when the
                // game is already in the target step, so leaving the current step is what actually
                // moves the game on.) The Bears untap in P1's own untap step, and an untap is not a
                // tap, so it leaves no stamp of its own.
                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)
                game.passUntilPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)
                game.passUntilPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                withClue("a later turn, same Bears, untapped again") {
                    (game.state.turnNumber > firstTurnStamp.lastBecameTappedTurn) shouldBe true
                    game.state.getEntity(bears)?.has<TappedComponent>() shouldBe false
                }

                val libraryBefore = game.librarySize(1)
                game.castSpell(1, "Tap Pulse", targetId = bears).error shouldBe null
                val events = game.resolveStack().flatMap { it.events }
                withClue("a stamp from an earlier turn does not close this turn's window") {
                    events.tapsOf(bears).single().firstThisTurn shouldBe true
                    game.librarySize(1) shouldBe libraryBefore - 1
                }
            }

            test("a creature that ENTERED tapped has not become tapped, so its first real tap fires") {
                // CR 701.26a — only untapped permanents can be tapped, so entering tapped is not a
                // transition and emits no TappedEvent. It must also not consume the turn's window.
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "First Tap Ledger")
                    .withCardOnBattlefield(1, "Grizzly Bears", tapped = true)
                    .withCardInHand(1, "Tap Pulse")
                    .withCardInHand(1, "Untap Pulse")
                    .withLandsOnBattlefield(1, "Plains", 6)
                    .withCardInLibrary(1, "Island")
                    .withCardInLibrary(1, "Island")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears").shouldNotBeNull()
                withClue("entering tapped records no became-tapped stamp") {
                    game.state.getEntity(bears)?.get<HasBecomeTappedComponent>().shouldBeNull()
                }

                game.castSpell(1, "Untap Pulse", targetId = bears).error shouldBe null
                game.resolveStack()

                val libraryBefore = game.librarySize(1)
                game.castSpell(1, "Tap Pulse", targetId = bears).error shouldBe null
                val events = game.resolveStack().flatMap { it.events }
                withClue("this is the first time it has *become* tapped this turn") {
                    events.tapsOf(bears).single().firstThisTurn shouldBe true
                    game.librarySize(1) shouldBe libraryBefore - 1
                }
            }

            test("a permanent that changes zones and comes back gets a fresh window (CR 400.7)") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "First Tap Ledger")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardsInHand(1, "Tap Pulse", 2)
                    .withCardInHand(1, "Unsummon")
                    .withLandsOnBattlefield(1, "Island", 4)
                    .withLandsOnBattlefield(1, "Forest", 4)
                    .withCardInLibrary(1, "Island")
                    .withCardInLibrary(1, "Island")
                    .withCardInLibrary(1, "Island")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears").shouldNotBeNull()
                val libraryBefore = game.librarySize(1)

                game.castSpell(1, "Tap Pulse", targetId = bears).error shouldBe null
                game.resolveStack()
                game.state.getEntity(bears)
                    ?.get<HasBecomeTappedComponent>().shouldNotBeNull()
                withClue("the first tap of the turn fired the ledger") {
                    game.librarySize(1) shouldBe libraryBefore - 1
                }

                game.castSpell(1, "Unsummon", targetId = bears).error shouldBe null
                game.resolveStack()

                withClue("the object that left the battlefield carries no became-tapped memory") {
                    game.isInHand(1, "Grizzly Bears") shouldBe true
                    game.state.getEntity(bears)
                        ?.get<HasBecomeTappedComponent>().shouldBeNull()
                }

                // The behavioural half: replay it in the same turn. What comes back is a new object
                // with no memory of the tap it took minutes ago, so its window is open again — the
                // thing CR 400.7 actually buys, as opposed to the component merely being absent.
                game.castSpell(1, "Grizzly Bears").error shouldBe null
                game.resolveStack()
                val replayed = game.findPermanent("Grizzly Bears").shouldNotBeNull()

                game.castSpell(1, "Tap Pulse", targetId = replayed).error shouldBe null
                val events = game.resolveStack().flatMap { it.events }
                withClue("the returning object's first tap fires the ledger a second time") {
                    events.tapsOf(replayed).single().firstThisTurn shouldBe true
                    game.librarySize(1) shouldBe libraryBefore - 2
                }
            }

            // ---- the tapping paths -------------------------------------------------------------
            // Tap transitions funnel through the `tap()` atom (guarded by TapEventEnforcementTest,
            // regeneration excepted — see the class KDoc), so these prove representative callers of
            // each shape carry the flag rather than enumerating a list the feature depends on.

            test("declaring an attacker is a first tap") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Craw Wurm")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val wurm = game.findPermanent("Craw Wurm").shouldNotBeNull()
                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)

                val result = game.declareAttackers(mapOf("Craw Wurm" to 2))
                result.error shouldBe null
                result.events.tapsOf(wurm).single().firstThisTurn shouldBe true
            }

            test("an Effects.Tap effect is a first tap") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Craw Wurm")
                    .withCardInHand(1, "Tap Pulse")
                    .withLandsOnBattlefield(1, "Plains", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val wurm = game.findPermanent("Craw Wurm").shouldNotBeNull()
                game.castSpell(1, "Tap Pulse", targetId = wurm).error shouldBe null
                val events = game.resolveStack().flatMap { it.events }
                events.tapsOf(wurm).single().firstThisTurn shouldBe true
            }

            test("crewing a Vehicle is a first tap") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Craw Wurm")
                    .withCardOnBattlefield(1, "Careening Mine Cart")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val wurm = game.findPermanent("Craw Wurm").shouldNotBeNull()
                val cart = game.findPermanent("Careening Mine Cart").shouldNotBeNull()

                val result = game.execute(CrewVehicle(game.player1Id, cart, listOf(wurm)))
                result.error shouldBe null
                result.events.tapsOf(wurm).single().firstThisTurn shouldBe true
            }

            test("paying a teamwork additional cost is a first tap") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Craw Wurm")
                    .withCardInHand(1, "Repulsor Blast")
                    .withLandsOnBattlefield(1, "Mountain", 4)
                    .withCardOnBattlefield(2, "Wall of Swords")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val wurm = game.findPermanent("Craw Wurm").shouldNotBeNull()
                val wall = game.findPermanent("Wall of Swords").shouldNotBeNull()

                val result = game.castSpellWithTeamwork(1, "Repulsor Blast", "Craw Wurm", targetId = wall)
                result.error shouldBe null
                result.events.tapsOf(wurm).single().firstThisTurn shouldBe true
                withClue("the lands tapped for the same cast are first taps too") {
                    result.events.filterIsInstance<TappedEvent>()
                        .filter { it.entityName == "Mountain" }
                        .map { it.firstThisTurn }.toSet() shouldBe setOf(true)
                }
            }

            test("a creature tapped to pay a spell's mana cost is a first tap") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Llanowar Elves", summoningSickness = false)
                    .withCardOnBattlefield(1, "Craw Wurm")
                    .withCardInHand(1, "Tap Pulse")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val elves = game.findPermanent("Llanowar Elves").shouldNotBeNull()
                val wurm = game.findPermanent("Craw Wurm").shouldNotBeNull()

                val result = game.castSpell(1, "Tap Pulse", targetId = wurm)
                result.error shouldBe null
                withClue("the Elves were the only mana source, so the payment tapped them") {
                    game.state.getEntity(elves)?.has<TappedComponent>() shouldBe true
                    result.events.tapsOf(elves).single().firstThisTurn shouldBe true
                }
            }

            // ---- the batch match site ----------------------------------------------------------

            test("a batch trigger fires once when several creatures take their first tap together") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "First Tap Roll Call")
                    .withCardOnBattlefield(1, "Craw Wurm")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Island")
                    .withCardInLibrary(1, "Island")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val libraryBefore = game.librarySize(1)
                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Craw Wurm" to 2, "Grizzly Bears" to 2)).error shouldBe null
                game.resolveStack()

                // "One or more … become tapped" batches because the trigger event is itself plural,
                // so one simultaneous tap batch is one occurrence of it. (Not CR 603.2c, this
                // codebase's usual shorthand: that rule is about one event containing *multiple*
                // occurrences, which is the per-permanent `becomesTapped` reading instead.)
                withClue("two first taps in one batch is one trigger") {
                    game.librarySize(1) shouldBe libraryBefore - 1
                }
            }

            test("a batch of taps that are all re-taps does not fire the batch trigger") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "First Tap Roll Call")
                    .withCardOnBattlefield(1, "Craw Wurm")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardsInHand(1, "Tap Pulse", 2)
                    .withCardsInHand(1, "Untap Pulse", 2)
                    .withLandsOnBattlefield(1, "Plains", 8)
                    .withCardInLibrary(1, "Island")
                    .withCardInLibrary(1, "Island")
                    .withCardInLibrary(1, "Island")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val wurm = game.findPermanent("Craw Wurm").shouldNotBeNull()
                val bears = game.findPermanent("Grizzly Bears").shouldNotBeNull()

                // Burn both creatures' windows one at a time, then untap them.
                game.castSpell(1, "Tap Pulse", targetId = wurm).error shouldBe null
                game.resolveStack()
                game.castSpell(1, "Tap Pulse", targetId = bears).error shouldBe null
                game.resolveStack()
                game.castSpell(1, "Untap Pulse", targetId = wurm).error shouldBe null
                game.resolveStack()
                game.castSpell(1, "Untap Pulse", targetId = bears).error shouldBe null
                game.resolveStack()

                val libraryBefore = game.librarySize(1)
                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                val result = game.declareAttackers(mapOf("Craw Wurm" to 2, "Grizzly Bears" to 2))
                result.error shouldBe null
                withClue("both really were tapped again, and both taps report not-first") {
                    result.events.tapsOf(wurm).single().firstThisTurn shouldBe false
                    result.events.tapsOf(bears).single().firstThisTurn shouldBe false
                }
                game.resolveStack()
                withClue("a batch holding only re-taps narrows to nothing, so no draw") {
                    game.librarySize(1) shouldBe libraryBefore
                }
            }

            // ---- the data types ---------------------------------------------------------------

            test("the window is opt-in and renders in the pattern description") {
                EventPattern.TapEvent().firstTimeEachTurn shouldBe false
                EventPattern.TapEvent().description shouldBe "a permanent becomes tapped"
                EventPattern.TapEvent(firstTimeEachTurn = true).description shouldContain
                    "for the first time each turn"
            }

            test("firstThisTurn round-trips through serialization and defaults when absent") {
                val json = Json { serializersModule = engineSerializersModule }

                val retap: GameEvent = TappedEvent(
                    entityId = EntityId.of("e1"),
                    entityName = "Grizzly Bears",
                    tappedById = EntityId.of("player-1"),
                    firstThisTurn = false,
                )
                json.decodeFromString<GameEvent>(json.encodeToString(retap)) shouldBe retap

                withClue("an event encoded before the field existed must still decode") {
                    val legacy = """{"type":"TappedEvent","entityId":"e1","entityName":"Grizzly Bears"}"""
                    (json.decodeFromString<GameEvent>(legacy) as TappedEvent).firstThisTurn shouldBe true
                }
            }
        }
    }
}

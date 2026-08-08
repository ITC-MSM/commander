package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CrewVehicle
import com.wingedsheep.engine.core.GameEvent
import com.wingedsheep.engine.core.TappedEvent
import com.wingedsheep.engine.core.engineSerializersModule
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.TapReason
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlinx.serialization.json.Json

/**
 * The **tap-reason** primitive: [TapReason] on [TappedEvent], matched by
 * [EventPattern.TapEvent.reason].
 *
 * Named for the mechanic rather than for a card because it is engine vocabulary — Agent Maria Hill
 * (MSH #2) is its first reader, and her own behaviour lives in `AgentMariaHillScenarioTest`. What
 * these tests pin is the *axis*: which tap sites name a cause, which deliberately don't, and that
 * asking for no cause still matches every tap.
 *
 * The claim that matters is the negative one. Teamwork, crew, attacking and a mana payment are all
 * taps performed by the permanent's own controller, so `TappedEvent.tappedById` is identical across
 * them; if the reason were not carried, or were guessed at, a "becomes tapped to pay a teamwork
 * cost" trigger would fire on all four. Only teamwork is classified today (CR 702.194a), and each
 * unclassified site is asserted to report [TapReason.UNSPECIFIED] rather than anything else.
 */
class TapReasonScenarioTest : ScenarioTestBase() {

    private fun List<GameEvent>.tapsOf(entityId: EntityId): List<TappedEvent> =
        filterIsInstance<TappedEvent>().filter { it.entityId == entityId }

    init {
        context("tap reason") {

            test("a creature tapped to pay a teamwork cost carries TapReason.TEAMWORK") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
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

                val tap = result.events.tapsOf(wurm).single()
                tap.reason shouldBe TapReason.TEAMWORK
                withClue("attribution is a separate axis and stays the controller's") {
                    tap.tappedById shouldBe game.player1Id
                }

                withClue("the mana payment for the same cast taps lands, and a land tap names no cause") {
                    val landTaps = result.events.filterIsInstance<TappedEvent>()
                        .filter { it.entityName == "Mountain" }
                    landTaps.shouldNotBeEmpty()
                    landTaps.map { it.reason }.toSet() shouldBe setOf(TapReason.UNSPECIFIED)
                }
            }

            test("the same spell cast without teamwork taps nothing and names no cause") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Craw Wurm")
                    .withCardInHand(1, "Repulsor Blast")
                    .withLandsOnBattlefield(1, "Mountain", 4)
                    .withCardOnBattlefield(2, "Wall of Swords")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val wurm = game.findPermanent("Craw Wurm").shouldNotBeNull()
                val wall = game.findPermanent("Wall of Swords").shouldNotBeNull()

                val result = game.castSpell(1, "Repulsor Blast", targetId = wall)
                result.error shouldBe null

                result.events.tapsOf(wurm).shouldBeEmpty()
                game.state.getEntity(wurm)?.has<TappedComponent>() shouldBe false
                withClue("no teamwork was declared, so no tap anywhere in the cast may claim it") {
                    result.events.filterIsInstance<TappedEvent>()
                        .none { it.reason == TapReason.TEAMWORK } shouldBe true
                }
            }

            test("declaring an attacker names no cause") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Craw Wurm")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val wurm = game.findPermanent("Craw Wurm").shouldNotBeNull()
                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)

                val result = game.declareAttackers(mapOf("Craw Wurm" to 2))
                result.error shouldBe null

                val tap = result.events.tapsOf(wurm).single()
                withClue("the attacker tap is performed by the same player as a teamwork tap, so " +
                    "only the cause separates them — and this cause is not classified") {
                    tap.reason shouldBe TapReason.UNSPECIFIED
                    tap.tappedById shouldBe game.player1Id
                }
            }

            test("crewing a Vehicle names no cause") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Craw Wurm")
                    .withCardOnBattlefield(1, "Careening Mine Cart")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val wurm = game.findPermanent("Craw Wurm").shouldNotBeNull()
                val cart = game.findPermanent("Careening Mine Cart").shouldNotBeNull()

                val result = game.execute(CrewVehicle(game.player1Id, cart, listOf(wurm)))
                result.error shouldBe null

                withClue("crew is the same 'tap creatures with total power N or more' selection as " +
                    "teamwork, on the ability rail — the nearest miss there is") {
                    result.events.tapsOf(wurm).single().reason shouldBe TapReason.UNSPECIFIED
                }
            }

            test("a cause-agnostic 'becomes tapped' trigger still fires on a teamwork tap") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    // "Whenever this creature becomes tapped during your turn, untap it" — no cause
                    // named, so it must keep matching every tap, teamwork included. At power 0 it
                    // contributes nothing to the threshold; the Wurm carries it.
                    .withCardOnBattlefield(1, "Interface Ace")
                    .withCardOnBattlefield(1, "Craw Wurm")
                    .withCardInHand(1, "Repulsor Blast")
                    .withLandsOnBattlefield(1, "Mountain", 4)
                    .withCardOnBattlefield(2, "Wall of Swords")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val ace = game.findPermanent("Interface Ace").shouldNotBeNull()
                val wall = game.findPermanent("Wall of Swords").shouldNotBeNull()

                val result = game.castSpellWithTeamwork(
                    1, "Repulsor Blast", "Interface Ace", "Craw Wurm", targetId = wall,
                )
                result.error shouldBe null
                result.events.tapsOf(ace).single().reason shouldBe TapReason.TEAMWORK
                game.state.getEntity(ace)?.has<TappedComponent>() shouldBe true

                game.resolveStack()

                withClue("its untap trigger saw the teamwork tap, because a null reason on the " +
                    "pattern asks for no particular cause") {
                    game.state.getEntity(ace)?.has<TappedComponent>() shouldBe false
                }
            }

            test("the pattern renders the cause in its description and defaults to cause-agnostic") {
                EventPattern.TapEvent().reason shouldBe null
                EventPattern.TapEvent().description shouldBe "a permanent becomes tapped"
                EventPattern.TapEvent(reason = TapReason.TEAMWORK).description shouldContain
                    "to pay a teamwork cost"
            }

            test("the reason round-trips through serialization and defaults when absent") {
                val json = Json { serializersModule = engineSerializersModule }

                val teamworkTap: GameEvent = TappedEvent(
                    entityId = EntityId.of("e1"),
                    entityName = "Agent Maria Hill",
                    tappedById = EntityId.of("player-1"),
                    reason = TapReason.TEAMWORK,
                )
                val decoded = json.decodeFromString<GameEvent>(json.encodeToString(teamworkTap))
                decoded shouldBe teamworkTap

                withClue("an event encoded before the field existed must still decode") {
                    val legacy = """{"type":"TappedEvent","entityId":"e1","entityName":"Agent Maria Hill"}"""
                    val legacyTap = json.decodeFromString<GameEvent>(legacy) as TappedEvent
                    legacyTap.reason shouldBe TapReason.UNSPECIFIED
                }
            }
        }
    }
}

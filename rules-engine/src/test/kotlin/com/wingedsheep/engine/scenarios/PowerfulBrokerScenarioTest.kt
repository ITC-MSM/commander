package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.msh.cards.KidLoki
import com.wingedsheep.mtg.sets.definitions.msh.cards.PowerfulBroker
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldContain as shouldContainElement
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

/**
 * Scenario tests for Powerful Broker (Marvel Super Heroes #179).
 *
 * {2}{G} · Creature — Human Villain · 3/3
 *   {T}: For each kind of counter on target permanent or player, give that permanent or player
 *   another counter of that kind. Activate only as a sorcery.
 *
 * Targeted proliferate: the same "one more of each kind already there" placement as CR 701.34a,
 * but aimed at exactly one *target* — announced with the ability, so it is respondable and must
 * still be legal on resolution. These cover both halves of the "permanent or player" requirement,
 * the sorcery-speed restriction, and that the placement is attributed to the activating player —
 * the controller of an ability is who carries out its instructions — pinned behaviorally through
 * Kid Loki's counter-history hexproof grant.
 *
 * The primitive's own edge cases (no counters, fizzle, untargeted proliferate unchanged) live in
 * `TargetedProliferateTest`.
 */
class PowerfulBrokerScenarioTest : ScenarioTestBase() {

    private val brokerAbilityId = PowerfulBroker.activatedAbilities.first().id

    private fun counters(game: TestGame, id: EntityId, type: CounterType): Int =
        game.state.getEntity(id)?.get<CountersComponent>()?.getCount(type) ?: 0

    private fun seedCounters(game: TestGame, id: EntityId, vararg seeds: Pair<CounterType, Int>) {
        game.state = game.state.updateEntity(id) { c ->
            var component = c.get<CountersComponent>() ?: CountersComponent()
            seeds.forEach { (type, amount) -> component = component.withAdded(type, amount) }
            c.with(component)
        }
    }

    private fun activate(game: TestGame, target: ChosenTarget) =
        game.execute(
            ActivateAbility(
                playerId = game.player1Id,
                sourceId = game.findPermanent("Powerful Broker")!!,
                abilityId = brokerAbilityId,
                targets = listOf(target),
            )
        )

    init {
        cardRegistry.register(PowerfulBroker)
        cardRegistry.register(KidLoki)

        context("Powerful Broker") {

            test("target creature gets one more counter of every kind it already has") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Powerful Broker")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val broker = game.findPermanent("Powerful Broker")!!
                seedCounters(
                    game, bears,
                    CounterType.PLUS_ONE_PLUS_ONE to 2,
                    CounterType.STUN to 1,
                )

                val activation = activate(game, ChosenTarget.Permanent(bears))
                withClue("activating the ability should succeed: ${activation.error}") {
                    activation.error shouldBe null
                }
                game.resolveStack()

                withClue("{T} in the cost taps the Broker") {
                    game.state.getEntity(broker)?.has<TappedComponent>() shouldBe true
                }
                withClue("one more +1/+1, not double") {
                    counters(game, bears, CounterType.PLUS_ONE_PLUS_ONE) shouldBe 3
                }
                withClue("every kind, including a stun counter") {
                    counters(game, bears, CounterType.STUN) shouldBe 2
                }
            }

            test("it can target an opposing player, adding a poison counter") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Powerful Broker")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                seedCounters(game, game.player2Id, CounterType.POISON to 4)

                val activation = activate(game, ChosenTarget.Player(game.player2Id))
                withClue("'target permanent or player' includes players: ${activation.error}") {
                    activation.error shouldBe null
                }
                game.resolveStack()

                counters(game, game.player2Id, CounterType.POISON) shouldBe 5
            }

            test("it can target a land — the wording is 'permanent', not 'creature'") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Powerful Broker")
                    .withLandsOnBattlefield(2, "Island", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val island = game.findPermanent("Island")!!
                seedCounters(game, island, CounterType.CHARGE to 1)

                val activation = activate(game, ChosenTarget.Permanent(island))
                withClue("an opponent's land is a legal target permanent: ${activation.error}") {
                    activation.error shouldBe null
                }
                game.resolveStack()

                counters(game, island, CounterType.CHARGE) shouldBe 2
            }

            test("'Activate only as a sorcery' — it can't be activated on the opponent's turn") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Powerful Broker")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withActivePlayer(2)
                    .withPriorityPlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                seedCounters(game, bears, CounterType.PLUS_ONE_PLUS_ONE to 1)

                val activation = activate(game, ChosenTarget.Permanent(bears))
                withClue("rejected for timing, not for some unrelated reason: ${activation.error}") {
                    activation.error.shouldNotBeNull() shouldContain "sorcery"
                }
                withClue("and nothing happened") {
                    counters(game, bears, CounterType.PLUS_ONE_PLUS_ONE) shouldBe 1
                }
            }

            test("a summoning-sick Broker can't pay the {T} cost") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Powerful Broker", summoningSickness = true)
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                seedCounters(game, bears, CounterType.PLUS_ONE_PLUS_ONE to 1)

                val activation = activate(game, ChosenTarget.Permanent(bears))
                withClue("rejected for the {T} cost, not for targeting: ${activation.error}") {
                    activation.error.shouldNotBeNull() shouldContain "summoning sickness"
                }
                counters(game, bears, CounterType.PLUS_ONE_PLUS_ONE) shouldBe 1
            }

            test("the enumerated activation offers a permanent and a player as targets") {
                // Everything else in this file hands the engine an explicit ChosenTarget, which
                // never touches TargetEnumerationUtils. The server is authoritative for legal
                // actions and the client renders only what it enumerates, so without this the
                // card could be entirely unselectable in the real UI with every test green.
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Powerful Broker")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withActivePlayer(1)
                    .withPriorityPlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val broker = game.findPermanent("Powerful Broker")!!
                seedCounters(game, bears, CounterType.PLUS_ONE_PLUS_ONE to 1)

                val activation = game.getLegalActions(1)
                    .firstOrNull { (it.action as? ActivateAbility)?.sourceId == broker }
                withClue("the ability has to be offered at all") {
                    activation.shouldNotBeNull()
                }

                val validTargets = activation!!.validTargets.shouldNotBeNull()
                withClue("the permanent half of 'target permanent or player'") {
                    validTargets shouldContainElement bears
                }
                withClue("the player half — an opponent is selectable") {
                    validTargets shouldContainElement game.player2Id
                }
                withClue("you are a legal target too; nothing restricts it to opponents") {
                    validTargets shouldContainElement game.player1Id
                }
                withClue("a permanent with no counters is still a legal target") {
                    validTargets shouldContainElement broker
                }
            }

            test("the counter it places counts as placed by you (Kid Loki grants hexproof)") {
                // Kid Loki: "Each creature you control that you've put one or more +1/+1 counters
                // on this turn has hexproof." Seeding a counter directly doesn't record a
                // placement, so the grant only appears once the Broker actually places one.
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Powerful Broker")
                    .withCardOnBattlefield(1, "Kid Loki")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                seedCounters(game, bears, CounterType.PLUS_ONE_PLUS_ONE to 1)

                withClue("no counter has been *placed* on it yet") {
                    game.state.projectedState.hasKeyword(bears, Keyword.HEXPROOF) shouldBe false
                }

                activate(game, ChosenTarget.Permanent(bears)).error shouldBe null
                game.resolveStack()

                withClue("the proliferated +1/+1 counter is a placement by its controller") {
                    counters(game, bears, CounterType.PLUS_ONE_PLUS_ONE) shouldBe 2
                    game.state.projectedState.hasKeyword(bears, Keyword.HEXPROOF) shouldBe true
                }
            }
        }
    }
}

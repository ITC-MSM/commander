package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CountersAddedEvent
import com.wingedsheep.engine.core.SpellFizzledEvent
import com.wingedsheep.engine.handlers.PredicateContext
import com.wingedsheep.engine.handlers.PredicateEvaluator
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.CantReceiveCounters
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPermanentOrPlayer
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

/**
 * Engine tests for [com.wingedsheep.sdk.scripting.effects.ProliferateEffect]'s two shapes —
 * the untargeted keyword action (CR 701.34a) and the **targeted** single-object form the
 * `target` field selects (Powerful Broker).
 *
 * The two differ only in how the recipients are picked: untargeted proliferate chooses them when
 * the effect resolves (a decision, no targeting), the targeted form has them chosen on
 * announcement (CR 601.2c), locked in, and re-checked on resolution (CR 608.2b). The counters
 * placed are the same in both cases — one of each kind already there — which is why both go
 * through the same placement helper.
 *
 * Test cards are defined inline so these pin the primitive itself, not any one printed card.
 */
class TargetedProliferateTest : ScenarioTestBase() {

    // "For each kind of counter on target permanent or player, give that permanent or player
    // another counter of that kind." — the targeted form under test.
    private val brokerage = card("Test Brokerage") {
        manaCost = "{1}"
        typeLine = "Sorcery"
        oracleText = "For each kind of counter on target permanent or player, give that permanent " +
            "or player another counter of that kind."
        spell {
            val recipient = target("target permanent or player", Targets.PermanentOrPlayer)
            effect = Effects.Proliferate(recipient)
        }
    }

    // Plain proliferate — the untargeted shape, kept here so the shared placement helper is
    // exercised from both entry points in one file.
    private val spreading = card("Test Spreading") {
        manaCost = "{1}"
        typeLine = "Sorcery"
        oracleText = "Proliferate."
        spell {
            effect = Effects.Proliferate()
        }
    }

    // "Target artifact or player" — the narrowed permanent half, which is the whole reason the
    // requirement is parameterized by a filter rather than being a second one-off type.
    private val artifactBrokerage = card("Test Artifact Brokerage") {
        manaCost = "{1}"
        typeLine = "Sorcery"
        oracleText = "For each kind of counter on target artifact or player, give it another " +
            "counter of that kind."
        spell {
            val recipient = target(
                "target artifact or player",
                TargetPermanentOrPlayer(permanentFilter = TargetFilter.Artifact)
            )
            effect = Effects.Proliferate(recipient)
        }
    }

    // Two *independent* targets, so CR 608.2b resolves the spell when only one of them has gone
    // illegal — the case where single-target fizzling stops protecting the executor.
    private val doubleBrokerage = card("Test Double Brokerage") {
        manaCost = "{1}"
        typeLine = "Sorcery"
        oracleText = "For each kind of counter on each of two target permanents or players, give " +
            "it another counter of that kind."
        spell {
            val first = target("first recipient", Targets.PermanentOrPlayer)
            val second = target("second recipient", Targets.PermanentOrPlayer)
            effect = Effects.Composite(
                Effects.Proliferate(first),
                Effects.Proliferate(second),
            )
        }
    }

    // "This permanent can't have counters put on it" — Blossombind's static, self-scoped.
    private val warded = card("Test Warded Idol") {
        manaCost = "{2}"
        typeLine = "Artifact Creature — Construct"
        power = 1
        toughness = 1
        oracleText = "Test Warded Idol can't have counters put on it."
        staticAbility {
            ability = CantReceiveCounters(GroupFilter.source())
        }
    }

    // Proliferates itself through a *non-target* EffectTarget — nothing rechecks it on resolution.
    private val selfBrokerage = card("Test Self Brokerage") {
        manaCost = "{2}"
        typeLine = "Creature — Human"
        power = 1
        toughness = 1
        oracleText = "{T}: For each kind of counter on this creature, give it another counter of " +
            "that kind."
        activatedAbility {
            cost = Costs.Tap
            effect = Effects.Proliferate(EffectTarget.Self)
        }
    }

    private val shielded = card("Test Shielded Sentry") {
        manaCost = "{2}"
        typeLine = "Creature — Soldier"
        power = 2
        toughness = 2
        oracleText = "Hexproof"
        keywords(Keyword.HEXPROOF)
    }

    private val predicateEvaluator = PredicateEvaluator()

    private fun counters(game: TestGame, id: EntityId, type: CounterType): Int =
        game.state.getEntity(id)?.get<CountersComponent>()?.getCount(type) ?: 0

    private fun seedCounters(game: TestGame, id: EntityId, vararg counters: Pair<CounterType, Int>) {
        game.state = game.state.updateEntity(id) { c ->
            var component = c.get<CountersComponent>() ?: CountersComponent()
            counters.forEach { (type, amount) -> component = component.withAdded(type, amount) }
            c.with(component)
        }
    }

    private fun castBrokerageAt(game: TestGame, target: ChosenTarget) =
        game.execute(
            CastSpell(
                playerId = game.player1Id,
                cardId = game.findCardsInHand(1, "Test Brokerage").first(),
                targets = listOf(target),
            )
        )

    private fun castAt(game: TestGame, cardName: String, vararg targets: ChosenTarget) =
        game.execute(
            CastSpell(
                playerId = game.player1Id,
                cardId = game.findCardsInHand(1, cardName).first(),
                targets = targets.toList(),
            )
        )

    init {
        cardRegistry.register(brokerage)
        cardRegistry.register(spreading)
        cardRegistry.register(artifactBrokerage)
        cardRegistry.register(doubleBrokerage)
        cardRegistry.register(warded)
        cardRegistry.register(selfBrokerage)
        cardRegistry.register(shielded)

        context("targeted proliferate") {

            test("a target with no counters is legal and simply receives nothing") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInHand(1, "Test Brokerage")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!

                val cast = castBrokerageAt(game, ChosenTarget.Permanent(bears))
                withClue("a counterless permanent is still a legal target: ${cast.error}") {
                    cast.error shouldBe null
                }
                val results = game.resolveStack()

                withClue("nothing to copy, so no counter is placed") {
                    val total = game.state.getEntity(bears)?.get<CountersComponent>()
                        ?.counters?.values?.sum() ?: 0
                    total shouldBe 0
                }
                withClue("and no placement event is emitted") {
                    results.flatMap { it.events }.filterIsInstance<CountersAddedEvent>() shouldBe emptyList()
                }
                withClue("the targeted form never pauses for a resolution-time choice") {
                    game.hasPendingDecision() shouldBe false
                }
            }

            test("every kind on the target gets exactly one more, and only one more") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInHand(1, "Test Brokerage")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                seedCounters(
                    game, bears,
                    CounterType.PLUS_ONE_PLUS_ONE to 2,
                    CounterType.STUN to 1,
                    CounterType.SHIELD to 3,
                )

                castBrokerageAt(game, ChosenTarget.Permanent(bears)).error shouldBe null
                game.resolveStack()

                withClue("+1/+1 goes 2 -> 3, not doubled") {
                    counters(game, bears, CounterType.PLUS_ONE_PLUS_ONE) shouldBe 3
                }
                withClue("stun goes 1 -> 2") {
                    counters(game, bears, CounterType.STUN) shouldBe 2
                }
                withClue("shield goes 3 -> 4 — every kind, whatever it is") {
                    counters(game, bears, CounterType.SHIELD) shouldBe 4
                }
            }

            test("a noncreature permanent is a legal target — 'permanent', not 'creature'") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Test Brokerage")
                    .withLandsOnBattlefield(1, "Forest", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val forest = game.findPermanent("Forest")!!
                seedCounters(game, forest, CounterType.CHARGE to 3)

                val cast = castBrokerageAt(game, ChosenTarget.Permanent(forest))
                withClue("a land with charge counters is a legal 'target permanent': ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                counters(game, forest, CounterType.CHARGE) shouldBe 4
            }

            test("a player is a legal target and gets one more of each kind they have") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Test Brokerage")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                seedCounters(game, game.player2Id, CounterType.POISON to 2)

                val cast = castBrokerageAt(game, ChosenTarget.Player(game.player2Id))
                withClue("'or player' half of the requirement: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                counters(game, game.player2Id, CounterType.POISON) shouldBe 3
            }

            test("a target that becomes illegal before resolution fizzles the spell (CR 608.2b)") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withCardOnBattlefield(1, "Savannah Lions")
                    .withCardInHand(1, "Test Brokerage")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val lions = game.findPermanent("Savannah Lions")!!
                seedCounters(game, bears, CounterType.PLUS_ONE_PLUS_ONE to 1)
                seedCounters(game, lions, CounterType.PLUS_ONE_PLUS_ONE to 1)

                castBrokerageAt(game, ChosenTarget.Permanent(bears)).error shouldBe null

                // The only target leaves the battlefield while the spell is on the stack.
                game.state = game.state
                    .removeFromZone(ZoneKey(game.player2Id, Zone.BATTLEFIELD), bears)
                    .addToZone(ZoneKey(game.player2Id, Zone.GRAVEYARD), bears)

                val results = game.resolveStack()

                withClue("its only target is gone, so the spell is countered on resolution") {
                    results.flatMap { it.events }.any { it is SpellFizzledEvent } shouldBe true
                }
                withClue("a countered spell places no counters at all") {
                    results.flatMap { it.events }.filterIsInstance<CountersAddedEvent>() shouldBe emptyList()
                }
                withClue("and it certainly doesn't fall back to some other permanent") {
                    counters(game, lions, CounterType.PLUS_ONE_PLUS_ONE) shouldBe 1
                }
            }

            test("the placement is recorded for the counter-history predicates (kind + placer)") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInHand(1, "Test Brokerage")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                seedCounters(game, bears, CounterType.PLUS_ONE_PLUS_ONE to 1)

                // Seeding the component directly bypasses the recorder, so the history starts empty.
                withClue("nothing has been *placed* yet — only seeded") {
                    matchesHistory(game, bears) shouldBe false
                }

                castBrokerageAt(game, ChosenTarget.Permanent(bears)).error shouldBe null
                game.resolveStack()

                // No rule number here on purpose: CR 122.6a is about entering the battlefield
                // *with* counters, which isn't this. The principle is just that the ability's
                // controller is the one who carries out its instructions.
                withClue("proliferating a +1/+1 counter is a placement by its controller") {
                    matchesHistory(game, bears) shouldBe true
                }
                withClue("recorded under the counter's own kind, not a different one") {
                    matchesHistory(game, bears, counterType = Counters.STUN) shouldBe false
                }
            }
        }

        context("legality of the new requirement") {

            test("an opponent's hexproof permanent can't be chosen at announcement") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(2, "Test Shielded Sentry")
                    .withCardInHand(1, "Test Brokerage")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val sentry = game.findPermanent("Test Shielded Sentry")!!
                seedCounters(game, sentry, CounterType.PLUS_ONE_PLUS_ONE to 1)

                val cast = castBrokerageAt(game, ChosenTarget.Permanent(sentry))
                withClue("the permanent half gets the ordinary targeting checks") {
                    cast.error.shouldNotBeNull() shouldContain "hexproof"
                }
                withClue("and the rejected spell places nothing") {
                    counters(game, sentry, CounterType.PLUS_ONE_PLUS_ONE) shouldBe 1
                }
            }

            test("your own hexproof permanent is still a legal target") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Test Shielded Sentry")
                    .withCardInHand(1, "Test Brokerage")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val sentry = game.findPermanent("Test Shielded Sentry")!!
                seedCounters(game, sentry, CounterType.PLUS_ONE_PLUS_ONE to 1)

                // Hexproof only stops *opponents* — the previous test must be failing on
                // hexproof, not on "the requirement rejects every permanent".
                val cast = castBrokerageAt(game, ChosenTarget.Permanent(sentry))
                withClue("hexproof doesn't stop its own controller: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                counters(game, sentry, CounterType.PLUS_ONE_PLUS_ONE) shouldBe 2
            }

            test("permanentFilter narrows the permanent half but leaves players alone") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Test Warded Idol")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardsInHand(1, "Test Artifact Brokerage", 3)
                    .withLandsOnBattlefield(1, "Forest", 6)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val idol = game.findPermanent("Test Warded Idol")!!
                val bears = game.findPermanent("Grizzly Bears")!!
                seedCounters(game, bears, CounterType.PLUS_ONE_PLUS_ONE to 1)
                seedCounters(game, game.player2Id, CounterType.POISON to 1)

                withClue("a creature is not an artifact, so 'target artifact or player' rejects it") {
                    castAt(game, "Test Artifact Brokerage", ChosenTarget.Permanent(bears))
                        .error.shouldNotBeNull() shouldContain "artifact"
                }
                withClue("the artifact half accepts an artifact") {
                    castAt(game, "Test Artifact Brokerage", ChosenTarget.Permanent(idol))
                        .error shouldBe null
                }
                game.resolveStack()

                withClue("narrowing the permanents must not narrow the players") {
                    castAt(game, "Test Artifact Brokerage", ChosenTarget.Player(game.player2Id))
                        .error shouldBe null
                }
                game.resolveStack()
                counters(game, game.player2Id, CounterType.POISON) shouldBe 2
            }
        }

        context("prohibitions and zones") {

            test("a permanent that can't have counters put on it gets none (targeted)") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Test Warded Idol")
                    .withCardInHand(1, "Test Brokerage")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val idol = game.findPermanent("Test Warded Idol")!!
                // Seeded directly, so the counters are already there despite the prohibition —
                // exactly the state proliferate would otherwise happily add to.
                seedCounters(game, idol, CounterType.PLUS_ONE_PLUS_ONE to 1)

                withClue("it is still a legal target — the prohibition is on placement, not targeting") {
                    castBrokerageAt(game, ChosenTarget.Permanent(idol)).error shouldBe null
                }
                val results = game.resolveStack()

                withClue("no counter is added") {
                    counters(game, idol, CounterType.PLUS_ONE_PLUS_ONE) shouldBe 1
                }
                withClue("and nothing is announced as added") {
                    results.flatMap { it.events }.filterIsInstance<CountersAddedEvent>() shouldBe emptyList()
                }
            }

            test("the same prohibition applies to untargeted proliferate") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Test Warded Idol")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInHand(1, "Test Spreading")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val idol = game.findPermanent("Test Warded Idol")!!
                val bears = game.findPermanent("Grizzly Bears")!!
                seedCounters(game, idol, CounterType.PLUS_ONE_PLUS_ONE to 1)
                seedCounters(game, bears, CounterType.PLUS_ONE_PLUS_ONE to 1)

                game.castSpell(1, "Test Spreading").error shouldBe null
                game.resolveStack()
                game.selectCards(listOf(idol, bears)).error shouldBe null

                withClue("choosing it is allowed; placing on it is not") {
                    counters(game, idol, CounterType.PLUS_ONE_PLUS_ONE) shouldBe 1
                }
                withClue("the other chosen permanent is unaffected by the skip") {
                    counters(game, bears, CounterType.PLUS_ONE_PLUS_ONE) shouldBe 2
                }
            }

            test("a non-target recipient that left the battlefield gets nothing") {
                // EffectTarget.Self is not a *target*, so nothing rechecks it — StackResolver's
                // CR 608.2b pass only drops declared targets. The ability outlives its source
                // (CR 608.2), and the source keeps its CountersComponent in the graveyard, so
                // only the executor's own zone check stops the counters landing there.
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Test Self Brokerage")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val source = game.findPermanent("Test Self Brokerage")!!
                seedCounters(game, source, CounterType.PLUS_ONE_PLUS_ONE to 1)

                game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = source,
                        abilityId = selfBrokerage.activatedAbilities.first().id,
                    )
                ).error shouldBe null

                // It dies with its ability still on the stack.
                game.state = game.state
                    .removeFromZone(ZoneKey(game.player1Id, Zone.BATTLEFIELD), source)
                    .addToZone(ZoneKey(game.player1Id, Zone.GRAVEYARD), source)

                val results = game.resolveStack()

                withClue("a graveyard card is not a permanent and can't be proliferated") {
                    counters(game, source, CounterType.PLUS_ONE_PLUS_ONE) shouldBe 1
                }
                withClue("and nothing is announced as added") {
                    results.flatMap { it.events }.filterIsInstance<CountersAddedEvent>() shouldBe emptyList()
                }
            }

            test("with a second legal target the spell resolves, but the illegal one gets nothing") {
                // CR 608.2b: only *all* targets being illegal counters the spell. With one
                // survivor the effect still runs and the illegal target is merely "not affected"
                // — here because StackResolver drops it, so the BoundVariable resolves to null.
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withCardOnBattlefield(1, "Savannah Lions")
                    .withCardInHand(1, "Test Double Brokerage")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val lions = game.findPermanent("Savannah Lions")!!
                seedCounters(game, bears, CounterType.PLUS_ONE_PLUS_ONE to 1)
                seedCounters(game, lions, CounterType.PLUS_ONE_PLUS_ONE to 1)

                castAt(
                    game,
                    "Test Double Brokerage",
                    ChosenTarget.Permanent(bears),
                    ChosenTarget.Permanent(lions),
                ).error shouldBe null

                game.state = game.state
                    .removeFromZone(ZoneKey(game.player2Id, Zone.BATTLEFIELD), bears)
                    .addToZone(ZoneKey(game.player2Id, Zone.GRAVEYARD), bears)

                val results = game.resolveStack()

                withClue("one legal target left, so the spell is not countered") {
                    results.flatMap { it.events }.any { it is SpellFizzledEvent } shouldBe false
                }
                withClue("the surviving target is proliferated normally") {
                    counters(game, lions, CounterType.PLUS_ONE_PLUS_ONE) shouldBe 2
                }
                withClue("the graveyard object keeps the counters it had and gains none") {
                    counters(game, bears, CounterType.PLUS_ONE_PLUS_ONE) shouldBe 1
                }
                withClue("and no event claims otherwise") {
                    results.flatMap { it.events }.filterIsInstance<CountersAddedEvent>()
                        .none { it.entityId == bears } shouldBe true
                }
            }
        }

        context("untargeted proliferate is unchanged") {

            test("it still pauses for a choose-any-number decision and adds one of each kind") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardOnBattlefield(2, "Savannah Lions")
                    .withCardInHand(1, "Test Spreading")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val lions = game.findPermanent("Savannah Lions")!!
                seedCounters(game, bears, CounterType.PLUS_ONE_PLUS_ONE to 1, CounterType.STUN to 1)
                seedCounters(game, lions, CounterType.PLUS_ONE_PLUS_ONE to 1)

                game.castSpell(1, "Test Spreading").error shouldBe null
                game.resolveStack()

                withClue("proliferate resolves through a selection decision") {
                    game.hasPendingDecision() shouldBe true
                }
                game.selectCards(listOf(bears)).error shouldBe null

                withClue("the chosen entity gets one of each kind") {
                    counters(game, bears, CounterType.PLUS_ONE_PLUS_ONE) shouldBe 2
                    counters(game, bears, CounterType.STUN) shouldBe 2
                }
                withClue("an unchosen entity is untouched") {
                    counters(game, lions, CounterType.PLUS_ONE_PLUS_ONE) shouldBe 1
                }
            }
        }
    }

    /** Does [id] match "has had a counter of [counterType] put on it by its controller this turn"? */
    private fun matchesHistory(
        game: TestGame,
        id: EntityId,
        counterType: String = Counters.PLUS_ONE_PLUS_ONE
    ): Boolean = predicateEvaluator.matches(
        game.state,
        game.state.projectedState,
        id,
        GameObjectFilter.Creature.receivedCounterThisTurn(counterType, placedByController = true),
        PredicateContext(controllerId = game.player1Id),
    )
}

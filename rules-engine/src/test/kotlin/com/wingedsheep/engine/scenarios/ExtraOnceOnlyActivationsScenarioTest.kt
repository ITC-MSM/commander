package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.event.GrantedStaticAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.identity.FaceDownComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.ExtraOnceOnlyActivations
import com.wingedsheep.sdk.scripting.OnceOnlyAbilityKind
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Feature tests for [ExtraOnceOnlyActivations] — the permission that lifts the "Activate this
 * ability only once" limit the exhaust (CR 702.177) and power-up (CR 702.193) keywords install as
 * an [ActivationRestriction.Once].
 *
 * The type carries two axes and both are under test here:
 *  - **`kind`** picks which keyword is lifted. Exhaust and power-up desugar to the *same* `Once`,
 *    so without this axis a power-up permission would silently re-arm every exhaust ability on the
 *    board. Neither kind ever reaches a plain `Once` an ordinary ability printed for itself.
 *  - **`extraActivations`** picks *how much*: `null` waives the limit outright (Elvish Refueler's
 *    shape, already covered end-to-end by [ElvishRefuelerScenarioTest]), an integer raises it by
 *    that many and **sums** across the battlefield (Wonder Man's shape).
 *
 * Counted permissions are the new half, so they get the bulk of the coverage: the ceiling is a
 * ceiling and not a waiver, two sources stack, control matters, the permission is read at
 * activation time rather than banked, and a *prohibition* still beats it (CR 101.2) — Kang the
 * Conqueror's turn-scoped power-up lockout is checked ahead of the ability's own restrictions, so
 * a raised ceiling has nothing to raise.
 *
 * Every assertion is made twice, against the enumerator (`getLegalActions`) and against the
 * handler (`execute`), because those are two separate code paths that have drifted before.
 */
class ExtraOnceOnlyActivationsScenarioTest : ScenarioTestBase() {

    /** A cheap power-up body — the ability whose limit the permissions below raise. */
    private val powerUpBody = card("Stunt Double") {
        manaCost = "{1}{R}"
        typeLine = "Creature — Human Performer"
        power = 2
        toughness = 2
        oracleText = "Power-up — {R}: Put a +1/+1 counter on this creature."
        activatedAbility {
            isPowerUp = true
            cost = Costs.Mana("{R}")
            effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        }
    }

    /** An exhaust body, to prove the `kind` axis keeps the two keywords apart. */
    private val exhaustBody = card("Stunt Rigger") {
        manaCost = "{1}{R}"
        typeLine = "Creature — Human Artificer"
        power = 2
        toughness = 2
        oracleText = "Exhaust — {R}: Put a +1/+1 counter on this creature."
        activatedAbility {
            isExhaust = true
            cost = Costs.Mana("{R}")
            effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        }
    }

    /** Neither keyword, but printed "Activate only once" — no permission may reach it. */
    private val plainOnceBody = card("Stunt Coordinator") {
        manaCost = "{1}{R}"
        typeLine = "Creature — Human Advisor"
        power = 2
        toughness = 2
        oracleText = "{R}: Put a +1/+1 counter on this creature. Activate only once."
        activatedAbility {
            cost = Costs.Mana("{R}")
            restrictions = listOf(ActivationRestriction.Once)
            effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        }
    }

    /** Wonder Man's static in isolation: +1 power-up activation, no condition. */
    private val encoreStar = card("Encore Star") {
        manaCost = "{3}{R}"
        typeLine = "Creature — Human Performer"
        power = 3
        toughness = 3
        oracleText = "Each power-up ability of permanents you control can be activated an additional time."
        staticAbility {
            ability = ExtraOnceOnlyActivations(
                kind = OnceOnlyAbilityKind.POWER_UP,
                extraActivations = 1
            )
        }
    }

    /** A second, distinctly named copy of the same static, so two sources can be told apart. */
    private val understudyStar = card("Understudy Star") {
        manaCost = "{3}{R}"
        typeLine = "Creature — Human Performer"
        power = 3
        toughness = 3
        oracleText = "Each power-up ability of permanents you control can be activated an additional time."
        staticAbility {
            ability = ExtraOnceOnlyActivations(
                kind = OnceOnlyAbilityKind.POWER_UP,
                extraActivations = 1
            )
        }
    }

    /** The waive shape of the exhaust kind, to pin "unlimited beats a sum". */
    private val stageManager = card("Stage Manager") {
        manaCost = "{3}{R}"
        typeLine = "Creature — Human Advisor"
        power = 3
        toughness = 3
        oracleText = "You may activate exhaust abilities as though they haven't been activated."
        staticAbility {
            ability = ExtraOnceOnlyActivations(
                kind = OnceOnlyAbilityKind.EXHAUST,
                extraActivations = null
            )
        }
    }

    /** The other `kind`, counted rather than waived — it must not touch power-up abilities. */
    private val riggingCrew = card("Rigging Crew") {
        manaCost = "{3}{R}"
        typeLine = "Creature — Human Artificer"
        power = 3
        toughness = 3
        oracleText = "Each exhaust ability of permanents you control can be activated an additional time."
        staticAbility {
            ability = ExtraOnceOnlyActivations(
                kind = OnceOnlyAbilityKind.EXHAUST,
                extraActivations = 1
            )
        }
    }

    /** Kang's rider in isolation: an extra turn during which power-up abilities can't be activated. */
    private val temporalWarlord = card("Chrono Villain") {
        manaCost = "{2}{U}{U}"
        typeLine = "Creature — Human Villain"
        power = 3
        toughness = 3
        oracleText = "Power-up — {U}: Take an extra turn after this one. During that turn, " +
            "power-up abilities can't be activated."
        activatedAbility {
            isPowerUp = true
            cost = Costs.Mana("{U}")
            effect = Effects.TakeExtraTurn(powerUpAbilitiesCantBeActivated = true)
        }
    }

    /** {0} bounce, so a permission source can be removed mid-turn. */
    private val bounce = card("Cut The Scene") {
        manaCost = "{0}"
        typeLine = "Sorcery"
        oracleText = "Return target creature to its owner's hand."
        spell {
            val t = target("target creature", Targets.Creature)
            effect = Effects.ReturnToHand(t)
        }
    }

    private val powerUpAbilityId
        get() = cardRegistry.getCard("Stunt Double")!!.script.activatedAbilities[0].id
    private val exhaustAbilityId
        get() = cardRegistry.getCard("Stunt Rigger")!!.script.activatedAbilities[0].id
    private val plainOnceAbilityId
        get() = cardRegistry.getCard("Stunt Coordinator")!!.script.activatedAbilities[0].id
    private val warlordAbilityId
        get() = cardRegistry.getCard("Chrono Villain")!!.script.activatedAbilities[0].id

    /** The offered activation of one specific ability, or null when the enumerator withholds it. */
    private fun TestGame.actionFor(playerNumber: Int, abilityId: AbilityId) =
        getLegalActions(playerNumber).firstOrNull {
            it.action.let { a -> a is ActivateAbility && a.abilityId == abilityId }
        }

    /** Activate [abilityId] of [sourceId] for [playerNumber] and resolve it; returns the error, if any. */
    private fun TestGame.activate(playerNumber: Int, sourceId: EntityId, abilityId: AbilityId): String? {
        val playerId = if (playerNumber == 1) player1Id else player2Id
        val error = execute(ActivateAbility(playerId, sourceId, abilityId)).error
        if (error != null) return error
        if (getPendingDecision() is SelectManaSourcesDecision) submitManaSourcesAutoPay()
        resolveStack()
        return null
    }

    /** Cast [name] for [playerNumber], auto-paying mana, and resolve it; returns the error, if any. */
    private fun TestGame.castAndResolve(playerNumber: Int, name: String, targetId: EntityId? = null): String? {
        val error = castSpell(playerNumber, name, targetId).error
        if (error != null) return error
        if (getPendingDecision() is SelectManaSourcesDecision) submitManaSourcesAutoPay()
        resolveStack()
        return null
    }

    /** Advance play until it is player 1's turn again, [turns] turn boundaries later. */
    private fun TestGame.advanceTurns(turns: Int) {
        repeat(turns) {
            passUntilPhase(Phase.ENDING, Step.END)
            passUntilPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
        }
    }

    private fun TestGame.countersOn(entityId: EntityId): Int =
        state.getEntity(entityId)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    init {
        cardRegistry.register(powerUpBody)
        cardRegistry.register(exhaustBody)
        cardRegistry.register(plainOnceBody)
        cardRegistry.register(encoreStar)
        cardRegistry.register(understudyStar)
        cardRegistry.register(stageManager)
        cardRegistry.register(riggingCrew)
        cardRegistry.register(temporalWarlord)
        cardRegistry.register(bounce)

        /** Everything on the battlefield for player 1, with red mana for repeated {R} activations. */
        fun baseScenario() = scenario()
            .withPlayers("Player", "Opponent")
            .withCardOnBattlefield(1, "Stunt Double")
            .withLandsOnBattlefield(1, "Mountain", 8)
            .withCardInLibrary(1, "Mountain")
            .withCardInLibrary(1, "Mountain")
            .withCardInLibrary(2, "Mountain")
            .withCardInLibrary(2, "Mountain")
            .withActivePlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)

        context("ExtraOnceOnlyActivations — counted (raise the limit by N)") {

            test("without the permission, a power-up ability is spent after one activation") {
                val game = baseScenario().build()
                val bodyId = game.findPermanent("Stunt Double")!!

                game.activate(1, bodyId, powerUpAbilityId) shouldBe null
                game.countersOn(bodyId) shouldBe 1

                withClue("control: the plain once-only limit still applies with no permission out") {
                    game.actionFor(1, powerUpAbilityId) shouldBe null
                }
                game.activate(1, bodyId, powerUpAbilityId) shouldNotBe null
                game.countersOn(bodyId) shouldBe 1
            }

            test("one permission grants exactly one extra activation — a ceiling, not a waiver") {
                val game = baseScenario().withCardOnBattlefield(1, "Encore Star").build()
                val bodyId = game.findPermanent("Stunt Double")!!

                game.activate(1, bodyId, powerUpAbilityId) shouldBe null
                withClue("the enumerator must offer the ability again after the first activation") {
                    game.actionFor(1, powerUpAbilityId) shouldNotBe null
                }
                withClue("and the handler must accept it") {
                    game.activate(1, bodyId, powerUpAbilityId) shouldBe null
                }
                game.countersOn(bodyId) shouldBe 2

                withClue("two activations is the whole allowance: 1 printed + 1 extra") {
                    game.actionFor(1, powerUpAbilityId) shouldBe null
                }
                game.activate(1, bodyId, powerUpAbilityId) shouldNotBe null
                withClue("a third activation must not have resolved") { game.countersOn(bodyId) shouldBe 2 }
            }

            test("two permissions stack to three activations") {
                val game = baseScenario()
                    .withCardOnBattlefield(1, "Encore Star")
                    .withCardOnBattlefield(1, "Understudy Star")
                    .build()
                val bodyId = game.findPermanent("Stunt Double")!!

                repeat(3) { i ->
                    withClue("activation ${i + 1} of 3 must be legal") {
                        game.activate(1, bodyId, powerUpAbilityId) shouldBe null
                    }
                }
                game.countersOn(bodyId) shouldBe 3

                withClue("1 printed + 2 extra is the whole allowance") {
                    game.actionFor(1, powerUpAbilityId) shouldBe null
                }
                game.activate(1, bodyId, powerUpAbilityId) shouldNotBe null
                game.countersOn(bodyId) shouldBe 3
            }

            test("the permission reaches other permanents you control, not an opponent's") {
                val game = baseScenario()
                    .withCardOnBattlefield(1, "Encore Star")
                    .withCardOnBattlefield(2, "Stunt Double")
                    .withLandsOnBattlefield(2, "Mountain", 8)
                    .build()
                val permanents = game.findPermanents("Stunt Double")
                val mine = permanents.first { it in game.state.getBattlefield(game.player1Id) }
                val theirs = permanents.first { it in game.state.getBattlefield(game.player2Id) }

                game.activate(1, mine, powerUpAbilityId) shouldBe null
                withClue("'permanents you control' covers a body other than the permission's own source") {
                    game.activate(1, mine, powerUpAbilityId) shouldBe null
                }
                game.countersOn(mine) shouldBe 2

                game.passPriority()
                game.state.priorityPlayerId shouldBe game.player2Id
                withClue("control: the opponent's own unspent power-up is offered to them") {
                    game.actionFor(2, powerUpAbilityId) shouldNotBe null
                }
                game.activate(2, theirs, powerUpAbilityId) shouldBe null
                withClue("the opponent controls no permission, so their body is spent after one") {
                    game.activate(2, theirs, powerUpAbilityId) shouldNotBe null
                }
                game.countersOn(theirs) shouldBe 1
            }

            test("the allowance is read at activation time, not banked when the source leaves") {
                val game = baseScenario()
                    .withCardOnBattlefield(1, "Encore Star")
                    .withCardInHand(1, "Cut The Scene")
                    .build()
                val bodyId = game.findPermanent("Stunt Double")!!
                val starId = game.findPermanent("Encore Star")!!

                game.activate(1, bodyId, powerUpAbilityId) shouldBe null
                game.castSpell(1, "Cut The Scene", starId).error shouldBe null
                game.resolveStack()

                withClue("with the permission gone the ceiling is back to one activation") {
                    game.actionFor(1, powerUpAbilityId) shouldBe null
                }
                game.activate(1, bodyId, powerUpAbilityId) shouldNotBe null
                game.countersOn(bodyId) shouldBe 1
            }

            test("a spent allowance does not come back at end of turn") {
                val game = baseScenario().withCardOnBattlefield(1, "Encore Star").build()
                val bodyId = game.findPermanent("Stunt Double")!!

                game.activate(1, bodyId, powerUpAbilityId) shouldBe null
                game.activate(1, bodyId, powerUpAbilityId) shouldBe null
                game.countersOn(bodyId) shouldBe 2

                game.advanceTurns(2)
                withClue("the opponent's turn, then a fresh turn of the player's") {
                    game.state.activePlayerId shouldBe game.player1Id
                }

                withClue("Once is per object lifetime, not per turn — the raised ceiling is too") {
                    game.actionFor(1, powerUpAbilityId) shouldBe null
                }
                game.activate(1, bodyId, powerUpAbilityId) shouldNotBe null
                game.countersOn(bodyId) shouldBe 2
            }

            test("a body that leaves and returns is a new object with a fresh allowance (CR 400.7)") {
                val game = baseScenario()
                    .withCardOnBattlefield(1, "Encore Star")
                    .withCardInHand(1, "Cut The Scene")
                    .build()
                val bodyId = game.findPermanent("Stunt Double")!!

                game.activate(1, bodyId, powerUpAbilityId) shouldBe null
                game.activate(1, bodyId, powerUpAbilityId) shouldBe null
                game.countersOn(bodyId) shouldBe 2
                withClue("the allowance is spent on this object") {
                    game.actionFor(1, powerUpAbilityId) shouldBe null
                }

                game.castAndResolve(1, "Cut The Scene", bodyId) shouldBe null
                game.castAndResolve(1, "Stunt Double") shouldBe null
                // The engine reuses the card's EntityId across zones, so "new object" here is a
                // property of the state attached to it, not of the identifier — hence the
                // behavioural assertions below rather than an id comparison.
                val newBodyId = game.findPermanent("Stunt Double")!!
                withClue("the +1/+1 counters did not survive the trip to hand either") {
                    game.countersOn(newBodyId) shouldBe 0
                }

                withClue("a new object carries no once-only memory, so 1 printed + 1 extra again") {
                    game.actionFor(1, powerUpAbilityId) shouldNotBe null
                    game.activate(1, newBodyId, powerUpAbilityId) shouldBe null
                    game.activate(1, newBodyId, powerUpAbilityId) shouldBe null
                }
                withClue("counters are a fresh object's too") { game.countersOn(newBodyId) shouldBe 2 }
                withClue("and the fresh allowance is still a ceiling, not a waiver") {
                    game.actionFor(1, powerUpAbilityId) shouldBe null
                    game.activate(1, newBodyId, powerUpAbilityId) shouldNotBe null
                }
                game.countersOn(newBodyId) shouldBe 2
            }

            test("a permission that arrives after the ability was spent still re-arms it") {
                val game = baseScenario().withCardInHand(1, "Encore Star").build()
                val bodyId = game.findPermanent("Stunt Double")!!

                game.activate(1, bodyId, powerUpAbilityId) shouldBe null
                game.actionFor(1, powerUpAbilityId) shouldBe null

                game.castSpell(1, "Encore Star").error shouldBe null
                game.resolveStack()
                withClue("the permission is continuous, so it applies to memory already written") {
                    game.actionFor(1, powerUpAbilityId) shouldNotBe null
                }
                game.activate(1, bodyId, powerUpAbilityId) shouldBe null
                game.countersOn(bodyId) shouldBe 2
            }
        }

        context("ExtraOnceOnlyActivations — the kind axis") {

            test("a power-up permission does not re-arm an exhaust ability") {
                val game = baseScenario()
                    .withCardOnBattlefield(1, "Encore Star")
                    .withCardOnBattlefield(1, "Stunt Rigger")
                    .build()
                val riggerId = game.findPermanent("Stunt Rigger")!!

                game.activate(1, riggerId, exhaustAbilityId) shouldBe null
                withClue("exhaust and power-up share the same Once — only `kind` keeps them apart") {
                    game.actionFor(1, exhaustAbilityId) shouldBe null
                }
                game.activate(1, riggerId, exhaustAbilityId) shouldNotBe null
                game.countersOn(riggerId) shouldBe 1
            }

            test("an exhaust permission does not re-arm a power-up ability") {
                val game = baseScenario().withCardOnBattlefield(1, "Rigging Crew").build()
                val bodyId = game.findPermanent("Stunt Double")!!

                game.activate(1, bodyId, powerUpAbilityId) shouldBe null
                game.actionFor(1, powerUpAbilityId) shouldBe null
                game.activate(1, bodyId, powerUpAbilityId) shouldNotBe null
                game.countersOn(bodyId) shouldBe 1
            }

            test("an exhaust permission does re-arm an exhaust ability, once") {
                val game = baseScenario()
                    .withCardOnBattlefield(1, "Rigging Crew")
                    .withCardOnBattlefield(1, "Stunt Rigger")
                    .build()
                val riggerId = game.findPermanent("Stunt Rigger")!!

                game.activate(1, riggerId, exhaustAbilityId) shouldBe null
                withClue("the matching kind is lifted: 1 printed + 1 extra") {
                    game.activate(1, riggerId, exhaustAbilityId) shouldBe null
                }
                game.countersOn(riggerId) shouldBe 2
                game.activate(1, riggerId, exhaustAbilityId) shouldNotBe null
                game.countersOn(riggerId) shouldBe 2
            }

            test("a waiver of the same kind beats a counted one — unlimited, not a sum") {
                val game = baseScenario()
                    .withCardOnBattlefield(1, "Rigging Crew")
                    .withCardOnBattlefield(1, "Stage Manager")
                    .withCardOnBattlefield(1, "Stunt Rigger")
                    .build()
                val riggerId = game.findPermanent("Stunt Rigger")!!

                repeat(4) { i ->
                    withClue("activation ${i + 1} — a waiver short-circuits the sum, so there is no ceiling") {
                        game.activate(1, riggerId, exhaustAbilityId) shouldBe null
                    }
                }
                withClue("a counted 1 + a waiver is unlimited, not 1 printed + 1 extra") {
                    game.countersOn(riggerId) shouldBe 4
                    game.actionFor(1, exhaustAbilityId) shouldNotBe null
                }
            }

            test("no permission reaches a plain Once an ordinary ability printed for itself") {
                val game = baseScenario()
                    .withCardOnBattlefield(1, "Encore Star")
                    .withCardOnBattlefield(1, "Rigging Crew")
                    .withCardOnBattlefield(1, "Stunt Coordinator")
                    .build()
                val coordinatorId = game.findPermanent("Stunt Coordinator")!!

                game.activate(1, coordinatorId, plainOnceAbilityId) shouldBe null
                withClue("both permissions are out and neither may touch a keyword-less Once") {
                    game.actionFor(1, plainOnceAbilityId) shouldBe null
                }
                game.activate(1, coordinatorId, plainOnceAbilityId) shouldNotBe null
                game.countersOn(coordinatorId) shouldBe 1
            }
        }

        context("ExtraOnceOnlyActivations — a face-down source (CR 708.2a)") {

            // CR 708.2a: a face-down permanent is a 2/2 with no text, so it grants nothing its card
            // printed. Abilities *granted* to it at runtime are a separate continuous effect and do
            // still apply, which is why `extraActivationsFor` suppresses only the printed half. The
            // two halves are asserted separately — an implementation that skipped the whole
            // permanent would pass the first test and fail the second.

            test("a face-down permission source grants nothing its card printed") {
                val game = baseScenario().withCardOnBattlefield(1, "Encore Star").build()
                val bodyId = game.findPermanent("Stunt Double")!!
                val starId = game.findPermanent("Encore Star")!!

                game.state = game.state.updateEntity(starId) { it.with(FaceDownComponent) }

                game.activate(1, bodyId, powerUpAbilityId) shouldBe null
                withClue("the printed static is text the face-down permanent no longer has") {
                    game.actionFor(1, powerUpAbilityId) shouldBe null
                }
                game.activate(1, bodyId, powerUpAbilityId) shouldNotBe null
                withClue("only the printed activation resolved") { game.countersOn(bodyId) shouldBe 1 }
            }

            test("a permission granted to that same face-down permanent still applies") {
                val game = baseScenario().withCardOnBattlefield(1, "Encore Star").build()
                val bodyId = game.findPermanent("Stunt Double")!!
                val starId = game.findPermanent("Encore Star")!!

                game.state = game.state
                    .updateEntity(starId) { it.with(FaceDownComponent) }
                    .copy(
                        grantedStaticAbilities = listOf(
                            GrantedStaticAbility(
                                entityId = starId,
                                ability = ExtraOnceOnlyActivations(
                                    kind = OnceOnlyAbilityKind.POWER_UP,
                                    extraActivations = 1
                                ),
                                duration = Duration.Permanent
                            )
                        )
                    )

                game.activate(1, bodyId, powerUpAbilityId) shouldBe null
                withClue("a granted ability is not the card's text, so being face down doesn't strip it") {
                    game.actionFor(1, powerUpAbilityId) shouldNotBe null
                    game.activate(1, bodyId, powerUpAbilityId) shouldBe null
                }
                game.countersOn(bodyId) shouldBe 2

                withClue("and it is still a ceiling: 1 printed + 1 granted extra") {
                    game.actionFor(1, powerUpAbilityId) shouldBe null
                }
            }
        }

        context("ExtraOnceOnlyActivations vs. the power-up lockout (CR 101.2)") {

            test("a prohibition beats the permission: no power-up during a locked-out turn") {
                val game = baseScenario()
                    .withCardOnBattlefield(1, "Encore Star")
                    .withCardOnBattlefield(1, "Chrono Villain")
                    .withLandsOnBattlefield(1, "Island", 4)
                    .build()
                val bodyId = game.findPermanent("Stunt Double")!!
                val warlordId = game.findPermanent("Chrono Villain")!!

                // Spend the printed activation this turn, leaving the extra one unspent.
                game.activate(1, bodyId, powerUpAbilityId) shouldBe null
                game.countersOn(bodyId) shouldBe 1
                withClue("the permission has the ability re-armed right now") {
                    game.actionFor(1, powerUpAbilityId) shouldNotBe null
                }

                game.activate(1, warlordId, warlordAbilityId) shouldBe null
                game.passUntilPhase(Phase.ENDING, Step.END)
                game.passUntilPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                withClue("the opponent skipped, so the next turn to begin is the locked extra turn") {
                    game.state.activePlayerId shouldBe game.player1Id
                }

                withClue("the lockout is a hard gate ahead of the ability's own restrictions") {
                    game.actionFor(1, powerUpAbilityId) shouldBe null
                }
                val rejected = game.execute(ActivateAbility(game.player1Id, bodyId, powerUpAbilityId))
                withClue("and it must be the lockout that rejects it, not the spent Once") {
                    rejected.error shouldBe "Power-up abilities can't be activated this turn"
                }
                game.countersOn(bodyId) shouldBe 1
            }

            test("once the locked turn is over the unspent extra activation is still there") {
                val game = baseScenario()
                    .withCardOnBattlefield(1, "Encore Star")
                    .withCardOnBattlefield(1, "Chrono Villain")
                    .withLandsOnBattlefield(1, "Island", 4)
                    .build()
                val bodyId = game.findPermanent("Stunt Double")!!
                val warlordId = game.findPermanent("Chrono Villain")!!

                game.activate(1, bodyId, powerUpAbilityId) shouldBe null
                game.activate(1, warlordId, warlordAbilityId) shouldBe null

                repeat(3) {
                    game.passUntilPhase(Phase.ENDING, Step.END)
                    game.passUntilPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                }
                withClue("locked extra turn, opponent's turn, then a fresh turn of the player's") {
                    game.state.activePlayerId shouldBe game.player1Id
                }

                withClue("the lockout only ever bound one turn; the allowance was never consumed") {
                    game.actionFor(1, powerUpAbilityId) shouldNotBe null
                }
                game.activate(1, bodyId, powerUpAbilityId) shouldBe null
                game.countersOn(bodyId) shouldBe 2
            }
        }
    }
}

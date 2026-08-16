package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.effects.permanent.stats.SetBaseStatsExecutor
import com.wingedsheep.engine.handlers.effects.permanent.stats.contextScopedReferenceIn
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.effects.SetBaseStatsEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

/**
 * `SetBaseStatsEffect(reevaluateContinuously = true)` — the layer 7b base-P/T *set* whose
 * `DynamicAmount` is recomputed on every projection pass instead of snapshotted at resolution.
 *
 * This is what an effect that hands a creature a quoted static ability needs: Ms. Marvel, Kamala
 * Khan's "Until end of turn, this creature gains 'This creature's base power is equal to the number
 * of cards in your hand.'" Such a self-granted ability is **not** a characteristic-defining ability
 * (CR 604.3a fails on criterion 2, "printed on the card it affects", and criterion 4, "not an
 * ability that an object grants to itself"), so it applies in layer 7b — CR 613.4b, "effects that
 * refer to the base power and/or toughness of a creature apply in this layer" — and not in 7a.
 *
 * The matrix below pins the whole contract of the flag:
 *  - the value moves when the input moves, and the default (snapshot) does not;
 *  - +1/+1 counters and pump effects — both layer 7c, which CR 613.4c defines as "effects **and
 *    counters** that modify power and/or toughness" — apply *on top* of the re-evaluated base rather
 *    than being overwritten by it;
 *  - a `null` half leaves the other printed stat alone, in both the power-only and toughness-only
 *    directions;
 *  - the effect ends with its `Duration`;
 *  - it keeps applying, and keeps re-evaluating, after the permanent that granted it has left
 *    the battlefield (the projector falls back to the effect's captured controller);
 *  - two competing layer 7b sets resolve in timestamp order, in both directions;
 *  - and a `DynamicAmount` the projector could not evaluate is rejected at resolution rather than
 *    silently read as 0 on every pass.
 *
 * The test cards below all say "target creature **you control**" deliberately: under
 * `reevaluateContinuously = true` the projector rebuilds the context from the *source*, so "your
 * hand" inside the quoted clause is the granting player's hand, not the affected creature's
 * controller's. That is right for a self-grant and for a creature you control, and would be wrong
 * for a grant to an opponent's creature — see the contract on `SetBaseStatsEffect`.
 */
class SetBaseStatsContinuousScenarioTest : ScenarioTestBase() {

    private val continuousPower = card("Continuous Base Power Test") {
        manaCost = "{U}"
        typeLine = "Instant"
        oracleText = "Until end of turn, target creature you control gains \"This creature's base " +
            "power is equal to the number of cards in your hand.\""
        spell {
            val creature = target("creature", Targets.CreatureYouControl)
            effect = Effects.SetBasePower(
                creature,
                DynamicAmounts.cardsInYourHand(),
                Duration.EndOfTurn,
                reevaluateContinuously = true,
            )
        }
    }

    private val snapshotPower = card("Snapshot Base Power Test") {
        manaCost = "{U}"
        typeLine = "Instant"
        oracleText = "Target creature's base power becomes the number of cards in your hand " +
            "until end of turn."
        spell {
            val creature = target("creature", Targets.Creature)
            effect = Effects.SetBasePower(
                creature,
                DynamicAmounts.cardsInYourHand(),
                Duration.EndOfTurn,
            )
        }
    }

    private val continuousToughness = card("Continuous Base Toughness Test") {
        manaCost = "{U}"
        typeLine = "Instant"
        oracleText = "Until end of turn, target creature you control gains \"This creature's base " +
            "toughness is equal to the number of cards in your hand.\""
        spell {
            val creature = target("creature", Targets.CreatureYouControl)
            effect = Effects.SetBaseToughness(
                creature,
                DynamicAmounts.cardsInYourHand(),
                Duration.EndOfTurn,
                reevaluateContinuously = true,
            )
        }
    }

    private val drawTwo = card("Draw Two Test") {
        manaCost = "{U}"
        typeLine = "Instant"
        oracleText = "Draw two cards."
        spell {
            effect = Effects.DrawCards(2)
        }
    }

    private val addCounter = card("Add Counter Test") {
        manaCost = "{U}"
        typeLine = "Instant"
        oracleText = "Put a +1/+1 counter on target creature."
        spell {
            val creature = target("creature", Targets.Creature)
            effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, creature)
        }
    }

    private val pump = card("Pump Test") {
        manaCost = "{U}"
        typeLine = "Instant"
        oracleText = "Target creature gets +2/+2 until end of turn."
        spell {
            val creature = target("creature", Targets.Creature)
            effect = Effects.ModifyStats(2, 2, creature)
        }
    }

    /**
     * The granter for the "source has left the battlefield" case: it sacrifices itself as the
     * activation cost, so by the time the ability resolves the permanent that produced the
     * continuous effect is already in the graveyard.
     */
    private val embiggenEngine = card("Embiggen Engine Test") {
        manaCost = "{1}{U}"
        typeLine = "Creature — Construct"
        power = 1
        toughness = 1
        oracleText = "Sacrifice this creature: Until end of turn, target creature you control " +
            "gains \"This creature's base power is equal to the number of cards in your hand.\""
        activatedAbility {
            cost = Costs.SacrificeSelf
            val creature = target("creature", Targets.CreatureYouControl)
            effect = Effects.SetBasePower(
                creature,
                DynamicAmounts.cardsInYourHand(),
                Duration.EndOfTurn,
                reevaluateContinuously = true,
            )
            description = "Sacrifice this creature: Until end of turn, target creature you " +
                "control gains \"This creature's base power is equal to the number of cards in " +
                "your hand.\""
        }
    }

    init {
        cardRegistry.register(
            listOf(
                continuousPower,
                snapshotPower,
                continuousToughness,
                drawTwo,
                addCounter,
                pump,
                embiggenEngine,
            )
        )

        // Fixed board: a printed 2/2 to point the effects at, plenty of mana, and a library deep
        // enough for the draw-two. Hand contents vary per test and are stated at each call site,
        // because the number under test *is* the hand size.
        fun build(vararg handCards: String) = scenario()
            .withPlayers()
            .withCardOnBattlefield(1, "Grizzly Bears") // printed 2/2
            .withLandsOnBattlefield(1, "Island", 6)
            .withCardInLibrary(1, "Hill Giant")
            .withCardInLibrary(1, "Hill Giant")
            .withCardInLibrary(1, "Hill Giant")
            .let { builder -> handCards.fold(builder) { acc, name -> acc.withCardInHand(1, name) } }
            .withActivePlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            .build()

        fun TestGame.castAndResolve(spellName: String, targetId: EntityId? = null) {
            val result = castSpell(1, spellName, targetId)
            withClue("casting $spellName should succeed: ${result.error}") { result.error shouldBe null }
            if (getPendingDecision() is SelectManaSourcesDecision) submitManaSourcesAutoPay()
            resolveStack()
        }

        test("re-evaluated base power tracks hand size as it changes mid-turn") {
            // Hand: the two spells + 3 filler = 5.
            val game = build(
                "Continuous Base Power Test", "Draw Two Test",
                "Hill Giant", "Hill Giant", "Hill Giant",
            )
            val bear = game.findPermanent("Grizzly Bears")!!
            game.handSize(1) shouldBe 5

            game.castAndResolve("Continuous Base Power Test", bear)

            // The spell itself has left the hand: 4 cards, so base power is 4. Toughness untouched.
            withClue("hand after the spell resolved") { game.handSize(1) shouldBe 4 }
            game.state.projectedState.getPower(bear) shouldBe 4
            game.state.projectedState.getToughness(bear) shouldBe 2

            // Draw Two leaves the hand (-1) and then draws 2, so the hand ends at 5 — a different
            // number from the one that was true when the effect resolved.
            game.castAndResolve("Draw Two Test")
            withClue("hand after drawing two") { game.handSize(1) shouldBe 5 }
            withClue("base power must follow the new hand size, not the resolution-time snapshot") {
                game.state.projectedState.getPower(bear) shouldBe 5
            }
            game.state.projectedState.getToughness(bear) shouldBe 2
        }

        test("the default (snapshot) mode freezes the value at resolution") {
            val game = build(
                "Snapshot Base Power Test", "Draw Two Test",
                "Hill Giant", "Hill Giant", "Hill Giant",
            )
            val bear = game.findPermanent("Grizzly Bears")!!

            game.castAndResolve("Snapshot Base Power Test", bear)
            game.state.projectedState.getPower(bear) shouldBe 4

            game.castAndResolve("Draw Two Test")
            game.handSize(1) shouldBe 5
            withClue("reevaluateContinuously = false must keep the number it saw at resolution") {
                game.state.projectedState.getPower(bear) shouldBe 4
            }
        }

        test("a re-evaluated base toughness set leaves power at its printed value") {
            val game = build(
                "Continuous Base Toughness Test", "Draw Two Test",
                "Hill Giant", "Hill Giant", "Hill Giant",
            )
            val bear = game.findPermanent("Grizzly Bears")!!

            game.castAndResolve("Continuous Base Toughness Test", bear)
            game.state.projectedState.getPower(bear) shouldBe 2
            game.state.projectedState.getToughness(bear) shouldBe 4

            game.castAndResolve("Draw Two Test")
            game.state.projectedState.getPower(bear) shouldBe 2
            withClue("the toughness half re-evaluates the same way the power half does") {
                game.state.projectedState.getToughness(bear) shouldBe 5
            }
        }

        test("a +1/+1 counter applies on top of the re-evaluated base and rides the re-evaluation") {
            val game = build(
                "Add Counter Test", "Continuous Base Power Test", "Draw Two Test",
                "Hill Giant", "Hill Giant", "Hill Giant",
            )
            val bear = game.findPermanent("Grizzly Bears")!!

            // Counter first: 2/2 -> 3/3.
            game.castAndResolve("Add Counter Test", bear)
            game.state.projectedState.getPower(bear) shouldBe 3
            game.state.projectedState.getToughness(bear) shouldBe 3

            // Then the layer 7b set, with a 4-card hand. The counter is layer 7c (CR 613.4c,
            // "effects **and counters** that modify power and/or toughness") and is applied *after*
            // the set, so it is not overwritten: 4 (hand) + 1 = 5 power, 2 + 1 = 3 toughness.
            game.castAndResolve("Continuous Base Power Test", bear)
            game.handSize(1) shouldBe 4
            withClue("the +1/+1 counter must stack on top of the layer 7b base, not be erased") {
                game.state.projectedState.getPower(bear) shouldBe 5
            }
            game.state.projectedState.getToughness(bear) shouldBe 3

            // Move the hand afterwards so the interaction is pinned *for the re-evaluated mode*:
            // a snapshot executor would leave power at 5 here.
            game.castAndResolve("Draw Two Test")
            game.handSize(1) shouldBe 5
            withClue("the counter keeps riding on top as the base re-evaluates") {
                game.state.projectedState.getPower(bear) shouldBe 6
            }
            game.state.projectedState.getToughness(bear) shouldBe 3
        }

        test("a pump spell applies on top of the re-evaluated base") {
            val game = build(
                "Continuous Base Power Test", "Pump Test",
                "Hill Giant", "Hill Giant", "Hill Giant",
            )
            val bear = game.findPermanent("Grizzly Bears")!!

            game.castAndResolve("Continuous Base Power Test", bear)
            game.state.projectedState.getPower(bear) shouldBe 4

            // Pump leaves the hand, so the re-evaluated base drops to 3 and the +2/+2 rides on top.
            game.castAndResolve("Pump Test", bear)
            game.handSize(1) shouldBe 3
            withClue("layer 7c pump applies after the re-evaluated layer 7b base") {
                game.state.projectedState.getPower(bear) shouldBe 5
            }
            game.state.projectedState.getToughness(bear) shouldBe 4
        }

        test("the re-evaluated set ends with its duration") {
            val game = build(
                "Continuous Base Power Test", "Draw Two Test",
                "Hill Giant", "Hill Giant", "Hill Giant",
            )
            val bear = game.findPermanent("Grizzly Bears")!!

            game.castAndResolve("Continuous Base Power Test", bear)
            game.state.projectedState.getPower(bear) shouldBe 4

            // Prove the effect is live *in re-evaluated mode* before checking that it expires —
            // otherwise a snapshot executor passes this test unchanged.
            game.castAndResolve("Draw Two Test")
            game.handSize(1) shouldBe 5
            game.state.projectedState.getPower(bear) shouldBe 5

            game.passUntilPhase(Phase.ENDING, Step.END)
            game.passUntilPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN) // opponent's turn

            withClue("the until-end-of-turn effect must be gone, printed 2/2 restored") {
                game.state.projectedState.getPower(bear) shouldBe 2
                game.state.projectedState.getToughness(bear) shouldBe 2
            }
        }

        test("the effect survives its granter leaving the battlefield and keeps re-evaluating") {
            val game = scenario()
                .withPlayers()
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withCardOnBattlefield(1, "Embiggen Engine Test")
                .withLandsOnBattlefield(1, "Island", 6)
                .withCardInLibrary(1, "Hill Giant")
                .withCardInLibrary(1, "Hill Giant")
                .withCardInLibrary(1, "Hill Giant")
                .withCardInHand(1, "Draw Two Test")
                .withCardInHand(1, "Hill Giant")
                .withCardInHand(1, "Hill Giant")
                .withCardInHand(1, "Hill Giant")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val bear = game.findPermanent("Grizzly Bears")!!
            val engine = game.findPermanent("Embiggen Engine Test")!!
            val abilityId = embiggenEngine.activatedAbilities.first().id

            val activation = game.execute(
                ActivateAbility(
                    playerId = game.player1Id,
                    sourceId = engine,
                    abilityId = abilityId,
                    targets = listOf(ChosenTarget.Permanent(bear)),
                )
            )
            withClue("activation should succeed: ${activation.error}") { activation.error shouldBe null }
            if (game.getPendingDecision() is SelectManaSourcesDecision) game.submitManaSourcesAutoPay()
            game.resolveStack()

            withClue("the granter sacrificed itself as the activation cost") {
                game.isInGraveyard(1, "Embiggen Engine Test") shouldBe true
            }
            game.handSize(1) shouldBe 4
            game.state.projectedState.getPower(bear) shouldBe 4
            game.state.projectedState.getToughness(bear) shouldBe 2

            game.castAndResolve("Draw Two Test")
            game.handSize(1) shouldBe 5
            withClue("still re-evaluating with the granting permanent in the graveyard") {
                game.state.projectedState.getPower(bear) shouldBe 5
            }
        }

        test("a later fixed layer 7b set overwrites an earlier re-evaluated one") {
            val game = build(
                "Continuous Base Power Test", "Snapshot Base Power Test", "Draw Two Test",
                "Hill Giant", "Hill Giant", "Hill Giant",
            )
            val bear = game.findPermanent("Grizzly Bears")!!

            game.castAndResolve("Continuous Base Power Test", bear)
            game.state.projectedState.getPower(bear) shouldBe 5

            // Same sublayer (SET_VALUES), so CR 613.7 timestamp order decides and the snapshot,
            // being later, wins outright — the earlier re-evaluated set is fully overwritten.
            game.castAndResolve("Snapshot Base Power Test", bear)
            game.state.projectedState.getPower(bear) shouldBe 4

            game.castAndResolve("Draw Two Test")
            game.handSize(1) shouldBe 5
            withClue("the later fixed 7b set wins, so the hand moving must not move power") {
                game.state.projectedState.getPower(bear) shouldBe 4
            }
        }

        test("a later re-evaluated layer 7b set overwrites an earlier fixed one and keeps tracking") {
            val game = build(
                "Snapshot Base Power Test", "Continuous Base Power Test", "Draw Two Test",
                "Hill Giant", "Hill Giant", "Hill Giant",
            )
            val bear = game.findPermanent("Grizzly Bears")!!

            game.castAndResolve("Snapshot Base Power Test", bear)
            game.state.projectedState.getPower(bear) shouldBe 5

            game.castAndResolve("Continuous Base Power Test", bear)
            game.state.projectedState.getPower(bear) shouldBe 4

            game.castAndResolve("Draw Two Test")
            game.handSize(1) shouldBe 5
            withClue("the later re-evaluated 7b set wins and goes on tracking the hand") {
                game.state.projectedState.getPower(bear) shouldBe 5
            }
        }

        test("a context-scoped amount is rejected under re-evaluation, not silently read as 0") {
            // The projector rebuilds a bare EffectContext from the source, so a reference to the
            // triggering object — the exact shape Belligerent Yearling uses in snapshot mode —
            // has nothing to resolve against and would read 0 on every pass forever.
            val triggeringPower = DynamicAmount.EntityProperty(
                EntityReference.Triggering,
                EntityNumericProperty.Power,
            )

            withClue("the scan finds a context-scoped reference at any depth, and only there") {
                contextScopedReferenceIn(DynamicAmounts.cardsInYourHand()) shouldBe null
                contextScopedReferenceIn(triggeringPower) shouldBe "Triggering"
                contextScopedReferenceIn(DynamicAmount.XValue) shouldBe "XValue"
                contextScopedReferenceIn(
                    DynamicAmount.Add(DynamicAmounts.cardsInYourHand(), DynamicAmount.XValue)
                ) shouldBe "XValue"
                contextScopedReferenceIn(
                    DynamicAmount.Add(
                        DynamicAmounts.cardsInYourHand(),
                        DynamicAmount.Multiply(triggeringPower, 2),
                    )
                ) shouldBe "Triggering"
            }

            val game = build("Hill Giant")
            val bear = game.findPermanent("Grizzly Bears")!!
            val context = EffectContext(sourceId = bear, controllerId = game.player1Id)
            val executor = SetBaseStatsExecutor()

            withClue("snapshot mode still accepts it — it is evaluated here, against a real context") {
                val snapshot = Effects.SetBasePower(
                    EffectTarget.Self, triggeringPower, Duration.EndOfTurn
                ) as SetBaseStatsEffect
                executor.execute(game.state, snapshot, context).error shouldBe null
            }

            val reevaluated = Effects.SetBasePower(
                EffectTarget.Self, triggeringPower, Duration.EndOfTurn, reevaluateContinuously = true
            ) as SetBaseStatsEffect
            val thrown = shouldThrow<IllegalArgumentException> {
                executor.execute(game.state, reevaluated, context)
            }
            thrown.message!! shouldContain "Triggering"
        }
    }
}

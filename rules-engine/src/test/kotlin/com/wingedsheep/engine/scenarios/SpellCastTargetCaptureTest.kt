package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.state.components.stack.TargetsComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ForEachInCollectionEffect
import com.wingedsheep.sdk.scripting.effects.IterationSpace
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * "Whenever you cast a spell that targets one or more [filter], **those** … " — the trigger-time
 * capture behind `Triggers.youCastSpellTargeting(filter)`.
 *
 * `TriggerDetector` records exactly the targets that satisfied the trigger's own
 * `SpellCastPredicate.TargetsMatching` gate into `TriggerContext.capturedEntityIds`, which the
 * resolving ability sees as the pipeline collection `IterationSpace.TRIGGER_CAPTURED_COLLECTION` —
 * the same engine-seeded slot a batched ETB payoff reads (Kambal, Profiteering Mayor). The payoff
 * is then a plain `ForEachInCollectionEffect`, with no effect that reads the stack.
 *
 * The capture is a **snapshot taken when the ability triggers**, not a read of the spell at
 * resolution, and that is the load-bearing part: the trigger sits on top of the spell that caused
 * it, so an opponent with priority can counter or retarget that spell before the trigger resolves.
 * A live read would find nothing (countering strips `TargetsComponent`) or the wrong creature. The
 * triggered ability exists on the stack independently of its cause (CR 113.7a).
 *
 * The proof cards mostly put a **+1/+1 counter** on each captured target rather than granting a
 * keyword, so the assertions count iterations directly: a body that ran once, twice or not at all
 * is distinguishable, and nothing can be satisfied by an unrelated projection path. The last test
 * needs Storm's own keyword-granting shape, because what it is guarding against is a grant that
 * lands on a permanent the captured *spell* became.
 */
class SpellCastTargetCaptureTest : FunSpec({

    /** A spell with two creature target slots. */
    val doublePump = card("Test Double Pump") {
        manaCost = "{G}"
        typeLine = "Instant"
        spell {
            val a = target("first creature", Targets.Creature)
            val b = target("second creature", Targets.Creature)
            effect = Effects.ModifyStats(1, 1, a).then(Effects.ModifyStats(1, 1, b))
        }
    }

    /** A spell that targets a creature *and* a player — the player target must not be captured. */
    val splitPump = card("Test Split Pump") {
        manaCost = "{G}"
        typeLine = "Instant"
        spell {
            val c = target("target creature", Targets.Creature)
            val p = target("target player", Targets.Player)
            effect = Effects.ModifyStats(1, 1, c).then(Effects.GainLife(1, p))
        }
    }

    /** Storm's shape, generalized: a counter on every captured target. */
    val anyCreatureObserver = card("Test Target Observer") {
        manaCost = "{2}{W}"
        typeLine = "Creature — Human Scout"
        power = 2
        toughness = 2
        triggeredAbility {
            trigger = Triggers.youCastSpellTargeting(GameObjectFilter.Creature)
            effect = ForEachInCollectionEffect(
                collection = IterationSpace.TRIGGER_CAPTURED_COLLECTION,
                effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
            )
            description = "Whenever you cast a spell that targets one or more creatures, put a " +
                "+1/+1 counter on each of those creatures."
        }
    }

    /** The same payoff, but the trigger — and therefore the capture — is narrowed. */
    val ownCreatureObserver = card("Test Own Target Observer") {
        manaCost = "{2}{W}"
        typeLine = "Creature — Human Scout"
        power = 2
        toughness = 2
        triggeredAbility {
            trigger = Triggers.youCastSpellTargeting(GameObjectFilter.Creature.youControl())
            effect = ForEachInCollectionEffect(
                collection = IterationSpace.TRIGGER_CAPTURED_COLLECTION,
                effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
            )
            description = "Whenever you cast a spell that targets one or more creatures you " +
                "control, put a +1/+1 counter on each of those creatures."
        }
    }

    /**
     * A spell that targets *a spell on the stack* and leaves it alone. Casting it at a creature
     * spell fires a `youCastSpellTargeting(Creature)` trigger — a creature card on the stack
     * satisfies `IsCreature`, which is pre-existing behaviour shared with Mockingbird and Iron
     * Fist — so it is the probe for what such a trigger may capture.
     */
    val spellNudge = card("Test Spell Nudge") {
        manaCost = "{U}"
        typeLine = "Instant"
        spell {
            target("target spell", Targets.Spell)
            effect = Effects.GainLife(1)
        }
    }

    /** Storm's own payoff shape, for the cases where a counter can't tell the story. */
    val flyingObserver = card("Test Flying Target Observer") {
        manaCost = "{2}{W}"
        typeLine = "Creature — Human Scout"
        power = 2
        toughness = 2
        triggeredAbility {
            trigger = Triggers.youCastSpellTargeting(GameObjectFilter.Creature)
            effect = ForEachInCollectionEffect(
                collection = IterationSpace.TRIGGER_CAPTURED_COLLECTION,
                effect = Effects.GrantKeyword(Keyword.FLYING, EffectTarget.Self)
            )
            description = "Whenever you cast a spell that targets one or more creatures, those " +
                "creatures gain flying until end of turn."
        }
    }

    /**
     * Bolt Bend's shape — "change the target of target spell with a single target". Cast by the
     * opponent while the trigger sits on top of the spell, it is the other half of the snapshot
     * argument: countering proves a live read would find *nothing*, retargeting proves it would
     * find the *wrong* creature.
     */
    val retarget = card("Test Retarget") {
        manaCost = "{U}"
        typeLine = "Instant"
        spell {
            target("target spell with a single target", Targets.SpellOrAbilityWithSingleTarget)
            effect = Effects.ChangeTarget()
        }
    }

    val extras = listOf(
        doublePump, splitPump, anyCreatureObserver, ownCreatureObserver, spellNudge, flyingObserver,
        retarget
    )

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + extras)
        d.initMirrorMatch(deck = Deck.of("Forest" to 40), skipMulligans = true)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    fun GameTestDriver.plusOneCounters(id: EntityId): Int =
        state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    /** Resolve everything on the stack (trigger first, then the spell). */
    fun GameTestDriver.resolveStack() {
        repeat(8) { if (pendingDecision != null) autoResolveDecision() else bothPass() }
    }

    test("the body runs once per captured target, and not on the trigger's own source") {
        val d = driver()
        val me = d.activePlayer!!

        val observer = d.putCreatureOnBattlefield(me, "Test Target Observer")
        val bearA = d.putCreatureOnBattlefield(me, "Grizzly Bears")
        val bearB = d.putCreatureOnBattlefield(me, "Centaur Courser")
        val pump = d.putCardInHand(me, "Test Double Pump")
        d.giveMana(me, Color.GREEN, 1)

        d.castSpellWithTargets(
            me, pump, listOf(ChosenTarget.Permanent(bearA), ChosenTarget.Permanent(bearB))
        ).error shouldBe null
        d.resolveStack()

        d.plusOneCounters(bearA) shouldBe 1
        d.plusOneCounters(bearB) shouldBe 1
        withClue("EffectTarget.Self must bind to the iterated target, never the ability's source") {
            d.plusOneCounters(observer) shouldBe 0
        }
    }

    test("a player target is not captured; only the creature target is iterated") {
        val d = driver()
        val me = d.activePlayer!!
        val opp = d.getOpponent(me)

        d.putCreatureOnBattlefield(me, "Test Target Observer")
        val bear = d.putCreatureOnBattlefield(me, "Grizzly Bears")
        val spell = d.putCardInHand(me, "Test Split Pump")
        d.giveMana(me, Color.GREEN, 1)

        d.castSpellWithTargets(
            me, spell, listOf(ChosenTarget.Permanent(bear), ChosenTarget.Player(opp))
        ).error shouldBe null
        d.resolveStack()

        d.plusOneCounters(bear) shouldBe 1
    }

    test("the trigger's filter narrows the capture to the targets that matched it") {
        val d = driver()
        val me = d.activePlayer!!
        val opp = d.getOpponent(me)

        d.putCreatureOnBattlefield(me, "Test Own Target Observer")
        val mine = d.putCreatureOnBattlefield(me, "Grizzly Bears")
        val theirs = d.putCreatureOnBattlefield(opp, "Centaur Courser")
        val pump = d.putCardInHand(me, "Test Double Pump")
        d.giveMana(me, Color.GREEN, 1)

        d.castSpellWithTargets(
            me, pump, listOf(ChosenTarget.Permanent(mine), ChosenTarget.Permanent(theirs))
        ).error shouldBe null
        d.resolveStack()

        d.plusOneCounters(mine) shouldBe 1
        withClue("a target that didn't match the trigger's own filter is not captured") {
            d.plusOneCounters(theirs) shouldBe 0
        }
    }

    test("a spell with no targets captures nothing (no trigger, no iteration)") {
        val d = driver()
        val me = d.activePlayer!!

        d.putCreatureOnBattlefield(me, "Test Target Observer")
        val bear = d.putCreatureOnBattlefield(me, "Grizzly Bears")
        val untargeted = d.putCardInHand(me, "Careful Study")
        d.giveMana(me, Color.BLUE, 1)

        d.castSpell(me, untargeted)
        d.resolveStack()

        d.plusOneCounters(bear) shouldBe 0
    }

    test("a creature *spell* on the stack is not one of \"those creatures\"") {
        val d = driver()
        val me = d.activePlayer!!

        d.putCreatureOnBattlefield(me, "Test Flying Target Observer")
        val bearsCard = d.putCardInHand(me, "Grizzly Bears")
        val nudge = d.putCardInHand(me, "Test Spell Nudge")
        d.giveMana(me, Color.GREEN, 1)
        d.giveColorlessMana(me, 1)
        d.giveMana(me, Color.BLUE, 1)

        // A creature spell goes on the stack, and I respond with a spell that targets the *spell*.
        // A creature card on the stack matches IsCreature, so my observer triggers off it.
        d.castSpell(me, bearsCard).error shouldBe null
        val creatureSpell = d.state.stack.first()
        d.submit(
            CastSpell(
                playerId = me,
                cardId = nudge,
                targets = listOf(ChosenTarget.Spell(creatureSpell)),
                paymentStrategy = PaymentStrategy.FromPool
            )
        ).error shouldBe null
        withClue("the creature spell, my spell, and the observer's trigger are all on the stack") {
            d.stackSize shouldBe 3
        }
        d.resolveStack()

        val bears = d.findPermanent(me, "Grizzly Bears")
        withClue("the creature spell resolved") { bears shouldNotBe null }
        withClue("a permanent spell keeps its entity id, so capturing it would grant the keyword to the permanent it becomes") {
            d.state.projectedState.hasKeyword(bears!!, Keyword.FLYING) shouldBe false
        }
    }

    test("countering the triggering spell in response does not erase the capture") {
        val d = driver()
        val me = d.activePlayer!!
        val opp = d.getOpponent(me)

        d.putCreatureOnBattlefield(me, "Test Target Observer")
        val bear = d.putCreatureOnBattlefield(me, "Grizzly Bears")
        val growth = d.putCardInHand(me, "Giant Growth")
        val counter = d.putCardInHand(opp, "Counterspell")
        d.giveMana(me, Color.GREEN, 1)
        d.giveMana(opp, Color.BLUE, 2)

        d.castSpell(me, growth, listOf(bear)).error shouldBe null
        withClue("the spell and its cast trigger are both on the stack") {
            d.stackSize shouldBe 2
        }

        // The trigger is on top of the spell, so the opponent can answer the spell underneath it.
        val spellOnStack = d.state.stack.first()
        d.passPriority(me)
        d.submit(
            CastSpell(
                playerId = opp,
                cardId = counter,
                targets = listOf(ChosenTarget.Spell(spellOnStack)),
                paymentStrategy = PaymentStrategy.FromPool
            )
        ).error shouldBe null
        d.resolveStack()

        withClue("the spell was countered") {
            d.getGraveyardCardNames(me) shouldBe listOf("Giant Growth")
        }
        withClue("\"those creatures\" was fixed when the ability triggered (CR 113.7a)") {
            d.plusOneCounters(bear) shouldBe 1
        }
    }

    test("retargeting the triggering spell in response does not move the capture") {
        val d = driver()
        val me = d.activePlayer!!
        val opp = d.getOpponent(me)

        val observer = d.putCreatureOnBattlefield(me, "Test Target Observer")
        val aimedAt = d.putCreatureOnBattlefield(me, "Grizzly Bears")
        val movedTo = d.putCreatureOnBattlefield(me, "Grizzly Bears")
        val growth = d.putCardInHand(me, "Giant Growth")
        val bend = d.putCardInHand(opp, "Test Retarget")
        d.giveMana(me, Color.GREEN, 1)
        d.giveMana(opp, Color.BLUE, 1)

        d.castSpell(me, growth, listOf(aimedAt)).error shouldBe null
        withClue("the spell and its cast trigger are both on the stack") {
            d.stackSize shouldBe 2
        }

        // Same window the counter test uses: the trigger is on top, so the spell underneath it is
        // still answerable — here by moving its target rather than removing it.
        val spellOnStack = d.state.stack.first()
        d.passPriority(me)
        d.submit(
            CastSpell(
                playerId = opp,
                cardId = bend,
                targets = listOf(ChosenTarget.Spell(spellOnStack)),
                paymentStrategy = PaymentStrategy.FromPool
            )
        ).error shouldBe null

        // The redirect resolves first and asks its own controller to pick the new target. The
        // observer is a legal new target too, so the choice is submitted rather than auto-resolved.
        var guard = 0
        while (d.pendingDecision !is SelectCardsDecision && guard++ < 8) d.bothPass()
        withClue("the redirect paused for the new-target choice") {
            (d.pendingDecision is SelectCardsDecision) shouldBe true
        }
        withClue("the creature the spell already targets is not offered as a *new* target") {
            (d.pendingDecision as SelectCardsDecision).options.toSet() shouldBe setOf(observer, movedTo)
        }
        d.submitCardSelection(opp, listOf(movedTo)).error shouldBe null

        withClue("the retarget really landed: the spell under the trigger now points somewhere else") {
            d.state.getEntity(spellOnStack)?.get<TargetsComponent>()?.targets shouldBe
                listOf(ChosenTarget.Permanent(movedTo))
        }

        d.resolveStack()

        withClue("\"those creatures\" was fixed when the ability triggered, so the counter stayed put") {
            d.plusOneCounters(aimedAt) shouldBe 1
        }
        withClue("and did not follow the spell to its new target") {
            d.plusOneCounters(movedTo) shouldBe 0
            d.plusOneCounters(observer) shouldBe 0
        }
    }
})

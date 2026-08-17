package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Storm, Windrider [MSH 230] — {1}{G}{W}{W} Legendary Creature — Mutant Hero 4/4
 *
 * Flying
 * Creatures with flying can't attack you or block creatures you control.
 * Whenever you cast a spell that targets one or more creatures, those creatures gain flying
 * until end of turn.
 *
 * Three clauses on three different rules, so three groups of tests: the defender-side attack
 * restriction (`CantBeAttackedBy`), the attacker-side block restriction over the whole "creatures
 * you control" group (`CantBeBlockedBy`), and the cast trigger whose payoff iterates the targets
 * the trigger captured (`ForEachInCollectionEffect` over `TRIGGER_CAPTURED_COLLECTION`; the capture
 * mechanism itself is covered by `SpellCastTargetCaptureTest`).
 *
 * The attack tests keep an unrestricted ground creature on the attacking side: with no legal attack
 * at all the engine skips the declare-attackers step, and an "expect a rejection" assertion would
 * then pass for the wrong reason.
 */
class StormWindriderScenarioTest : FunSpec({

    /** A two-creature-target instant, so "those creatures" is genuinely plural. */
    val doubleBlessing = card("Storm Test Double Blessing") {
        manaCost = "{G}"
        typeLine = "Instant"
        spell {
            val a = target("first creature", Targets.Creature)
            val b = target("second creature", Targets.Creature)
            effect = Effects.ModifyStats(1, 1, a).then(Effects.ModifyStats(1, 1, b))
        }
    }

    val testFlier = card("Storm Test Kestrel") {
        manaCost = "{1}{U}"
        typeLine = "Creature — Bird"
        power = 2
        toughness = 2
        keywords(Keyword.FLYING)
    }

    val testGrounder = card("Storm Test Ox") {
        manaCost = "{1}{G}"
        typeLine = "Creature — Ox"
        power = 2
        toughness = 2
    }

    val extras = listOf(doubleBlessing, testFlier, testGrounder)

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + extras)
        d.initMirrorMatch(deck = Deck.of("Forest" to 40), skipMulligans = true)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    fun GameTestDriver.hasFlying(id: EntityId): Boolean =
        state.projectedState.hasKeyword(id, Keyword.FLYING)

    /** Reach the declare-attackers step of [player]'s own turn, and prove we are there. */
    fun GameTestDriver.attackStepOf(player: EntityId) {
        passPriorityUntil(Step.DECLARE_ATTACKERS)
        if (activePlayer != player) {
            passPriorityUntil(Step.POSTCOMBAT_MAIN)
            passPriorityUntil(Step.DECLARE_ATTACKERS)
        }
        currentStep shouldBe Step.DECLARE_ATTACKERS
        activePlayer shouldBe player
    }

    /**
     * Resolve everything currently on the stack and stop. Deliberately **not** a fixed number of
     * `bothPass()` calls: passing on past an empty stack walks the turn forward, and an extra pass
     * or two reaches the cleanup step — which expires exactly the `Duration.EndOfTurn` grant these
     * tests are asserting on.
     */
    fun GameTestDriver.resolveStack() {
        var guard = 0
        while ((stackSize > 0 || pendingDecision != null) && guard++ < 16) {
            if (pendingDecision != null) autoResolveDecision() else bothPass()
        }
    }

    fun GameTestDriver.readyOx(player: EntityId): EntityId {
        val id = putCreatureOnBattlefield(player, "Storm Test Ox")
        removeSummoningSickness(id)
        return id
    }

    test("Storm has flying") {
        val d = driver()
        val storm = d.putCreatureOnBattlefield(d.activePlayer!!, "Storm, Windrider")
        d.hasFlying(storm) shouldBe true
    }

    // ---- "Creatures with flying can't attack you" ---------------------------------

    test("a creature with flying can't attack Storm's controller") {
        val d = driver()
        val attacker = d.activePlayer!!
        val stormPlayer = d.getOpponent(attacker)

        d.putCreatureOnBattlefield(stormPlayer, "Storm, Windrider")
        val ox = d.readyOx(attacker)
        val kestrel = d.putCreatureOnBattlefield(attacker, "Storm Test Kestrel")
        d.removeSummoningSickness(kestrel)

        d.attackStepOf(attacker)
        d.declareAttackers(attacker, listOf(kestrel), stormPlayer).error shouldNotBe null
        withClue("the ground attacker is unaffected") {
            d.declareAttackers(attacker, listOf(ox), stormPlayer).error shouldBe null
        }
    }

    test("a creature without flying attacks Storm's controller freely") {
        val d = driver()
        val attacker = d.activePlayer!!
        val stormPlayer = d.getOpponent(attacker)

        d.putCreatureOnBattlefield(stormPlayer, "Storm, Windrider")
        val ox = d.readyOx(attacker)

        d.attackStepOf(attacker)
        d.declareAttackers(attacker, listOf(ox), stormPlayer).error shouldBe null
    }

    // ---- "... or block creatures you control" -------------------------------------

    test("a creature with flying can't block Storm herself") {
        val d = driver()
        val stormPlayer = d.activePlayer!!
        val defender = d.getOpponent(stormPlayer)

        val storm = d.putCreatureOnBattlefield(stormPlayer, "Storm, Windrider")
        d.removeSummoningSickness(storm)
        val kestrel = d.putCreatureOnBattlefield(defender, "Storm Test Kestrel")

        d.attackStepOf(stormPlayer)
        d.declareAttackers(stormPlayer, listOf(storm), defender).error shouldBe null
        d.passPriorityUntil(Step.DECLARE_BLOCKERS)
        withClue("Storm is herself \"a creature you control\"") {
            d.declareBlockers(defender, mapOf(kestrel to listOf(storm))).error shouldNotBe null
        }
    }

    test("a creature with flying can't block another creature Storm's controller controls") {
        val d = driver()
        val stormPlayer = d.activePlayer!!
        val defender = d.getOpponent(stormPlayer)

        d.putCreatureOnBattlefield(stormPlayer, "Storm, Windrider")
        val ox = d.readyOx(stormPlayer)
        val kestrel = d.putCreatureOnBattlefield(defender, "Storm Test Kestrel")

        d.attackStepOf(stormPlayer)
        d.declareAttackers(stormPlayer, listOf(ox), defender).error shouldBe null
        d.passPriorityUntil(Step.DECLARE_BLOCKERS)
        d.declareBlockers(defender, mapOf(kestrel to listOf(ox))).error shouldNotBe null
    }

    test("a creature without flying blocks normally") {
        val d = driver()
        val stormPlayer = d.activePlayer!!
        val defender = d.getOpponent(stormPlayer)

        d.putCreatureOnBattlefield(stormPlayer, "Storm, Windrider")
        val ox = d.readyOx(stormPlayer)
        val blocker = d.putCreatureOnBattlefield(defender, "Storm Test Ox")

        d.attackStepOf(stormPlayer)
        d.declareAttackers(stormPlayer, listOf(ox), defender).error shouldBe null
        d.passPriorityUntil(Step.DECLARE_BLOCKERS)
        d.declareBlockers(defender, mapOf(blocker to listOf(ox))).error shouldBe null
    }

    // ---- "those creatures gain flying until end of turn" --------------------------

    test("a spell targeting two creatures gives both of them flying") {
        val d = driver()
        val me = d.activePlayer!!
        val opp = d.getOpponent(me)

        d.putCreatureOnBattlefield(me, "Storm, Windrider")
        val mine = d.putCreatureOnBattlefield(me, "Storm Test Ox")
        val theirs = d.putCreatureOnBattlefield(opp, "Storm Test Ox")
        val blessing = d.putCardInHand(me, "Storm Test Double Blessing")
        d.giveMana(me, Color.GREEN, 1)

        withClue("neither creature starts with flying") {
            d.hasFlying(mine) shouldBe false
            d.hasFlying(theirs) shouldBe false
        }

        d.castSpellWithTargets(
            me, blessing, listOf(ChosenTarget.Permanent(mine), ChosenTarget.Permanent(theirs))
        ).error shouldBe null
        d.resolveStack()

        withClue("\"those creatures\" is every creature the spell targeted, whoever controls it") {
            d.hasFlying(mine) shouldBe true
            d.hasFlying(theirs) shouldBe true
        }
    }

    test("a spell targeting a single creature gives that creature flying") {
        val d = driver()
        val me = d.activePlayer!!

        d.putCreatureOnBattlefield(me, "Storm, Windrider")
        val ox = d.putCreatureOnBattlefield(me, "Storm Test Ox")
        val growth = d.putCardInHand(me, "Giant Growth")
        d.giveMana(me, Color.GREEN, 1)

        d.castSpell(me, growth, listOf(ox)).error shouldBe null
        d.resolveStack()

        d.hasFlying(ox) shouldBe true
    }

    test("the granted flying feeds the block restriction: a pumped blocker can no longer block") {
        val d = driver()
        val stormPlayer = d.activePlayer!!
        val defender = d.getOpponent(stormPlayer)

        d.putCreatureOnBattlefield(stormPlayer, "Storm, Windrider")
        val ox = d.readyOx(stormPlayer)
        val blocker = d.putCreatureOnBattlefield(defender, "Storm Test Ox")
        val growth = d.putCardInHand(stormPlayer, "Giant Growth")
        d.giveMana(stormPlayer, Color.GREEN, 1)

        // Pump the opponent's would-be blocker: Storm's trigger hands it flying, which the middle
        // line then turns against it.
        d.castSpell(stormPlayer, growth, listOf(blocker)).error shouldBe null
        d.resolveStack()
        d.hasFlying(blocker) shouldBe true

        d.attackStepOf(stormPlayer)
        d.declareAttackers(stormPlayer, listOf(ox), defender).error shouldBe null
        d.passPriorityUntil(Step.DECLARE_BLOCKERS)
        d.declareBlockers(defender, mapOf(blocker to listOf(ox))).error shouldNotBe null
    }
})

package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CreatureGoadedEvent
import com.wingedsheep.engine.state.components.combat.GoadedComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.nulls.shouldNotBeNull
import com.wingedsheep.engine.core.Outcome
import io.kotest.matchers.shouldNotBe

/**
 * Tests for the Goad mechanic (CR 701.15).
 *
 * Goad: "Until your next turn, this creature attacks each combat if able and attacks
 * a player other than you if able."
 *
 * Driven by the test-only "Goad Spell" sorcery ({R}: goad target creature) so the
 * mechanic can be exercised without going through Glóin's activated ability + Treasure
 * setup.
 *
 * Covers:
 * - CR 701.15a: duration is "until that player's next turn"
 * - CR 701.15b: goaded creature attacks each combat if able
 * - CR 701.15c: goaded creature attacks a player other than its goader if able;
 *   the requirement stacks per goader
 * - CR 701.15d: same goader re-goading is a no-op
 */
class GoadEffectTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(
            deck = Deck.of(
                "Mountain" to 20,
                "Grizzly Bears" to 20
            ),
            skipMulligans = true
        )
        return driver
    }

    test("Goad Spell tags target creature with GoadedComponent containing the caster") {
        val driver = createDriver()
        val caster = driver.activePlayer!!
        val opponent = driver.getOpponent(caster)

        // Put a creature on the opponent's battlefield
        val target = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")

        // Cast Goad Spell on caster's main phase
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val goadSpell = driver.putCardInHand(caster, "Goad Spell")
        driver.giveMana(caster, Color.RED, 1)
        val castResult = driver.castSpell(caster, goadSpell, listOf(target))
        castResult.outcome shouldBe Outcome.Done
        driver.bothPass() // resolve

        // Target should now have GoadedComponent with the caster as the goader
        val goaded = driver.state.getEntity(target)?.get<GoadedComponent>()
        goaded.shouldNotBeNull()
        goaded.goaderIds shouldBe setOf(caster)

        // CreatureGoadedEvent should carry the goader's PLAYER name, not a placeholder
        // (regression guard for goader lookup going through PlayerComponent, not
        // CardComponent — players don't carry a CardComponent at all).
        val goadEvent = driver.events.filterIsInstance<CreatureGoadedEvent>().lastOrNull()
        goadEvent.shouldNotBeNull()
        goadEvent.goaderId shouldBe caster
        listOf("Player 1", "Player 2") shouldContain goadEvent.goaderName
    }

    test("CR 701.15b: goaded creature must attack on its controller's turn") {
        val driver = createDriver()
        val caster = driver.activePlayer!!
        val opponent = driver.getOpponent(caster)

        // Pre-place a creature on the opponent's battlefield and give it haste-equivalent
        // (remove summoning sickness so it's a valid attacker the next combat).
        val creature = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")
        driver.removeSummoningSickness(creature)

        // Caster goads it
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val goadSpell = driver.putCardInHand(caster, "Goad Spell")
        driver.giveMana(caster, Color.RED, 1)
        driver.castSpell(caster, goadSpell, listOf(creature))
        driver.bothPass()

        // Pass to opponent's turn
        driver.passPriorityUntil(Step.END, maxPasses = 200)
        driver.bothPass()
        driver.activePlayer shouldBe opponent

        // On opponent's declare-attackers, declaring no attackers must fail
        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        val noAttack = driver.declareAttackers(opponent, emptyMap())
        noAttack.outcome shouldNotBe Outcome.Done
    }

    test("CR 701.15c: goaded creature must attack a player other than its goader if able") {
        val driver = createDriver()
        val caster = driver.activePlayer!!
        val opponent = driver.getOpponent(caster)

        val creature = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")
        driver.removeSummoningSickness(creature)

        // In a 2-player game caster IS the only defender, so this test instead verifies
        // the "if able" carve-out: when the goader is the only legal defender, the
        // creature is still required to attack (and may attack the goader).
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val goadSpell = driver.putCardInHand(caster, "Goad Spell")
        driver.giveMana(caster, Color.RED, 1)
        driver.castSpell(caster, goadSpell, listOf(creature))
        driver.bothPass()

        driver.passPriorityUntil(Step.END, maxPasses = 200)
        driver.bothPass()
        driver.activePlayer shouldBe opponent

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        // Only the goader is available to attack — "if able" lets the creature attack them
        val attackResult = driver.declareAttackers(opponent, listOf(creature), caster)
        attackResult.outcome shouldBe Outcome.Done
    }

    test("CR 701.15a: goaded designation expires at the goader's next turn") {
        val driver = createDriver()
        val caster = driver.activePlayer!!
        val opponent = driver.getOpponent(caster)

        val creature = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")

        // Caster goads it on their own turn
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val goadSpell = driver.putCardInHand(caster, "Goad Spell")
        driver.giveMana(caster, Color.RED, 1)
        driver.castSpell(caster, goadSpell, listOf(creature))
        driver.bothPass()

        // Component is present right after resolution
        driver.state.getEntity(creature)?.has<GoadedComponent>() shouldBe true

        // Tap the goaded creature so it stays out of opponent's upcoming combat —
        // that lets the auto-priority-passer's empty-attackers submission pass goad
        // validation as we cross the turn boundary.
        driver.tapPermanent(creature)

        // Advance past caster's end-of-turn into opponent's upkeep step. Using UPKEEP
        // as the stop point (rather than END + bothPass) lets the auto-passer resolve
        // CLEANUP-step discard decisions, which bothPass alone won't.
        driver.passPriorityUntil(Step.UPKEEP, maxPasses = 500)
        driver.activePlayer shouldBe opponent

        // Component still in place during opponent's turn (it expires at the *goader's*
        // — caster's — next turn, not at every turn change)
        driver.state.getEntity(creature)?.has<GoadedComponent>() shouldBe true

        // Re-tap the creature (opponent's untap step already ran).
        driver.tapPermanent(creature)

        // Advance to caster's next turn via END step + bothPass, then keep auto-passing
        // until we hit a step beyond UNTAP so the post-startTurn goad cleanup has run.
        driver.passPriorityUntil(Step.END, maxPasses = 500)
        driver.bothPass()
        // Resolve any opponent-CLEANUP discard, then advance into caster's UPKEEP.
        driver.passPriorityUntil(Step.UPKEEP, maxPasses = 200)
        driver.activePlayer shouldBe caster
        // Goad designation expired at the start of caster's turn (CR 701.15a)
        driver.state.getEntity(creature)?.has<GoadedComponent>() shouldBe false
    }

    test("CR 701.15d: same goader re-goading is a no-op (goader set deduplicates)") {
        val driver = createDriver()
        val caster = driver.activePlayer!!
        val opponent = driver.getOpponent(caster)

        val creature = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Goad once
        val firstGoad = driver.putCardInHand(caster, "Goad Spell")
        driver.giveMana(caster, Color.RED, 1)
        driver.castSpell(caster, firstGoad, listOf(creature))
        driver.bothPass()

        // Goad again — same caster, same target
        val secondGoad = driver.putCardInHand(caster, "Goad Spell")
        driver.giveMana(caster, Color.RED, 1)
        driver.castSpell(caster, secondGoad, listOf(creature))
        driver.bothPass()

        val goaded = driver.state.getEntity(creature)?.get<GoadedComponent>()
        goaded.shouldNotBeNull()
        goaded.goaderIds shouldBe setOf(caster) // still a single goader
    }

    test("goaded creature on the battlefield is listed as a mandatory attacker") {
        val driver = createDriver()
        val caster = driver.activePlayer!!
        val opponent = driver.getOpponent(caster)

        // Put two creatures on the opponent's battlefield — only one will be goaded
        val goadedCreature = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")
        val freeCreature = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")
        driver.removeSummoningSickness(goadedCreature)
        driver.removeSummoningSickness(freeCreature)

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val goadSpell = driver.putCardInHand(caster, "Goad Spell")
        driver.giveMana(caster, Color.RED, 1)
        driver.castSpell(caster, goadSpell, listOf(goadedCreature))
        driver.bothPass()

        // Hand to opponent
        driver.passPriorityUntil(Step.END, maxPasses = 200)
        driver.bothPass()
        driver.activePlayer shouldBe opponent

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)

        // The free creature alone may attack (or stay back), but the goaded creature
        // must be in the attack — leaving it home is illegal.
        val partialAttack = driver.declareAttackers(opponent, listOf(freeCreature), caster)
        partialAttack.outcome shouldNotBe Outcome.Done

        // Including the goaded creature is fine.
        val fullAttack = driver.declareAttackers(opponent, listOf(goadedCreature, freeCreature), caster)
        fullAttack.outcome shouldBe Outcome.Done
    }

    test("tapped goaded creature does not need to attack (\"if able\" carve-out)") {
        val driver = createDriver()
        val caster = driver.activePlayer!!
        val opponent = driver.getOpponent(caster)

        val creature = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")
        driver.removeSummoningSickness(creature)

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val goadSpell = driver.putCardInHand(caster, "Goad Spell")
        driver.giveMana(caster, Color.RED, 1)
        driver.castSpell(caster, goadSpell, listOf(creature))
        driver.bothPass()

        driver.passPriorityUntil(Step.END, maxPasses = 200)
        driver.bothPass()
        driver.activePlayer shouldBe opponent

        // Tap the creature so it isn't a valid attacker
        driver.tapPermanent(creature)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)

        // Declaring no attackers is now legal — the goaded creature is tapped.
        val noAttack = driver.declareAttackers(opponent, emptyMap())
        noAttack.outcome shouldBe Outcome.Done
    }

    test("CR 701.15c: two distinct goaders compound into the goader set; each expires on its own turn") {
        // 2-player coverage note: CR 701.15c says additional goaders compound the
        // "attack a player other than the goader" requirement. The *rejection* path
        // of that requirement (legal non-goader defender exists but attacker chose a
        // goader-controlled defender) is structurally unreachable here — in a
        // 2-player game with the goader as the only opponent, every defender is
        // goader-controlled, so the carve-out fires before rejection can. What this
        // test verifies is the SET semantics that compound the requirement: two
        // distinct goader IDs both land in [GoadedComponent.goaderIds], and each
        // entry expires independently on its own goader's next turn (CR 701.15a +
        // CR 701.15c interaction).
        val driver = createDriver()
        val alice = driver.activePlayer!!         // turn 1 is Alice's
        val bob = driver.getOpponent(alice)

        // The creature lives on Bob's side; both Alice and Bob will end up as goaders.
        val creature = driver.putCreatureOnBattlefield(bob, "Grizzly Bears")

        // Alice goads it on her main phase.
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val aliceGoad = driver.putCardInHand(alice, "Goad Spell")
        driver.giveMana(alice, Color.RED, 1)
        driver.castSpell(alice, aliceGoad, listOf(creature))
        driver.bothPass()
        driver.state.getEntity(creature)?.get<GoadedComponent>()?.goaderIds shouldBe setOf(alice)

        // Advance into Bob's main phase so Bob can also cast Goad Spell on his own
        // creature. (CR 701.15 doesn't restrict the goader to opponents.)
        driver.passPriorityUntil(Step.END, maxPasses = 200)
        driver.bothPass()
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN, maxPasses = 200)
        driver.activePlayer shouldBe bob

        val bobGoad = driver.putCardInHand(bob, "Goad Spell")
        driver.giveMana(bob, Color.RED, 1)
        driver.castSpell(bob, bobGoad, listOf(creature))
        driver.bothPass()

        // Both goaders now stacked (CR 701.15c).
        driver.state.getEntity(creature)?.get<GoadedComponent>()?.goaderIds shouldBe setOf(alice, bob)

        // Each goad emitted its own CreatureGoadedEvent with the goader's player
        // name — regression guard for the PlayerComponent-vs-CardComponent lookup
        // fix on multi-goader stacking (both names should land, not collapse to
        // placeholders).
        val goadEvents = driver.events.filterIsInstance<CreatureGoadedEvent>()
            .filter { it.creatureId == creature }
        goadEvents.map { it.goaderId }.toSet() shouldBe setOf(alice, bob)
        goadEvents.map { it.goaderName }.toSet() shouldBe setOf("Player 1", "Player 2")

        // Tap the creature before crossing Bob's combat: the auto-passer's empty-
        // attackers submission would otherwise fail goad validation (creature is
        // now a valid mandatory attacker for Bob). Same trick as the CR 701.15a test.
        driver.tapPermanent(creature)

        // Cross the turn boundary into Alice's next turn. After her untap-step
        // cleanup, only Alice (not Bob) should be dropped from the goader set —
        // CR 701.15a is per-goader, not "expire on every turn change".
        driver.passPriorityUntil(Step.END, maxPasses = 200)
        driver.bothPass()
        driver.passPriorityUntil(Step.UPKEEP, maxPasses = 200)
        driver.activePlayer shouldBe alice
        val afterAliceTurn = driver.state.getEntity(creature)?.get<GoadedComponent>()
        afterAliceTurn.shouldNotBeNull()
        afterAliceTurn.goaderIds shouldBe setOf(bob)

        // One more turn boundary into Bob's next turn — Bob's slot now expires too,
        // emptying the goader set, which drops the component entirely. (Alice has
        // no creatures, so her combat empty-submits without touching goad
        // validation. Bob's untap re-untaps the creature, but goad cleanup runs in
        // the same hook so it never becomes a mandatory attacker.)
        driver.passPriorityUntil(Step.END, maxPasses = 200)
        driver.bothPass()
        driver.passPriorityUntil(Step.UPKEEP, maxPasses = 200)
        driver.activePlayer shouldBe bob
        driver.state.getEntity(creature)?.has<GoadedComponent>() shouldBe false
    }
})

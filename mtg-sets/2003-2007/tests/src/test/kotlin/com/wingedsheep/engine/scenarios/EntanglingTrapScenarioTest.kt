package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.AdderStaffBoggart
import com.wingedsheep.mtg.sets.definitions.lrw.cards.EntanglingTrap
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Entangling Trap (LRW #13) — "Whenever you clash, tap target creature an opponent controls.
 * If you won, that creature doesn't untap during its controller's next untap step."
 *
 * The clash payoff that pays out *either way*, so it is the mirror of Sylvan Echoes: the trigger
 * is unconditional and only the freeze rides on the outcome (CR 701.30d). The three things worth
 * pinning down:
 *
 *  - **Losing still taps.** An implementation that puts "and win" on the trigger — the natural
 *    thing to copy from Sylvan Echoes — makes a lost clash do nothing at all.
 *  - **Winning also freezes.** The `DOESNT_UNTAP` grant is bounded to that one untap step, so the
 *    creature is still tapped after its controller's untap step and untaps the turn after.
 *  - **One target, both halves.** Tap and freeze address the same chosen creature.
 *
 * Its ruling ("if you clash because of a spell or ability an opponent controls, the ability will
 * still trigger; likewise, you can still win a clash you didn't initiate") is covered by having the
 * *opponent's* Adder-Staff Boggart start every clash here — the Trap's controller never clashes on
 * purpose.
 */
class EntanglingTrapScenarioTest : FunSpec({

    val Boulder = com.wingedsheep.sdk.dsl.card("Clash Boulder") {
        manaCost = "{5}"; typeLine = "Artifact"; oracleText = ""
    }
    val Pebble = com.wingedsheep.sdk.dsl.card("Clash Pebble") {
        manaCost = "{0}"; typeLine = "Artifact"; oracleText = ""
    }

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + listOf(EntanglingTrap, AdderStaffBoggart, Boulder, Pebble))
        d.initMirrorMatch(deck = Deck.of("Forest" to 40), startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    /** Keep both top-or-bottom prompts on "leave it on top" — the clash's own two decisions. */
    fun GameTestDriver.answerClash() {
        repeat(4) {
            val decision = pendingDecision as? SelectCardsDecision ?: return
            submitDecision(decision.playerId, CardsSelectedResponse(decision.id, emptyList()))
        }
    }

    /**
     * Player 1 casts an Adder-Staff Boggart; its ETB clash is what fires player 2's Trap.
     * Player 2 is the Trap's controller and never initiates anything.
     */
    fun GameTestDriver.opponentClashes() {
        val cardId = putCardInHand(player1, "Adder-Staff Boggart")
        giveMana(player1, Color.RED, 2)
        castSpell(player1, cardId)
        bothPass()
        bothPass()
        answerClash()
    }

    /** Answer the Trap's "target creature an opponent controls" prompt; report whether it appeared. */
    fun GameTestDriver.chooseTrapTarget(target: EntityId): Boolean {
        var guard = 0
        while (guard++ < 8) {
            val decision = pendingDecision
            if (decision is ChooseTargetsDecision) {
                submitTargetSelection(decision.playerId, listOf(target))
                return true
            }
            if (stackSize == 0) return false
            bothPass()
        }
        return false
    }

    /**
     * Walk a whole turn cycle, back to player 1's main phase — which means through player 1's untap
     * step, the one the freeze is aimed at. Two `passPriorityUntil` hops per turn: the call stops
     * the instant the step matches, so asking for `PRECOMBAT_MAIN` twice in a row would be a no-op.
     *
     * Both hands are emptied first: a turn cycle reaches a cleanup step, and a hand over seven cards
     * pauses everything on a discard prompt that has nothing to do with this card.
     */
    fun GameTestDriver.passWholeTurnCycle() {
        listOf(player1, player2).forEach { p -> getHand(p).forEach { moveToGraveyard(it) } }
        repeat(2) {
            passPriorityUntil(Step.END)
            passPriorityUntil(Step.PRECOMBAT_MAIN)
        }
    }

    test("losing the clash still taps the chosen creature — the tap is not conditional") {
        val d = driver()
        d.putPermanentOnBattlefield(d.player2, "Entangling Trap")
        val victim = d.putCreatureOnBattlefield(d.player1, "Grizzly Bears")
        d.putCardOnTopOfLibrary(d.player1, "Clash Boulder")  // the clasher reveals MV 5 and wins
        d.putCardOnTopOfLibrary(d.player2, "Clash Pebble")   // the Trap's controller reveals MV 0

        d.opponentClashes()
        withClue("a lost clash still triggers and still needs a target") {
            d.chooseTrapTarget(victim) shouldBe true
        }
        d.bothPass()

        withClue("\"tap target creature\" is outside the \"if you won\" rider") {
            d.isTapped(victim) shouldBe true
        }
    }

    test("a creature tapped on a lost clash untaps normally on its controller's next untap step") {
        val d = driver()
        d.putPermanentOnBattlefield(d.player2, "Entangling Trap")
        val victim = d.putCreatureOnBattlefield(d.player1, "Grizzly Bears")
        d.putCardOnTopOfLibrary(d.player1, "Clash Boulder")
        d.putCardOnTopOfLibrary(d.player2, "Clash Pebble")

        d.opponentClashes()
        d.chooseTrapTarget(victim)
        d.bothPass()
        d.isTapped(victim) shouldBe true

        // Player 1 is the active player, so their next untap step is a full turn cycle away.
        d.passWholeTurnCycle()

        withClue("no freeze was applied, so the creature untaps as usual") {
            d.isTapped(victim) shouldBe false
        }
    }

    test("winning the clash freezes the creature through its controller's next untap step") {
        val d = driver()
        d.putPermanentOnBattlefield(d.player2, "Entangling Trap")
        val victim = d.putCreatureOnBattlefield(d.player1, "Grizzly Bears")
        d.putCardOnTopOfLibrary(d.player1, "Clash Pebble")   // the clasher reveals MV 0
        d.putCardOnTopOfLibrary(d.player2, "Clash Boulder")  // the Trap's controller wins with MV 5

        d.opponentClashes()
        d.chooseTrapTarget(victim)
        d.bothPass()
        d.isTapped(victim) shouldBe true

        d.passWholeTurnCycle()

        withClue("the win grants DOESNT_UNTAP, so player 1's untap step skips this creature") {
            d.isTapped(victim) shouldBe true
        }
    }

    test("the freeze expires after that one untap step — the creature untaps the cycle after") {
        val d = driver()
        d.putPermanentOnBattlefield(d.player2, "Entangling Trap")
        val victim = d.putCreatureOnBattlefield(d.player1, "Grizzly Bears")
        d.putCardOnTopOfLibrary(d.player1, "Clash Pebble")
        d.putCardOnTopOfLibrary(d.player2, "Clash Boulder")

        d.opponentClashes()
        d.chooseTrapTarget(victim)
        d.bothPass()

        // Player 1's next untap step is skipped...
        d.passWholeTurnCycle()
        d.isTapped(victim) shouldBe true

        // ...and the one after that is not: the duration covers exactly one untap step.
        d.passWholeTurnCycle()

        withClue("\"next untap step\" is one step, not a standing lock") {
            d.isTapped(victim) shouldBe false
        }
    }

    test("only a creature an opponent of the Trap's controller controls is a legal target") {
        val d = driver()
        d.putPermanentOnBattlefield(d.player2, "Entangling Trap")
        val theirs = d.putCreatureOnBattlefield(d.player1, "Grizzly Bears")
        val ours = d.putCreatureOnBattlefield(d.player2, "Grizzly Bears")
        d.putCardOnTopOfLibrary(d.player1, "Clash Pebble")
        d.putCardOnTopOfLibrary(d.player2, "Clash Boulder")

        d.opponentClashes()

        var guard = 0
        var decision: ChooseTargetsDecision? = null
        while (guard++ < 8) {
            val pending = d.pendingDecision
            if (pending is ChooseTargetsDecision) { decision = pending; break }
            if (d.stackSize == 0) break
            d.bothPass()
        }
        val options = checkNotNull(decision) { "the Trap should have asked for a target" }
            .legalTargets.values.flatten()

        withClue("\"an opponent controls\" is read from the Trap's controller, not the clasher") {
            options.contains(theirs) shouldBe true
            options.contains(ours) shouldBe false
        }
    }
})

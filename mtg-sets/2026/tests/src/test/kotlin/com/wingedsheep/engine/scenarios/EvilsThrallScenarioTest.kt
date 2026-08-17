package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.player.SkipNextTurnComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Evil's Thrall (MSH #128) — {2}{R} Sorcery.
 *
 *   Gain control of target creature until end of turn. If you control a Villain with greater mana
 *   value than that creature, gain control of that creature until the end of your next turn
 *   instead. Untap that creature. It gains haste until end of turn.
 *
 * Each case is a turn-boundary question the card's two branches answer differently:
 *
 *  1. **No qualifying Villain** — the ordinary Threaten. Control reverts at this turn's cleanup, and
 *     the untap + haste riders land regardless of which branch ran.
 *  2. **A bigger Villain** — [com.wingedsheep.sdk.scripting.Duration.EndOfYourNextTurn]. The steal
 *     must survive *three* boundaries the short branch does not: the cleanup of the caster's own
 *     turn (a sorcery is always cast on your turn, so "your next turn" can never be this one), the
 *     opponent's whole turn in between, and the caster's next untap step — then end at the cleanup
 *     of that turn. That middle span is the load-bearing path: an expiry keyed to "the next cleanup"
 *     or to "any player's next turn" passes case 1 and fails here. The same case also pins the
 *     payoff: on that next turn the creature attacks, with the "until end of turn" haste long gone
 *     (CR 302.6 — it has been under your control continuously since the turn began).
 *  3. **A Villain that merely ties** the target's mana value — the boundary of "greater". Six is not
 *     greater than six, so the short branch runs.
 *  4. **An extra turn** — the extra turn *is* "your next turn", so the steal must end at that turn's
 *     cleanup and not survive into the one after. This is the case that distinguishes the turn-number
 *     **floor** from an exact turn number: a skipped turn (how a two-player extra turn is modeled)
 *     consumes no turn number, so `turnNumber > floor` would strand the effect while
 *     `turnNumber >= floor` ends it correctly. Case 2 cannot tell the two apart; this one can.
 *  5. **A mana-value-0 target and no Villain at all** — the empty-aggregate boundary. `MAX` over no
 *     Villains is 0, and 0 is not greater than 0, so the short branch runs.
 */
class EvilsThrallScenarioTest : ScenarioTestBase() {

    /**
     * Advance to the *next* turn's upkeep. The untap step holds no priority, so the first upkeep
     * reached after leaving this turn's main phase belongs to the next player; stepping through a
     * main phase in between keeps [TestGame.passUntilPhase] from stopping at the upkeep we are
     * already standing in.
     */
    private fun TestGame.advanceToNextUpkeep() {
        passUntilPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
        passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
    }

    private fun TestGame.manaValueOf(id: EntityId): Int =
        state.getEntity(id)?.get<CardComponent>()?.manaValue ?: error("no card component")

    init {
        context("Evil's Thrall") {

            test("no Villain: an ordinary Threaten — control reverts at end of the turn") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Evil's Thrall")
                    .withLandsOnBattlefield(1, "Mountain", 3)
                    .withCardOnBattlefield(2, "Grizzly Bears", tapped = true, summoningSickness = false)
                    .withCardInLibrary(1, "Mountain")
                    .withCardInLibrary(1, "Mountain")
                    .withCardInLibrary(2, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val cast = game.castSpell(1, "Evil's Thrall", bears)
                withClue("Casting Evil's Thrall should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                withClue("control of the Bears passes to player 1") {
                    game.state.projectedState.getController(bears) shouldBe game.player1Id
                }
                withClue("the stolen creature is untapped") {
                    (game.state.getEntity(bears)?.has<TappedComponent>() ?: false) shouldBe false
                }
                withClue("the stolen creature gains haste") {
                    game.state.projectedState.hasKeyword(bears, Keyword.HASTE) shouldBe true
                }

                game.advanceToNextUpkeep()
                withClue("first stop is player 2's upkeep") {
                    game.state.activePlayerId shouldBe game.player2Id
                }
                withClue("without a bigger Villain the steal ended at player 1's cleanup") {
                    game.state.projectedState.getController(bears) shouldBe game.player2Id
                }
            }

            test("a Villain with greater mana value: the steal lasts until the end of your next turn") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Evil's Thrall")
                    .withLandsOnBattlefield(1, "Mountain", 3)
                    // {5}{B} Human Villain — mana value 6, comfortably above the Bears' 2.
                    .withCardOnBattlefield(1, "The Masters of Evil", summoningSickness = false)
                    .withCardOnBattlefield(2, "Grizzly Bears", tapped = true, summoningSickness = false)
                    .withCardInLibrary(1, "Mountain")
                    .withCardInLibrary(1, "Mountain")
                    .withCardInLibrary(1, "Mountain")
                    .withCardInLibrary(2, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val villain = game.findPermanent("The Masters of Evil")!!
                withClue("the setup really is a bigger Villain than the target") {
                    (game.manaValueOf(villain) > game.manaValueOf(bears)) shouldBe true
                }

                val cast = game.castSpell(1, "Evil's Thrall", bears)
                withClue("Casting Evil's Thrall should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()
                withClue("control of the Bears passes to player 1") {
                    game.state.projectedState.getController(bears) shouldBe game.player1Id
                }

                // Boundary 1 — the caster's OWN cleanup. "Your next turn" is never this turn.
                game.advanceToNextUpkeep()
                withClue("first stop is player 2's upkeep") {
                    game.state.activePlayerId shouldBe game.player2Id
                }
                withClue("the steal survived the caster's own cleanup") {
                    game.state.projectedState.getController(bears) shouldBe game.player1Id
                }

                // Boundary 2 — the opponent's cleanup, and the caster's untap step after it.
                game.advanceToNextUpkeep()
                withClue("second stop is player 1's next upkeep") {
                    game.state.activePlayerId shouldBe game.player1Id
                }
                withClue("the steal survived the opponent's turn and player 1's untap step") {
                    game.state.projectedState.getController(bears) shouldBe game.player1Id
                }

                // The payoff (CR 302.6): the creature has been under your control continuously since
                // this turn began, so it can attack even though the haste grant has long expired.
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                withClue("the haste grant was 'until end of turn' and is gone by now") {
                    game.state.projectedState.hasKeyword(bears, Keyword.HASTE) shouldBe false
                }
                val attack = game.declareAttackers(mapOf("Grizzly Bears" to 2))
                withClue("the stolen creature can attack on your next turn: ${attack.error}") {
                    attack.error shouldBe null
                }

                // Still there at the end step of that turn — the duration runs through the whole turn.
                game.passUntilPhase(Phase.ENDING, Step.END)
                withClue("the steal is still live in the end step of player 1's next turn") {
                    game.state.projectedState.getController(bears) shouldBe game.player1Id
                }

                // Boundary 3 — the cleanup of the caster's next turn ends it. We are standing in
                // that turn's end step, so one hop to the next upkeep crosses exactly that cleanup.
                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                withClue("third stop is player 2's upkeep again") {
                    game.state.activePlayerId shouldBe game.player2Id
                }
                withClue("the steal ended at the cleanup of player 1's next turn") {
                    game.state.projectedState.getController(bears) shouldBe game.player2Id
                }
            }

            test("a Villain that only ties the target's mana value does not extend the steal") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Evil's Thrall")
                    .withLandsOnBattlefield(1, "Mountain", 3)
                    .withCardOnBattlefield(1, "The Masters of Evil", summoningSickness = false)
                    // {4}{R}{R} — mana value 6, exactly the Villain's.
                    .withCardOnBattlefield(2, "Shivan Dragon", summoningSickness = false)
                    .withCardInLibrary(1, "Mountain")
                    .withCardInLibrary(1, "Mountain")
                    .withCardInLibrary(2, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val dragon = game.findPermanent("Shivan Dragon")!!
                val villain = game.findPermanent("The Masters of Evil")!!
                withClue("the setup really is a tie, not a bigger Villain") {
                    game.manaValueOf(villain) shouldBe game.manaValueOf(dragon)
                }

                val cast = game.castSpell(1, "Evil's Thrall", dragon)
                withClue("Casting Evil's Thrall should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()
                withClue("control of the Dragon passes to player 1 for the turn") {
                    game.state.projectedState.getController(dragon) shouldBe game.player1Id
                }

                game.advanceToNextUpkeep()
                withClue("first stop is player 2's upkeep") {
                    game.state.activePlayerId shouldBe game.player2Id
                }
                withClue("an equal mana value is not a *greater* one — the short branch ran") {
                    game.state.projectedState.getController(dragon) shouldBe game.player2Id
                }
            }

            test("an extra turn is 'your next turn': the steal ends at the extra turn's cleanup") {
                // Time Warp models a two-player extra turn as the opponent skipping their next turn
                // (SkipNextTurnComponent), so player 1's own next turn immediately follows this one.
                // A skipped turn consumes no turn number, which is exactly why the expiry is keyed to
                // a floor (`turnNumber >= expiresAfterTurn`) rather than an exact turn: with a strict
                // `>` the effect would sail past the extra turn.
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Evil's Thrall")
                    .withCardInHand(1, "Time Warp")
                    .withLandsOnBattlefield(1, "Mountain", 4) // {2}{R}
                    .withLandsOnBattlefield(1, "Island", 6)   // {3}{U}{U}
                    .withCardOnBattlefield(1, "The Masters of Evil", summoningSickness = false)
                    .withCardOnBattlefield(2, "Grizzly Bears", summoningSickness = false)
                    .withCardInLibrary(1, "Mountain")
                    .withCardInLibrary(1, "Mountain")
                    .withCardInLibrary(1, "Mountain")
                    .withCardInLibrary(1, "Mountain")
                    .withCardInLibrary(2, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!

                val steal = game.castSpell(1, "Evil's Thrall", bears)
                withClue("Casting Evil's Thrall should succeed: ${steal.error}") {
                    steal.error shouldBe null
                }
                game.resolveStack()
                withClue("the bigger-Villain branch ran: control passes to player 1") {
                    game.state.projectedState.getController(bears) shouldBe game.player1Id
                }

                val warp = game.castSpellTargetingPlayer(1, "Time Warp", targetPlayerNumber = 1)
                withClue("Casting Time Warp should succeed: ${warp.error}") {
                    warp.error shouldBe null
                }
                game.resolveStack()
                withClue("player 2 is set to skip, i.e. player 1 takes the extra turn") {
                    game.state.getEntity(game.player2Id)?.has<SkipNextTurnComponent>() shouldBe true
                }

                // The extra turn comes straight after this one — no opponent turn in between.
                game.advanceToNextUpkeep()
                withClue("the next upkeep is player 1's own extra turn") {
                    game.state.activePlayerId shouldBe game.player1Id
                }
                withClue("the steal survived the caster's own cleanup") {
                    game.state.projectedState.getController(bears) shouldBe game.player1Id
                }

                game.passUntilPhase(Phase.ENDING, Step.END)
                withClue("the steal runs through the whole extra turn") {
                    game.state.projectedState.getController(bears) shouldBe game.player1Id
                }

                // The extra turn IS "your next turn" — it must end here, not one turn later.
                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                withClue("player 2's turn follows the extra turn") {
                    game.state.activePlayerId shouldBe game.player2Id
                }
                withClue("the steal ended at the cleanup of the extra turn, not the turn after") {
                    game.state.projectedState.getController(bears) shouldBe game.player2Id
                }
            }

            test("no Villain and a mana-value-0 target: zero is not greater than zero") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Evil's Thrall")
                    .withLandsOnBattlefield(1, "Mountain", 3)
                    // {0} artifact creature — mana value 0, matching MAX over an empty Villain set.
                    .withCardOnBattlefield(2, "Ornithopter", summoningSickness = false)
                    .withCardInLibrary(1, "Mountain")
                    .withCardInLibrary(1, "Mountain")
                    .withCardInLibrary(2, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val thopter = game.findPermanent("Ornithopter")!!
                withClue("the target really has mana value 0") {
                    game.manaValueOf(thopter) shouldBe 0
                }

                val cast = game.castSpell(1, "Evil's Thrall", thopter)
                withClue("Casting Evil's Thrall should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()
                withClue("control of the Ornithopter passes to player 1 for the turn") {
                    game.state.projectedState.getController(thopter) shouldBe game.player1Id
                }

                game.advanceToNextUpkeep()
                withClue("first stop is player 2's upkeep") {
                    game.state.activePlayerId shouldBe game.player2Id
                }
                withClue("an empty Villain aggregate is 0, which is not greater than 0") {
                    game.state.projectedState.getController(thopter) shouldBe game.player2Id
                }
            }
        }
    }
}

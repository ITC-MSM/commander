package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Red Guardian, Super-Soldier (MSH #34).
 *
 * "{2}{W} Legendary Creature — Human Soldier Villain 2/2. Flash.
 *  When Red Guardian enters, destroy target creature an opponent controls that dealt damage
 *  this turn."
 *
 * The target filter is the **active** voice — `StatePredicate.HasDealtDamage(thisTurnOnly = true)`,
 * reached through `TargetFilter.hasDealtDamageThisTurn()`. Two near-misses have to stay excluded and
 * each gets its own test:
 *
 *  - a creature that *was dealt* damage but dealt none (the passive `WasDealtDamageThisTurn`
 *    predicate, which the identically-shaped Rooftop Assassin / Stingblade Assassin use);
 *  - a creature that dealt damage on an **earlier** turn (the lifetime-scoped window of the same
 *    predicate, `thisTurnOnly = false`).
 *
 * Plus the controller half — your own creature dealing damage doesn't hand you a target — and the
 * happy path. With no legal target the trigger is removed from the stack (CR 603.3d) and the body
 * still enters.
 *
 * **How the negative tests discriminate.** "The victim survived" proves nothing on its own:
 * `resolveStack()` stops the moment a decision is raised, so a predicate that wrongly matched would
 * leave the trigger sitting on the stack waiting for a target and every survival assertion would
 * still hold. Each negative test therefore asserts on the *decision*, via [noTargetWasOffered]: the
 * stack drained and no target decision was ever raised. Break the predicate (make
 * `hasDealtDamage` return `true`) and all three go red on that assertion; the happy path, which needs
 * a decision, is the positive control for the same machinery.
 */
class RedGuardianSuperSoldierScenarioTest : ScenarioTestBase() {

    private val sorcererAbilityId by lazy {
        cardRegistry.getCard("Prodigal Sorcerer")!!.activatedAbilities[0].id
    }

    /**
     * The ETB trigger found no legal target, so it was removed from the stack on resolution
     * (CR 603.3d) without ever asking its controller to choose. This — not the intended victim's
     * survival — is what distinguishes a working predicate from a broken one: a wrongly-matching
     * predicate parks a target decision here instead.
     */
    private fun ScenarioTestBase.TestGame.noTargetWasOffered() {
        withClue("A target decision was raised, so something matched the filter: ${state.pendingDecision}") {
            state.pendingDecision shouldBe null
        }
        withClue("The trigger left the stack rather than waiting on a choice (CR 603.3d)") {
            state.stack.isEmpty() shouldBe true
        }
    }

    init {
        context("Red Guardian, Super-Soldier") {

            test("ETB destroys an opponent creature that dealt combat damage to a blocked attacker") {
                // P1 attacks with Grizzly Bears (2/2); P2's Hill Giant (3/3) blocks. The Giant deals
                // 3 (killing the Bears) and takes 2, so it survives having dealt damage this turn.
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Red Guardian, Super-Soldier")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardOnBattlefield(2, "Hill Giant", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Plains", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                val giant = game.findPermanent("Hill Giant")!!

                game.declareAttackers(mapOf("Grizzly Bears" to 2)).error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
                game.declareBlockers(mapOf("Hill Giant" to listOf("Grizzly Bears"))).error shouldBe null
                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)

                withClue("Hill Giant survives the 2 damage it was dealt") {
                    game.isOnBattlefield("Hill Giant") shouldBe true
                }
                withClue("Grizzly Bears died to the Giant's 3 damage") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                }

                game.castSpell(1, "Red Guardian, Super-Soldier").error shouldBe null
                game.resolveStack() // enters -> ETB asks for a target
                withClue("The trigger found a legal target and asked for it — the positive control " +
                    "for the `noTargetWasOffered()` assertion the negative tests make") {
                    (game.state.pendingDecision != null) shouldBe true
                }
                game.selectTargets(listOf(giant)).error shouldBe null
                game.resolveStack()

                withClue("Hill Giant dealt damage this turn, so the ETB destroys it") {
                    game.isOnBattlefield("Hill Giant") shouldBe false
                }
                withClue("Red Guardian is on the battlefield") {
                    game.isOnBattlefield("Red Guardian, Super-Soldier") shouldBe true
                }
            }

            test("a creature that was DEALT damage but dealt none is not a legal target") {
                // The passive/active discrimination. Shock the Giant: it now carries the
                // WasDealtDamageThisTurn marker that Rooftop Assassin reads, but it has dealt
                // nothing, so Red Guardian must not be able to kill it.
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Red Guardian, Super-Soldier")
                    .withCardInHand(1, "Shock")
                    .withCardOnBattlefield(2, "Hill Giant") // 3/3, survives Shock's 2 damage
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withLandsOnBattlefield(1, "Plains", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val giant = game.findPermanent("Hill Giant")!!
                game.castSpell(1, "Shock", giant).error shouldBe null
                game.resolveStack()
                withClue("Hill Giant survives the Shock") {
                    game.isOnBattlefield("Hill Giant") shouldBe true
                }

                game.castSpell(1, "Red Guardian, Super-Soldier").error shouldBe null
                withClue("Red Guardian is on the stack, so its ETB really does get a chance to fire") {
                    game.state.stack.isEmpty() shouldBe false
                }
                game.resolveStack()

                game.noTargetWasOffered()
                withClue("Being dealt damage is the wrong direction — the Giant is untouched") {
                    game.isOnBattlefield("Hill Giant") shouldBe true
                }
                withClue("Red Guardian still enters (CR 603.3d — the trigger just leaves the stack)") {
                    game.isOnBattlefield("Red Guardian, Super-Soldier") shouldBe true
                }
            }

            test("a creature that dealt damage on an EARLIER turn is not a legal target") {
                // The per-turn/lifetime discrimination. The Giant connects on P2's turn; by P1's
                // next turn the window has closed, even though the Giant has never left play.
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Red Guardian, Super-Soldier")
                    .withCardOnBattlefield(2, "Hill Giant", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Plains", 3)
                    // Both players need something to draw when the turn rolls over, or the game ends
                    // on an empty-library draw before Red Guardian can be cast.
                    .withCardInLibrary(1, "Plains")
                    .withCardInLibrary(1, "Plains")
                    .withCardInLibrary(2, "Mountain")
                    .withCardInLibrary(2, "Mountain")
                    .withActivePlayer(2)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                game.declareAttackers(mapOf("Hill Giant" to 1)).error shouldBe null
                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)
                withClue("Hill Giant connected for 3 on the opponent's turn") {
                    game.getLifeTotal(1) shouldBe 17
                }

                // Roll on to the next turn — P1's precombat main.
                game.passUntilPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                withClue("It is now a later turn") {
                    (game.state.turnNumber > 1) shouldBe true
                }

                game.castSpell(1, "Red Guardian, Super-Soldier").error shouldBe null
                withClue("Red Guardian is on the stack, so its ETB really does get a chance to fire") {
                    game.state.stack.isEmpty() shouldBe false
                }
                game.resolveStack()

                game.noTargetWasOffered()
                withClue("The Giant dealt damage last turn, not this one — no legal target") {
                    game.isOnBattlefield("Hill Giant") shouldBe true
                }
                withClue("Red Guardian still enters") {
                    game.isOnBattlefield("Red Guardian, Super-Soldier") shouldBe true
                }
            }

            test("your own creature dealing damage does not supply a target") {
                // P1's Bears connect unblocked; P2's Hill Giant dealt nothing. The only creature
                // that dealt damage this turn is P1's own, which "an opponent controls" excludes.
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Red Guardian, Super-Soldier")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardOnBattlefield(2, "Hill Giant")
                    .withLandsOnBattlefield(1, "Plains", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                game.declareAttackers(mapOf("Grizzly Bears" to 2)).error shouldBe null
                game.declareNoBlockers()
                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)
                withClue("Grizzly Bears connected for 2") {
                    game.getLifeTotal(2) shouldBe 18
                }

                game.castSpell(1, "Red Guardian, Super-Soldier").error shouldBe null
                withClue("Red Guardian is on the stack, so its ETB really does get a chance to fire") {
                    game.state.stack.isEmpty() shouldBe false
                }
                game.resolveStack()

                game.noTargetWasOffered()
                withClue("Hill Giant dealt no damage, so it is not a legal target") {
                    game.isOnBattlefield("Hill Giant") shouldBe true
                }
                withClue("Your own Grizzly Bears is not a legal target either") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe true
                }
                withClue("Red Guardian still enters") {
                    game.isOnBattlefield("Red Guardian, Super-Soldier") shouldBe true
                }
            }

            test("an opponent creature that dealt noncombat damage this turn is a legal target") {
                // Combat is not an axis of the predicate: a pinger's activated-ability damage
                // qualifies the same way a blocker's does. P1 passes priority so the opponent can
                // ping in P1's main phase; priority then returns to P1, the active player.
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Red Guardian, Super-Soldier")
                    .withCardOnBattlefield(1, "Hill Giant") // the ping's recipient, survives 1 damage
                    .withCardOnBattlefield(2, "Prodigal Sorcerer", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Plains", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val sorcerer = game.findPermanent("Prodigal Sorcerer")!!
                val giant = game.findPermanent("Hill Giant")!!

                game.passPriority().error shouldBe null
                val ping = game.execute(
                    ActivateAbility(
                        playerId = game.player2Id,
                        sourceId = sorcerer,
                        abilityId = sorcererAbilityId,
                        targets = listOf(ChosenTarget.Permanent(giant))
                    )
                )
                withClue("The opponent's Prodigal Sorcerer pings the Giant: ${ping.error}") {
                    ping.error shouldBe null
                }
                game.resolveStack()
                withClue("Hill Giant survives 1 damage") {
                    game.isOnBattlefield("Hill Giant") shouldBe true
                }

                // The opponent acted last, so hand priority back to the active player before casting.
                // A bounded loop that fails loudly rather than a fixed number of guessed passes: if
                // the priority shape ever changes, this errors here instead of quietly leaving the
                // wrong seat holding priority and failing somewhere less obvious.
                var passes = 0
                while (game.state.priorityPlayerId != game.player1Id) {
                    if (passes++ >= 4) error("Priority never came back to player 1 (still ${game.state.priorityPlayerId})")
                    game.passPriority()
                }

                game.castSpell(1, "Red Guardian, Super-Soldier").error shouldBe null
                game.resolveStack()
                game.selectTargets(listOf(sorcerer)).error shouldBe null
                game.resolveStack()

                withClue("The Sorcerer dealt noncombat damage this turn, so it dies") {
                    game.isOnBattlefield("Prodigal Sorcerer") shouldBe false
                }
            }
        }
    }
}

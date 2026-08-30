package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Curse of Hospitality (VOW #152) — {2}{R} Enchantment — Aura Curse.
 *
 * "Enchant player
 *  Creatures attacking enchanted player have trample.
 *  Whenever a creature deals combat damage to enchanted player, that player exiles the top card of
 *  their library. Until end of turn, that creature's controller may play that card and they may
 *  spend mana as though it were mana of any color to cast that spell."
 *
 * Both new pieces of vocabulary here scope a *player* by the source Aura's **attachment**, where
 * every pre-existing sibling scopes it by the ability's **controller** —
 * `StatePredicate.IsAttackingEnchantedPlayer` for the static and `RecipientFilter.EnchantedPlayer`
 * for the trigger. The self-curse test below is what separates the two readings: with the Curse on
 * its own controller, a controller-scoped predicate goes quiet and an attachment-scoped one fires.
 */
class CurseOfHospitalityScenarioTest : ScenarioTestBase() {

    init {
        context("Curse of Hospitality") {

            // Distinct card names per player, because the harness's `declareAttackers` resolves an
            // attacker by *name* and takes the first match on the battlefield — two same-named
            // creatures silently declare the wrong player's, and every negative assertion here
            // would then pass for the wrong reason.
            val p1Attacker = "Grizzly Bears"
            val p2Attacker = "Llanowar Elves"

            /**
             * A Curse controlled by player 1 and attached to [cursedPlayer], with one creature on
             * each player's battlefield and a Lightning Bolt on top of each library.
             *
             * The Aura is attached directly rather than cast: "enchant player" needs an
             * [AttachedToComponent] pointing at a *player* id, which the builder's
             * `withCardAttachedTo` (permanent hosts only) can't produce.
             */
            fun cursed(cursedPlayer: Int, activePlayer: Int = 1): TestGame {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Curse of Hospitality")
                    .withCardOnBattlefield(1, p1Attacker)
                    .withCardOnBattlefield(2, p2Attacker)
                    .withCardInLibrary(1, "Lightning Bolt")
                    .withCardInLibrary(2, "Lightning Bolt")
                    .withActivePlayer(activePlayer)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val curse = game.findPermanent("Curse of Hospitality")!!
                val victimId = if (cursedPlayer == 1) game.player1Id else game.player2Id
                game.state = game.state.updateEntity(curse) { it.with(AttachedToComponent(victimId)) }
                return game
            }

            fun attackerOf(game: TestGame, playerNumber: Int): EntityId =
                game.findPermanent(if (playerNumber == 1) p1Attacker else p2Attacker)!!

            context("Creatures attacking enchanted player have trample") {

                test("an attacker pointed at the cursed player has trample") {
                    val game = cursed(cursedPlayer = 2)
                    game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    game.declareAttackers(mapOf(p1Attacker to 2))
                    game.state.projectedState
                        .hasKeyword(attackerOf(game, 1), Keyword.TRAMPLE) shouldBe true
                }

                test("an attacker pointed at a player who isn't cursed has none") {
                    // Player 2 attacks player 1; the Curse is on player 2. The grant follows the
                    // *defender*, so nothing here qualifies.
                    val game = cursed(cursedPlayer = 2, activePlayer = 2)
                    game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    game.declareAttackers(mapOf(p2Attacker to 1))
                    withClue("the Curse is on player 2, and player 2 is not being attacked") {
                        game.state.projectedState
                            .hasKeyword(attackerOf(game, 2), Keyword.TRAMPLE) shouldBe false
                    }
                }

                test("an opponent's attacker gets trample when the Curse's own controller is cursed") {
                    // The discriminating case: player 1 controls the Curse and has put it on
                    // themself, so player 2's attacker gets trample. A controller-scoped reading
                    // gets this wrong — `attackingAnOpponent` asked by player 1 says false here.
                    val game = cursed(cursedPlayer = 1, activePlayer = 2)
                    game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    game.declareAttackers(mapOf(p2Attacker to 1))
                    game.state.projectedState
                        .hasKeyword(attackerOf(game, 2), Keyword.TRAMPLE) shouldBe true
                }

                test("an unattached Curse grants nothing") {
                    val game = scenario()
                        .withPlayers("Player1", "Player2")
                        .withCardOnBattlefield(1, "Curse of Hospitality")
                        .withCardOnBattlefield(1, p1Attacker)
                        .withCardInLibrary(1, "Lightning Bolt")
                        .withCardInLibrary(2, "Lightning Bolt")
                        .withActivePlayer(1)
                        .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                        .build()
                    game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    game.declareAttackers(mapOf(p1Attacker to 2))
                    withClue("no attachment target means no enchanted player — fail closed") {
                        game.state.projectedState
                            .hasKeyword(attackerOf(game, 1), Keyword.TRAMPLE) shouldBe false
                    }
                }
            }

            context("Whenever a creature deals combat damage to enchanted player") {

                test("the cursed player exiles the top card of their library") {
                    val game = cursed(cursedPlayer = 2)
                    val libraryBefore = game.librarySize(2)
                    game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    game.declareAttackers(mapOf(p1Attacker to 2))
                    game.passUntilPhase(Phase.COMBAT, Step.END_COMBAT)
                    game.resolveStack()

                    withClue("the exile comes off the *cursed* player's library, not the attacker's") {
                        game.isInExile(2, "Lightning Bolt") shouldBe true
                        game.librarySize(2) shouldBe libraryBefore - 1
                        game.isInExile(1, "Lightning Bolt") shouldBe false
                    }
                }

                test("combat damage to a player who isn't cursed does nothing") {
                    // Player 2 attacks player 1, who carries no Curse.
                    val game = cursed(cursedPlayer = 2, activePlayer = 2)
                    game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    game.declareAttackers(mapOf(p2Attacker to 1))
                    game.passUntilPhase(Phase.COMBAT, Step.END_COMBAT)
                    game.resolveStack()

                    withClue("the recipient filter is the attachment, not 'any opponent'") {
                        game.isInExile(1, "Lightning Bolt") shouldBe false
                        game.isInExile(2, "Lightning Bolt") shouldBe false
                    }
                }

                test("the damaging creature's controller gets the play permission, not the Curse's") {
                    // Player 1 controls the Curse but puts it on themself; player 2 connects, so
                    // *player 2* may play the card exiled off player 1's library. The permission
                    // rides ControllerOfTriggeringEntity, which the source filter on the trigger is
                    // what binds to the damaging creature.
                    val game = cursed(cursedPlayer = 1, activePlayer = 2)
                    game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    game.declareAttackers(mapOf(p2Attacker to 1))
                    game.passUntilPhase(Phase.COMBAT, Step.END_COMBAT)
                    game.resolveStack()

                    withClue("the exile is off the enchanted player's library — player 1's") {
                        game.isInExile(1, "Lightning Bolt") shouldBe true
                    }
                    withClue("player 2 dealt the damage, so player 2 may cast it") {
                        game.getLegalActions(2).any {
                            it.actionType == "CastSpell" && it.description.contains("Lightning Bolt")
                        } shouldBe true
                    }
                }
            }
        }
    }
}

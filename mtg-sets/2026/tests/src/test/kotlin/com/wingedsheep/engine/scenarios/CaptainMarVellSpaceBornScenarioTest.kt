package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Captain Mar-Vell, Space-Born — "Cosmic Awareness — As long as an opponent has cast a spell this
 * turn, you may cast spells as though they had flash."
 *
 * A `GrantFlashToSpellType(Any, controllerOnly = true)` behind an "as long as" gate, so the whole
 * card rests on `FlashTypeGrants` unwrapping the `ConditionalStaticAbility` the DSL produces. The
 * failure mode worth guarding is the one this card was written to close: a gated grant that is
 * *silently inert* — flash never appearing at all — which would look identical to "the opponent
 * hasn't cast anything yet" if the closed-gate case were the only one tested. So every test here
 * asserts through `getLegalActions` (what the player is actually offered) and the positive case
 * additionally casts for real, exercising the second, authoritative read site in `CastZoneResolver`.
 *
 * Board shape: player 2's Lightning Bolt both opens the gate *and* leaves the stack non-empty, so
 * sorcery-speed casting is illegal from that point on and any creature cast still offered to player
 * 1 can only be a flash cast.
 */
class CaptainMarVellSpaceBornScenarioTest : ScenarioTestBase() {

    init {
        context("Captain Mar-Vell, Space-Born — Cosmic Awareness") {

            /** Whether [playerNumber] is currently offered a cast of Grizzly Bears from hand. */
            fun bearsOffered(game: ScenarioTestBase.TestGame, playerNumber: Int) =
                game.getLegalActions(playerNumber).any { info ->
                    (info.action as? CastSpell)?.let { cast ->
                        game.state.getEntity(cast.cardId)?.get<CardComponent>()?.name == "Grizzly Bears"
                    } == true
                }

            test("no opponent spell this turn — the grant is closed and nothing gains flash") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Captain Mar-Vell, Space-Born", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Forest", 4)
                    .withCardInHand(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .withPriorityPlayer(1)
                    // The end step is sorcery-speed-illegal, so any offered creature cast is flash.
                    .inPhase(Phase.ENDING, Step.END)
                    .build()

                withClue("the opponent has cast nothing this turn, so Cosmic Awareness is off") {
                    bearsOffered(game, 1) shouldBe false
                }
                withClue("and the cast handler rejects it too") {
                    game.castSpell(1, "Grizzly Bears").error shouldNotBe null
                }
            }

            test("after an opponent casts a spell, you may cast at instant speed") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Captain Mar-Vell, Space-Born", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Forest", 4)
                    .withCardInHand(1, "Grizzly Bears")
                    .withLandsOnBattlefield(2, "Mountain", 1)
                    .withCardInHand(2, "Lightning Bolt")
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .withPriorityPlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpellTargetingPlayer(2, "Lightning Bolt", 1).error shouldBe null
                // Player 2 keeps priority after casting; pass it back so player 1 can act with the
                // Bolt still on the stack.
                game.passPriority()
                game.state.stack.isEmpty() shouldBe false
                game.state.priorityPlayerId shouldBe game.player1Id

                withClue("an opponent has cast a spell this turn, so every spell you cast has flash") {
                    bearsOffered(game, 1) shouldBe true
                }
                withClue("and the authoritative cast-time check agrees") {
                    game.castSpell(1, "Grizzly Bears").error shouldBe null
                }
                game.resolveStack()
                game.isOnBattlefield("Grizzly Bears") shouldBe true
            }

            test("control: the same board without Mar-Vell offers no instant-speed creature") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withLandsOnBattlefield(1, "Forest", 4)
                    .withCardInHand(1, "Grizzly Bears")
                    .withLandsOnBattlefield(2, "Mountain", 1)
                    .withCardInHand(2, "Lightning Bolt")
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .withPriorityPlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpellTargetingPlayer(2, "Lightning Bolt", 1).error shouldBe null
                game.passPriority()

                withClue("the opponent's spell alone grants nothing — the flash came from Mar-Vell") {
                    bearsOffered(game, 1) shouldBe false
                }
            }

            test("your own spell does not open the gate — it has to be an opponent's") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Captain Mar-Vell, Space-Born", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Forest", 4)
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withCardInHand(1, "Lightning Bolt")
                    .withCardInHand(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .withPriorityPlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpellTargetingPlayer(1, "Lightning Bolt", 2).error shouldBe null
                // Player 1 keeps priority with their *own* Bolt on the stack, so sorcery-speed
                // casting is illegal from here — a Grizzly Bears offer could only be flash.
                game.state.stack.isEmpty() shouldBe false
                game.state.priorityPlayerId shouldBe game.player1Id

                withClue("the count is over opponents only, so your own spell leaves the gate shut") {
                    bearsOffered(game, 1) shouldBe false
                }
            }

            test("the permission expires at the turn boundary — 'this turn' is per turn") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Captain Mar-Vell, Space-Born", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Forest", 4)
                    .withCardInHand(1, "Grizzly Bears")
                    .withLandsOnBattlefield(2, "Mountain", 1)
                    .withCardInHand(2, "Lightning Bolt")
                    // Enough to survive the draw steps of the extra turn this test walks through.
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .withPriorityPlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpellTargetingPlayer(2, "Lightning Bolt", 1).error shouldBe null

                // Player 1's own end step: sorcery speed is illegal in the ending phase, and the
                // opponent's Bolt was cast this turn, so the offer here is the grant working.
                game.passUntilPhase(Phase.ENDING, Step.END)
                game.state.priorityPlayerId shouldBe game.player1Id
                withClue("still the turn the opponent cast their Bolt in") {
                    bearsOffered(game, 1) shouldBe true
                }

                // Into player 2's turn and on to its end step, where player 1 holds priority again
                // in exactly the same sorcery-illegal window — but on a turn where no opponent has
                // cast anything.
                game.passUntilPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                game.state.activePlayerId shouldBe game.player2Id
                game.passUntilPhase(Phase.ENDING, Step.END)
                game.passPriority()
                game.state.priorityPlayerId shouldBe game.player1Id

                withClue("new turn, no opponent spell yet — the cast history is per turn") {
                    bearsOffered(game, 1) shouldBe false
                }
            }

            test("the permission is the controller's only — the opponent who opened it gains nothing") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Captain Mar-Vell, Space-Born", summoningSickness = false)
                    .withLandsOnBattlefield(2, "Mountain", 1)
                    .withLandsOnBattlefield(2, "Forest", 4)
                    .withCardInHand(2, "Lightning Bolt")
                    .withCardInHand(2, "Grizzly Bears")
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .withPriorityPlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpellTargetingPlayer(2, "Lightning Bolt", 1).error shouldBe null
                // Player 2 still holds priority with their own Bolt on the stack, and has four
                // untapped Forests, so only `controllerOnly` can be what withholds the cast.
                game.state.priorityPlayerId shouldBe game.player2Id

                withClue("Cosmic Awareness says 'you may cast', and player 2 is not its controller") {
                    bearsOffered(game, 2) shouldBe false
                }
            }
        }
    }
}

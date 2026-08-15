package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Triple Triad (FIN).
 *
 * At the beginning of your upkeep, each player exiles the top card of their library. Until end of
 * turn, you may play (free) the card you own exiled this way and each other card exiled this way
 * with lesser mana value than it. Exercises the relative-mana-value impulse: your card is always
 * playable; an opponent's exiled card is playable only when its mana value is strictly less than
 * the mana value of the card you exiled.
 *
 * "Playable" is asserted by enumerating a free CastSpell action for player 1 at their precombat
 * main phase (sorcery-speed window). Mons's Goblin Raiders = {R} (MV 1); Wind Drake = {2}{U} (MV 3).
 */
class TripleTriadScenarioTest : ScenarioTestBase() {

    init {
        context("Triple Triad") {

            test("an opponent's card with lesser mana value than yours becomes playable") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Triple Triad")
                    .withCardInLibrary(1, "Wind Drake")             // yours: MV 3
                    .withCardInLibrary(2, "Mons's Goblin Raiders")  // opponent: MV 1 (< 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.BEGINNING, Step.UNTAP)
                    .build()

                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                game.resolveStack()

                withClue("Each player exiles the top card of their library on your upkeep") {
                    game.isInExile(1, "Wind Drake") shouldBe true
                    game.isInExile(2, "Mons's Goblin Raiders") shouldBe true
                }

                game.passUntilPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                game.resolveStack()

                val playable = game.getLegalActions(1)
                    .mapNotNull { it.action as? CastSpell }
                    .mapNotNull { game.state.getEntity(it.cardId)?.get<CardComponent>()?.name }
                    .toSet()

                withClue("Your own exiled card is always playable") {
                    playable.contains("Wind Drake") shouldBe true
                }
                withClue("The opponent's exiled card has lesser mana value, so it is playable") {
                    playable.contains("Mons's Goblin Raiders") shouldBe true
                }
            }

            test("an opponent's card with greater-or-equal mana value than yours is NOT playable") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Triple Triad")
                    .withCardInLibrary(1, "Mons's Goblin Raiders") // yours: MV 1
                    .withCardInLibrary(2, "Wind Drake")            // opponent: MV 3 (NOT < 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.BEGINNING, Step.UNTAP)
                    .build()

                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                game.resolveStack()

                withClue("Both top cards are still exiled this way") {
                    game.isInExile(1, "Mons's Goblin Raiders") shouldBe true
                    game.isInExile(2, "Wind Drake") shouldBe true
                }

                game.passUntilPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                game.resolveStack()

                val playable = game.getLegalActions(1)
                    .mapNotNull { it.action as? CastSpell }
                    .mapNotNull { game.state.getEntity(it.cardId)?.get<CardComponent>()?.name }
                    .toSet()

                withClue("Your own exiled card is always playable") {
                    playable.contains("Mons's Goblin Raiders") shouldBe true
                }
                withClue("The opponent's card is not of lesser mana value, so it stays unplayable") {
                    playable.contains("Wind Drake") shouldBe false
                }
            }
        }
    }
}

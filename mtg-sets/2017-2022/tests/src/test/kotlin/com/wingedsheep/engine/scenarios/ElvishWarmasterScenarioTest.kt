package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Elvish Warmaster (KHM #167) — {1}{G} Creature — Elf Warrior, 2/2.
 *
 *   Whenever one or more other Elves you control enter, create a 1/1 green Elf Warrior
 *   creature token. This ability triggers only once each turn.
 *   {5}{G}{G}: Elves you control get +2/+2 and gain deathtouch until end of turn.
 *
 * Two things about the trigger are easy to get wrong and are what these tests pin:
 *
 *  - **"one or more … enter" is a batch, not a per-permanent trigger.** A single spell that puts
 *    two Elves onto the battlefield makes *one* token, not two. That is the batching
 *    `PermanentsEnteredEvent`, not a per-entry `ZoneChangeEvent`.
 *  - **"triggers only once each turn" is the ability's own cap**, enforced by the engine per
 *    `(sourceId, abilityId)` and reset at end of turn — a second, separate Elf entering later in
 *    the same turn makes no token.
 */
class ElvishWarmasterScenarioTest : ScenarioTestBase() {

    init {
        context("the batch trigger") {

            test("one Elf entering makes exactly one token") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Elvish Warmaster")
                    .withCardInHand(1, "Llanowar Elves")
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val before = game.findAllPermanents("Elf Warrior Token").size

                val cast = game.castSpell(1, "Llanowar Elves")
                withClue("Casting Llanowar Elves should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                withClue("The Warmaster's trigger made one 1/1 Elf Warrior token") {
                    game.findAllPermanents("Elf Warrior Token").size shouldBe before + 1
                }
            }

            test("the Warmaster's own entry does not trigger it") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Elvish Warmaster")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cast = game.castSpell(1, "Elvish Warmaster")
                withClue("Casting Elvish Warmaster should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                withClue("\"other Elves\" excludes the Warmaster itself, so no token was made") {
                    game.isOnBattlefield("Elvish Warmaster") shouldBe true
                    game.findAllPermanents("Elf Warrior Token").size shouldBe 0
                }
            }

            test("a second Elf later the same turn makes no further token") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Elvish Warmaster")
                    .withCardInHand(1, "Llanowar Elves")
                    .withCardsInHand(1, "Elvish Mystic", 1)
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Llanowar Elves")
                game.resolveStack()

                val afterFirst = game.findAllPermanents("Elf Warrior Token").size
                withClue("The first Elf triggered the Warmaster") { afterFirst shouldBe 1 }

                game.castSpell(1, "Elvish Mystic")
                game.resolveStack()

                withClue("\"only once each turn\" caps the ability, so the second Elf makes nothing") {
                    game.findAllPermanents("Elf Warrior Token").size shouldBe afterFirst
                }
            }
        }
    }
}

package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for Faerie Trickery (LRW #62, {1}{U}{U}, Kindred Instant — Faerie).
 *
 *   Counter target non-Faerie spell. If that spell is countered this way, exile it instead of
 *   putting it into its owner's graveyard.
 *
 * Two things are worth proving. The exile rider — a countered spell must land in exile, not in its
 * owner's graveyard — and the "non-Faerie" restriction, which lives in the *target filter* rather
 * than in a resolution-time check, so a Faerie spell is simply not a legal target. A changeling
 * spell is every creature type, Faerie included, so it is off limits too.
 */
class FaerieTrickeryScenarioTest : ScenarioTestBase() {

    private fun trickery(opponentSpell: String) = scenario()
        .withPlayers("Alice", "Bob")
        .withCardInHand(1, "Faerie Trickery")
        .withLandsOnBattlefield(1, "Island", 3)
        .withCardInHand(2, opponentSpell)
        .withLandsOnBattlefield(2, "Swamp", 2)
        .withLandsOnBattlefield(2, "Forest", 2)
        .withLandsOnBattlefield(2, "Plains", 3)
        .withCardInLibrary(1, "Island")
        .withCardInLibrary(2, "Swamp")
        .withActivePlayer(2)
        .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
        .build()

    init {
        context("Faerie Trickery") {

            test("counters a non-Faerie spell and exiles it instead of binning it") {
                val game = trickery("Grizzly Bears")
                game.castSpell(2, "Grizzly Bears").error shouldBe null
                game.passPriority()
                game.castSpellTargetingStackSpell(1, "Faerie Trickery", "Grizzly Bears").error shouldBe null
                game.resolveStack()

                game.isOnBattlefield("Grizzly Bears") shouldBe false
                withClue("The rider replaces the graveyard destination with exile") {
                    game.isInExile(2, "Grizzly Bears") shouldBe true
                    game.isInGraveyard(2, "Grizzly Bears") shouldBe false
                }
            }

            test("a Faerie spell is not a legal target") {
                val game = trickery("Nightshade Stinger") // {B} Creature — Faerie Rogue
                game.castSpell(2, "Nightshade Stinger").error shouldBe null
                game.passPriority()

                withClue("\"non-Faerie\" is a targeting restriction, so the cast must be rejected") {
                    game.castSpellTargetingStackSpell(1, "Faerie Trickery", "Nightshade Stinger")
                        .error shouldNotBe null
                }
            }

            test("a changeling spell counts as a Faerie and is off limits") {
                val game = trickery("Avian Changeling") // {2}{W} Creature — Shapeshifter, changeling
                game.castSpell(2, "Avian Changeling").error shouldBe null
                game.passPriority()

                withClue("Changeling makes the spell every creature type, Faerie included") {
                    game.castSpellTargetingStackSpell(1, "Faerie Trickery", "Avian Changeling")
                        .error shouldNotBe null
                }
            }
        }
    }
}

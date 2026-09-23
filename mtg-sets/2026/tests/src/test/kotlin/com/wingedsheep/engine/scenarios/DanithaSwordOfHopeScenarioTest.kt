package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Danitha, Sword of Hope (FRA #196) — "Whenever you cast an Equipment spell or a spell that targets
 * a creature you control, draw a card. This ability triggers only once each turn."
 *
 * Pins `SpellCastPredicate.AnyOf` over `SpellMatches` and `TargetsMatching`: either half fires it,
 * a spell matching neither doesn't, and the once-each-turn limit spans both halves.
 */
class DanithaSwordOfHopeScenarioTest : ScenarioTestBase() {
    init {
        fun game() = scenario().withPlayers()
            .withCardOnBattlefield(1, "Danitha, Sword of Hope")
            .withCardOnBattlefield(2, "Grizzly Bears")
            .withCardInHand(1, "Bonesplitter")
            .withCardInHand(1, "Giant Growth")
            .withCardInHand(1, "Lightning Bolt")
            .withLandsOnBattlefield(1, "Plains", 1)
            .withLandsOnBattlefield(1, "Forest", 1)
            .withLandsOnBattlefield(1, "Mountain", 1)
            .withCardInLibrary(1, "Plains")
            .withCardInLibrary(1, "Plains")
            .withCardInLibrary(2, "Plains")
            .withActivePlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            .build()

        test("a spell targeting a creature you control draws a card") {
            val game = game()
            val danitha = game.findPermanent("Danitha, Sword of Hope")!!
            val hand = game.handSize(1)
            game.castSpell(1, "Giant Growth", danitha).error shouldBe null
            game.resolveStack()
            game.handSize(1) shouldBe hand // Giant Growth left, a card came in
        }

        test("an Equipment spell draws a card, and only once each turn") {
            val game = game()
            val danitha = game.findPermanent("Danitha, Sword of Hope")!!
            val hand = game.handSize(1)
            game.castSpell(1, "Bonesplitter").error shouldBe null
            game.resolveStack()
            game.handSize(1) shouldBe hand

            withClue("the second qualifying spell this turn draws nothing") {
                game.castSpell(1, "Giant Growth", danitha).error shouldBe null
                game.resolveStack()
                game.handSize(1) shouldBe hand - 1
            }
        }

        test("a spell targeting an opponent's creature draws nothing") {
            val game = game()
            val bears = game.findPermanent("Grizzly Bears")!!
            val hand = game.handSize(1)
            game.castSpell(1, "Lightning Bolt", bears).error shouldBe null
            game.resolveStack()
            game.handSize(1) shouldBe hand - 1
        }
    }
}

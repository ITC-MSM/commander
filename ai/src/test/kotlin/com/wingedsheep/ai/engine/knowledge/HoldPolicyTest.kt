package com.wingedsheep.ai.engine.knowledge

import com.wingedsheep.engine.support.ScenarioTestBase
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * The timing half of Phase 6, at the two windows that decide it.
 *
 * `PuzzleSuiteTest` measures the outcome (instants 3/6 → 5/6); this pins the *reason*, so a change
 * that quietly stops classifying Giant Growth as a combat trick fails here rather than showing up
 * as a mysterious puzzle regression.
 */
class HoldPolicyTest : ScenarioTestBase() {

    init {
        test("a combat trick outside combat has no window at all") {
            val game = scenario()
                .withPlayers()
                .withLandsOnBattlefield(1, "Forest", 1)
                .withCardInHand(1, "Giant Growth")
                .withCardOnBattlefield(1, "Grizzly Bears")
                .build()
            val policy = HoldPolicy(IntentCatalog.of(cardRegistry))

            policy.verdictFor(game.state, game.player1Id, "Giant Growth")
                .shouldBeInstanceOf<TimingVerdict.NoWindow>()
        }

        test("instant removal is neutral in our own main phase, not penalized") {
            val game = scenario()
                .withPlayers()
                .withLandsOnBattlefield(1, "Plains", 2)
                .withCardInHand(1, "Disenchant")
                .withCardOnBattlefield(2, "Icy Manipulator")
                .build()
            val policy = HoldPolicy(IntentCatalog.of(cardRegistry))

            // Deliberately *not* a penalty — see `HoldPolicy` for why the plan's proposed
            // "hold it, this is the wrong time" constant was built, measured and removed.
            policy.verdictFor(game.state, game.player1Id, "Disenchant") shouldBe TimingVerdict.Neutral
        }

        test("a sorcery is never judged on timing") {
            val game = scenario()
                .withPlayers()
                .withLandsOnBattlefield(1, "Plains", 4)
                .withCardInHand(1, "Wrath of God")
                .build()
            val policy = HoldPolicy(IntentCatalog.of(cardRegistry))

            policy.verdictFor(game.state, game.player1Id, "Wrath of God") shouldBe TimingVerdict.Neutral
        }

        test("a policy with no catalog says nothing about anything") {
            val game = scenario()
                .withPlayers()
                .withLandsOnBattlefield(1, "Forest", 1)
                .withCardInHand(1, "Giant Growth")
                .build()
            val policy = HoldPolicy(IntentCatalog.NONE)

            policy.isEnabled shouldBe false
            policy.verdictFor(game.state, game.player1Id, "Giant Growth") shouldBe TimingVerdict.Neutral
        }
    }
}

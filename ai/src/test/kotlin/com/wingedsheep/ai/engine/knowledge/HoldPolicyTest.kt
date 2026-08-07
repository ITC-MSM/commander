package com.wingedsheep.ai.engine.knowledge

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.TargetsResponse
import com.wingedsheep.engine.support.ScenarioTestBase
import io.kotest.matchers.collections.shouldHaveSize
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

        test("a trick answers damage the creature is dying to") {
            val game = scenario()
                .withPlayers()
                .withActivePlayer(2)
                .withLandsOnBattlefield(2, "Mountain", 1)
                .withCardInHand(2, "Lightning Bolt")
                .withLandsOnBattlefield(1, "Forest", 1)
                .withCardInHand(1, "Giant Growth")
                .withCardOnBattlefield(1, "Grizzly Bears")
                .build()
            game.castSpell(2, "Lightning Bolt", game.findPermanent("Grizzly Bears"))
            val policy = HoldPolicy(IntentCatalog.of(cardRegistry))

            policy.verdictFor(game.state, game.player1Id, "Giant Growth")
                .shouldBeInstanceOf<TimingVerdict.Adjust>()
        }

        test("a trick answers nothing on a creature that already survives the damage") {
            // Three damage, four toughness: the deadline is real and the card is irrelevant to it.
            val game = scenario()
                .withPlayers()
                .withActivePlayer(2)
                .withLandsOnBattlefield(2, "Mountain", 1)
                .withCardInHand(2, "Lightning Bolt")
                .withLandsOnBattlefield(1, "Forest", 1)
                .withCardInHand(1, "Giant Growth")
                .withCardOnBattlefield(1, "Craw Wurm")
                .build()
            game.castSpell(2, "Lightning Bolt", game.findPermanent("Craw Wurm"))
            val policy = HoldPolicy(IntentCatalog.of(cardRegistry))

            policy.verdictFor(game.state, game.player1Id, "Giant Growth")
                .shouldBeInstanceOf<TimingVerdict.NoWindow>()
        }

        test("no amount of toughness answers destruction") {
            val game = scenario()
                .withPlayers()
                .withActivePlayer(2)
                .withLandsOnBattlefield(2, "Swamp", 3)
                .withCardInHand(2, "Murder")
                .withLandsOnBattlefield(1, "Forest", 1)
                .withCardInHand(1, "Giant Growth")
                .withCardOnBattlefield(1, "Grizzly Bears")
                .build()
            game.castSpell(2, "Murder", game.findPermanent("Grizzly Bears"))
            val policy = HoldPolicy(IntentCatalog.of(cardRegistry))

            policy.verdictFor(game.state, game.player1Id, "Giant Growth")
                .shouldBeInstanceOf<TimingVerdict.NoWindow>()
        }

        test("a spell on the stack that threatens nothing of ours is not a window") {
            val game = scenario()
                .withPlayers()
                .withActivePlayer(2)
                .withLandsOnBattlefield(2, "Forest", 4)
                .withCardInHand(2, "Craw Wurm")
                .withLandsOnBattlefield(1, "Forest", 1)
                .withCardInHand(1, "Giant Growth")
                .withCardOnBattlefield(1, "Grizzly Bears")
                .build()
            game.castSpell(2, "Craw Wurm")
            val policy = HoldPolicy(IntentCatalog.of(cardRegistry))

            policy.verdictFor(game.state, game.player1Id, "Giant Growth")
                .shouldBeInstanceOf<TimingVerdict.NoWindow>()
        }

        test("a triggered ability that threatens nothing of ours is not a window") {
            // The mistake this pins: a trigger carries no `CardComponent`, so before the policy
            // could read an ability it fell through as "unreadable" and bought the trick the full
            // response bonus. Any ETB on the opponent's main phase was enough to make the AI dump
            // a pump that provably wears off before combat.
            val game = scenario()
                .withPlayers()
                .withActivePlayer(2)
                .withLandsOnBattlefield(2, "Forest", 2)
                .withCardInHand(2, "Elvish Visionary")
                .withLandsOnBattlefield(1, "Forest", 1)
                .withCardInHand(1, "Giant Growth")
                .withCardOnBattlefield(1, "Grizzly Bears")
                .build()
            game.castSpell(2, "Elvish Visionary")
            // One pass each resolves the creature and leaves its draw trigger on the stack.
            game.passPriority()
            game.passPriority()
            game.state.stack.shouldHaveSize(1)
            val policy = HoldPolicy(IntentCatalog.of(cardRegistry))

            policy.verdictFor(game.state, game.player1Id, "Giant Growth")
                .shouldBeInstanceOf<TimingVerdict.NoWindow>()
        }

        test("a triggered ability that is killing our creature is a window") {
            // The other half: reading abilities must not turn into a blanket veto on them.
            val game = scenario()
                .withPlayers()
                .withActivePlayer(2)
                .withLandsOnBattlefield(2, "Mountain", 4)
                .withCardInHand(2, "Flametongue Kavu")
                .withLandsOnBattlefield(1, "Forest", 1)
                .withCardInHand(1, "Giant Growth")
                .withCardOnBattlefield(1, "Grizzly Bears")
                .build()
            game.castSpell(2, "Flametongue Kavu")
            game.resolveStack()
            val targeting = game.state.pendingDecision as? ChooseTargetsDecision
                ?: error("expected the ETB to ask for a target; got ${game.state.pendingDecision}")
            game.submitDecision(
                TargetsResponse(targeting.id, mapOf(0 to listOf(game.findPermanent("Grizzly Bears")!!)))
            )
            val policy = HoldPolicy(IntentCatalog.of(cardRegistry))

            // Four damage on a 2/2: dying, and +3/+3 carries it out of range.
            policy.verdictFor(game.state, game.player1Id, "Giant Growth")
                .shouldBeInstanceOf<TimingVerdict.Adjust>()
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

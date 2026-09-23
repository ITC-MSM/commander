package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Craterclaw Colossus — "When this creature enters, creatures you control gain trample and get
 * +X/+0 until end of turn, where X is the number of artifacts you control."
 *
 * The Colossus is itself an artifact, so it counts toward X, and it is one of the creatures
 * pumped. The opponent's creatures are untouched.
 */
class CraterclawColossusScenarioTest : ScenarioTestBase() {
    init {
        test("creatures you control get +X/+0 and trample, X counting the Colossus itself") {
            val game = scenario().withPlayers()
                .withCardInHand(1, "Craterclaw Colossus")
                .withLandsOnBattlefield(1, "Mountain", 7)
                .withCardOnBattlefield(1, "Ornithopter")
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withCardOnBattlefield(2, "Grizzly Bears")
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            game.castSpell(1, "Craterclaw Colossus").error shouldBe null
            game.resolveStack()

            val projected = game.state.projectedState
            val colossus = game.findPermanent("Craterclaw Colossus")!!
            val (ours, theirs) = game.findAllPermanents("Grizzly Bears")
                .partition { projected.getController(it) == game.player1Id }

            withClue("two artifacts (Ornithopter + Colossus): Bears is 4/2 with trample") {
                projected.getPower(ours.single()) shouldBe 4
                projected.getToughness(ours.single()) shouldBe 2
                projected.hasKeyword(ours.single(), Keyword.TRAMPLE) shouldBe true
            }
            withClue("the Colossus pumps itself: 7/5 with trample") {
                projected.getPower(colossus) shouldBe 7
                projected.hasKeyword(colossus, Keyword.TRAMPLE) shouldBe true
            }
            withClue("the opponent's creature is not in the group") {
                projected.getPower(theirs.single()) shouldBe 2
                projected.hasKeyword(theirs.single(), Keyword.TRAMPLE) shouldBe false
            }
        }
    }
}

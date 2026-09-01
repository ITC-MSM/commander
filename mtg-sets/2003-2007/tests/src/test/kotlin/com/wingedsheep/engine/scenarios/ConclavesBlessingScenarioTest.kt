package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Conclave's Blessing (RAV #11) — {3}{W} Enchantment — Aura, convoke.
 *
 * "Enchant creature. Enchanted creature gets +0/+2 for each other creature you control."
 *
 * "Other" is measured against the enchanted creature, which is what `excludeSelf` means on an
 * aggregate evaluated for a *granted* effect — self there is the affected entity, not the Aura.
 * The off-by-one that reading is guarding against is exactly one point of toughness, so the two
 * counting tests below pin the number rather than the direction. "You" stays the Aura's
 * controller, hence the stolen-host case.
 */
class ConclavesBlessingScenarioTest : ScenarioTestBase() {

    init {
        context("Conclave's Blessing") {

            test("alone on the battlefield the host gets nothing — it is not its own \"other\"") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardAttachedTo(1, "Conclave's Blessing", "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears").shouldNotBeNull()

                withClue("zero other creatures means +0/+0") {
                    game.state.projectedState.getPower(bears) shouldBe 2
                    game.state.projectedState.getToughness(bears) shouldBe 2
                }
            }

            test("+0/+2 for each other creature its controller controls") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardOnBattlefield(1, "Centaur Courser")
                    .withCardOnBattlefield(1, "Hill Giant")
                    .withCardAttachedTo(1, "Conclave's Blessing", "Grizzly Bears")
                    // Bob's creature must not be counted.
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanents("Grizzly Bears")
                    .first { game.state.projectedState.getController(it) == game.player1Id }

                withClue("two other creatures Alice controls, so +0/+4") {
                    game.state.projectedState.getPower(bears) shouldBe 2
                    game.state.projectedState.getToughness(bears) shouldBe 6
                }
            }

            test("on an opponent's creature it counts the Aura controller's creatures") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    // Bob's creature carries Alice's Aura.
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withCardAttachedTo(1, "Conclave's Blessing", "Grizzly Bears")
                    .withCardOnBattlefield(1, "Centaur Courser")
                    .withCardOnBattlefield(1, "Hill Giant")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears").shouldNotBeNull()

                withClue("\"you\" is Alice: her two creatures count, and the host is not among them") {
                    game.state.projectedState.getToughness(bears) shouldBe 6
                }
            }
        }
    }
}

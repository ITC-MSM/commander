package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Song of Stupefaction (LCI #77) — {1}{U} Enchantment — Aura.
 *
 *   Enchant creature or Vehicle
 *   When this Aura enters, you may mill two cards.
 *   Fathomless descent — Enchanted permanent gets -X/-0, where X is the number of
 *   permanent cards in your graveyard.
 *
 * The static power penalty is a continuously recomputed [GrantDynamicStatsEffect] on the
 * attached permanent: power is reduced by the count of permanent cards in the Aura
 * controller's graveyard (negated via `Multiply(..., -1)`), toughness untouched. Because the
 * Aura can enchant a (non-creature) Vehicle, the penalty must land on any attached permanent,
 * not just creatures.
 *
 * The self-mill ETB is `Patterns.Library.mill(2)` with `optional = true` (same shape as
 * Mineshaft Spider / Deathcap Marionette). The mill tests below are the regression net for the
 * TriggerProcessor bug where a no-target optional trigger with no `elseEffect` dropped the
 * `optional` flag and resolved as mandatory — the player must get a yes/no and a "no" must
 * skip the mill.
 */
class SongOfStupefactionScenarioTest : ScenarioTestBase() {

    init {
        context("Song of Stupefaction") {

            test("enchanted creature gets -X/-0 equal to permanent cards in your graveyard") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withCardAttachedTo(1, "Song of Stupefaction", "Grizzly Bears")
                    .withCardInGraveyard(1, "Forest")
                    .withCardInGraveyard(1, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                withClue("2 permanent cards in graveyard -> -2/-0, so 2/2 becomes 0/2") {
                    game.state.projectedState.getPower(bears) shouldBe 0
                    game.state.projectedState.getToughness(bears) shouldBe 2
                }
            }

            test("no permanent cards in graveyard leaves the enchanted creature unmodified") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withCardAttachedTo(1, "Song of Stupefaction", "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                withClue("Empty graveyard -> X = 0, so the creature stays 2/2") {
                    game.state.projectedState.getPower(bears) shouldBe 2
                    game.state.projectedState.getToughness(bears) shouldBe 2
                }
            }

            test("only permanent cards count toward X (instants are ignored)") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withCardAttachedTo(1, "Song of Stupefaction", "Grizzly Bears")
                    .withCardInGraveyard(1, "Forest")          // permanent card -> counts
                    .withCardInGraveyard(1, "Lightning Bolt")  // instant -> ignored
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                withClue("Only the Forest counts -> -1/-0, so 2/2 becomes 1/2") {
                    game.state.projectedState.getPower(bears) shouldBe 1
                    game.state.projectedState.getToughness(bears) shouldBe 2
                }
            }

            test("ETB mill is optional — accepting the prompt mills two cards") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardInHand(1, "Song of Stupefaction")
                    .withLandsOnBattlefield(1, "Island", 2)
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(1, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                game.castSpell(1, "Song of Stupefaction", targetId = bears).error shouldBe null
                game.resolveStack()

                withClue("the optional mill must pause on a yes/no prompt, not mill unasked") {
                    (game.state.pendingDecision != null) shouldBe true
                    game.state.getZone(ZoneKey(game.player1Id, Zone.GRAVEYARD)).size shouldBe 0
                }

                game.answerYesNo(true)

                withClue("accepting mills the top two cards") {
                    game.state.getZone(ZoneKey(game.player1Id, Zone.GRAVEYARD)).size shouldBe 2
                    game.state.getZone(ZoneKey(game.player1Id, Zone.LIBRARY)).size shouldBe 1
                }
            }

            test("ETB mill is optional — declining the prompt mills nothing") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardInHand(1, "Song of Stupefaction")
                    .withLandsOnBattlefield(1, "Island", 2)
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(1, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                game.castSpell(1, "Song of Stupefaction", targetId = bears).error shouldBe null
                game.resolveStack()

                withClue("the optional mill must pause on a yes/no prompt") {
                    (game.state.pendingDecision != null) shouldBe true
                }

                game.answerYesNo(false)

                withClue("declining leaves the library untouched") {
                    game.state.getZone(ZoneKey(game.player1Id, Zone.GRAVEYARD)).size shouldBe 0
                    game.state.getZone(ZoneKey(game.player1Id, Zone.LIBRARY)).size shouldBe 2
                }
            }

            test("can legally enchant a Vehicle; the -X/-0 lies dormant while it isn't a creature") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardOnBattlefield(1, "Cloudspire Skycycle") // 2/3 Vehicle (not a creature)
                    .withCardAttachedTo(1, "Song of Stupefaction", "Cloudspire Skycycle")
                    .withCardInGraveyard(1, "Forest")
                    .withCardInGraveyard(1, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val vehicle = game.findPermanent("Cloudspire Skycycle")!!
                val song = game.findPermanent("Song of Stupefaction")!!

                // "Enchant creature or Vehicle": a Vehicle is a legal host, so the Aura attaches.
                withClue("Song of Stupefaction legally enchants the Vehicle") {
                    game.state.getEntity(song)?.get<AttachedToComponent>()?.targetId shouldBe vehicle
                }

                // CR 208.3: a noncreature permanent has no power or toughness, even when printed P/T
                // appear on it (a Vehicle that isn't currently a creature). The engine therefore does
                // NOT apply the -X/-0 ModifyPowerToughness while the host isn't a creature — the
                // penalty would only manifest once the Vehicle is crewed/animated into a creature.
                // The three creature cases above are the real -X/-0 scaling correctness net; here we
                // just confirm the legal Vehicle attachment and the dormant (printed) P/T.
                withClue("While the Vehicle isn't a creature the -X/-0 stays dormant: it keeps its printed 2/3") {
                    game.state.projectedState.getPower(vehicle) shouldBe 2
                    game.state.projectedState.getToughness(vehicle) shouldBe 3
                }
            }
        }
    }
}

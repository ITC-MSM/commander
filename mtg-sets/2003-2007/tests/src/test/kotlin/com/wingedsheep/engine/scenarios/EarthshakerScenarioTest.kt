package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.DamageComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Earthshaker (CHK #165) — {4}{R}{R} Creature — Spirit 4/5.
 *
 *   Whenever you cast a Spirit or Arcane spell, this creature deals 2 damage to each creature
 *   without flying.
 *
 * **This pins the "Whenever you cast a Spirit or Arcane spell" trigger, which thirteen Champions of
 * Kamigawa cards share and which no card in the corpus had before this sweep.** The trigger is a
 * `SpellCastEvent` over `withAnySubtype("Spirit", "Arcane")` with `TriggerBinding.ANY`, so three
 * things need proving and each fails silently on its own:
 *
 *  1. a **Spirit** spell fires it — the `HasSubtype(Spirit)` half of the `CardPredicate.Or`;
 *  2. an **Arcane** spell fires it — the other half, which a predicate list flattened as a
 *     *conjunction* rather than a disjunction would quietly never satisfy (a Spirit-and-Arcane
 *     card is vanishingly rare, so an AND would look almost exactly like a working card);
 *  3. a spell that is **neither** does not fire it, which is what catches a filter that matched
 *     everything.
 *
 * The sweep itself is the payoff rather than the point, but it doubles as the assertion: Earthshaker
 * has no flying, so it takes its own 2 damage, and a flyer on the board must be untouched.
 */
class EarthshakerScenarioTest : ScenarioTestBase() {

    init {
        context("Earthshaker") {

            test("casting a Spirit spell sweeps every creature without flying") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardOnBattlefield(1, "Earthshaker")
                    .withCardOnBattlefield(1, "Kabuto Moth")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withCardInHand(1, "Lantern Kami")
                    .withLandsOnBattlefield(1, "Plains", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val earthshaker = game.findPermanent("Earthshaker").shouldNotBeNull()

                // Lantern Kami is {W} Creature — Spirit: the Spirit half of the OR.
                game.castSpell(1, "Lantern Kami").error shouldBe null
                game.resolveStack()

                withClue("Grizzly Bears is a 2/2 without flying — 2 damage kills it") {
                    game.findPermanent("Grizzly Bears") shouldBe null
                }
                withClue("Kabuto Moth has flying, so the sweep skips it entirely") {
                    game.findPermanent("Kabuto Moth").shouldNotBeNull()
                }
                withClue("Earthshaker has no flying either — 'each creature' includes itself") {
                    game.state.getEntity(earthshaker)?.get<DamageComponent>()?.amount shouldBe 2
                }
            }

            test("casting an Arcane spell fires the same trigger") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardOnBattlefield(1, "Earthshaker")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withCardInHand(1, "Lava Spike")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                // Lava Spike is {R} Sorcery — Arcane: no Spirit anywhere on it.
                game.castSpellTargetingPlayer(1, "Lava Spike", 2).error shouldBe null
                game.resolveStack()

                withClue("the Arcane half of the OR must fire the trigger on its own") {
                    game.findPermanent("Grizzly Bears") shouldBe null
                }
            }

            test("a spell that is neither Spirit nor Arcane does not fire it") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardOnBattlefield(1, "Earthshaker")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withCardInHand(1, "Kitsune Blademaster")
                    .withLandsOnBattlefield(1, "Plains", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                // Kitsune Blademaster is Creature — Fox Samurai: a CHK creature carrying neither subtype.
                game.castSpell(1, "Kitsune Blademaster").error shouldBe null
                game.resolveStack()

                withClue("a filter that matched every spell would have killed the Bears here") {
                    game.findPermanent("Grizzly Bears").shouldNotBeNull()
                }
            }
        }
    }
}

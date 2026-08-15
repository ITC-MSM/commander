package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Angelic Destiny (M12 #3, reprinted as FDN #565) — {2}{W}{W} Enchantment — Aura.
 *
 * "Enchant creature
 *  Enchanted creature gets +4/+4, has flying and first strike, and is an Angel in addition
 *  to its other types.
 *  When enchanted creature dies, return this card to its owner's hand."
 *
 * Pins both halves: the four-part continuous grant (stats, two keywords, the added Angel
 * subtype) and the recursion trigger — which must return *the Aura itself* from the
 * graveyard it was put into by state-based actions, not the dead creature.
 */
class AngelicDestinyScenarioTest : ScenarioTestBase() {

    private val projector = StateProjector()

    init {
        context("Angelic Destiny") {

            test("grants +4/+4, flying, first strike, and the Angel type") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardAttachedTo(1, "Angelic Destiny", "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val projected = projector.project(game.state)

                withClue("2/2 base plus +4/+4") {
                    projected.getPower(bears) shouldBe 6
                    projected.getToughness(bears) shouldBe 6
                }
                projected.hasKeyword(bears, Keyword.FLYING) shouldBe true
                projected.hasKeyword(bears, Keyword.FIRST_STRIKE) shouldBe true
                withClue("Angel is added, the printed Bear type is kept") {
                    projected.hasSubtype(bears, "Angel") shouldBe true
                    projected.hasSubtype(bears, "Bear") shouldBe true
                }
            }

            test("returns itself to its owner's hand when the enchanted creature dies") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardAttachedTo(1, "Angelic Destiny", "Grizzly Bears")
                    .withCardInHand(2, "Doom Blade")
                    .withLandsOnBattlefield(2, "Swamp", 2)
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                game.castSpell(2, "Doom Blade", bears).error shouldBe null
                game.resolveStack()

                withClue("the enchanted creature died") {
                    game.isInGraveyard(1, "Grizzly Bears") shouldBe true
                }
                withClue("the Aura went to the graveyard as an SBA, then its trigger recurred it") {
                    game.isInHand(1, "Angelic Destiny") shouldBe true
                    game.isInGraveyard(1, "Angelic Destiny") shouldBe false
                }
            }
        }
    }
}

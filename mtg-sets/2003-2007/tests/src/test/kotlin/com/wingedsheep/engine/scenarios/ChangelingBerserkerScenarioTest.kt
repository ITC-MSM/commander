package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe

/**
 * Changeling Berserker (LRW #160) — changeling, haste, champion a creature.
 *
 * The Champion keyword's own rules matrix lives in `ChampionKeywordTest`; what this file pins is
 * that *this card* is wired to it with the printed quality ("a creature", not a tribe) and that its
 * two other keywords survive alongside it.
 */
class ChangelingBerserkerScenarioTest : ScenarioTestBase() {

    private val stateProjector = StateProjector()

    init {
        context("Changeling Berserker — champion a creature") {

            test("the champion choice offers every other creature you control, whatever its type") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardInHand(1, "Changeling Berserker")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardOnBattlefield(1, "Llanowar Elves")
                    .withCardOnBattlefield(2, "Savannah Lions")
                    .withLandsOnBattlefield(1, "Mountain", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Changeling Berserker").error shouldBe null
                game.resolveStack()

                val decision = game.getPendingDecision() as SelectCardsDecision
                withClue("'another creature you control' — not the Berserker, not Bob's Bears") {
                    decision.options shouldContainExactlyInAnyOrder listOf(
                        game.findPermanent("Grizzly Bears")!!,
                        game.findPermanent("Llanowar Elves")!!
                    )
                }
            }

            test("exiling an Elf keeps the Berserker, and the Elf comes back when it dies") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardInHand(1, "Changeling Berserker")
                    .withCardInHand(1, "Doom Blade")
                    .withCardOnBattlefield(1, "Llanowar Elves")
                    .withLandsOnBattlefield(1, "Mountain", 4)
                    .withLandsOnBattlefield(1, "Swamp", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val elf = game.findPermanent("Llanowar Elves")!!
                game.castSpell(1, "Changeling Berserker").error shouldBe null
                game.resolveStack()
                game.selectCards(listOf(elf)).error shouldBe null
                game.resolveStack()

                val berserker = game.findPermanent("Changeling Berserker")!!
                withClue("the Berserker stays; the Elf is exiled") {
                    game.isOnBattlefield("Changeling Berserker") shouldBe true
                    game.isInExile(1, "Llanowar Elves") shouldBe true
                }

                game.castSpell(1, "Doom Blade", berserker).error shouldBe null
                game.resolveStack()

                withClue("the leaves trigger returns the championed Elf") {
                    game.isOnBattlefield("Changeling Berserker") shouldBe false
                    game.isOnBattlefield("Llanowar Elves") shouldBe true
                }
            }

            test("declining the choice sacrifices the Berserker") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardInHand(1, "Changeling Berserker")
                    .withCardOnBattlefield(1, "Llanowar Elves")
                    .withLandsOnBattlefield(1, "Mountain", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Changeling Berserker").error shouldBe null
                game.resolveStack()
                game.skipSelection().error shouldBe null
                game.resolveStack()

                game.isInGraveyard(1, "Changeling Berserker") shouldBe true
                withClue("nothing was exiled") {
                    game.isOnBattlefield("Llanowar Elves") shouldBe true
                }
            }

            test("it is every creature type and has haste") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Changeling Berserker")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val berserker = game.findPermanent("Changeling Berserker")!!
                val projected = stateProjector.project(game.state)
                projected.hasKeyword(berserker, Keyword.CHANGELING) shouldBe true
                projected.hasKeyword(berserker, Keyword.HASTE) shouldBe true
                projected.getPower(berserker) shouldBe 5
                projected.getToughness(berserker) shouldBe 3
            }
        }
    }
}

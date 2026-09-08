package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.lrw.cards.WrensRunPackmaster
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe

/**
 * Wren's Run Packmaster (LRW #244) — champion an Elf, "{2}{G}: Create a 2/2 green Wolf creature
 * token", and "Wolves you control have deathtouch."
 *
 * The champion keyword's matrix lives in `ChampionKeywordTest`; this pins the Elf quality and the
 * lord clause's printed scope — every Wolf permanent you control, not only the tokens this makes
 * (2007-10-01 ruling).
 */
class WrensRunPackmasterScenarioTest : ScenarioTestBase() {

    private val stateProjector = StateProjector()
    private val wolfAbility = WrensRunPackmaster.activatedAbilities.single().id

    init {
        context("Wren's Run Packmaster — champion an Elf") {

            test("only Elves you control are offered") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardInHand(1, "Wren's Run Packmaster")
                    .withCardOnBattlefield(1, "Llanowar Elves")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Forest", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Wren's Run Packmaster").error shouldBe null
                game.resolveStack()

                val decision = game.getPendingDecision() as SelectCardsDecision
                decision.options shouldContain game.findPermanent("Llanowar Elves")!!
                withClue("a Bear is not an Elf") {
                    decision.options shouldNotContain game.findPermanent("Grizzly Bears")!!
                }
            }

            test("exiling the Elf keeps the Packmaster, and it returns when the Packmaster dies") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardInHand(1, "Wren's Run Packmaster")
                    .withCardInHand(1, "Doom Blade")
                    .withCardOnBattlefield(1, "Llanowar Elves")
                    .withLandsOnBattlefield(1, "Forest", 4)
                    .withLandsOnBattlefield(1, "Swamp", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val elf = game.findPermanent("Llanowar Elves")!!
                game.castSpell(1, "Wren's Run Packmaster").error shouldBe null
                game.resolveStack()
                game.selectCards(listOf(elf)).error shouldBe null
                game.resolveStack()
                game.isInExile(1, "Llanowar Elves") shouldBe true

                game.castSpell(1, "Doom Blade", game.findPermanent("Wren's Run Packmaster")!!)
                    .error shouldBe null
                game.resolveStack()
                game.isOnBattlefield("Llanowar Elves") shouldBe true
            }
        }

        context("Wren's Run Packmaster — Wolves and deathtouch") {

            test("the activated ability makes a 2/2 Wolf token, and it has deathtouch") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Wren's Run Packmaster")
                    .withLandsOnBattlefield(1, "Forest", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val packmaster = game.findPermanent("Wren's Run Packmaster")!!
                game.execute(ActivateAbility(game.player1Id, packmaster, wolfAbility))
                    .error shouldBe null
                game.resolveStack()

                val wolf = game.findPermanent("Wolf Token")!!
                val projected = stateProjector.project(game.state)
                projected.getPower(wolf) shouldBe 2
                projected.getToughness(wolf) shouldBe 2
                withClue("Wolves you control have deathtouch") {
                    projected.hasKeyword(wolf, Keyword.DEATHTOUCH) shouldBe true
                }
                withClue("the Packmaster is an Elf Warrior, not a Wolf — it gets nothing") {
                    projected.hasKeyword(packmaster, Keyword.DEATHTOUCH) shouldBe false
                }
            }

            test("the lord clause reaches every Wolf you control, and no opponent's") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Wren's Run Packmaster")
                    .withCardOnBattlefield(1, "Timber Wolves")
                    .withCardOnBattlefield(2, "Timber Wolves")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val wolves = game.findPermanents("Timber Wolves")
                val projected = stateProjector.project(game.state)
                val mine = wolves.first { projected.getController(it) == game.player1Id }
                val theirs = wolves.first { projected.getController(it) == game.player2Id }

                withClue("a Wolf that did not come from this Packmaster still gets deathtouch") {
                    projected.hasKeyword(mine, Keyword.DEATHTOUCH) shouldBe true
                }
                withClue("'Wolves you control' — Bob's Wolf is untouched") {
                    projected.hasKeyword(theirs, Keyword.DEATHTOUCH) shouldBe false
                }
            }

            test("it is a 5/5") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Wren's Run Packmaster")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val packmaster = game.findPermanent("Wren's Run Packmaster")!!
                val projected = stateProjector.project(game.state)
                projected.getPower(packmaster) shouldBe 5
                projected.getToughness(packmaster) shouldBe 5
            }
        }
    }
}

package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Nettlevine Blight (LRW #131, {4}{B}{B}, Enchantment — Aura).
 *
 *   Enchant creature or land
 *   Enchanted permanent has "At the beginning of your end step, sacrifice this permanent and
 *   attach Nettlevine Blight to a creature or land you control."
 *
 * Every test here aims at the same axis, because it is the whole card and the 2007-10-01 ruling
 * exists to spell it out: the ability is *granted to the enchanted permanent*, so the quoted "you"
 * is that permanent's controller — not the Aura's. Play it on an opponent's creature (the only way
 * anyone plays it) and the wrong wiring gives the opposite card: the Aura's controller would be
 * prompted, would pick from their own board, and the Blight would walk through their permanents
 * instead of their opponent's. So the assertions are on *who* decides, *whose* permanents are
 * offered, and *whose* end step fires it.
 *
 * The fourth question is a type one: "creature or land" has to hold on both ends. A land is offered
 * as a new host, and picking it must leave the Aura attached rather than falling off — the printed
 * `AttachTargetEquipmentToCreature` is named for Equipment, and its executor reads neither type.
 */
class NettlevineBlightScenarioTest : ScenarioTestBase() {

    init {
        context("Nettlevine Blight") {

            test("the enchanted permanent's controller sacrifices it and re-attaches the Aura") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withCardAttachedTo(1, "Nettlevine Blight", "Grizzly Bears")
                    .withCardOnBattlefield(2, "Hill Giant")
                    .withLandsOnBattlefield(2, "Forest", 1)
                    .withCardOnBattlefield(1, "Craw Wurm")
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val aura = game.findPermanent("Nettlevine Blight")!!
                val giant = game.findPermanent("Hill Giant")!!
                val forest = game.findPermanent("Forest")!!

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                val decision = game.getPendingDecision()
                decision.shouldBeInstanceOf<SelectCardsDecision>()
                withClue("\"you\" is the enchanted permanent's controller, so Bob chooses") {
                    decision.playerId shouldBe game.player2Id
                }
                withClue("the pool is Bob's creatures and lands — never Alice's Craw Wurm, and " +
                    "never the Grizzly Bears he just sacrificed") {
                    decision.options.toSet() shouldBe setOf(giant, forest)
                }

                game.selectCards(listOf(giant)).error shouldBe null
                game.resolveStack()
                game.checkStateBasedActions()

                withClue("the host was sacrificed by its own controller") {
                    game.isInGraveyard(2, "Grizzly Bears") shouldBe true
                }
                withClue("the Aura moved to the chosen permanent rather than falling off") {
                    game.isOnBattlefield("Nettlevine Blight") shouldBe true
                    game.state.getEntity(aura)!!.get<AttachedToComponent>()!!.targetId shouldBe giant
                }
            }

            test("a land is a legal new host — \"creature or land\" holds on the re-attach too") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withCardAttachedTo(1, "Nettlevine Blight", "Grizzly Bears")
                    .withLandsOnBattlefield(2, "Forest", 1)
                    .withLandsOnBattlefield(2, "Mountain", 1)
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val aura = game.findPermanent("Nettlevine Blight")!!
                val forest = game.findPermanent("Forest")!!

                // Two lands, so `chooseExactly(1)` raises a real prompt instead of auto-picking the
                // single candidate — the shape the first test's creature-and-land board also has.
                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()
                game.selectCards(listOf(forest)).error shouldBe null
                game.resolveStack()
                game.checkStateBasedActions()

                withClue("an Aura on a land survives the unattached-Auras and illegal-attachment SBAs") {
                    game.isOnBattlefield("Nettlevine Blight") shouldBe true
                    game.state.getEntity(aura)!!.get<AttachedToComponent>()!!.targetId shouldBe forest
                }
            }

            test("a single candidate is taken without a prompt — the choice is forced") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withCardAttachedTo(1, "Nettlevine Blight", "Grizzly Bears")
                    .withLandsOnBattlefield(2, "Swamp", 1)
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val aura = game.findPermanent("Nettlevine Blight")!!
                val swamp = game.findPermanent("Swamp")!!

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()
                game.checkStateBasedActions()

                withClue("with one legal host, `chooseExactly(1)` auto-picks rather than asking") {
                    game.hasPendingDecision() shouldBe false
                    game.state.getEntity(aura)!!.get<AttachedToComponent>()!!.targetId shouldBe swamp
                }
            }

            test("with nothing left to move to, the host still dies and the Aura follows it") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withCardAttachedTo(1, "Nettlevine Blight", "Grizzly Bears")
                    .withCardOnBattlefield(1, "Craw Wurm")
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()
                game.checkStateBasedActions()

                withClue("Alice's Craw Wurm is not a candidate, so Bob is never asked") {
                    game.hasPendingDecision() shouldBe false
                }
                withClue("the sacrifice is not conditional on there being a new host") {
                    game.isInGraveyard(2, "Grizzly Bears") shouldBe true
                }
                withClue("an unattached Aura is put into its owner's graveyard (CR 704.5m)") {
                    game.isOnBattlefield("Nettlevine Blight") shouldBe false
                    game.isInGraveyard(1, "Nettlevine Blight") shouldBe true
                }
            }

            test("the Aura's controller's end step does nothing — the trigger is not hers") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withCardAttachedTo(1, "Nettlevine Blight", "Grizzly Bears")
                    .withCardOnBattlefield(1, "Craw Wurm")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()
                game.checkStateBasedActions()

                withClue("printing the ability on the Aura would have fired it here") {
                    game.hasPendingDecision() shouldBe false
                    game.isOnBattlefield("Grizzly Bears") shouldBe true
                    game.isOnBattlefield("Nettlevine Blight") shouldBe true
                }
            }
        }
    }
}

package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.fin.cards.DarkKnightsGreatsword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Final Fantasy "Job select" Equipment cycle — three cards that all share the `jobSelect()` ETB
 * (create a 1/1 colorless Hero token, then attach to it) but differ in their equipped-creature
 * grants and equip cost:
 *
 *  - Dark Knight's Greatsword: +3/+0, Knight; "Chaosbringer — Equip—Pay 3 life. Activate only
 *    once each turn." (non-mana equip cost + once-per-turn restriction).
 *  - Dragoon's Lance: +1/+0, Knight; "During your turn, equipped creature has flying." (a
 *    conditional static grant gated on the controller's turn).
 *  - Red Mage's Rapier: Wizard; grants the equipped creature "Whenever you cast a noncreature
 *    spell, this creature gets +2/+0 until end of turn."
 */
class FinEquipmentJobSelectScenarioTest : ScenarioTestBase() {

    private val stateProjector = StateProjector()

    init {
        context("Dark Knight's Greatsword") {
            test("ETB makes a Hero token, grants +3/+0 and Knight to its bearer") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardInHand(1, "Dark Knight's Greatsword")
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cast = game.castSpell(1, "Dark Knight's Greatsword")
                withClue("Casting should succeed: ${cast.error}") { cast.error shouldBe null }
                if (game.hasPendingDecision()) game.submitManaSourcesAutoPay()
                game.resolveStack()

                val hero = game.findPermanent("Hero Token")!!
                val sword = game.findPermanent("Dark Knight's Greatsword")!!
                withClue("Greatsword should be attached to the Hero token") {
                    game.state.getEntity(sword)?.get<AttachedToComponent>()?.targetId shouldBe hero
                }

                val projected = stateProjector.project(game.state)
                withClue("Equipped Hero is 1/1 + (+3/+0) = 4/1") {
                    projected.getPower(hero) shouldBe 4
                    projected.getToughness(hero) shouldBe 1
                }
                withClue("Equipped Hero is a Knight in addition to Hero") {
                    projected.hasSubtype(hero, "Knight") shouldBe true
                    projected.hasSubtype(hero, "Hero") shouldBe true
                }
            }

            test("Chaosbringer equip costs 3 life and is usable only once each turn") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Dark Knight's Greatsword")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardOnBattlefield(1, "Hill Giant", summoningSickness = false)
                    .withLifeTotal(1, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val sword = game.findPermanent("Dark Knight's Greatsword")!!
                val bears = game.findPermanent("Grizzly Bears")!!
                val giant = game.findPermanent("Hill Giant")!!
                val equipId = DarkKnightsGreatsword.activatedAbilities.first().id

                val lifeBefore = game.getLifeTotal(1)
                val first = game.execute(
                    ActivateAbility(game.player1Id, sword, equipId, targets = listOf(ChosenTarget.Permanent(bears)))
                )
                withClue("First equip should succeed: ${first.error}") { first.error shouldBe null }
                game.resolveStack()

                withClue("Equip cost 3 life") { game.getLifeTotal(1) shouldBe lifeBefore - 3 }
                withClue("Greatsword now equips Grizzly Bears") {
                    game.state.getEntity(sword)?.get<AttachedToComponent>()?.targetId shouldBe bears
                }

                // OncePerTurn: the equip ability is no longer offered, and re-activating fails.
                withClue("Equip should not be offered a second time this turn") {
                    game.getLegalActions(1).find {
                        it.actionType == "ActivateAbility" &&
                            (it.action as? ActivateAbility)?.sourceId == sword
                    } shouldBe null
                }
                val second = game.execute(
                    ActivateAbility(game.player1Id, sword, equipId, targets = listOf(ChosenTarget.Permanent(giant)))
                )
                withClue("Second equip same turn should be rejected") { second.error shouldNotBe null }
                withClue("Still attached to Grizzly Bears, life unchanged after rejected equip") {
                    game.state.getEntity(sword)?.get<AttachedToComponent>()?.targetId shouldBe bears
                    game.getLifeTotal(1) shouldBe lifeBefore - 3
                }
            }
        }

        context("Dragoon's Lance") {
            test("on its controller's turn the equipped Hero is a 2/1 flying Knight") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardInHand(1, "Dragoon's Lance")
                    .withLandsOnBattlefield(1, "Plains", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cast = game.castSpell(1, "Dragoon's Lance")
                withClue("Casting should succeed: ${cast.error}") { cast.error shouldBe null }
                if (game.hasPendingDecision()) game.submitManaSourcesAutoPay()
                game.resolveStack()

                val hero = game.findPermanent("Hero Token")!!
                val onTurn = stateProjector.project(game.state)
                withClue("Equipped Hero is 1/1 + (+1/+0) = 2/1 Knight") {
                    onTurn.getPower(hero) shouldBe 2
                    onTurn.getToughness(hero) shouldBe 1
                    onTurn.hasSubtype(hero, "Knight") shouldBe true
                }
                withClue("During its controller's turn the equipped Hero has flying") {
                    onTurn.hasKeyword(hero, com.wingedsheep.sdk.core.Keyword.FLYING) shouldBe true
                }
            }

            test("on the opponent's turn the equipped Hero keeps +1/+0 and Knight but loses flying") {
                // Alice's Lance is already equipped to her Grizzly Bears; it is Bob's turn.
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardAttachedTo(1, "Dragoon's Lance", "Grizzly Bears")
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!

                val offTurn = stateProjector.project(game.state)
                withClue("Active player is Bob") { (game.state.activePlayerId == game.player2Id) shouldBe true }
                withClue("Equipped creature keeps +1/+0 and Knight regardless of whose turn it is") {
                    offTurn.getPower(bears) shouldBe 3 // Grizzly Bears 2/2 + 1/0
                    offTurn.getToughness(bears) shouldBe 2
                    offTurn.hasSubtype(bears, "Knight") shouldBe true
                }
                withClue("On the opponent's turn the equipped creature does NOT have flying") {
                    offTurn.hasKeyword(bears, com.wingedsheep.sdk.core.Keyword.FLYING) shouldBe false
                }
            }
        }

        context("Red Mage's Rapier") {
            test("equipped Hero is a Wizard and gets +2/+0 per noncreature spell its controller casts") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardInHand(1, "Red Mage's Rapier")
                    .withCardInHand(1, "Shock")
                    .withCardInHand(1, "Shock")
                    .withLandsOnBattlefield(1, "Mountain", 4)
                    .withCardOnBattlefield(2, "Hill Giant", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cast = game.castSpell(1, "Red Mage's Rapier")
                withClue("Casting should succeed: ${cast.error}") { cast.error shouldBe null }
                if (game.hasPendingDecision()) game.submitManaSourcesAutoPay()
                game.resolveStack()

                val hero = game.findPermanent("Hero Token")!!
                val before = stateProjector.project(game.state)
                withClue("Equipped Hero is a Wizard, base 1/1 with no spell cast yet") {
                    before.hasSubtype(hero, "Wizard") shouldBe true
                    before.getPower(hero) shouldBe 1
                    before.getToughness(hero) shouldBe 1
                }

                val giant = game.findPermanent("Hill Giant")!!
                val shock1 = game.castSpell(1, "Shock", giant)
                withClue("First Shock should succeed: ${shock1.error}") { shock1.error shouldBe null }
                if (game.hasPendingDecision()) game.submitManaSourcesAutoPay()
                game.resolveStack()

                val afterOne = stateProjector.project(game.state)
                withClue("Casting one noncreature spell pumps the Hero to 3/1 (+2/+0)") {
                    afterOne.getPower(hero) shouldBe 3
                    afterOne.getToughness(hero) shouldBe 1
                }

                val shock2 = game.castSpell(1, "Shock", giant)
                withClue("Second Shock should succeed: ${shock2.error}") { shock2.error shouldBe null }
                if (game.hasPendingDecision()) game.submitManaSourcesAutoPay()
                game.resolveStack()

                val afterTwo = stateProjector.project(game.state)
                withClue("A second noncreature spell stacks another +2/+0 → 5/1 this turn") {
                    afterTwo.getPower(hero) shouldBe 5
                    afterTwo.getToughness(hero) shouldBe 1
                }
            }
        }
    }
}

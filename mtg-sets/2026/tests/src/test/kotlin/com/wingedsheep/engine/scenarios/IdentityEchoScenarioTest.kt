package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.ControllerComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Identity Echo (FRA #87) — {2}{R} Enchantment.
 *
 *   {3}{R}: Exile target creature or planeswalker you control. Reveal cards from the top of your
 *   library until you reveal a creature or planeswalker card. Put that card onto the battlefield
 *   and the rest on the bottom of your library in a random order. Activate only as a sorcery.
 */
class IdentityEchoScenarioTest : ScenarioTestBase() {

    private val abilityId by lazy { cardRegistry.getCard("Identity Echo")!!.script.activatedAbilities.single().id }

    private fun board(
        library: List<String>,
        phase: Phase = Phase.PRECOMBAT_MAIN,
        step: Step = Step.PRECOMBAT_MAIN
    ): TestGame {
        var builder = scenario().withPlayers()
            .withCardOnBattlefield(1, "Identity Echo")
            .withCardOnBattlefield(1, "Grizzly Bears")
            .withCardOnBattlefield(2, "Savannah Lions")
            .withLandsOnBattlefield(1, "Mountain", 4)
            .withCardInLibrary(2, "Plains")
            .withActivePlayer(1)
            .inPhase(phase, step)
        for (name in library) builder = builder.withCardInLibrary(1, name)
        return builder.build()
    }

    private fun TestGame.activate(target: EntityId) = execute(
        ActivateAbility(player1Id, findPermanent("Identity Echo")!!, abilityId, targets = listOf(ChosenTarget.Permanent(target)))
    )

    private fun TestGame.libraryNames(): List<String> =
        state.getLibrary(player1Id).map { state.getEntity(it)!!.get<CardComponent>()!!.name }

    init {
        test("exiles your creature, puts the first creature card revealed onto the battlefield, bottoms the rest") {
            val game = board(listOf("Mountain", "Plains", "Centaur Courser", "Island"))
            val bears = game.findPermanent("Grizzly Bears")!!

            game.activate(bears).error shouldBe null
            game.resolveStack()

            game.isInExile(1, "Grizzly Bears") shouldBe true
            val courser = game.findPermanent("Centaur Courser")
            courser shouldNotBe null
            game.state.getEntity(courser!!)!!.get<ControllerComponent>()!!.playerId shouldBe game.player1Id
            withClue("the unrevealed Island stays on top; the two revealed lands go under it") {
                game.libraryNames().first() shouldBe "Island"
                game.libraryNames().drop(1) shouldContainExactlyInAnyOrder listOf("Mountain", "Plains")
            }
        }

        test("a planeswalker card counts as a hit") {
            val game = board(listOf("Plains", "Ajani Resolute", "Centaur Courser"))

            game.activate(game.findPermanent("Grizzly Bears")!!).error shouldBe null
            game.resolveStack()

            game.findPermanent("Ajani Resolute") shouldNotBe null
            game.findPermanent("Centaur Courser") shouldBe null
            game.libraryNames() shouldBe listOf("Centaur Courser", "Plains")
        }

        test("with no creature or planeswalker card left, nothing enters and every revealed card is bottomed") {
            val game = board(listOf("Mountain", "Plains"))

            game.activate(game.findPermanent("Grizzly Bears")!!).error shouldBe null
            game.resolveStack()

            game.isInExile(1, "Grizzly Bears") shouldBe true
            game.libraryNames() shouldContainExactlyInAnyOrder listOf("Mountain", "Plains")
        }

        test("can't target a creature you don't control") {
            val game = board(listOf("Centaur Courser"))

            game.activate(game.findPermanent("Savannah Lions")!!).error shouldNotBe null
        }

        test("activate only as a sorcery") {
            val game = board(listOf("Centaur Courser"), Phase.COMBAT, Step.BEGIN_COMBAT)

            game.activate(game.findPermanent("Grizzly Bears")!!).error shouldNotBe null
        }
    }
}

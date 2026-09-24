package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.DeclareAttackers
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Tomik, Orzhov Lawmage (FRA #206) — {1}{W} Legendary Creature — Human Advisor 2/1.
 *
 *   Flying
 *   Planeswalkers you control have "No more than one creature can attack this planeswalker each
 *   combat."
 *   {T}: Target creature with a +1/+1 counter on it gains flying until end of turn.
 */
class TomikOrzhovLawmageScenarioTest : ScenarioTestBase() {

    /** Player 1 attacks with two creatures; player 2 controls a planeswalker (and maybe Tomik). */
    private fun combat(opponentHasTomik: Boolean, attackerHasTomik: Boolean = false): TestGame {
        var builder = scenario().withPlayers("Attacker", "Defender")
            .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
            .withCardOnBattlefield(1, "Savannah Lions", summoningSickness = false)
            .withCardOnBattlefield(2, "Ajani Resolute")
        if (opponentHasTomik) builder = builder.withCardOnBattlefield(2, "Tomik, Orzhov Lawmage")
        if (attackerHasTomik) builder = builder.withCardOnBattlefield(1, "Tomik, Orzhov Lawmage")
        return builder
            .withCardInLibrary(1, "Plains")
            .withCardInLibrary(2, "Plains")
            .withActivePlayer(1)
            .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
            .build()
    }

    private fun TestGame.attack(vararg pairs: Pair<String, EntityId>) =
        execute(DeclareAttackers(player1Id, pairs.associate { (name, defender) -> findPermanent(name)!! to defender }))

    init {
        test("no more than one creature can attack a planeswalker Tomik's controller controls") {
            val game = combat(opponentHasTomik = true)
            val ajani = game.findPermanent("Ajani Resolute")!!

            game.attack("Grizzly Bears" to ajani, "Savannah Lions" to ajani).error shouldNotBe null
        }

        test("one creature on the planeswalker and the rest on the player is fine") {
            val game = combat(opponentHasTomik = true)
            val ajani = game.findPermanent("Ajani Resolute")!!

            game.attack("Grizzly Bears" to ajani, "Savannah Lions" to game.player2Id).error shouldBe null
        }

        test("the player themself is not protected — both creatures may attack them") {
            val game = combat(opponentHasTomik = true)

            game.attack("Grizzly Bears" to game.player2Id, "Savannah Lions" to game.player2Id).error shouldBe null
        }

        test("without Tomik, two creatures may attack the same planeswalker") {
            val game = combat(opponentHasTomik = false)
            val ajani = game.findPermanent("Ajani Resolute")!!

            game.attack("Grizzly Bears" to ajani, "Savannah Lions" to ajani).error shouldBe null
        }

        test("Tomik protects only planeswalkers its controller controls") {
            val game = combat(opponentHasTomik = false, attackerHasTomik = true)
            val ajani = game.findPermanent("Ajani Resolute")!!

            withClue("the attacker's Tomik does nothing for the defender's Ajani") {
                game.attack("Grizzly Bears" to ajani, "Savannah Lions" to ajani).error shouldBe null
            }
        }

        test("{T}: a creature with a +1/+1 counter gains flying until end of turn") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, "Tomik, Orzhov Lawmage", summoningSickness = false)
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withCardOnBattlefield(1, "Savannah Lions")
                .withCardInLibrary(1, "Plains")
                .withCardInLibrary(2, "Plains")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()
            val tomik = game.findPermanent("Tomik, Orzhov Lawmage")!!
            val bears = game.findPermanent("Grizzly Bears")!!
            val lions = game.findPermanent("Savannah Lions")!!
            game.state = game.state.updateEntity(bears) {
                it.with(CountersComponent(mapOf(CounterType.PLUS_ONE_PLUS_ONE to 1)))
            }
            val abilityId = cardRegistry.getCard("Tomik, Orzhov Lawmage")!!.script.activatedAbilities.single().id

            withClue("a creature without a +1/+1 counter isn't a legal target") {
                game.execute(
                    ActivateAbility(game.player1Id, tomik, abilityId, targets = listOf(ChosenTarget.Permanent(lions)))
                ).error shouldNotBe null
            }

            game.execute(
                ActivateAbility(game.player1Id, tomik, abilityId, targets = listOf(ChosenTarget.Permanent(bears)))
            ).error shouldBe null
            game.resolveStack()

            game.state.projectedState.hasKeyword(bears, Keyword.FLYING) shouldBe true
            game.state.projectedState.hasKeyword(lions, Keyword.FLYING) shouldBe false
        }
    }
}

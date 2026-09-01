package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.rav.cards.RaziasPurification
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Razia's Purification (RAV #224) — "Each player chooses three permanents they control, then
 * sacrifices the rest."
 *
 * Two things are worth proving about the `ChooseExactly(3)` keep. First that it is symmetric: the
 * caster is a "player" too, so an implementation that quietly scoped the gather to opponents
 * (or to the controller) leaves one board untouched. Second, the 2005-10-01 ruling — "if a player
 * doesn't control three permanents, that player chooses all the permanents they do control and
 * doesn't sacrifice anything" — which is exactly `ChooseExactly`'s clamp to the collection size,
 * and would instead read as a *deadlock or a lost board* under a strict-N selection.
 */
class RaziasPurificationScenarioTest : FunSpec({

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + RaziasPurification)
        driver.initMirrorMatch(
            deck = Deck.of("Mountain" to 40),
            startingLife = 20,
            startingPlayer = 0,
            skipMulligans = true,
        )
        return driver
    }

    /** Every permanent [player] controls, in any zone-order the engine reports. */
    fun GameTestDriver.permanentsOf(player: EntityId): List<EntityId> =
        state.projectedState.getBattlefieldControlledBy(player)

    /**
     * Cast the Purification from [caster] and answer every keep-selection by taking the first
     * options offered, which is all a count assertion needs.
     */
    fun GameTestDriver.castAndKeepFirst(caster: EntityId) {
        val spell = putCardInHand(caster, "Razia's Purification")
        giveColorlessMana(caster, 4)
        giveMana(caster, Color.RED, 1)
        giveMana(caster, Color.WHITE, 1)
        castSpell(caster, spell).isSuccess shouldBe true
        bothPass()

        var guard = 0
        while (isPaused && guard++ < 20) {
            val decision = pendingDecision
            if (decision is SelectCardsDecision) {
                submitCardSelection(decision.playerId, decision.options.take(decision.minSelections))
            } else {
                autoResolveDecision()
            }
        }
        guard = 0
        while (stackSize > 0 && guard++ < 50) bothPass()
    }

    test("every player keeps exactly three permanents and sacrifices the rest") {
        val driver = newDriver()
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)

        repeat(3) { driver.putCreatureOnBattlefield(me, "Grizzly Bears") }
        driver.putLandOnBattlefield(me, "Mountain")
        driver.putLandOnBattlefield(me, "Mountain")
        repeat(4) { driver.putCreatureOnBattlefield(opponent, "Grizzly Bears") }

        driver.castAndKeepFirst(me)

        withClue("the caster is a player too — their board is cut to three as well") {
            driver.permanentsOf(me).size shouldBe 3
        }
        driver.permanentsOf(opponent).size shouldBe 3
    }

    test("a player controlling fewer than three permanents sacrifices nothing") {
        val driver = newDriver()
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)

        repeat(4) { driver.putCreatureOnBattlefield(me, "Grizzly Bears") }
        val survivor = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")
        val survivor2 = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")

        driver.castAndKeepFirst(me)

        driver.permanentsOf(me).size shouldBe 3
        withClue("two permanents means both are kept, per the 2005-10-01 ruling") {
            driver.permanentsOf(opponent).toSet() shouldBe setOf(survivor, survivor2)
        }
    }
})

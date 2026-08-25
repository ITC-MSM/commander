package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lea.cards.GiantGrowth
import com.wingedsheep.mtg.sets.definitions.lea.cards.ProdigalSorcerer
import com.wingedsheep.mtg.sets.definitions.ons.cards.GlorySeeker
import com.wingedsheep.mtg.sets.definitions.vow.cards.StormchaserDrake
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Stormchaser Drake — "Whenever this creature becomes the target of a spell you control, draw a card."
 *
 * Three axes have to line up for the trigger to fire, and each one is a separate test here: the
 * targeted permanent is the Drake itself (SELF binding), the targeting source is a *spell* rather
 * than an ability (`spellsOnly`), and that spell is controlled by the Drake's controller (`byYou`).
 */
class StormchaserDrakeScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(
            TestCards.all + listOf(StormchaserDrake, GiantGrowth, ProdigalSorcerer, GlorySeeker)
        )
        return driver
    }

    test("your own spell targeting the Drake draws a card") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 20, "Island" to 20), startingLife = 20)

        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val drake = driver.putCreatureOnBattlefield(player, "Stormchaser Drake")
        val drawn = driver.putCardOnTopOfLibrary(player, "Glory Seeker")

        driver.giveMana(player, Color.GREEN, 1)
        val growth = driver.putCardInHand(player, "Giant Growth")
        val cast = driver.castSpellWithTargets(player, growth, listOf(ChosenTarget.Permanent(drake)))
        cast.error shouldBe null

        // The trigger goes on the stack above Giant Growth and resolves first.
        driver.bothPass()
        (drawn in driver.getHand(player)) shouldBe true
    }

    test("an opponent's spell targeting the Drake draws nothing") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 20, "Island" to 20), startingLife = 20)

        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val drake = driver.putCreatureOnBattlefield(player, "Stormchaser Drake")
        val notDrawn = driver.putCardOnTopOfLibrary(player, "Glory Seeker")

        driver.giveMana(opponent, Color.GREEN, 1)
        val growth = driver.putCardInHand(opponent, "Giant Growth")
        driver.passPriority(player)
        val cast = driver.castSpellWithTargets(opponent, growth, listOf(ChosenTarget.Permanent(drake)))
        cast.error shouldBe null

        driver.bothPass()
        (notDrawn in driver.getHand(player)) shouldBe false
    }

    test("an ability you control targeting the Drake draws nothing") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 20, "Island" to 20), startingLife = 20)

        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val drake = driver.putCreatureOnBattlefield(player, "Stormchaser Drake")
        val tim = driver.putCreatureOnBattlefield(player, "Prodigal Sorcerer")
        driver.removeSummoningSickness(tim)
        val notDrawn = driver.putCardOnTopOfLibrary(player, "Glory Seeker")

        val ping = driver.submit(
            ActivateAbility(
                playerId = player,
                sourceId = tim,
                abilityId = ProdigalSorcerer.activatedAbilities[0].id,
                targets = listOf(ChosenTarget.Permanent(drake))
            )
        )
        ping.isSuccess shouldBe true

        driver.bothPass()
        (notDrawn in driver.getHand(player)) shouldBe false
    }
})

package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Rolling Spoil — land destruction, plus a symmetric -1/-1 sweep if {B} was among the mana paid.
 */
class RollingSpoilScenarioTest : FunSpec({

    test("shrinks every creature, both players', when black mana was spent") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val caster = driver.activePlayer!!
        val opponent = driver.getOpponent(caster)
        val spoil = driver.putCardInHand(caster, "Rolling Spoil")
        val theirLand = driver.putLandOnBattlefield(opponent, "Forest")
        val mine = driver.putCreatureOnBattlefield(caster, "Grizzly Bears")
        val theirs = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")

        // {2}{G}{G} paid as two green plus two black: black covers the generic.
        driver.giveMana(caster, Color.GREEN, 2)
        driver.giveMana(caster, Color.BLACK, 2)

        driver.castSpellWithTargets(caster, spoil, listOf(ChosenTarget.Permanent(theirLand)))
            .error shouldBe null
        driver.bothPass()

        driver.assertInGraveyard(opponent, "Forest")
        driver.state.projectedState.getPower(mine) shouldBe 1
        driver.state.projectedState.getToughness(mine) shouldBe 1
        driver.state.projectedState.getPower(theirs) shouldBe 1
        driver.state.projectedState.getToughness(theirs) shouldBe 1
    }

    test("destroys the land but leaves creatures alone when no black mana was spent") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val caster = driver.activePlayer!!
        val opponent = driver.getOpponent(caster)
        val spoil = driver.putCardInHand(caster, "Rolling Spoil")
        val theirLand = driver.putLandOnBattlefield(opponent, "Forest")
        val mine = driver.putCreatureOnBattlefield(caster, "Grizzly Bears")

        driver.giveMana(caster, Color.GREEN, 4)

        driver.castSpellWithTargets(caster, spoil, listOf(ChosenTarget.Permanent(theirLand)))
            .error shouldBe null
        driver.bothPass()

        driver.assertInGraveyard(opponent, "Forest")
        driver.state.projectedState.getPower(mine) shouldBe 2
        driver.state.projectedState.getToughness(mine) shouldBe 2
    }
})

package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Craterhoof Behemoth (canonical printing: Avacyn Restored #172).
 *
 * {5}{G}{G}{G} · Creature — Beast · 5/5 · Haste
 * "When this creature enters, creatures you control gain trample and get +X/+X until end
 * of turn, where X is the number of creatures you control."
 *
 * Exercises the ETB trigger: every creature the controller has (including Craterhoof
 * itself) gains trample and +X/+X, where X is locked in at resolution as the controller's
 * creature count. Opponent creatures are untouched.
 */
class CraterhoofBehemothScenarioTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        return driver
    }

    test("ETB pumps all creatures you control by the creature count and grants trample") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Forest" to 40),
            skipMulligans = true
        )
        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Two of my creatures already in play, plus an opponent creature.
        val bears = driver.putCreatureOnBattlefield(me, "Grizzly Bears")
        val lions = driver.putCreatureOnBattlefield(me, "Savannah Lions")
        val theirBears = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")

        // Cast Craterhoof Behemoth — once it resolves there are three of my creatures
        // (Bears, Lions, Craterhoof), so X = 3.
        val hoof = driver.putCardInHand(me, "Craterhoof Behemoth")
        driver.giveMana(me, Color.GREEN, 8)
        driver.castSpell(me, hoof)
        driver.bothPass() // resolve the creature spell (ETB trigger goes on the stack)
        driver.bothPass() // resolve the ETB trigger

        val hoofId = driver.findPermanent(me, "Craterhoof Behemoth")!!

        // X = 3: my creatures get +3/+3 and trample.
        projector.getProjectedPower(driver.state, bears) shouldBe 5
        projector.getProjectedToughness(driver.state, bears) shouldBe 5
        projector.getProjectedPower(driver.state, lions) shouldBe 4
        projector.getProjectedToughness(driver.state, lions) shouldBe 4
        projector.getProjectedPower(driver.state, hoofId) shouldBe 8
        projector.getProjectedToughness(driver.state, hoofId) shouldBe 8

        projector.hasProjectedKeyword(driver.state, bears, Keyword.TRAMPLE) shouldBe true
        projector.hasProjectedKeyword(driver.state, lions, Keyword.TRAMPLE) shouldBe true
        projector.hasProjectedKeyword(driver.state, hoofId, Keyword.TRAMPLE) shouldBe true
        // Haste is intrinsic to Craterhoof.
        projector.hasProjectedKeyword(driver.state, hoofId, Keyword.HASTE) shouldBe true

        // Opponent's creature is untouched.
        projector.getProjectedPower(driver.state, theirBears) shouldBe 2
        projector.getProjectedToughness(driver.state, theirBears) shouldBe 2
        projector.hasProjectedKeyword(driver.state, theirBears, Keyword.TRAMPLE) shouldBe false
    }

    test("ETB with no other creatures still pumps Craterhoof itself by 1") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Forest" to 40),
            skipMulligans = true
        )
        val me = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val hoof = driver.putCardInHand(me, "Craterhoof Behemoth")
        driver.giveMana(me, Color.GREEN, 8)
        driver.castSpell(me, hoof)
        driver.bothPass()
        driver.bothPass()

        val hoofId = driver.findPermanent(me, "Craterhoof Behemoth")!!

        // Only creature is Craterhoof, so X = 1: 5/5 -> 6/6 with trample.
        projector.getProjectedPower(driver.state, hoofId) shouldBe 6
        projector.getProjectedToughness(driver.state, hoofId) shouldBe 6
        projector.hasProjectedKeyword(driver.state, hoofId, Keyword.TRAMPLE) shouldBe true
    }
})

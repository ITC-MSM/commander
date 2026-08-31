package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Seed Spark — destroys an artifact or enchantment, and adds two Saprolings if {G} was among the
 * mana paid.
 */
class SeedSparkScenarioTest : FunSpec({

    test("creates two Saprolings when green mana was spent to cast it") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val caster = driver.activePlayer!!
        val opponent = driver.getOpponent(caster)
        val spark = driver.putCardInHand(caster, "Seed Spark")
        val artifact = driver.putPermanentOnBattlefield(opponent, "Icy Manipulator")

        // {3}{W} paid as one white plus three green: green covers the generic.
        driver.giveMana(caster, Color.WHITE, 1)
        driver.giveMana(caster, Color.GREEN, 3)

        driver.castSpellWithTargets(caster, spark, listOf(ChosenTarget.Permanent(artifact)))
            .error shouldBe null
        driver.bothPass()

        driver.assertInGraveyard(opponent, "Icy Manipulator")
        // The engine names an unnamed token "<types> Token", so a Saproling is "Saproling Token".
        withClue("tokens arrived: " + driver.getCreatures(caster).map { driver.getCardName(it) }) {
            driver.getCreatures(caster).count { driver.getCardName(it) == "Saproling Token" } shouldBe 2
        }
    }

    test("creates no Saprolings when no green mana was spent") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val caster = driver.activePlayer!!
        val opponent = driver.getOpponent(caster)
        val spark = driver.putCardInHand(caster, "Seed Spark")
        val artifact = driver.putPermanentOnBattlefield(opponent, "Icy Manipulator")

        driver.giveMana(caster, Color.WHITE, 4)

        driver.castSpellWithTargets(caster, spark, listOf(ChosenTarget.Permanent(artifact)))
            .error shouldBe null
        driver.bothPass()

        driver.assertInGraveyard(opponent, "Icy Manipulator")
        driver.getCreatures(caster).shouldBe(emptyList())
    }
})

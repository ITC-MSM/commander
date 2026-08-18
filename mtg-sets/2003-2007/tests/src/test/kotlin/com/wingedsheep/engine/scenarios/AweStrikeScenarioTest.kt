package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class AweStrikeScenarioTest : FunSpec({
    test("prevents the targeted creature's next damage and gains that much life") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)

        val caster = driver.activePlayer!!
        val opponent = driver.getOpponent(caster)
        val aweStrike = driver.putCardInHand(caster, "Awe Strike")
        driver.putLandOnBattlefield(caster, "Plains")
        val sorcerer = driver.putCreatureOnBattlefield(opponent, "Prodigal Sorcerer")
        driver.removeSummoningSickness(sorcerer)

        driver.castSpellWithTargets(caster, aweStrike, listOf(ChosenTarget.Permanent(sorcerer))).error shouldBe null
        driver.bothPass()
        val shield = driver.state.floatingEffects.single {
            it.effect.modification is com.wingedsheep.engine.mechanics.layers.SerializableModification.PreventNextDamageFromSourceShield
        }.effect.modification as com.wingedsheep.engine.mechanics.layers.SerializableModification.PreventNextDamageFromSourceShield
        shield.damageSourceId shouldBe sorcerer

        driver.passPriority(caster)
        val pingAbility = driver.cardRegistry.requireCard("Prodigal Sorcerer").activatedAbilities.single()
        driver.submit(
            ActivateAbility(
                playerId = opponent,
                sourceId = sorcerer,
                abilityId = pingAbility.id,
                targets = listOf(ChosenTarget.Player(caster))
            )
        ).error shouldBe null
        driver.bothPass()
        driver.bothPass() // resolve the linked life-gain trigger

        driver.assertLifeTotal(caster, 21)
    }
})

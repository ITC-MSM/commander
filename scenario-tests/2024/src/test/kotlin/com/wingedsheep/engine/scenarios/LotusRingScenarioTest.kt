package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.ChooseColorDecision
import com.wingedsheep.engine.core.ColorChosenResponse
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.big.cards.LotusRing
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Lotus Ring — {3} Artifact — Equipment
 *
 * "Indestructible
 *  Equipped creature gets +3/+3 and has vigilance and "{T}, Sacrifice this creature:
 *  Add three mana of any one color."
 *  Equip {3}"
 *
 * Pins the equipment wiring: the {3} equip attaches it, the static layer grants +3/+3 and
 * vigilance to the equipped creature, the Equipment carries Indestructible itself, and the
 * equipped creature gains the granted "{T}, Sacrifice this creature: Add three mana of any
 * one color." activated mana ability.
 */
class LotusRingScenarioTest : FunSpec({

    val projector = StateProjector()

    // The granted "{T}, Sacrifice this creature: Add three mana of any one color." ability.
    val grantedAbilityId = LotusRing.staticAbilities
        .filterIsInstance<GrantActivatedAbility>()
        .first().ability.id

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + LotusRing)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    test("equip {3} attaches, granting +3/+3, vigilance, and the sacrifice-for-mana ability") {
        val driver = createDriver()
        val you = driver.activePlayer!!

        val courser = driver.putCreatureOnBattlefield(you, "Centaur Courser") // 3/3
        driver.removeSummoningSickness(courser)
        val ring = driver.putPermanentOnBattlefield(you, "Lotus Ring")
        val equipId = LotusRing.activatedAbilities.first().id

        // Unequipped baseline: 3/3, no vigilance.
        projector.getProjectedPower(driver.state, courser) shouldBe 3
        projector.getProjectedToughness(driver.state, courser) shouldBe 3
        projector.project(driver.state).hasKeyword(courser, Keyword.VIGILANCE) shouldBe false

        // The Equipment itself has Indestructible.
        projector.project(driver.state).hasKeyword(ring, Keyword.INDESTRUCTIBLE) shouldBe true

        driver.giveColorlessMana(you, 3)
        driver.submit(
            ActivateAbility(you, ring, equipId, targets = listOf(ChosenTarget.Permanent(courser)))
        ).isSuccess shouldBe true
        driver.bothPass()

        // Attached to the chosen creature.
        driver.state.getEntity(ring)?.get<AttachedToComponent>()?.targetId shouldBe courser

        // +3/+3 and vigilance on the equipped creature.
        projector.getProjectedPower(driver.state, courser) shouldBe 6
        projector.getProjectedToughness(driver.state, courser) shouldBe 6
        projector.project(driver.state).hasKeyword(courser, Keyword.VIGILANCE) shouldBe true

        // The equipped creature gains the granted "{T}, Sacrifice this creature: Add three
        // mana of any one color." mana ability. Activating it taps + sacrifices the creature
        // and lets the controller pick one color, producing three of it.
        val result = driver.submit(ActivateAbility(playerId = you, sourceId = courser, abilityId = grantedAbilityId))
        result.isPaused shouldBe true
        driver.pendingDecision.shouldBeInstanceOf<ChooseColorDecision>()
        val decision = driver.pendingDecision as ChooseColorDecision
        driver.submitDecision(you, ColorChosenResponse(decision.id, Color.GREEN))

        // Three green mana, and the creature was sacrificed to pay the cost.
        driver.state.getEntity(you)?.get<ManaPoolComponent>()?.green shouldBe 3
        driver.findPermanent(you, "Centaur Courser") shouldBe null
    }
})

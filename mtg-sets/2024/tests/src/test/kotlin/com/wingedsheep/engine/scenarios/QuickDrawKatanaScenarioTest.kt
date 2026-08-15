package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fdn.cards.QuickDrawKatana
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Quick-Draw Katana — {2} Artifact — Equipment, equip {2}
 *
 * "During your turn, equipped creature gets +2/+0 and has first strike."
 *
 * Both the +2/+0 and first strike are gated on `Conditions.IsYourTurn`. These tests pin that the
 * bonus is present on the controller's own turn and *gone* on the opponent's turn.
 */
class QuickDrawKatanaScenarioTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(QuickDrawKatana))
        return driver
    }

    test("equipped creature gets +2/+0 and first strike on your turn; nothing on the opponent's turn") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)

        val courser = driver.putCreatureOnBattlefield(me, "Centaur Courser") // 3/3
        val katana = driver.putPermanentOnBattlefield(me, "Quick-Draw Katana")
        val equipId = QuickDrawKatana.activatedAbilities.first().id

        // Unequipped baseline: 3/3, no first strike.
        projector.getProjectedPower(driver.state, courser) shouldBe 3
        projector.getProjectedToughness(driver.state, courser) shouldBe 3
        projector.project(driver.state).hasKeyword(courser, Keyword.FIRST_STRIKE) shouldBe false

        // Equip {2}.
        driver.giveColorlessMana(me, 2)
        driver.submit(
            ActivateAbility(me, katana, equipId, targets = listOf(ChosenTarget.Permanent(courser)))
        ).isSuccess shouldBe true
        driver.bothPass()

        driver.state.getEntity(katana)?.get<AttachedToComponent>()?.targetId shouldBe courser

        // On my turn: +2/+0 and first strike.
        projector.getProjectedPower(driver.state, courser) shouldBe 5
        projector.getProjectedToughness(driver.state, courser) shouldBe 3
        projector.project(driver.state).hasKeyword(courser, Keyword.FIRST_STRIKE) shouldBe true

        // Advance to the opponent's turn.
        var safety = 0
        while (driver.activePlayer != opp && safety < 20) {
            driver.passPriorityUntil(Step.END)
            driver.bothPass()
            safety++
        }
        driver.activePlayer shouldBe opp
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Still attached, but the bonus and first strike are gone (not my turn).
        driver.state.getEntity(katana)?.get<AttachedToComponent>()?.targetId shouldBe courser
        projector.getProjectedPower(driver.state, courser) shouldBe 3
        projector.getProjectedToughness(driver.state, courser) shouldBe 3
        projector.project(driver.state).hasKeyword(courser, Keyword.FIRST_STRIKE) shouldBe false
    }
})

package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.eoe.cards.ArchenemysCharm
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Archenemy's Charm — {B}{B}{B} Instant, modal "Choose one —".
 *
 * Mode 1: exile target creature or planeswalker.
 * Mode 2: return one or two target creature and/or planeswalker cards from your graveyard to hand.
 * Mode 3: two +1/+1 counters on target creature you control; it gains lifelink until end of turn.
 *
 * Mode 2 is the regression: its "one or two target" requirement spans two slots of the flat target
 * list, so the return has to run once per chosen slot ([ForEachTargetEffect] over
 * `ContextTarget(0)`). Handing the requirement's bound handle to a single-target `ReturnToHand`
 * resolved to nothing at all and the mode silently did nothing — with one card in the graveyard
 * and with two.
 */
class ArchenemysCharmScenarioTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(ArchenemysCharm)
        return driver
    }

    test("Mode 2 — returns a single creature card from the graveyard") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 30), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val me = driver.activePlayer!!
        val dead = driver.putCardInGraveyard(me, "Centaur Courser")

        val spell = driver.putCardInHand(me, "Archenemy's Charm")
        driver.giveMana(me, Color.BLACK, 3)

        val targets = listOf<ChosenTarget>(ChosenTarget.Card(dead, me, Zone.GRAVEYARD))
        driver.submit(
            CastSpell(
                playerId = me,
                cardId = spell,
                targets = targets,
                chosenModes = listOf(1),
                modeTargetsOrdered = listOf(targets)
            )
        ).isSuccess shouldBe true
        driver.bothPass()

        driver.getHand(me).contains(dead) shouldBe true
        driver.getGraveyard(me).contains(dead) shouldBe false
    }

    test("Mode 2 — returns two creature cards from the graveyard") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 30), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val me = driver.activePlayer!!
        val first = driver.putCardInGraveyard(me, "Centaur Courser")
        val second = driver.putCardInGraveyard(me, "Grizzly Bears")

        val spell = driver.putCardInHand(me, "Archenemy's Charm")
        driver.giveMana(me, Color.BLACK, 3)

        val targets = listOf<ChosenTarget>(
            ChosenTarget.Card(first, me, Zone.GRAVEYARD),
            ChosenTarget.Card(second, me, Zone.GRAVEYARD)
        )
        driver.submit(
            CastSpell(
                playerId = me,
                cardId = spell,
                targets = targets,
                chosenModes = listOf(1),
                modeTargetsOrdered = listOf(targets)
            )
        ).isSuccess shouldBe true
        driver.bothPass()

        driver.getHand(me).containsAll(listOf(first, second)) shouldBe true
    }

    test("Mode 2 — the mode is offered with a single legal target, min 1 / max 2") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 30), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val me = driver.activePlayer!!
        val dead = driver.putCardInGraveyard(me, "Centaur Courser")
        driver.putCardInHand(me, "Archenemy's Charm")
        driver.giveMana(me, Color.BLACK, 3)

        val action = driver.legalActions(me).single { it.description.startsWith("Return one or two") }
        action.minTargets shouldBe 1
        action.targetCount shouldBe 2
        action.validTargets shouldBe listOf(dead)
    }

    test("Mode 1 — exiles target creature") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 30), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)
        val victim = driver.putCreatureOnBattlefield(opponent, "Centaur Courser")

        val spell = driver.putCardInHand(me, "Archenemy's Charm")
        driver.giveMana(me, Color.BLACK, 3)

        val targets = listOf<ChosenTarget>(ChosenTarget.Permanent(victim))
        driver.submit(
            CastSpell(
                playerId = me,
                cardId = spell,
                targets = targets,
                chosenModes = listOf(0),
                modeTargetsOrdered = listOf(targets)
            )
        ).isSuccess shouldBe true
        driver.bothPass()

        driver.getExile(opponent).contains(victim) shouldBe true
    }

    test("Mode 3 — two +1/+1 counters and lifelink until end of turn") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 30), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val me = driver.activePlayer!!
        val mine = driver.putCreatureOnBattlefield(me, "Centaur Courser") // 3/3

        val spell = driver.putCardInHand(me, "Archenemy's Charm")
        driver.giveMana(me, Color.BLACK, 3)

        val targets = listOf<ChosenTarget>(ChosenTarget.Permanent(mine))
        driver.submit(
            CastSpell(
                playerId = me,
                cardId = spell,
                targets = targets,
                chosenModes = listOf(2),
                modeTargetsOrdered = listOf(targets)
            )
        ).isSuccess shouldBe true
        driver.bothPass()

        projector.getProjectedPower(driver.state, mine) shouldBe 5
        projector.getProjectedToughness(driver.state, mine) shouldBe 5
        driver.state.projectedState.hasKeyword(mine, com.wingedsheep.sdk.core.Keyword.LIFELINK) shouldBe true
    }
})

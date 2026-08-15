package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.j22.cards.IngeniousLeonin
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Ingenious Leonin (J22, reprinted in FDN) — {4}{W} Creature — Cat Soldier, 4/4.
 *
 * "{3}{W}: Put a +1/+1 counter on another target attacking creature you control. If that creature
 *  is a Cat, it gains first strike until end of turn."
 *
 * The counter always lands; the first-strike grant is gated on the chosen target being a Cat. These
 * tests pin both halves: a Cat attacker ends up +1/+1 *and* first striking, a non-Cat attacker gets
 * only the counter, and the ability can't target a non-attacking creature.
 */
class IngeniousLeoninScenarioTest : FunSpec({

    val catAttacker = card("Test Cat Attacker") {
        manaCost = "{1}{W}"
        typeLine = "Creature — Cat"
        power = 2
        toughness = 2
    }
    val dogAttacker = card("Test Dog Attacker") {
        manaCost = "{1}{W}"
        typeLine = "Creature — Dog"
        power = 2
        toughness = 2
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(IngeniousLeonin, catAttacker, dogAttacker))
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        return driver
    }

    fun GameTestDriver.resolveStack() {
        var guard = 0
        while (state.stack.isNotEmpty() && guard < 50) {
            bothPass()
            guard++
        }
    }

    /** Put Leonin + [attackerCard] into play, declare [attackerCard] as an attacker, and return its id. */
    fun GameTestDriver.setUpAttacker(attackerCard: String): Pair<EntityId, EntityId> {
        val me = activePlayer!!
        val opponent = getOpponent(me)
        val leonin = putCreatureOnBattlefield(me, "Ingenious Leonin")
        val attacker = putCreatureOnBattlefield(me, attackerCard)
        removeSummoningSickness(leonin)
        removeSummoningSickness(attacker)
        passPriorityUntil(Step.DECLARE_ATTACKERS)
        declareAttackers(me, listOf(attacker), opponent)
        giveMana(me, Color.WHITE, 4)
        return leonin to attacker
    }

    fun GameTestDriver.activateLeonin(leonin: EntityId, target: EntityId) {
        val abilityId = IngeniousLeonin.activatedAbilities[0].id
        submitSuccess(
            ActivateAbility(
                playerId = activePlayer!!,
                sourceId = leonin,
                abilityId = abilityId,
                targets = listOf(ChosenTarget.Permanent(target)),
            ),
        )
        resolveStack()
    }

    test("targeting a Cat attacker: +1/+1 counter and first strike") {
        val driver = createDriver()
        val (leonin, cat) = driver.setUpAttacker("Test Cat Attacker")

        driver.activateLeonin(leonin, cat)

        driver.state.projectedState.getPower(cat) shouldBe 3
        driver.state.projectedState.getToughness(cat) shouldBe 3
        driver.state.projectedState.hasKeyword(cat, Keyword.FIRST_STRIKE) shouldBe true
    }

    test("targeting a non-Cat attacker: +1/+1 counter but no first strike") {
        val driver = createDriver()
        val (leonin, dog) = driver.setUpAttacker("Test Dog Attacker")

        driver.activateLeonin(leonin, dog)

        driver.state.projectedState.getPower(dog) shouldBe 3
        driver.state.projectedState.getToughness(dog) shouldBe 3
        driver.state.projectedState.hasKeyword(dog, Keyword.FIRST_STRIKE) shouldBe false
    }
})

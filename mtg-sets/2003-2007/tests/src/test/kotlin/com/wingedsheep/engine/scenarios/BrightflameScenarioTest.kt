package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.state.components.battlefield.DamageComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.rav.cards.*
import com.wingedsheep.mtg.sets.definitions.drk.cards.BloodOfTheMartyr
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class BrightflameScenarioTest : FunSpec({
    fun driver(): GameTestDriver = GameTestDriver().apply {
        registerCards(TestCards.all + listOf(Brightflame, Watchwolf, LightOfSanction, GhostsOfTheInnocent, Phytohydra, BloodOfTheMartyr))
        initMirrorMatch(Deck.of("Mountain" to 40), skipMulligans = true, startingPlayer = 0)
        passPriorityUntil(Step.PRECOMBAT_MAIN)
    }

    fun GameTestDriver.cast(target: EntityId, x: Int) {
        giveMana(player1, Color.RED, x + 2)
        giveMana(player1, Color.WHITE, 2)
        castXSpell(player1, putCardInHand(player1, "Brightflame"), x, listOf(target)).error shouldBe null
    }

    fun GameTestDriver.resolve() {
        var passes = 0
        while (stackSize > 0 && passes++ < 20) bothPass()
        stackSize shouldBe 0
    }

    fun GameTestDriver.damage(id: EntityId) = state.getEntity(id)?.get<DamageComponent>()?.amount ?: 0

    test("radiance counts each matching creature once across both players and both colors") {
        val d = driver()
        val target = d.putCreatureOnBattlefield(d.player2, "Watchwolf")
        val green = d.putCreatureOnBattlefield(d.player1, "Centaur Courser")
        val white = d.putCreatureOnBattlefield(d.player2, "Savannah Lions")
        val red = d.putCreatureOnBattlefield(d.player2, "Goblin Guide")
        d.cast(target, 1)
        d.resolve()
        d.getLifeTotal(d.player1) shouldBe 23
        d.damage(target) shouldBe 1
        d.damage(green) shouldBe 1
        d.getGraveyard(d.player2).contains(white) shouldBe true
        d.damage(red) shouldBe 0
    }

    test("a colorless target is damaged without affecting other colorless creatures") {
        val d = driver()
        val target = d.putCreatureOnBattlefield(d.player2, "Artifact Creature")
        val other = d.putCreatureOnBattlefield(d.player1, "Artifact Creature")
        d.cast(target, 1)
        d.resolve()
        d.getLifeTotal(d.player1) shouldBe 21
        d.damage(target) shouldBe 1
        d.damage(other) shouldBe 0
    }

    test("damage beyond lethal still counts fully toward life gain") {
        val d = driver()
        val target = d.putCreatureOnBattlefield(d.player2, "Watchwolf")
        d.putCreatureOnBattlefield(d.player1, "Centaur Courser")
        d.cast(target, 5)
        d.resolve()
        d.getLifeTotal(d.player1) shouldBe 30
    }

    test("prevented damage is excluded from life gain") {
        val d = driver()
        val target = d.putCreatureOnBattlefield(d.player2, "Watchwolf")
        val protected = d.putCreatureOnBattlefield(d.player1, "Centaur Courser")
        d.putPermanentOnBattlefield(d.player1, "Light of Sanction")
        d.cast(target, 2)
        d.resolve()
        d.damage(protected) shouldBe 0
        d.damage(target) shouldBe 2
        d.getLifeTotal(d.player1) shouldBe 22
    }

    test("damage replacement changes the amount gained") {
        val d = driver()
        val target = d.putCreatureOnBattlefield(d.player2, "Ghosts of the Innocent")
        d.cast(target, 3)
        d.resolve()
        d.damage(target) shouldBe 1
        d.getLifeTotal(d.player1) shouldBe 21
    }

    test("damage replaced with counters is not damage dealt") {
        val d = driver()
        val target = d.putCreatureOnBattlefield(d.player2, "Phytohydra")
        d.cast(target, 3)
        d.resolve()
        d.damage(target) shouldBe 0
        d.getLifeTotal(d.player1) shouldBe 20
    }

    test("life gain survives multiple damage redirection decisions") {
        val d = driver()
        val target = d.putCreatureOnBattlefield(d.player2, "Watchwolf")
        val other = d.putCreatureOnBattlefield(d.player1, "Centaur Courser")
        d.giveMana(d.player1, Color.WHITE, 3)
        d.castSpell(d.player1, d.putCardInHand(d.player1, "Blood of the Martyr")).error shouldBe null
        d.resolve()
        d.cast(target, 2)
        d.bothPass()
        (d.pendingDecision is YesNoDecision) shouldBe true
        d.submitYesNo(d.player1, false).error shouldBe null
        (d.pendingDecision is YesNoDecision) shouldBe true
        d.submitYesNo(d.player1, true).error shouldBe null
        if (d.stackSize > 0) d.resolve()
        d.damage(target) shouldBe 2
        d.damage(other) shouldBe 0
        // Two damage is redirected to the caster; all four damage was still dealt by Brightflame.
        d.getLifeTotal(d.player1) shouldBe 22
    }

    test("X zero deals no damage and gains no life") {
        val d = driver()
        val target = d.putCreatureOnBattlefield(d.player2, "Watchwolf")
        d.cast(target, 0)
        d.resolve()
        d.damage(target) shouldBe 0
        d.getLifeTotal(d.player1) shouldBe 20
    }

    test("an illegal sole target prevents all damage and life gain") {
        val d = driver()
        val target = d.putCreatureOnBattlefield(d.player2, "Watchwolf")
        val other = d.putCreatureOnBattlefield(d.player1, "Centaur Courser")
        d.cast(target, 2)
        d.moveToGraveyard(target)
        d.resolve()
        d.damage(other) shouldBe 0
        d.getLifeTotal(d.player1) shouldBe 20
    }
})

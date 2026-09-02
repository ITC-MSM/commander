package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.DamageComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.rav.cards.RaziaBorosArchangel
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Razia, Boros Archangel (RAV #223) — "{T}: The next 3 damage that would be dealt to target
 * creature you control this turn is dealt to another target creature instead."
 *
 * The first test is the shield itself; the second is the ruling "if either target creature leaves
 * the battlefield before damage is dealt, that damage won't be redirected" — before the recipient
 * liveness check in `OptionalDamageRedirect.redirectShieldCovers`, damage aimed at the shielded
 * creature was redirected into a recipient that no longer existed and silently vanished.
 */
class RaziaBorosArchangelScenarioTest : FunSpec({

    val abilityId = RaziaBorosArchangel.activatedAbilities.single().id

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + RaziaBorosArchangel)
        d.initMirrorMatch(deck = Deck.of("Mountain" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    fun GameTestDriver.damageOn(id: EntityId): Int = state.getEntity(id)?.get<DamageComponent>()?.amount ?: 0

    fun GameTestDriver.shield(me: EntityId, razia: EntityId, shielded: EntityId, recipient: EntityId) {
        submit(
            ActivateAbility(
                playerId = me,
                sourceId = razia,
                abilityId = abilityId,
                targets = listOf(ChosenTarget.Permanent(shielded), ChosenTarget.Permanent(recipient)),
            )
        ).isSuccess shouldBe true
        var guard = 0
        while (stackSize > 0 && guard++ < 10) bothPass()
    }

    fun GameTestDriver.bolt(caster: EntityId, target: EntityId) {
        giveMana(caster, Color.RED, 1)
        val bolt = putCardInHand(caster, "Lightning Bolt")
        castSpellWithTargets(caster, bolt, listOf(ChosenTarget.Permanent(target))).isSuccess shouldBe true
        var guard = 0
        while (stackSize > 0 && guard++ < 10) bothPass()
    }

    test("the next 3 damage to the shielded creature is dealt to the other creature instead") {
        val d = driver()
        val me = d.player1
        val opp = d.player2
        val razia = d.putCreatureOnBattlefield(me, "Razia, Boros Archangel")
        d.removeSummoningSickness(razia)
        val bears = d.putCreatureOnBattlefield(me, "Grizzly Bears")
        val courser = d.putCreatureOnBattlefield(opp, "Centaur Courser")

        d.shield(me, razia, shielded = bears, recipient = courser)
        d.isTapped(razia) shouldBe true

        d.bolt(me, bears)

        withClue("the Bears took none of the damage") {
            d.findPermanent(me, "Grizzly Bears").shouldNotBeNull()
            d.damageOn(bears) shouldBe 0
        }
        withClue("the 3 damage went to the Courser, which is lethal for a 3/3") {
            d.findPermanent(opp, "Centaur Courser") shouldBe null
            d.getGraveyardCardNames(opp) shouldContain "Centaur Courser"
        }
    }

    test("the recipient leaving the battlefield first means the damage is not redirected") {
        val d = driver()
        val me = d.player1
        val opp = d.player2
        val razia = d.putCreatureOnBattlefield(me, "Razia, Boros Archangel")
        d.removeSummoningSickness(razia)
        val bears = d.putCreatureOnBattlefield(me, "Grizzly Bears")
        val oppBears = d.putCreatureOnBattlefield(opp, "Grizzly Bears")

        d.shield(me, razia, shielded = bears, recipient = oppBears)

        // The recipient dies before any damage reaches the shielded creature.
        d.bolt(me, oppBears)
        d.findPermanent(opp, "Grizzly Bears") shouldBe null

        // Now the shielded creature is bolted: nothing is left to redirect to, so it takes the 3.
        d.bolt(me, bears)
        withClue("the ruling: the damage is dealt to the shielded creature after all") {
            d.findPermanent(me, "Grizzly Bears") shouldBe null
            d.getGraveyardCardNames(me) shouldContain "Grizzly Bears"
        }
    }
})

package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.rav.cards.PariahsShield
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Pariah's Shield — {5} Artifact — Equipment (Ravnica: City of Guilds #267)
 *
 * "All damage that would be dealt to you is dealt to equipped creature instead.
 *  Equip {3}"
 *
 * The whole card is one static `RedirectDamage` pointed at `EffectTarget.EquippedCreature`, so
 * the two things worth proving are that the redirect fires at all, and that it stops firing when
 * the Shield has nothing to redirect *to* — the card's own ruling: "If Pariah's Shield isn't
 * attached to a creature, all damage that would be dealt to you is dealt to you normally."
 */
class PariahsShieldScenarioTest : FunSpec({

    val equipAbilityId = PariahsShield.activatedAbilities.single().id

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + PariahsShield)
        d.initMirrorMatch(deck = Deck.of("Mountain" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    /** Puts the Shield and [creatureName] on player 1's battlefield and equips them. */
    fun GameTestDriver.equipTo(creatureName: String): Pair<EntityId, EntityId> {
        val shield = putPermanentOnBattlefield(player1, "Pariah's Shield")
        val creature = putCreatureOnBattlefield(player1, creatureName)
        giveColorlessMana(player1, 3)
        submit(
            ActivateAbility(
                playerId = player1,
                sourceId = shield,
                abilityId = equipAbilityId,
                targets = listOf(ChosenTarget.Permanent(creature))
            )
        ).isSuccess shouldBe true
        bothPass()
        state.getEntity(shield)?.get<AttachedToComponent>()?.targetId shouldBe creature
        return shield to creature
    }

    test("damage aimed at you lands on the equipped creature instead") {
        val d = driver()
        d.equipTo("Savannah Lions") // 1/1

        val bolt = d.putCardInHand(d.player1, "Lightning Bolt")
        d.giveMana(d.player1, Color.RED, 1)
        d.castSpell(d.player1, bolt, listOf(d.player1)).error shouldBe null
        d.bothPass()

        withClue("the 3 damage never reached the player") {
            d.getLifeTotal(d.player1) shouldBe 20
        }
        withClue("it went to the equipped creature, which 3 damage kills") {
            d.findPermanent(d.player1, "Savannah Lions") shouldBe null
        }
    }

    test("with nothing equipped the damage is dealt to you normally") {
        val d = driver()
        // On the battlefield but never equipped: the redirect has nowhere to point.
        val shield = d.putPermanentOnBattlefield(d.player1, "Pariah's Shield")

        val bolt = d.putCardInHand(d.player1, "Lightning Bolt")
        d.giveMana(d.player1, Color.RED, 1)
        d.castSpell(d.player1, bolt, listOf(d.player1)).error shouldBe null
        d.bothPass()

        d.state.getEntity(shield)?.get<AttachedToComponent>() shouldBe null
        withClue("no equipped creature, so nothing absorbs the damage") {
            d.getLifeTotal(d.player1) shouldBe 17
        }
    }
})

package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.battlefield.DamageComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * CR 608.2b/608.2c regression: legality is checked before the spell begins to resolve, but a
 * target made illegal by an earlier instruction is simply unaffected by the later instruction.
 * Other legal targets of that later instruction must still be affected.
 */
class ModalIntraResolutionPartialTargetTest : FunSpec({

    val destroyThenSpray: CardDefinition = card("Destroy Then Spray") {
        manaCost = "{R}"
        typeLine = "Sorcery"
        spell {
            modal(chooseCount = 2, minChooseCount = 2) {
                mode("Destroy target creature") {
                    val creature = target("creature to destroy", TargetCreature())
                    effect = Effects.Destroy(creature)
                }
                mode("Deal 1 damage to each of up to two target creatures") {
                    target("creatures to damage", TargetCreature(count = 2, minCount = 1))
                    effect = ForEachTargetEffect(
                        listOf(Effects.DealDamage(1, EffectTarget.ContextTarget(0)))
                    )
                }
            }
        }
    }

    fun driver(): GameTestDriver = GameTestDriver().also {
        it.registerCards(TestCards.all + listOf(destroyThenSpray))
    }

    test("later modal mode damages its remaining legal target after an earlier mode destroys the other") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Mountain" to 20))
        val p1 = d.activePlayer!!
        val p2 = d.getOpponent(p1)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        d.putCreatureOnBattlefield(p2, "Savannah Lions")
        d.putCreatureOnBattlefield(p2, "Centaur Courser")
        d.giveMana(p1, Color.RED, 1)
        val spell = d.putCardInHand(p1, "Destroy Then Spray")
        val lions = d.findPermanent(p2, "Savannah Lions")!!
        val courser = d.findPermanent(p2, "Centaur Courser")!!

        d.submit(
            CastSpell(
                playerId = p1,
                cardId = spell,
                targets = listOf(
                    ChosenTarget.Permanent(lions),
                    ChosenTarget.Permanent(lions),
                    ChosenTarget.Permanent(courser),
                ),
                chosenModes = listOf(0, 1),
                modeTargetsOrdered = listOf(
                    listOf(ChosenTarget.Permanent(lions)),
                    listOf(ChosenTarget.Permanent(lions), ChosenTarget.Permanent(courser)),
                ),
            )
        ).isSuccess shouldBe true

        d.bothPass()

        d.findPermanent(p2, "Savannah Lions") shouldBe null
        (d.state.getEntity(courser)?.get<DamageComponent>()?.amount ?: 0) shouldBe 1
    }
})

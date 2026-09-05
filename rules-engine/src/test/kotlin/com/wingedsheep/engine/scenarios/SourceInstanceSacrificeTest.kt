package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/** Old source references must not sacrifice a new battlefield visit of the same card. */
class SourceInstanceSacrificeTest : FunSpec({
    val sourceCard = card("Source Sacrifice Probe") {
        manaCost = "{U}"
        typeLine = "Creature — Shapeshifter"
        power = 1
        toughness = 1
        triggeredAbility {
            trigger = Triggers.EntersBattlefield
            optional = true
            effect = Effects.SacrificeTarget(com.wingedsheep.sdk.scripting.targets.EffectTarget.Self)
        }
    }
    val blink = card("Source Sacrifice Blink") {
        manaCost = "{U}"
        typeLine = "Instant"
        spell {
            val creature = target("creature", Targets.Creature)
            effect = Effects.Exile(creature).then(Effects.PutOntoBattlefield(creature))
        }
    }

    for (blinkSource in listOf(false, true)) {
        test("sacrifice refers to original source with blink=$blinkSource") {
            val d = GameTestDriver()
            d.registerCards(TestCards.all + listOf(sourceCard, blink))
            d.initMirrorMatch(Deck.of("Island" to 40), startingPlayer = 0)
            d.passPriorityUntil(Step.PRECOMBAT_MAIN)
            val source = d.putCardInHand(d.player1, "Source Sacrifice Probe")
            d.giveMana(d.player1, Color.BLUE, 1)
            d.castSpell(d.player1, source).error shouldBe null
            d.bothPass().error shouldBe null
            d.stackSize shouldBe 1

            if (blinkSource) {
                val flicker = d.putCardInHand(d.player1, "Source Sacrifice Blink")
                d.giveMana(d.player1, Color.BLUE, 1)
                d.castSpell(d.player1, flicker, listOf(source)).error shouldBe null
                d.bothPass().error shouldBe null
                d.stackSize shouldBe 2
                d.bothPass().error shouldBe null
                d.submitYesNo(d.player1, false).error shouldBe null
                d.stackSize shouldBe 1
            }
            d.bothPass().error shouldBe null
            d.submitYesNo(d.player1, true).error shouldBe null
            (source in d.state.getBattlefield()) shouldBe blinkSource
        }
    }

    test("an ability can modify a source that it returns itself") {
        val returning = card("Returning Source Probe") {
            manaCost = "{U}"
            typeLine = "Creature — Shapeshifter"
            power = 1
            toughness = 1
            triggeredAbility {
                trigger = Triggers.Dies
                effect = Effects.PutOntoBattlefield(com.wingedsheep.sdk.scripting.targets.EffectTarget.Self)
                    .then(Effects.ModifyStats(2, 0, com.wingedsheep.sdk.scripting.targets.EffectTarget.Self))
            }
        }
        val d = GameTestDriver()
        d.registerCards(TestCards.all + listOf(returning))
        d.initMirrorMatch(Deck.of("Island" to 40), startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val source = d.putCardInHand(d.player1, "Returning Source Probe")
        d.giveMana(d.player1, Color.BLUE, 1)
        d.castSpell(d.player1, source).error shouldBe null
        d.bothPass().error shouldBe null
        val bolt = d.putCardInHand(d.player1, "Lightning Bolt")
        d.giveMana(d.player1, Color.RED, 1)
        d.castSpell(d.player1, bolt, listOf(source)).error shouldBe null
        d.bothPass().error shouldBe null
        (source in d.state.getBattlefield()) shouldBe false
        d.bothPass().error shouldBe null
        (source in d.state.getBattlefield()) shouldBe true
        d.state.projectedState.getPower(source) shouldBe 3
        d.state.projectedState.getToughness(source) shouldBe 1
    }

})

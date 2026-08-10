package com.wingedsheep.mtg.sets

import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.predicates.ControllerPredicate
import com.wingedsheep.sdk.scripting.targets.TargetObject
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * The equip keyword and its "Equip [quality]" variants (CR 702.6a / 702.6c).
 *
 * CR 702.6a defines equip as "[Cost]: Attach this permanent to target creature you control.
 * Activate only as a sorcery." CR 702.6c allows a further restriction — "Equip [quality]" /
 * "Equip [quality] creature" — which "may legally target only a creature that's controlled by the
 * player activating the ability and that has the chosen quality."
 *
 * Two catalog-wide invariants follow, and both have been violated in this repo before:
 *
 *  1. **Every equip ability is flagged.** A quality-restricted equip authored as a bare
 *     `activatedAbility { }` is a real equip ability that the engine cannot see: everything that
 *     keys off `ActivatedAbility.isEquipAbility` — Forge Anew's free first equip, Eowyn's equip
 *     discount, Leonin Shikari's instant-speed-equip permission — silently skips it.
 *  2. **Every equip ability targets a creature *you control*.** The quality narrows the target
 *     set; it never widens it past the controller scope CR 702.6c states.
 *
 * Scoped to abilities already flagged `isEquipAbility` — a card whose attach happens on an ETB
 * trigger (Pirate's Cutlass, Super Suit) or a loyalty ability (The Aetherspark) is not an equip
 * ability and is correctly outside this.
 */
class EquipQualityVariantTest : FunSpec({

    val equipAbilities = MtgSetCatalog.all
        .flatMap { set -> set.cards }
        .flatMap { card -> card.script.activatedAbilities.filter { it.isEquipAbility }.map { card to it } }

    test("every equip ability is sorcery-speed and targets a single creature you control") {
        equipAbilities.size shouldNotBe 0
        assertSoftly {
            for ((card, ability) in equipAbilities) {
                withClue("${card.name}: ${ability.description}") {
                    ability.timing shouldBe TimingRule.SorcerySpeed
                    ability.targetRequirements.size shouldBe 1
                    val requirement = ability.targetRequirements.single()
                    withClue("equip targets a permanent, so the requirement is a TargetObject") {
                        (requirement is TargetObject) shouldBe true
                    }
                    val filter = (requirement as TargetObject).filter
                    withClue("CR 702.6c: an equip ability may target only a creature you control") {
                        filter.baseFilter.controllerPredicate shouldBe ControllerPredicate.ControlledByYou
                    }
                }
            }
        }
    }

    test("the cards with a target-restricted equip are authored through the equipAbility facade") {
        // Every card here activates a restricted equip through the facade, and each was a
        // hand-rolled activated ability missing `isEquipAbility` before the facade grew its
        // `quality`/`targetFilter` pair; this pins them to the shared rail.
        //
        // Only the first five are printed CR 702.6c "Equip [quality]" cards. Ghostfire Blade is
        // not: it prints one "Equip {3}" plus "costs {2} less to activate if it targets a
        // colorless creature", and its {1} ability is our *model* of that reduction, not a printed
        // quality restriction. It rides the same rail, so it belongs in this assertion — but the
        // label below describes our modelling, not its Oracle text.
        val restrictedEquipLabels = mapOf(
            "Blackblade Reforged" to "legendary creature you control",
            "Bilbo's Ring" to "Halfling creature you control",
            "Dúnedain Blade" to "Human creature you control",
            "Mjölnir, Hammer of Thor" to "worthy creature you control",
            "Pirate Hat" to "Pirate creature you control",
            // Modelled, not printed — see above.
            "Ghostfire Blade" to "colorless creature you control",
        )

        assertSoftly {
            for ((cardName, label) in restrictedEquipLabels) {
                val card = MtgSetCatalog.all.flatMap { it.cards }.firstOrNull { it.name == cardName }
                withClue("$cardName is in the catalog") { card shouldNotBe null }
                val labels = card!!.script.activatedAbilities
                    .filter { it.isEquipAbility }
                    .mapNotNull { it.targetRequirements.singleOrNull()?.id }
                withClue("$cardName has a flagged equip ability labelled '$label' (has: $labels)") {
                    (label in labels) shouldBe true
                }
            }
        }
    }
})

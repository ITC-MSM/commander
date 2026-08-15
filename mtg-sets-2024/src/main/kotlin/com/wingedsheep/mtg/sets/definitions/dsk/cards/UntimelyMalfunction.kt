package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Untimely Malfunction
 * {1}{R}
 * Instant
 * Choose one —
 * • Destroy target artifact.
 * • Change the target of target spell or ability with a single target.
 * • One or two target creatures can't block this turn.
 *
 * "Choose one" modal spell. Mode 2 reuses the change-target machinery from Willbender /
 * Return the Favor: [Effects.ChangeTarget] + [Targets.SpellOrAbilityWithSingleTarget], whose
 * executor enforces the single-target restriction and the "must change if possible" ruling at
 * resolution. Mode 3 ("one or two target creatures") is a variable target count (minCount = 1,
 * count = 2) with a per-target [Effects.CantBlock] applied via [ForEachTargetEffect] (same shape
 * as Amazing Acrobatics' "tap one or two target creatures").
 */
val UntimelyMalfunction = card("Untimely Malfunction") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Choose one —\n" +
        "• Destroy target artifact.\n" +
        "• Change the target of target spell or ability with a single target.\n" +
        "• One or two target creatures can't block this turn."

    spell {
        effect = ModalEffect(
            modes = listOf(
                Mode.withTarget(
                    effect = Effects.Destroy(EffectTarget.ContextTarget(0)),
                    target = Targets.Artifact,
                    description = "Destroy target artifact"
                ),
                Mode.withTarget(
                    effect = Effects.ChangeTarget(),
                    target = Targets.SpellOrAbilityWithSingleTarget,
                    description = "Change the target of target spell or ability with a single target"
                ),
                Mode.withTarget(
                    effect = ForEachTargetEffect(listOf(Effects.CantBlock(EffectTarget.ContextTarget(0)))),
                    target = TargetCreature(count = 2, minCount = 1),
                    description = "One or two target creatures can't block this turn"
                )
            ),
            chooseCount = 1,
            minChooseCount = 1
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "161"
        artist = "Jarel Threat"
        flavorText = "\"No! This worked perfectly in the lab!\""
        imageUri = "https://cards.scryfall.io/normal/front/8/5/857bfb0e-17dc-4dda-bc37-3df927a9eae6.jpg?1726286456"
    }
}

package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Windborne Charge
 * {2}{W}{W}
 * Sorcery
 * Two target creatures you control each get +2/+2 and gain flying until end of turn.
 *
 * "Two target creatures … each get" is one two-target requirement with a per-target body
 * ([ForEachTargetEffect]), so one target becoming illegal doesn't take the other with it.
 */
val WindborneCharge = card("Windborne Charge") {
    manaCost = "{2}{W}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Two target creatures you control each get +2/+2 and gain flying until end of turn."

    spell {
        target(
            "two target creatures you control",
            TargetCreature(count = 2, filter = TargetFilter.CreatureYouControl),
        )
        effect = ForEachTargetEffect(listOf(
            Effects.ModifyStats(2, 2, EffectTarget.ContextTarget(0)),
            Effects.GrantKeyword(Keyword.FLYING, EffectTarget.ContextTarget(0)),
        ))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "38"
        artist = "Ryan Pancoast"
        flavorText = "The merfolk call the sky goddess Emeria. The kor call her Kamsa. The two races agree on little except that she offers many blessings to the faithful."
        imageUri = "https://cards.scryfall.io/normal/front/9/a/9a8b0fd3-ae3e-41eb-b31b-d941b84b6c64.jpg"
    }
}

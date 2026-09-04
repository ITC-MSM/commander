package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Ethereal Guidance (Shadows over Innistrad #18)
 * {2}{W}
 * Sorcery
 *
 * Creatures you control get +2/+1 until end of turn.
 *
 * The team pump is the corpus's standard shape: [Effects.ForEachInGroup] over the creatures you
 * control, with the per-member body pumping the iteration's own member ([EffectTarget.Self]).
 */
val EtherealGuidance = card("Ethereal Guidance") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Creatures you control get +2/+1 until end of turn."

    spell {
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.youControl()),
            Effects.ModifyStats(2, 1, EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "18"
        artist = "Ben Maier"
        flavorText = "\"The call of the Blessed Sleep is not so strong as the call to protect those in need.\"\n—Saint Traft"
        imageUri = "https://cards.scryfall.io/normal/front/f/4/f47dd220-6193-4e31-a1df-591b6424ad27.jpg?1783937821"
    }
}

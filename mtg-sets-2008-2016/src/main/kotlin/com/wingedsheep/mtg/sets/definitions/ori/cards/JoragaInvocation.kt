package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.MustBeBlockedEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Joraga Invocation
 * {4}{G}{G}
 * Sorcery
 * Each creature you control gets +3/+3 until end of turn and must be blocked this turn if able.
 *
 * Both halves are per-creature, so the spell is one [Effects.ForEachInGroup] over the creatures
 * you control at resolution (creatures that arrive later are unaffected, CR 611.2c): a
 * `ModifyStats(3, 3)` floating effect plus a floating must-be-blocked requirement
 * ([MustBeBlockedEffect] with `allCreatures = false` — "must be blocked if able" needs only one
 * blocker, not the Lure-style every-able-blocker form). Both expire at end of turn.
 */
val JoragaInvocation = card("Joraga Invocation") {
    manaCost = "{4}{G}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Each creature you control gets +3/+3 until end of turn and must be blocked " +
        "this turn if able."

    spell {
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.youControl()),
            Effects.Composite(
                Effects.ModifyStats(3, 3, EffectTarget.Self),
                MustBeBlockedEffect(EffectTarget.Self, allCreatures = false),
            ),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "183"
        artist = "Kieran Yanner"
        flavorText = "\"A single tree does not a forest make. We are stronger when we stand " +
            "together.\"\n—Numa, Joraga chieftain"
        imageUri = "https://cards.scryfall.io/normal/front/6/5/65c89431-0881-4aa6-ac15-d4c13b075273.jpg?1783938321"
    }
}

package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.PreventDamageEffect

/**
 * Indestructible Aura
 * {W}
 * Instant
 *
 * Prevent all damage that would be dealt to target creature this turn.
 *
 * No `Effects.*` facade spells the plain "prevent all damage that would be dealt to
 * target this turn" shield — every parameter is [PreventDamageEffect]'s own default except the
 * recipient — so the card constructs it directly (it is not one of the facade-boundary types).
 */
val IndestructibleAura = card("Indestructible Aura") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Prevent all damage that would be dealt to target creature this turn."

    spell {
        val creature = target("target creature", Targets.Creature)
        effect = PreventDamageEffect(target = creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "21"
        artist = "Mark Poole"
        flavorText = "Theodar strode the battle lines, snatching swords with his bare hands and casting them " +
            "aside until all cowered before him."
        imageUri = "https://cards.scryfall.io/normal/front/e/d/ed2a7333-c9ce-4011-b00e-1304e1eec25e.jpg?1783948084"
    }
}

package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Heal the Scars
 * {3}{G}
 * Instant
 *
 * Regenerate target creature. You gain life equal to that creature's toughness.
 *
 * Both clauses read the same target, so the life gain is an
 * [DynamicAmount.EntityProperty] on [EntityReference.Target] rather than a fixed number — it is
 * measured on resolution, after the regeneration shield goes up, so a pumped or shrunken creature
 * pays out its current toughness. There is no `Effects.Regenerate` facade; [RegenerateEffect] is
 * the shipped spelling (see Reknit).
 */
val HealTheScars = card("Heal the Scars") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Regenerate target creature. You gain life equal to that creature's toughness."

    spell {
        val t = target("target creature", Targets.Creature)
        effect = Effects.Composite(
            RegenerateEffect(t),
            Effects.GainLife(
                DynamicAmount.EntityProperty(EntityReference.Target(0), EntityNumericProperty.Toughness)
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "217"
        artist = "Carl Frank"
        flavorText = "Elvish battle-magic has evolved two specialties: inflicting wounds that scar, " +
            "and healing wounds without scarring. Politics determines the recipients of each."
        imageUri = "https://cards.scryfall.io/normal/front/b/8/b802a7fe-9c8b-4bc3-95d8-4a5dafcf2c75.jpg?1783942862"
    }
}

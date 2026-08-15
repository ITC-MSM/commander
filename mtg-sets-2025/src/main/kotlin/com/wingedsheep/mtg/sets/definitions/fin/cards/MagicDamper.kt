package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature


/**
 * Magic Damper
 * {U}
 * Instant
 * Target creature you control gets +1/+1 and gains hexproof until end of turn. Untap it.
 */
val MagicDamper = card("Magic Damper") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Target creature you control gets +1/+1 and gains hexproof until end of turn. Untap it."
    spell {
        val t = target("target", TargetCreature(filter = TargetFilter.Creature.youControl()))
        effect = Effects.Composite(
            Effects.ModifyStats(1, 1, t),
            Effects.GrantKeyword(Keyword.HEXPROOF, t),
            Effects.Untap(t)
        )
    }
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "61"
        artist = "YASUNARI HIRASAKA"
        flavorText = "\"You're gonna wish you hadn't.\""
        imageUri = "https://cards.scryfall.io/normal/front/4/4/44921b2e-5938-4f63-92b9-0b719a2f8c68.jpg"
    }
}

package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Daring Escape
 * {R}
 * Instant
 * Target creature gets +1/+0 and gains first strike until end of turn. Scry 1.
 */
val DaringEscape = card("Daring Escape") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Target creature gets +1/+0 and gains first strike until end of turn. Scry 1."

    spell {
        val creature = target("target creature to get +1/+0 and first strike", TargetCreature())
        effect = Effects.ModifyStats(1, 0, creature)
            .then(Effects.GrantKeyword(Keyword.FIRST_STRIKE, creature))
            .then(Effects.Scry(1))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "104"
        artist = "Ekaterina Burmak"
        flavorText = "As the Adversary's forces descended upon the gala, Elspeth whispered to Giada, \"I can get you out of here, but we have to go now.\""
        imageUri = "https://cards.scryfall.io/normal/front/3/d/3d09dbcd-188b-4b52-943e-947cbf2c0002.jpg?1783923120"
    }
}

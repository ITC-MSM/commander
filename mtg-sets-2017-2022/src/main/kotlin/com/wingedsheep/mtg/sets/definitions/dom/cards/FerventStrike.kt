package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Fervent Strike
 * {R}
 * Instant
 * Target creature gets +1/+0 and gains first strike and haste until end of turn.
 */
val FerventStrike = card("Fervent Strike") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Target creature gets +1/+0 and gains first strike and haste until end of turn."

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.ModifyStats(1, 0, t)
            .then(Effects.GrantKeyword(Keyword.FIRST_STRIKE, t))
            .then(Effects.GrantKeyword(Keyword.HASTE, t))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "117"
        artist = "Winona Nelson"
        flavorText = "\"The flame paints a bright wheel on the sky, then shifts into a stabbing spark. The enemy falls; the smoke of victory rises.\"—\"Legends of the Firedancer\""
        imageUri = "https://cards.scryfall.io/normal/front/e/4/e4f76e51-a726-4a5f-ae2b-2e826e89f5f7.jpg?1591104513"
    }
}

package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Rebellious Strike
 * {1}{W}
 * Instant
 * Target creature gets +3/+0 until end of turn.
 * Draw a card.
 */
val RebelliousStrike = card("Rebellious Strike") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Target creature gets +3/+0 until end of turn.\nDraw a card."

    spell {
        val t = target("target", TargetCreature(filter = TargetFilter.Creature))
        effect = Effects.ModifyStats(power = 3, toughness = 0, target = t)
            .then(Effects.DrawCards(1))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "20"
        artist = "Evyn Fong"
        flavorText = "\"Even if you battle unseen, even if there is no one to witness your sacrifice—strike! Strike true. Strike from the heart. Let that be your legacy.\"\n—Shiko"
        imageUri = "https://cards.scryfall.io/normal/front/c/9/c9bafe19-3bd6-4da0-b3e5-e0b89262504c.jpg?1743204030"
    }
}

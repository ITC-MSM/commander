package com.wingedsheep.mtg.sets.definitions.isd.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Bramblecrush
 * {2}{G}{G}
 * Sorcery
 * Destroy target noncreature permanent.
 *
 * Canonical printing: Innistrad, the card's earliest real printing. Reprinted in Magic 2014.
 */
val Bramblecrush = card("Bramblecrush") {
    manaCost = "{2}{G}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Destroy target noncreature permanent."

    spell {
        val t = target("target", TargetPermanent(filter = TargetFilter.NoncreaturePermanent))
        effect = Effects.Destroy(t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "172"
        artist = "Drew Baker"
        flavorText = "\"Civilization is fertilizer.\"\n" +
            "—Garruk Wildspeaker"
        imageUri = "https://cards.scryfall.io/normal/front/6/0/60fa219e-5dba-4d49-9cae-40d254f140e4.jpg"
    }
}

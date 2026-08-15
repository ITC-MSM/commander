package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature


/**
 * Blitzball Shot
 * {1}{G}
 * Instant
 * Target creature gets +3/+3 and gains trample until end of turn.
 */
val BlitzballShot = card("Blitzball Shot") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Target creature gets +3/+3 and gains trample until end of turn."
    spell {
        val t = target("target", TargetCreature(filter = TargetFilter.Creature))
        effect = Effects.Composite(
            Effects.ModifyStats(3, 3, t),
            Effects.GrantKeyword(Keyword.TRAMPLE, t)
        )
    }
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "176"
        artist = "JUGEMT"
        flavorText = "\"Everyone seems to be calling for Wakka, folks!\"\n—Blitzball announcer"
        imageUri = "https://cards.scryfall.io/normal/front/e/c/ec8ab637-3d7d-4712-9f83-8920f808f715.jpg"
    }
}

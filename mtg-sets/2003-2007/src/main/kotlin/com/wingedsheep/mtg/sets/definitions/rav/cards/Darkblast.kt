package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature


/**
 * Darkblast
 * {B}
 * Instant
 * Target creature gets -1/-1 until end of turn.
 * Dredge 3 (If you would draw a card, you may mill three cards instead. If you do, return this card from your graveyard to your hand.)
 */
val Darkblast = card("Darkblast") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Target creature gets -1/-1 until end of turn.\nDredge 3 (If you would draw a card, you may mill three cards instead. If you do, return this card from your graveyard to your hand.)"
    spell {
        val t = target("target", TargetCreature(filter = TargetFilter.Creature))
        effect = Effects.ModifyStats(-1, -1, t)
    }
    keywordAbility(KeywordAbility.dredge(3))
    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "82"
        artist = "Randy Gallegos"
        imageUri = "https://cards.scryfall.io/normal/front/0/d/0dcaba91-06d3-4492-9e07-36a1b858ca47.jpg?1783943673"
    }
}

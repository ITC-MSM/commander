package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Veteran Cathar (Shadows over Innistrad #238)
 * {1}{G}
 * Creature — Human Soldier
 * 2 / 2
 *
 * {3}{W}: Target Human gains double strike until end of turn.
 *
 * "Human" is a **bare tribal noun**, so it names every *permanent* with the subtype rather than
 * only a creature — [TargetFilter.Permanent] with the subtype, not `TargetFilter.Creature`.
 */
val VeteranCathar = card("Veteran Cathar") {
    manaCost = "{1}{G}"
    colorIdentity = "GW"
    typeLine = "Creature — Human Soldier"
    power = 2
    toughness = 2
    oracleText = "{3}{W}: Target Human gains double strike until end of turn."

    activatedAbility {
        cost = Costs.Mana("{3}{W}")
        val t = target("target", TargetPermanent(filter = TargetFilter.Permanent.withSubtype("Human")))
        effect = Effects.GrantKeyword(Keyword.DOUBLE_STRIKE, t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "238"
        artist = "Deruchenko Alexander"
        flavorText = "\"Prayers can be powerful, but I prefer to put faith in my own two hands.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/4/a471e83e-c0e0-4af6-bfcf-7f4a39f6fccf.jpg?1783937716"
    }
}

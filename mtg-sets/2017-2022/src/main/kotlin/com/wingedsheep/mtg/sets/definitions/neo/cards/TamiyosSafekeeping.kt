package com.wingedsheep.mtg.sets.definitions.neo.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Tamiyo's Safekeeping — Kamigawa: Neon Dynasty #211 (canonical printing)
 * {G} · Instant
 *
 * Target permanent you control gains hexproof and indestructible until end of turn. You gain 2 life.
 *
 * "Permanent", not "creature": it protects a Vehicle, an equipped Equipment or a Saga just as well,
 * which is the point in a set whose threats are as often artifacts as bodies.
 */
val TamiyosSafekeeping = card("Tamiyo's Safekeeping") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Target permanent you control gains hexproof and indestructible until end of " +
        "turn. You gain 2 life. (A permanent with hexproof and indestructible can't be the target " +
        "of spells or abilities your opponents control. Damage and effects that say \"destroy\" " +
        "don't destroy it.)"

    spell {
        val t = target(
            "permanent you control",
            TargetPermanent(filter = TargetFilter.PermanentYouControl),
        )
        effect = (
            Effects.GrantKeyword(Keyword.HEXPROOF, t) then
                Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, t)
            ) then Effects.GainLife(2)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "211"
        artist = "Aurore Folny"
        flavorText = "To keep the Reality Chip out of Jin-Gitaxias's hands, the Wanderer entrusted " +
            "it to Tamiyo."
        imageUri = "https://cards.scryfall.io/normal/front/f/d/fd4b7ee2-de65-4288-872d-486065a4f226.jpg?1783923839"
    }
}

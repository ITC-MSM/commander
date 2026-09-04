package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Soulbound Guardians
 * {4}{W}
 * Creature — Kor Spirit
 * 4 / 5
 *
 * Defender, flying
 *
 * Modeling notes:
 *  - Two vanilla keywords printed on one line; `keywords(Keyword.DEFENDER, Keyword.FLYING)` matches
 *    the printed "Defender, flying" wording (see Monastery Flock for the same shape).
 */
val SoulboundGuardians = card("Soulbound Guardians") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Kor Spirit"
    power = 4
    toughness = 5
    oracleText = "Defender, flying"

    keywords(Keyword.DEFENDER, Keyword.FLYING)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "45"
        artist = "Erica Yang"
        flavorText = "The kor's most righteous dead are given the greatest reward: an eternity tied to the land they so cherish."
        imageUri = "https://cards.scryfall.io/normal/front/6/2/62e8128f-9858-4c48-ab43-1beca3db70e5.jpg?1783942003"
    }
}

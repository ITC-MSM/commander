package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Marhault Elsdragon
 * {3}{R}{R}{G}
 * Legendary Creature — Elf Warrior
 * 4/6
 *
 * Rampage 1 (Whenever this creature becomes blocked, it gets +1/+1 until end of turn for each creature blocking it beyond the first.)
 *
 * Rampage is wired by the [card] builder's `rampage(n)` helper: the printed keyword
 * ability is display-only, and the +N/+N-per-extra-blocker behaviour lives in the
 * "becomes blocked" triggered ability the helper installs alongside it.
 */
val MarhaultElsdragon = card("Marhault Elsdragon") {
    manaCost = "{3}{R}{R}{G}"
    colorIdentity = "GR"
    typeLine = "Legendary Creature — Elf Warrior"
    power = 4
    toughness = 6
    oracleText = "Rampage 1 (Whenever this creature becomes blocked, it gets +1/+1 until end of turn for each " +
        "creature blocking it beyond the first.)"

    rampage(1)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "244"
        artist = "Mark Poole"
        flavorText = "Marhault follows a strict philosophy, never letting emotions cloud his thoughts. No chance " +
            "observer could imagine the rage in his heart."
        imageUri = "https://cards.scryfall.io/normal/front/6/7/67330004-6720-46d9-9de0-c79230110583.jpg?1783948036"
    }
}

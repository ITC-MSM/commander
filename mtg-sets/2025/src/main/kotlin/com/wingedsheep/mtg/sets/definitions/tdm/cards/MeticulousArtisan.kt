package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Meticulous Artisan — Tarkir: Dragonstorm #112
 * {3}{R} · Creature — Djinn Artificer · 3/3
 *
 * Prowess (Whenever you cast a noncreature spell, this creature gets +1/+1 until end of turn.)
 * When this creature enters, create a Treasure token.
 *
 * Prowess via the [prowess] keyword helper; ETB creates a single predefined Treasure token via
 * [Effects.CreateTreasure].
 */
val MeticulousArtisan = card("Meticulous Artisan") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Djinn Artificer"
    power = 3
    toughness = 3
    oracleText = "Prowess (Whenever you cast a noncreature spell, this creature gets +1/+1 until " +
        "end of turn.)\n" +
        "When this creature enters, create a Treasure token. (It's an artifact with \"{T}, " +
        "Sacrifice this token: Add one mana of any color.\")"

    prowess()

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateTreasure(1)
        description = "When this creature enters, create a Treasure token."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "112"
        artist = "Anna Pavleeva"
        flavorText = "\"It isn't about expression or results. Doing the thing is itself the point.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/a/baf4c9dd-0546-41ac-a7ba-0bc312fef31e.jpg?1743204413"
    }
}

package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.model.Rarity

/**
 * Eager Trufflesnout
 * {2}{G}
 * Creature — Boar
 * 4/2
 *
 * Trample
 * Whenever this creature deals combat damage to a player, create a Food token.
 */
val EagerTrufflesnout = card("Eager Trufflesnout") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Boar"
    power = 4
    toughness = 2
    oracleText = "Trample (This creature can deal excess combat damage to the player or planeswalker it's attacking.)\n" +
        "Whenever this creature deals combat damage to a player, create a Food token. " +
        "(It's an artifact with \"{2}, {T}, Sacrifice this token: You gain 3 life.\")"

    keywords(Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        effect = Effects.CreateFood()
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "102"
        artist = "Filipe Pagliuso"
        imageUri = "https://cards.scryfall.io/normal/front/a/6/a6e8433d-eb2a-43d1-b59b-7d70ff97c8e7.jpg?1782689176"
    }
}

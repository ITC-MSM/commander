package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Foul Spirit
 * {2}{B}
 * Creature — Spirit
 *
 * The bare imperative "sacrifice a land" names no player, so it is [Effects.SacrificeOwn] — the
 * ability's controller sacrifices — not the `ForceSacrifice` shape that "target opponent
 * sacrifices" needs.
 */
val FoulSpirit = card("Foul Spirit") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Spirit"
    oracleText =
        "Flying\n" +
        "When this creature enters, sacrifice a land."
    power = 3
    toughness = 2
    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.SacrificeOwn(GameObjectFilter.Land)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "73"
        artist = "rk post"
        imageUri = "https://cards.scryfall.io/normal/front/6/4/64b63e5f-b2cf-42c2-8111-1ebb9ed5ca33.jpg"
    }
}

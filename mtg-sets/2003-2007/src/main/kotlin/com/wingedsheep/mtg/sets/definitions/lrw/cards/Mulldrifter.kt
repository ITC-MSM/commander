package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Mulldrifter
 * {4}{U}
 * Creature — Elemental
 * 2/2
 * Flying
 * When this creature enters, draw two cards.
 * Evoke {2}{U}
 */
val Mulldrifter = card("Mulldrifter") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Elemental"
    power = 2
    toughness = 2
    oracleText = "Flying\nWhen this creature enters, draw two cards.\n" +
        "Evoke {2}{U} (You may cast this spell for its evoke cost. If you do, it's sacrificed " +
        "when it enters.)"

    keywords(Keyword.FLYING)

    evoke = "{2}{U}"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrawCards(2)
        description = "draw two cards."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "76"
        artist = "Eric Fortune"
        imageUri = "https://cards.scryfall.io/normal/front/a/9/a97cfefa-ade7-49f6-b2aa-1118b9db4935.jpg?1783942900"
    }
}

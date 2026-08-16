package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Mossbeard Ancient
 * {5}{G}{G}
 * Creature — Treefolk
 * 7/7
 * Trample
 * When this creature enters, you gain 5 life.
 */
val MossbeardAncient = card("Mossbeard Ancient") {
    manaCost = "{5}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Treefolk"
    oracleText = "Trample\nWhen this creature enters, you gain 5 life."
    power = 7
    toughness = 7

    keywords(Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.GainLife(5)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "173"
        artist = "Alexandre Honoré"
        flavorText = "Older than the mountains, the dragons, and the wars of humans and machines."
        imageUri = "https://cards.scryfall.io/normal/front/7/e/7e528d36-cea6-4013-83d5-ba837d570713.jpg?1783921298"
    }
}

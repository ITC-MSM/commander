package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Commander Greven il-Vec
 * {3}{B}{B}{B}
 * Legendary Creature — Phyrexian Human Warrior
 * 7/5
 * Fear (This creature can't be blocked except by artifact creatures and/or black creatures.)
 * When Commander Greven il-Vec enters, sacrifice a creature.
 */
val CommanderGrevenIlVec = card("Commander Greven il-Vec") {
    manaCost = "{3}{B}{B}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Creature — Phyrexian Human Warrior"
    power = 7
    toughness = 5
    oracleText = "Fear (This creature can't be blocked except by artifact creatures and/or black creatures.)\n" +
        "When Commander Greven il-Vec enters, sacrifice a creature."

    keywords(Keyword.FEAR)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.SacrificeOwn(GameObjectFilter.Creature)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "115"
        artist = "Kev Walker"
        flavorText = "\"Rage is the only freedom left me.\"\n" +
            "—Greven *il*-Vec"
        imageUri = "https://cards.scryfall.io/normal/front/a/b/ab0ce69f-a259-4801-9ac3-f6754040434c.jpg"
    }
}

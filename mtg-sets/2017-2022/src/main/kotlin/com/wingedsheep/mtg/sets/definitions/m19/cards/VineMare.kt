package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlockedBy
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Vine Mare
 * {2}{G}{G}
 * Creature — Elemental Horse
 * 5/3
 * Hexproof (This creature can't be the target of spells or abilities your opponents control.)
 * This creature can't be blocked by black creatures.
 *
 * The evasion is a single [CantBeBlockedBy] over a coloured *creature* filter, evaluated against
 * projected state so a blocker's current colour decides, not its printed one.
 */
val VineMare = card("Vine Mare") {
    manaCost = "{2}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elemental Horse"
    power = 5
    toughness = 3
    oracleText = "Hexproof (This creature can't be the target of spells or abilities your opponents control.)\n" +
        "This creature can't be blocked by black creatures."

    keywords(Keyword.HEXPROOF)

    staticAbility {
        ability = CantBeBlockedBy(GameObjectFilter.Creature.withColor(Color.BLACK))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "207"
        artist = "Alex Konstad"
        flavorText = "When it passes, the dead are displaced by flourishing life."
        imageUri = "https://cards.scryfall.io/normal/front/b/9/b9980835-cd32-4870-88df-c79cd5534968.jpg"
    }
}

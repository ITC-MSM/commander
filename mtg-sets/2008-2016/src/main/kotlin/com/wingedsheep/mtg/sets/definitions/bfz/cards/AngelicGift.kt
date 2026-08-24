package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword

/**
 * Angelic Gift
 * {1}{W}
 * Enchantment — Aura
 * Enchant creature
 * When this Aura enters, draw a card.
 * Enchanted creature has flying.
 */
val AngelicGift = card("Angelic Gift") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "When this Aura enters, draw a card.\n" +
        "Enchanted creature has flying."

    auraTarget = Targets.Creature

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrawCards(1)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.FLYING)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "19"
        artist = "Josu Hernaiz"
        flavorText = "\"Zendikar will triumph this day, and our armies will fly with angels.\""
        imageUri = "https://cards.scryfall.io/normal/front/4/9/4941246b-7d6a-4b2d-8144-f36ac890a389.jpg?1783938222"
    }
}

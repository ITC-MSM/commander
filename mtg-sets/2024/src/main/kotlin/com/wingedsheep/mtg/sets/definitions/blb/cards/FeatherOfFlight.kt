package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Feather of Flight
 * {1}{W}
 * Enchantment — Aura
 * Flash
 * Enchant creature
 * When this Aura enters, draw a card.
 * Enchanted creature gets +1/+0 and has flying.
 */
val FeatherOfFlight = card("Feather of Flight") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment — Aura"
    oracleText = "Flash\nEnchant creature\nWhen this Aura enters, draw a card.\nEnchanted creature gets +1/+0 and has flying."

    keywords(Keyword.FLASH)

    auraTarget = Targets.Creature

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrawCards(1)
    }

    staticAbility {
        ability = ModifyStats(1, 0)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.FLYING)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "13"
        artist = "Borja Pindado"
        flavorText = "As he grasped the glowing feather, the lizard's heart soared, and the rest of him quickly followed."
        imageUri = "https://cards.scryfall.io/normal/front/9/f/9fb41503-8632-4bf1-9bfe-6d9b9993c337.jpg?1721425833"
    }
}

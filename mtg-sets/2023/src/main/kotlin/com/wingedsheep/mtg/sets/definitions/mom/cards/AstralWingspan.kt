package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Astral Wingspan
 * {4}{U}
 * Enchantment — Aura
 * Convoke
 * Enchant creature
 * When this Aura enters, draw a card.
 * Enchanted creature gets +2/+2 and has flying.
 */
val AstralWingspan = card("Astral Wingspan") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment — Aura"
    oracleText = "Convoke (Your creatures can help cast this spell. Each creature you tap while " +
        "casting this spell pays for {1} or one mana of that creature's color.)\n" +
        "Enchant creature\n" +
        "When this Aura enters, draw a card.\n" +
        "Enchanted creature gets +2/+2 and has flying."

    keywords(Keyword.CONVOKE)

    auraTarget = Targets.Creature

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrawCards(1)
    }

    staticAbility {
        ability = ModifyStats(2, 2)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.FLYING)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "48"
        artist = "Joseph Weston"
        imageUri = "https://cards.scryfall.io/normal/front/b/e/be2ef728-a2c7-45ee-8594-372ed135b482.jpg?1783917046"
        ruling(
            "2024-01-12",
            "When calculating a spell's total cost, include any alternative costs, additional " +
                "costs, or anything else that increases or reduces the cost to cast the spell. " +
                "Convoke applies after the total cost is calculated. Convoke doesn't change a " +
                "spell's mana cost or mana value."
        )
    }
}

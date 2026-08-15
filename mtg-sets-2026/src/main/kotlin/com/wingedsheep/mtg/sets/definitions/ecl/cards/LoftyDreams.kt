package com.wingedsheep.mtg.sets.definitions.ecl.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Lofty Dreams
 * {3}{U}{U}
 * Enchantment — Aura
 *
 * Convoke (Your creatures can help cast this spell. Each creature you tap while casting this spell pays for {1} or one mana of that creature's color.)
 * Enchant creature
 * When this Aura enters, draw a card.
 * Enchanted creature gets +2/+2 and has flying.
 */
val LoftyDreams = card("Lofty Dreams") {
    manaCost = "{3}{U}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment — Aura"
    oracleText = "Convoke (Your creatures can help cast this spell. Each creature you tap while casting this spell pays for {1} or one mana of that creature's color.)\n" +
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
        collectorNumber = "58"
        artist = "Steven Belledin"
        imageUri = "https://cards.scryfall.io/normal/front/a/2/a2908ed1-517e-4f1a-94e5-b06fb033c1a6.jpg?1767871777"
    }
}

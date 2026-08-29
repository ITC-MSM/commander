package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Setessan Training
 * {1}{G}
 * Enchantment — Aura
 *
 * Enchant creature you control
 * When this Aura enters, draw a card.
 * Enchanted creature gets +1/+0 and has trample. (It can deal excess combat damage to the player or planeswalker it's attacking.)
 *
 * "Enchant creature you control" narrows the aura target to [Targets.CreatureYouControl]. The last
 * printed line joins a stat modification and a keyword grant with "and", which the SDK spells as two
 * separate static abilities — both default their filter to the attached creature.
 */
val SetessanTraining = card("Setessan Training") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature you control\n" +
        "When this Aura enters, draw a card.\n" +
        "Enchanted creature gets +1/+0 and has trample. (It can deal excess combat damage to the player or planeswalker it's attacking.)"

    auraTarget = Targets.CreatureYouControl

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrawCards(1)
    }

    staticAbility {
        ability = ModifyStats(1, 0)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.TRAMPLE)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "201"
        artist = "Scott Murphy"
        flavorText = "In the heart of Setessa, the warriors of Leina Tower defend the polis and train its daughters."
        imageUri = "https://cards.scryfall.io/normal/front/b/8/b8322d31-6194-4fda-99a3-b6426c57488a.jpg"
    }
}

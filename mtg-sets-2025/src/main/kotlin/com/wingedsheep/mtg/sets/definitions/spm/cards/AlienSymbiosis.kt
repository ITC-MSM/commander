package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.GrantSubtype
import com.wingedsheep.sdk.scripting.MayCastSelfFromZones
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Alien Symbiosis
 * {1}{B}
 * Enchantment — Aura
 * Enchant creature
 * Enchanted creature gets +1/+1, has menace, and is a Symbiote in addition to its other types.
 * You may cast this card from your graveyard by discarding a card in addition to paying its
 * other costs.
 */
val AlienSymbiosis = card("Alien Symbiosis") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature gets +1/+1, has menace, and is a Symbiote in addition to its other types.\n" +
        "You may cast this card from your graveyard by discarding a card in addition to paying its other costs."

    auraTarget = Targets.Creature

    staticAbility {
        ability = ModifyStats(1, 1, Filters.EnchantedCreature)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.MENACE)
    }

    staticAbility {
        ability = GrantSubtype("Symbiote", Filters.EnchantedCreature)
    }

    // "You may cast this card from your graveyard by discarding a card in addition to paying
    // its other costs." Self-referential cast-from-graveyard permission with a bundled
    // additional cost. Normal (sorcery-speed) timing and the {1}{B} mana cost still apply.
    staticAbility {
        ability = MayCastSelfFromZones(
            zones = listOf(Zone.GRAVEYARD),
            additionalCost = Costs.additional.DiscardCards(1)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "50"
        artist = "JB Casacop"
        imageUri = "https://cards.scryfall.io/normal/front/b/8/b898ccb7-758e-4f11-95e0-b412721d8bf9.jpg?1783905348"
        ruling("2025-09-19", "You must follow all normal timing rules when casting Alien Symbiosis using its last ability.")
    }
}

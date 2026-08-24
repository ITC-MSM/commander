package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Talons of Wildwood
 * {1}{G}
 * Enchantment — Aura
 * Enchant creature
 * Enchanted creature gets +1/+1 and has trample. (It can deal excess combat damage to the player or planeswalker it's attacking.)
 * {2}{G}: Return this card from your graveyard to your hand.
 *
 * The recursion ability is activated from the *graveyard*, not the battlefield — `activateFromZone`
 * is what makes it legal there.
 */
val TalonsOfWildwood = card("Talons of Wildwood") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature gets +1/+1 and has trample. (It can deal excess combat damage to the player or planeswalker it's attacking.)\n" +
        "{2}{G}: Return this card from your graveyard to your hand."

    auraTarget = Targets.Creature

    staticAbility {
        ability = ModifyStats(1, 1)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.TRAMPLE)
    }

    activatedAbility {
        cost = Costs.Mana("{2}{G}")
        effect = Effects.ReturnToHandFromGraveyard(EffectTarget.Self)
        activateFromZone = Zone.GRAVEYARD
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "202"
        artist = "Uriah Voth"
        imageUri = "https://cards.scryfall.io/normal/front/9/f/9ffa2e83-bc78-4bde-9692-e5165c4ef63b.jpg"
    }
}

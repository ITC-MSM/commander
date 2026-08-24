package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Vineweft
 * {G}
 * Enchantment — Aura
 * Enchant creature
 * Enchanted creature gets +1/+1.
 * {4}{G}: Return this card from your graveyard to your hand.
 *
 * The recursion ability is activated from the *graveyard*, not the battlefield — `activateFromZone`
 * is what makes it legal there.
 */
val Vineweft = card("Vineweft") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Enchantment — Aura"
    oracleText =
        "Enchant creature\n" +
        "Enchanted creature gets +1/+1.\n" +
        "{4}{G}: Return this card from your graveyard to your hand."

    auraTarget = Targets.Creature

    staticAbility {
        ability = ModifyStats(+1, +1)
    }

    activatedAbility {
        cost = Costs.Mana("{4}{G}")
        effect = Effects.ReturnToHandFromGraveyard(EffectTarget.Self)
        activateFromZone = Zone.GRAVEYARD
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "207"
        artist = "Lucas Graciano"
        flavorText = "Fortified by the wilds."
        imageUri = "https://cards.scryfall.io/normal/front/4/5/4578f064-e9f8-4e87-8f46-7536af6c144e.jpg?1783939160"
    }
}

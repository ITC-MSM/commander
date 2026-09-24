package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantCardType
import com.wingedsheep.sdk.scripting.GrantSubtype
import com.wingedsheep.sdk.scripting.SetBasePowerToughnessStatic
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Puppet Crafting — Reality Fracture #111
 * {1}{G} · Enchantment — Aura · Rare
 *
 * Enchant artifact or non-Aura enchantment
 * Enchanted permanent is a Construct creature with base power and toughness 5/5 in addition to its
 * other types.
 * {4}{G}: Return this card from your graveyard to your hand.
 *
 * Zoetic Glyph's animation widened to non-Aura enchantments: a Layer 4 creature type grant, a
 * Layer 4 Construct subtype grant, and a Layer 7b base P/T setter, all scoped to the attached
 * permanent. The recursion ability is activated from the graveyard (Vineweft's shape).
 */
val PuppetCrafting = card("Puppet Crafting") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant artifact or non-Aura enchantment\n" +
        "Enchanted permanent is a Construct creature with base power and toughness 5/5 in addition to its other types.\n" +
        "{4}{G}: Return this card from your graveyard to your hand."

    auraTarget = TargetPermanent(
        filter = TargetFilter(
            GameObjectFilter.Artifact or GameObjectFilter.Enchantment.notSubtype(Subtype.AURA)
        )
    )

    staticAbility {
        ability = GrantCardType("CREATURE", filter = GroupFilter.attachedCreature())
    }
    staticAbility {
        ability = GrantSubtype("Construct", filter = GroupFilter.attachedCreature())
    }
    staticAbility {
        ability = SetBasePowerToughnessStatic(5, 5)
    }

    activatedAbility {
        cost = Costs.Mana("{4}{G}")
        effect = Effects.ReturnToHandFromGraveyard(EffectTarget.Self)
        activateFromZone = Zone.GRAVEYARD
        description = "{4}{G}: Return this card from your graveyard to your hand."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "111"
        artist = "Anna Pavleeva"
        imageUri = "https://cards.scryfall.io/normal/front/6/b/6b8789a6-3b63-4198-af5f-c2f2f49fafd9.jpg?1789470736"
        inBooster = false
    }
}

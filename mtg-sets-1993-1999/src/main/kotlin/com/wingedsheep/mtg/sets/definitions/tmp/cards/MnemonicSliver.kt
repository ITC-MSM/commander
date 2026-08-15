package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Mnemonic Sliver
 * {2}{U}
 * Creature — Sliver
 * 2/2
 * All Slivers have "{2}, Sacrifice this permanent: Draw a card."
 */
val MnemonicSliver = card("Mnemonic Sliver") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Sliver"
    power = 2
    toughness = 2
    oracleText = "All Slivers have \"{2}, Sacrifice this permanent: Draw a card.\""

    val sliverFilter = GroupFilter(GameObjectFilter.Creature.withSubtype("Sliver"))

    staticAbility {
        ability = GrantActivatedAbility(
            ability = ActivatedAbility(
                id = AbilityId.generate(),
                cost = Costs.Composite(Costs.Mana("{2}"), Costs.SacrificeSelf),
                effect = Effects.DrawCards(1)
            ),
            filter = sliverFilter
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "77"
        artist = "Randy Gallegos"
        flavorText = "\"A jigsaw puzzle that lives, breeds, and thinks.\"\n—Hanna, *Weatherlight* navigator"
        imageUri = "https://cards.scryfall.io/normal/front/2/b/2b167347-2f8f-4338-a651-c7543d812597.jpg?1562053268"
    }
}

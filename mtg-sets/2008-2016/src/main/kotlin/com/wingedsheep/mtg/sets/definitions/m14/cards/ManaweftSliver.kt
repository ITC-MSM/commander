package com.wingedsheep.mtg.sets.definitions.m14.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Manaweft Sliver
 * {1}{G}
 * Creature — Sliver
 * 1 / 1
 * Sliver creatures you control have "{T}: Add one mana of any color."
 */
val ManaweftSliver = card("Manaweft Sliver") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Sliver"
    power = 1
    toughness = 1
    oracleText = "Sliver creatures you control have \"{T}: Add one mana of any color.\""

    staticAbility {
        ability = GrantActivatedAbility(
            ability = ActivatedAbility(
                id = AbilityId.generate(),
                cost = Costs.Tap,
                effect = Effects.AddManaOfChoice(),
                timing = TimingRule.ManaAbility,
                isManaAbility = true
            ),
            filter = GroupFilter(GameObjectFilter.Creature.withSubtype(Subtype.SLIVER).youControl())
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "184"
        artist = "Trevor Claxton"
        flavorText = "\"I see in their interconnectedness a strange embodiment of the natural order.\"\n" +
            "—Dionus, elvish archdruid"
        imageUri = "https://cards.scryfall.io/normal/front/f/e/fe45433b-e124-44d7-9463-dada39310148.jpg"
    }
}

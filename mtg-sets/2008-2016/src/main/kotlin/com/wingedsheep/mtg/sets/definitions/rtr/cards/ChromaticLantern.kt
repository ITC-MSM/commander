package com.wingedsheep.mtg.sets.definitions.rtr.cards

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
 * Chromatic Lantern
 * {3}
 * Artifact
 * Lands you control have "{T}: Add one mana of any color."
 * {T}: Add one mana of any color.
 */
val ChromaticLantern = card("Chromatic Lantern") {
    manaCost = "{3}"
    typeLine = "Artifact"
    oracleText = "Lands you control have \"{T}: Add one mana of any color.\"\n" +
        "{T}: Add one mana of any color."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddAnyColorMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }
    staticAbility {
        ability = GrantActivatedAbility(
            ability = ActivatedAbility(
                id = AbilityId.generate(),
                cost = Costs.Tap,
                effect = Effects.AddAnyColorMana(1),
                isManaAbility = true,
                timing = TimingRule.ManaAbility
            ),
            filter = GroupFilter(GameObjectFilter.Land.youControl())
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "226"
        artist = "Jung Park"
        flavorText = "Dimir mages put the lanterns to good use, creating shapeshifters and sleeper agents from mana foreign to them."
        imageUri = "https://cards.scryfall.io/normal/front/5/7/57f4e0f0-13d1-43ed-8d95-13cff94a26e7.jpg?1783940325"
    }
}

package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Lifespring Druid
 * {2}{G}
 * Creature — Elf Druid
 * 2/1
 * {T}: Add one mana of any color.
 */
val LifespringDruid = card("Lifespring Druid") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Druid"
    power = 2
    toughness = 1
    oracleText = "{T}: Add one mana of any color."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddManaOfChoice()
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "177"
        artist = "Willian Murai"
        flavorText = "\"The land is not dead, merely sick. I will nurse it back to health.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/3/a3657719-7d4d-46db-a5f4-699ee2032ebe.jpg?1783938187"
    }
}

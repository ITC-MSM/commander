package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Deathbloom Gardener
 * {2}{G}
 * Creature — Elf Druid
 * 1/1
 * Deathtouch
 * {T}: Add one mana of any color.
 */
val DeathbloomGardener = card("Deathbloom Gardener") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Druid"
    oracleText = "Deathtouch\n{T}: Add one mana of any color."
    power = 1
    toughness = 1

    keywords(Keyword.DEATHTOUCH)

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddAnyColorMana()
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "159"
        artist = "Marta Nael"
        flavorText = "\"I can provide the Coalition with poisons that will break down Phyrexian machinery as easily as they stop the heart.\""
        imageUri = "https://cards.scryfall.io/normal/front/8/8/88dee3d1-0496-40ea-b208-7362a932f531.jpg?1783921304"
    }
}

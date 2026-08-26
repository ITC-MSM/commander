package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Greenweaver Druid
 * {2}{G}
 * Creature — Elf Druid
 * 1/1
 * {T}: Add {G}{G}.
 */
val GreenweaverDruid = card("Greenweaver Druid") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Druid"
    power = 1
    toughness = 1
    oracleText = "{T}: Add {G}{G}."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.GREEN, 2)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "164"
        artist = "Justin Sweet"
        flavorText = "The other tribes call them fanatics, but none deny that the Mul Daya elves have an iron-strong bond to some force greater than themselves."
        imageUri = "https://cards.scryfall.io/normal/front/7/4/747099f7-ce5b-4366-a8a4-f3d80100f66e.jpg"
    }
}

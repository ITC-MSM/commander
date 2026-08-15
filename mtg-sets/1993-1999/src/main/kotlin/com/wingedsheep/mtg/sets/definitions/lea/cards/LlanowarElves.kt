package com.wingedsheep.mtg.sets.definitions.lea.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddManaEffect

/**
 * Llanowar Elves
 * {G}
 * Creature — Elf Druid
 * 1/1
 * {T}: Add {G}.
 */
val LlanowarElves = card("Llanowar Elves") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Druid"
    power = 1
    toughness = 1
    oracleText = "{T}: Add {G}."

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.GREEN)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "210"
        artist = "Anson Maddocks"
        imageUri = "https://cards.scryfall.io/normal/front/d/4/d4f1cc9e-4f99-4c26-ac1b-8ef069fa8ceb.jpg?1559591371"
    }
}

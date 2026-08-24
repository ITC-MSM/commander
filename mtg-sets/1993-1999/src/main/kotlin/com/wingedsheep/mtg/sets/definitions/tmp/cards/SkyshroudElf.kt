package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Skyshroud Elf
 * {1}{G}
 * Creature — Elf Druid
 * 1/1
 * {T}: Add {G}.
 * {1}: Add {R} or {W}.
 */
val SkyshroudElf = card("Skyshroud Elf") {
    manaCost = "{1}{G}"
    colorIdentity = "WRG"
    typeLine = "Creature — Elf Druid"
    power = 1
    toughness = 1
    oracleText = "{T}: Add {G}.\n" +
        "{1}: Add {R} or {W}."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.GREEN)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Mana("{1}")
        effect = Effects.AddMana(Color.RED)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Mana("{1}")
        effect = Effects.AddMana(Color.WHITE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "255"
        artist = "Jeff Miracola"
        flavorText = "\"It is our duty to endure.\"\n" +
            "—Eladamri, Lord of Leaves"
        imageUri = "https://cards.scryfall.io/normal/front/2/6/26877a52-dec3-433d-b7a5-767f6cdf2365.jpg"
    }
}

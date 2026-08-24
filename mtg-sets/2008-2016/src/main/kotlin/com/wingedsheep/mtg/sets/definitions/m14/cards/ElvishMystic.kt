package com.wingedsheep.mtg.sets.definitions.m14.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Elvish Mystic
 * {G}
 * Creature — Elf Druid
 * 1/1
 * {T}: Add {G}.
 *
 * Canonical printing: Magic 2014, the card's earliest real-expansion printing. Reprinted in M15,
 * C14 and KHC as `Printing` rows.
 */
val ElvishMystic = card("Elvish Mystic") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Druid"
    power = 1
    toughness = 1
    oracleText = "{T}: Add {G}."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.GREEN)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "169"
        artist = "Wesley Burt"
        flavorText = "\"Life grows everywhere. My kin merely find those places where it grows strongest.\"\n—Nissa Revane"
        imageUri = "https://cards.scryfall.io/normal/front/6/0/60d0e6a6-629a-45a7-bfcb-25ba7156788b.jpg?1783939906"
    }
}

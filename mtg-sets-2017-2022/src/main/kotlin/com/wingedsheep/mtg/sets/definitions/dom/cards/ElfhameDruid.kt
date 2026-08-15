package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddManaEffect
import com.wingedsheep.sdk.scripting.effects.ManaRestriction

/**
 * Elfhame Druid
 * {1}{G}
 * Creature — Elf Druid
 * 0/2
 * {T}: Add {G}.
 * {T}: Add {G}{G}. Spend this mana only to cast kicked spells.
 *
 */
val ElfhameDruid = card("Elfhame Druid") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Druid"
    power = 0
    toughness = 2
    oracleText = "{T}: Add {G}.\n{T}: Add {G}{G}. Spend this mana only to cast kicked spells."

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.GREEN)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.GREEN, 2, ManaRestriction.KickedSpellsOnly)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "159"
        artist = "Yongjae Choi"
        flavorText = "The bond between Llanowar elves and their kavu empowers both."
        imageUri = "https://cards.scryfall.io/normal/front/5/e/5e62226e-3585-42d2-9b7a-2462fcd967f5.jpg?1562736480"
    }
}

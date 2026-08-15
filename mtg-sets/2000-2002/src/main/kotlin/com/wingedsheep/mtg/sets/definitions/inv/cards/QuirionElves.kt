package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ChoiceType
import com.wingedsheep.sdk.scripting.EntersWithChoice

/**
 * Quirion Elves
 * {1}{G}
 * Creature — Elf Druid
 * 1/1
 *
 * As this creature enters, choose a color.
 * {T}: Add {G}.
 * {T}: Add one mana of the chosen color.
 */
val QuirionElves = card("Quirion Elves") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Druid"
    power = 1
    toughness = 1
    oracleText = "As this creature enters, choose a color.\n{T}: Add {G}.\n{T}: Add one mana of the chosen color."

    replacementEffect(EntersWithChoice(ChoiceType.COLOR))

    // {T}: Add {G}
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.GREEN)
        manaAbility = true
    }

    // {T}: Add one mana of the chosen color
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddManaOfChosenColor()
        manaAbility = true
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "203"
        artist = "Douglas Shuler"
        imageUri = "https://cards.scryfall.io/normal/front/c/6/c660a748-82a9-4d6a-8023-56aeafe1bdce.jpg?1562934752"
    }
}

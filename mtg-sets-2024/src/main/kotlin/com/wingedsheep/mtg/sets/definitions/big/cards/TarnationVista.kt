package com.wingedsheep.mtg.sets.definitions.big.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ChoiceType
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.EntersWithChoice
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Tarnation Vista — Land (The Big Score, mythic).
 *
 * "This land enters tapped. As it enters, choose a color.
 *  {T}: Add one mana of the chosen color.
 *  {1}, {T}: For each color among monocolored permanents you control, add one mana of that color."
 *
 * Rulings:
 *  - The last ability is a mana ability — it doesn't use the stack and can't be responded to.
 *  - It can produce at most five mana (one per color: white, blue, black, red, green).
 */
val TarnationVista = card("Tarnation Vista") {
    typeLine = "Land"
    colorIdentity = ""
    oracleText = "This land enters tapped. As it enters, choose a color.\n" +
        "{T}: Add one mana of the chosen color.\n" +
        "{1}, {T}: For each color among monocolored permanents you control, add one mana of that color."

    replacementEffect(EntersTapped())
    replacementEffect(EntersWithChoice(ChoiceType.COLOR))

    // {T}: Add one mana of the chosen color
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddManaOfChosenColor()
        manaAbility = true
    }

    // {1}, {T}: For each color among monocolored permanents you control, add one mana of that color
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.Tap)
        effect = Effects.AddOneManaOfEachColorAmong(
            GameObjectFilter.Permanent.monocolored().youControl()
        )
        manaAbility = true
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "30"
        artist = "Alayna Danner"
        imageUri = "https://cards.scryfall.io/normal/front/9/6/962552a1-ec34-49e2-a23d-85dfb405d5e0.jpg?1739804252"
    }
}

package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Mox Amber
 * {0}
 * Legendary Artifact
 * {T}: Add one mana of any color among legendary creatures and planeswalkers you control.
 */
val MoxAmber = card("Mox Amber") {
    manaCost = "{0}"
    colorIdentity = ""
    typeLine = "Legendary Artifact"
    oracleText = "{T}: Add one mana of any color among legendary creatures and planeswalkers you control."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddManaOfColorAmong(
            GameObjectFilter.CreatureOrPlaneswalker.legendary().youControl()
        )
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "224"
        artist = "Steven Belledin"
        flavorText = "A moment in time made tangible, it has the power to realize epic visions."
        imageUri = "https://cards.scryfall.io/normal/front/6/6/66024e69-ad60-4c9a-a0ca-da138d33ad80.jpg?1685554120"
        ruling("2018-04-27", "Mox Amber's ability adds one mana of the color of your choice from among the colors of legendary creatures and legendary planeswalkers you control.")
        ruling("2018-04-27", "If you control no legendary creatures or legendary planeswalkers, you can activate Mox Amber's ability, but you won't add any mana.")
        ruling("2018-04-27", "If your legendary creatures and legendary planeswalkers are all colorless, you can activate Mox Amber's ability, but you won't add any mana. Colorless is not a color.")
    }
}

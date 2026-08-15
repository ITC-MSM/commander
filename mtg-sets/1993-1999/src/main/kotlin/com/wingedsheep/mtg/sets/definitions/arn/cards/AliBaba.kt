package com.wingedsheep.mtg.sets.definitions.arn.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Ali Baba
 * {R}
 * Creature — Human Rogue
 * 1/1
 * {R}: Tap target Wall.
 */
val AliBaba = card("Ali Baba") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Rogue"
    power = 1
    toughness = 1
    oracleText = "{R}: Tap target Wall."

    activatedAbility {
        cost = Costs.Mana("{R}")
        val wall = target("target Wall", TargetObject(filter = TargetFilter(GameObjectFilter.Creature.withSubtype("Wall"))))
        effect = Effects.Tap(wall)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "35"
        artist = "Julie Baroh"
        flavorText = "\"When he reached the entrance of the cavern, he pronounced the words, 'Open, Sesame!'\" —The Arabian Nights, Junior Classics trans."
        imageUri = "https://cards.scryfall.io/normal/front/2/9/29cd7064-3703-43e0-8702-d1ba13703fd8.jpg?1562902720"
    }
}

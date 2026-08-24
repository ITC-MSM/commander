package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Stone Haven Medic
 * {1}{W}
 * Creature — Kor Cleric
 * 1/3
 * {W}, {T}: You gain 1 life.
 */
val StoneHavenMedic = card("Stone Haven Medic") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Kor Cleric"
    power = 1
    toughness = 3
    oracleText = "{W}, {T}: You gain 1 life."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{W}"), Costs.Tap)
        effect = Effects.GainLife(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "51"
        artist = "Anna Steinbauer"
        flavorText = "\"These days, soldiers never stick around long enough for a proper healing. They just want me " +
            "to knit them up so they can hurry back to the fight.\""
        imageUri = "https://cards.scryfall.io/normal/front/3/9/3956563b-bde3-4aec-93fe-e03bade49458.jpg?1783938214"
    }
}

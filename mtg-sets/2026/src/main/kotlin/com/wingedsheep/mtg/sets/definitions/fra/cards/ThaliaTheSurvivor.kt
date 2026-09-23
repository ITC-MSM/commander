package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Thalia, the Survivor — the opponents-only cousin of Thalia, Guardian of Thraben's tax: her own
 * controller's noncreature spells cost nothing extra. The tax also reaches an opponent's alternative
 * cost (CR 118.9d).
 */
val ThaliaTheSurvivor = card("Thalia, the Survivor") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Creature — Human Soldier"
    power = 3
    toughness = 4
    oracleText = "Lifelink\nNoncreature spells your opponents cast cost {1} more to cast."

    keywords(Keyword.LIFELINK)

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.OpponentsCast(GameObjectFilter.Noncreature),
            modification = CostModification.IncreaseGeneric(1),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "205"
        artist = "Ryan Pancoast"
        flavorText = "\"In Sigarda's name, I will ensure your peaceful rest.\""
        imageUri = "https://cards.scryfall.io/normal/front/8/0/80226231-9e70-430e-aabc-f262f70b9226.jpg?1789127700"
        inBooster = false
    }
}

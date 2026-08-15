package com.wingedsheep.mtg.sets.definitions.isd.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.MayCastWithoutPayingManaCost

/**
 * Rooftop Storm
 * {5}{U}
 * Enchantment
 * You may pay {0} rather than pay the mana cost for Zombie creature spells you cast.
 *
 * "You may pay {0} rather than pay the mana cost" is the same {0} alternative cost modeled by
 * [MayCastWithoutPayingManaCost] (CR 118.9). Gated to the controller's own Zombie creature
 * spells via [controllerOnly] + a [spellFilter] of Zombie creatures (cf. Dracogenesis).
 */
val RooftopStorm = card("Rooftop Storm") {
    manaCost = "{5}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment"
    oracleText = "You may pay {0} rather than pay the mana cost for Zombie creature spells you cast."

    staticAbility {
        ability = MayCastWithoutPayingManaCost(
            controllerOnly = true,
            spellFilter = GameObjectFilter.Creature.withSubtype("Zombie")
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "71"
        artist = "John Stanko"
        flavorText = "\"Let those idiot priests tremble! A new era in unlife begins here and now. Oglor, raise the lightning vane!\"\n—Stitcher Geralf"
        imageUri = "https://cards.scryfall.io/normal/front/a/b/ab01d871-ba50-400a-95e7-09af9e34405f.jpg?1782714792"
    }
}

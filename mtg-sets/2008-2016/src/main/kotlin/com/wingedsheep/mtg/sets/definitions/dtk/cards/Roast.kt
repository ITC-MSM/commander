package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Roast
 * {1}{R}
 * Sorcery
 *
 * Roast deals 5 damage to target creature without flying.
 *
 * "Without flying" is a restriction on legality, so it belongs to the *target filter* rather than
 * to the damage effect — [GameObjectFilter.withoutKeyword] reads the projected keyword set, so a
 * creature that has been granted flying by a continuous effect is not a legal target and one that
 * loses flying becomes one.
 */
val Roast = card("Roast") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Roast deals 5 damage to target creature without flying."

    spell {
        val t = target(
            "target",
            TargetCreature(filter = TargetFilter(GameObjectFilter.Creature.withoutKeyword(Keyword.FLYING)))
        )
        effect = Effects.DealDamage(5, t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "151"
        artist = "Zoltan Boros"
        flavorText = "\"Intruders in the lands of Atarka have but two choices: be consumed by fire, or be consumed by maw.\"\n—Ulnok, Atarka shaman"
        imageUri = "https://cards.scryfall.io/normal/front/1/b/1ba2e9a8-fcbb-4328-b475-36730182b765.jpg?1783938587"
    }
}

package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Air Servant
 * {4}{U}
 * Creature — Elemental
 * 4/3
 *
 * Flying
 * {2}{U}: Tap target creature with flying.
 */
val AirServant = card("Air Servant") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Elemental"
    power = 4
    toughness = 3
    oracleText = "Flying\n" +
        "{2}{U}: Tap target creature with flying."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Mana("{2}{U}")
        val t = target("target", TargetCreature(filter = TargetFilter.Creature.withKeyword(Keyword.FLYING)))
        effect = Effects.Tap(t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "42"
        artist = "Lars Grant-West"
        flavorText = "\"Wind is forceful, yet ephemeral. It can knock a dragon out of the sky, yet pass through the smallest crack unhindered.\"\n" +
            "—Jestus Dreya, *Of Elements and Eternity*"
        imageUri = "https://cards.scryfall.io/normal/front/0/f/0f46eb67-a50d-4910-9919-1bb2ca1c0dad.jpg?1783941828"
    }
}

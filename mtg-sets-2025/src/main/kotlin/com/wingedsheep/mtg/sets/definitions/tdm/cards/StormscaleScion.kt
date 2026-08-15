package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Stormscale Scion — Tarkir: Dragonstorm #123
 * {4}{R}{R} · Creature — Dragon · 4/4
 *
 * Flying
 * Other Dragons you control get +1/+1.
 * Storm (When you cast this spell, copy it for each spell cast before it this turn.
 * Copies become tokens.)
 */
val StormscaleScion = card("Stormscale Scion") {
    manaCost = "{4}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Dragon"
    power = 4
    toughness = 4
    oracleText = "Flying\n" +
        "Other Dragons you control get +1/+1.\n" +
        "Storm (When you cast this spell, copy it for each spell cast before it this turn. " +
        "Copies become tokens.)"

    keywords(Keyword.FLYING, Keyword.STORM)

    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(
                GameObjectFilter.Creature.withSubtype("Dragon").youControl(),
                excludeSelf = true
            )
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "123"
        artist = "Andrew Mar"
        imageUri = "https://cards.scryfall.io/normal/front/0/a/0ac43386-bd32-425c-8776-cec00b064cbc.jpg?1743204459"
    }
}

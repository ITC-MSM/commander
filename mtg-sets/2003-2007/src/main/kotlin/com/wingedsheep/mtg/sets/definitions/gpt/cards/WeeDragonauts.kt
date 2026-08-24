package com.wingedsheep.mtg.sets.definitions.gpt.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Wee Dragonauts
 * {1}{U}{R}
 * Creature — Faerie Wizard
 * 1/3
 * Flying
 * Whenever you cast an instant or sorcery spell, this creature gets +2/+0 until end of turn.
 */
val WeeDragonauts = card("Wee Dragonauts") {
    manaCost = "{1}{U}{R}"
    colorIdentity = "UR"
    typeLine = "Creature — Faerie Wizard"
    oracleText = "Flying\n" +
        "Whenever you cast an instant or sorcery spell, this creature gets +2/+0 until end of turn."
    power = 1
    toughness = 3

    keywords(Keyword.FLYING)
    triggeredAbility {
        trigger = Triggers.YouCastInstantOrSorcery
        effect = Effects.ModifyStats(2, 0, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "137"
        artist = "Greg Staples"
        flavorText = "\"The blazekite is a simple concept, really—just a vehicular application of dragscoop ionics and electropropulsion magnetronics.\"\n—Juzba, Izzet tinker"
        imageUri = "https://cards.scryfall.io/normal/front/1/4/14fd3734-8c28-4bd0-b202-eee1ab98c328.jpg?1783943466"
    }
}

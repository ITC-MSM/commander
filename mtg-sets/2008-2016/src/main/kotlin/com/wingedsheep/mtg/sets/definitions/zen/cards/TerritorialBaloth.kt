package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Territorial Baloth
 * {4}{G}
 * Creature — Beast
 * 4/4
 * Landfall — Whenever a land you control enters, this creature gets +2/+2 until end of turn.
 *
 * Landfall is a plain [Triggers.LandYouControlEnters] — ANY binding, because the printed line
 * never says "another".
 */
val TerritorialBaloth = card("Territorial Baloth") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Beast"
    power = 4
    toughness = 4
    oracleText = "Landfall — Whenever a land you control enters, this creature gets +2/+2 until end of turn."

    triggeredAbility {
        trigger = Triggers.LandYouControlEnters
        effect = Effects.ModifyStats(2, 2, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "188"
        artist = "Jesper Ejsing"
        flavorText = "Its territory is defined by wherever it is at the moment."
        imageUri = "https://cards.scryfall.io/normal/front/4/5/45033b8a-f3a8-4a23-b6b0-e011e3e7a4c1.jpg?1783942130"
    }
}

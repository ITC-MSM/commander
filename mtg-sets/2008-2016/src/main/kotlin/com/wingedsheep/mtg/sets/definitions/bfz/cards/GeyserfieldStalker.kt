package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Geyserfield Stalker
 * {4}{B}
 * Creature — Elemental
 * 3/2
 * Menace (This creature can't be blocked except by two or more creatures.)
 * Landfall — Whenever a land you control enters, this creature gets +2/+2 until end of turn.
 *
 * Landfall is a plain [Triggers.LandYouControlEnters] — ANY binding, because the printed line never says "another".
 */
val GeyserfieldStalker = card("Geyserfield Stalker") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Elemental"
    power = 3
    toughness = 2
    oracleText = "Menace (This creature can't be blocked except by two or more creatures.)\n" +
        "Landfall — Whenever a land you control enters, this creature gets +2/+2 until end of turn."

    keywords(Keyword.MENACE)

    triggeredAbility {
        trigger = Triggers.LandYouControlEnters
        effect = Effects.ModifyStats(2, 2, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "111"
        artist = "Deruchenko Alexander"
        imageUri = "https://cards.scryfall.io/normal/front/d/4/d4d1e1e1-fa24-4e78-a77c-4d41a58b8aa0.jpg?1783938203"
    }
}

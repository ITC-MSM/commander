package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Kalastria Nightwatch
 * {4}{B}
 * Creature — Vampire Warrior Ally
 * 4/5
 * Whenever you gain life, this creature gains flying until end of turn.
 */
val KalastriaNightwatch = card("Kalastria Nightwatch") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire Warrior Ally"
    power = 4
    toughness = 5
    oracleText = "Whenever you gain life, this creature gains flying until end of turn."

    triggeredAbility {
        trigger = Triggers.YouGainLife
        effect = Effects.GrantKeyword(Keyword.FLYING, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "115"
        artist = "Jama Jurabaev"
        flavorText = "\"Kalitas may have returned to mindless servitude beneath the Eldrazi, but we say never " +
            "again.\"\n" +
            "—Drana, Kalastria bloodchief"
        imageUri = "https://cards.scryfall.io/normal/front/1/3/13989bf2-fd02-4702-9170-4b066837de65.jpg?1783938200"
    }
}

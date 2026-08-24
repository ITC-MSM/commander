package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Belligerent Whiptail
 * {3}{R}
 * Creature — Wurm
 * 4/2
 * Landfall — Whenever a land you control enters, this creature gains first strike until end of turn.
 *
 * Landfall is a plain [Triggers.LandYouControlEnters] — ANY binding, because the printed line never says "another".
 */
val BelligerentWhiptail = card("Belligerent Whiptail") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Wurm"
    power = 4
    toughness = 2
    oracleText = "Landfall — Whenever a land you control enters, this creature gains first strike until end of " +
        "turn."

    triggeredAbility {
        trigger = Triggers.LandYouControlEnters
        effect = Effects.GrantKeyword(Keyword.FIRST_STRIKE, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "141"
        artist = "Jakub Kasper"
        flavorText = "\"Whiptails can sense a traveler's footsteps from a great distance. Measuring that distance " +
            "has been . . . difficult.\"\n" +
            "—Jalun, Affa sentry"
        imageUri = "https://cards.scryfall.io/normal/front/a/5/a5b5b7d2-acb8-4aca-b9e7-67e59aeb384b.jpg?1783938195"
    }
}

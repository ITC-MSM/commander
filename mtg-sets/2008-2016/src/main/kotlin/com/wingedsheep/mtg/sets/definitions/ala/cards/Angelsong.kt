package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Angelsong
 * {1}{W}
 * Instant
 * Prevent all combat damage that would be dealt this turn.
 * Cycling {2} ({2}, Discard this card: Draw a card.)
 *
 * A Fog with a cycling line. [Effects.PreventAllCombatDamage] is the whole spell — one untargeted
 * `PreventDamageEffect` with `PreventionScope.CombatOnly` and no recipient narrowing, so it shields
 * every source and every recipient; its default `Duration.EndOfTurn` is the printed "this turn".
 * The second line is [KeywordAbility.cycling] rather than a bare `Keyword.CYCLING`, because the
 * cost is a parameter the ability has to carry.
 */
val Angelsong = card("Angelsong") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Prevent all combat damage that would be dealt this turn.\n" +
        "Cycling {2} ({2}, Discard this card: Draw a card.)"

    spell {
        effect = Effects.PreventAllCombatDamage()
    }

    keywordAbility(KeywordAbility.cycling("{2}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "4"
        artist = "Sal Villagran"
        flavorText = "Clash of sword and cry of beast fall mute when angels sound the call to prayer."
        imageUri = "https://cards.scryfall.io/normal/front/0/8/0832e87f-afef-41a5-ba36-3eaf65551576.jpg"
    }
}

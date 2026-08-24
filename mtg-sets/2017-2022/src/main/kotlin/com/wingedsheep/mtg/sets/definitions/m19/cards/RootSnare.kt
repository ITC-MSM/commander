package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Root Snare
 * {1}{G}
 * Instant
 * Prevent all combat damage that would be dealt this turn.
 *
 * A plain Fog. [Effects.PreventAllCombatDamage] is the whole spell — one untargeted
 * `PreventDamageEffect` with `PreventionScope.CombatOnly` and no recipient narrowing, so it
 * shields every source and every recipient; its default `Duration.EndOfTurn` is the printed
 * "this turn". Passing an explicit `target` here would select the *targeted* shield instead
 * and the card would stop covering the board.
 */
val RootSnare = card("Root Snare") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Prevent all combat damage that would be dealt this turn."

    spell {
        effect = Effects.PreventAllCombatDamage()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "199"
        artist = "Mitchell Malloy"
        flavorText = "The only casualties were a snapped spear, a lost helmet, and some bruised egos."
        imageUri = "https://cards.scryfall.io/normal/front/7/6/76b01fd2-139a-47ed-a8e3-021aa9c91b02.jpg"
    }
}

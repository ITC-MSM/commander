package com.wingedsheep.mtg.sets.definitions.neo.cards

import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Sky-Blessed Samurai — Kamigawa: Neon Dynasty #37 (canonical printing)
 * {6}{W} · Enchantment Creature — Human Samurai · 4/4
 *
 * Affinity for enchantments (This spell costs {1} less to cast for each enchantment you control.)
 * Flying
 *
 * [KeywordAbility.Affinity] is generic over [CardType] — the engine counts permanents of that type
 * you control in projected state, so this is the Frogmite value with `ENCHANTMENT` swapped in and
 * needs no new vocabulary. The card counts *itself*'s siblings only: affinity is locked in while
 * casting, when this is still a spell on the stack.
 */
val SkyBlessedSamurai = card("Sky-Blessed Samurai") {
    manaCost = "{6}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment Creature — Human Samurai"
    power = 4
    toughness = 4
    oracleText = "Affinity for enchantments (This spell costs {1} less to cast for each " +
        "enchantment you control.)\nFlying"

    keywordAbility(KeywordAbility.Affinity(CardType.ENCHANTMENT))
    keywords(Keyword.FLYING)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "37"
        artist = "Victor Adame Minguez"
        flavorText = "Imperial samurai who show exceptional honor and dexterity are invited to " +
            "train as mothriders, elite guards charged with protecting the skies of Eiganjo."
        imageUri = "https://cards.scryfall.io/normal/front/c/9/c9c7ba6f-c58e-4347-92e4-51ae8f2d5a88.jpg?1783923912"
    }
}

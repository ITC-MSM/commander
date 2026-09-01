package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Doomskar Oracle
 * {2}{W}
 * Creature — Human Cleric
 * 3/2
 * Whenever you cast your second spell each turn, you gain 2 life.
 * Foretell {W} (During your turn, you may pay {2} and exile this card from your hand face down. Cast it on a later turn for its foretell cost.)
 *
 * The second-spell ordinal is [Triggers.NthSpellCast] with n = 2 scoped to [Player.You]; the
 * engine tracks each player's per-turn cast count, so no bookkeeping lives on the card. Foretell
 * is lowered into [KeywordAbility.foretell] — it is a real cast, so it advances that count.
 */
val DoomskarOracle = card("Doomskar Oracle") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Cleric"
    oracleText = "Whenever you cast your second spell each turn, you gain 2 life.\n" +
        "Foretell {W} (During your turn, you may pay {2} and exile this card from your hand face down. Cast it on a later turn for its foretell cost.)"
    power = 3
    toughness = 2

    triggeredAbility {
        trigger = Triggers.NthSpellCast(2, Player.You)
        effect = Effects.GainLife(2)
    }

    keywordAbility(KeywordAbility.foretell("{W}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "10"
        artist = "Taylor Ingvarsson"
        flavorText = "\"Giants and monsters loom on the horizon, and the elves plan to murder our gods!\""
        imageUri = "https://cards.scryfall.io/normal/front/6/d/6dae01c8-15bb-44ad-a2c6-9bcf7a9e8c17.jpg"
    }
}

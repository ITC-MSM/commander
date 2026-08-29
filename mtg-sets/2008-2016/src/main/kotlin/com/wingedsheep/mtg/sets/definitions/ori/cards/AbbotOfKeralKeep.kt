package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Abbot of Keral Keep
 * {1}{R}
 * Creature — Human Monk
 * 2/1
 *
 * Prowess (Whenever you cast a noncreature spell, this creature gets +1/+1 until end of turn.)
 * When this creature enters, exile the top card of your library. Until end of turn, you may play that card.
 *
 * The ETB is the named "impulse draw" mechanic — [Patterns.Exile.impulse] gathers the top card,
 * moves the collection to exile and grants play permission expiring at end of turn. The card still
 * costs its mana, which is why this is `impulse` rather than the play-free variant.
 */
val AbbotOfKeralKeep = card("Abbot of Keral Keep") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Monk"
    oracleText = "Prowess (Whenever you cast a noncreature spell, this creature gets +1/+1 until end of turn.)\n" +
        "When this creature enters, exile the top card of your library. Until end of turn, you may play that card."
    power = 2
    toughness = 1

    prowess()

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Exile.impulse(1)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "127"
        artist = "Deruchenko Alexander"
        imageUri = "https://cards.scryfall.io/normal/front/c/b/cb7a2770-9a20-4f52-aac4-24502f50e374.jpg"
    }
}

package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.transmute
import com.wingedsheep.sdk.model.Rarity

val DizzySpell = card("Dizzy Spell") {
    manaCost = "{U}"
    typeLine = "Instant"
    oracleText = "Target creature gets -3/-0 until end of turn.\nTransmute {1}{U}{U} ({1}{U}{U}, Discard this card: Search your library for a card with the same mana value as this card, reveal it, put it into your hand, then shuffle. Transmute only as a sorcery.)"
    colorIdentity = "U"

    spell {
        val creature = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(-3, 0, creature)
    }
    transmute("{1}{U}{U}")

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "43"
        artist = "Christopher Moeller"
        imageUri = "https://cards.scryfall.io/normal/front/6/e/6e0db10d-fb6d-44df-9ff2-6f1e0e8f8209.jpg?1783943689"
    }
}

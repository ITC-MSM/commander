package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Neutralize
 * {1}{U}{U}
 * Instant
 * Counter target spell.
 * Cycling {2} ({2}, Discard this card: Draw a card.)
 */
val Neutralize = card("Neutralize") {
    manaCost = "{1}{U}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Counter target spell.\nCycling {2} ({2}, Discard this card: Draw a card.)"

    spell {
        target = Targets.Spell
        effect = Effects.CounterSpell()
    }

    keywordAbility(KeywordAbility.cycling("{2}"))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "59"
        artist = "Yongjae Choi"
        flavorText = "On Ikoria, reactive magic can make the difference between barely grazed and really dead."
        imageUri = "https://cards.scryfall.io/normal/front/0/4/0430da3c-9460-4b62-ae28-2e7e6f4d06a4.jpg?1783931073"
    }
}

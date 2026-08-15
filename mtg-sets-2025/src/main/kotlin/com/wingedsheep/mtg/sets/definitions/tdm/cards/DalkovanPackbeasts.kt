package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.mobilize
import com.wingedsheep.sdk.model.Rarity

/**
 * Dalkovan Packbeasts — Tarkir: Dragonstorm #7
 * {2}{W} · Creature — Ox · 0/4
 *
 * Vigilance
 * Mobilize 3 (Whenever this creature attacks, create three tapped and attacking 1/1 red Warrior
 * creature tokens. Sacrifice them at the beginning of the next end step.)
 *
 * Both abilities are keyword helpers: `keywords(Keyword.VIGILANCE)` for vigilance and the
 * `mobilize(n)` builder helper, which adds the display-only "Mobilize 3" keyword ability plus the
 * attack-triggered ability that creates three tapped-and-attacking Warrior tokens and schedules
 * their sacrifice at the next end step.
 */
val DalkovanPackbeasts = card("Dalkovan Packbeasts") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Ox"
    power = 0
    toughness = 4
    oracleText = "Vigilance\n" +
        "Mobilize 3 (Whenever this creature attacks, create three tapped and attacking 1/1 red Warrior creature tokens. Sacrifice them at the beginning of the next end step.)"

    keywords(Keyword.VIGILANCE)
    mobilize(3)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "7"
        artist = "Constantin Marin"
        flavorText = "A burden shared is a burden lightened.\n—Mardu proverb"
        imageUri = "https://cards.scryfall.io/normal/front/4/d/4df7b253-6107-47d6-b650-cb4d3e0aec6b.jpg?1743203980"
    }
}

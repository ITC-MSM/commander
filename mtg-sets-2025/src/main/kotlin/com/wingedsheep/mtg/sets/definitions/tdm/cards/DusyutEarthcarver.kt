package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Dusyut Earthcarver — Tarkir: Dragonstorm #141
 * {5}{G} · Creature — Elephant Druid · 4/4
 *
 * Reach
 * When this creature enters, it endures 3. (Put three +1/+1 counters on it or
 * create a 3/3 white Spirit creature token.)
 */
val DusyutEarthcarver = card("Dusyut Earthcarver") {
    manaCost = "{5}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elephant Druid"
    power = 4
    toughness = 4
    oracleText = "Reach\n" +
        "When this creature enters, it endures 3. " +
        "(Put three +1/+1 counters on it or create a 3/3 white Spirit creature token.)"

    keywords(Keyword.REACH)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Endure(3)
        description = "When this creature enters, it endures 3."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "141"
        artist = "Andrea Piparo"
        imageUri = "https://cards.scryfall.io/normal/front/b/9/b98ecc96-f557-479a-8685-2b5487d5b407.jpg?1743204529"
    }
}

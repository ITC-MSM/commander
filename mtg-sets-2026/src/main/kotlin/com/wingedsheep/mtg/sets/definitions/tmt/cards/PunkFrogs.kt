package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Punk Frogs
 * {3}{G/U}{G/U}
 * Creature — Frog Mutant Rebel
 * 4/5
 *
 * Ward {3}
 */
val PunkFrogs = card("Punk Frogs") {
    manaCost = "{3}{G/U}{G/U}"
    colorIdentity = "GU"
    typeLine = "Creature — Frog Mutant Rebel"
    oracleText = "Ward {3} (Whenever this creature becomes the target of a spell or ability an opponent controls, counter it unless that player pays {3}.)"
    power = 4
    toughness = 5

    keywords(Keyword.WARD)
    keywordAbility(KeywordAbility.ward("{3}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "164"
        artist = "Michele Giorgi"
        flavorText = "\"Weirdo ninjas got some nerve showing up on our turf. Sayin' they don't wanna fight but they got swords and stuff. Heh.\""
        imageUri = "https://cards.scryfall.io/normal/front/3/7/375c91a3-53c4-4f2d-bf7b-2c79a219d3ae.jpg?1771587049"
    }
}

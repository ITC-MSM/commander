package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

val SaheeliConsulOfOversight = card("Saheeli, Consul of Oversight") {
    manaCost = "{3}{W}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Creature — Human Advisor"
    power = 4
    toughness = 4
    oracleText = "Flying\nWhenever you scry or surveil, create a 1/1 colorless Thopter artifact creature token with flying. This ability triggers only once each turn."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.WheneverYouScryOrSurveil
        oncePerTurn = true
        effect = Effects.CreateToken(power = 1, toughness = 1, creatureTypes = setOf("Thopter"), artifactToken = true, keywords = setOf(Keyword.FLYING), imageUri = "https://cards.scryfall.io/normal/front/b/f/bfd6132f-c96b-4ce0-ac4d-c46356afc767.jpg?1789735223")
        description = "Whenever you scry or surveil, create a 1/1 colorless Thopter artifact creature token with flying. This ability triggers only once each turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "203"
        artist = "Billy Christian"
        flavorText = "\"A creative mind is a dangerous thing.\""
        imageUri = "https://cards.scryfall.io/normal/front/0/7/07572be0-6610-493c-a21e-14b78e9805c9.jpg?1789645608"
        inBooster = false
    }
}

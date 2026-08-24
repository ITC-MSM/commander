package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Bomat Bazaar Barge
 * {4}
 * Artifact — Vehicle
 * 5/5
 * When this Vehicle enters, draw a card.
 * Crew 3 (Tap any number of creatures you control with total power 3 or more: This Vehicle becomes
 * an artifact creature until end of turn.)
 *
 * The Vehicle enters as a noncreature artifact, so the enters trigger fires without any crewing —
 * plain [Triggers.EntersBattlefield]. Crew is the engine-owned [KeywordAbility.crew] ability.
 */
val BomatBazaarBarge = card("Bomat Bazaar Barge") {
    manaCost = "{4}"
    typeLine = "Artifact — Vehicle"
    oracleText = "When this Vehicle enters, draw a card.\n" +
        "Crew 3 (Tap any number of creatures you control with total power 3 or more: This Vehicle becomes an artifact creature until end of turn.)"
    power = 5
    toughness = 5

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrawCards(1)
    }

    keywordAbility(KeywordAbility.crew(3))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "198"
        artist = "Christine Choi"
        imageUri = "https://cards.scryfall.io/normal/front/0/f/0f32be75-979d-43a9-9132-2cf013ddaf3b.jpg?1783937161"
    }
}

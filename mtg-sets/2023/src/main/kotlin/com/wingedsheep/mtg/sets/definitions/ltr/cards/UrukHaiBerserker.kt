package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Uruk-hai Berserker
 * {2}{B}
 * Creature — Orc Berserker
 * 3/2
 *
 * When this creature enters, the Ring tempts you.
 */
val UrukHaiBerserker = card("Uruk-hai Berserker") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Orc Berserker"
    power = 3
    toughness = 2
    oracleText = "When this creature enters, the Ring tempts you."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.TheRingTemptsYou()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "112"
        artist = "Victor Harmatiuk"
        flavorText = "\"What of the dawn? We are the Uruk-hai; we do not stop the fight for night or day, for fair weather or for storm. We come to kill, by sun or moon. What of the dawn?\""
        imageUri = "https://cards.scryfall.io/normal/front/9/3/936421a0-2c2a-4410-941d-4e6b88166bd1.jpg?1687694551"
    }
}

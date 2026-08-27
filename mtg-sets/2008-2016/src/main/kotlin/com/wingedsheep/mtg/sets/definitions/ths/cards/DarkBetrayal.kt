package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Dark Betrayal
 * {B}
 * Instant
 *
 * Destroy target black creature.
 *
 * The "black" is a targeting predicate, so it rides [Targets.CreatureWithColor] rather than a
 * separate condition.
 */
val DarkBetrayal = card("Dark Betrayal") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Destroy target black creature."

    spell {
        val t = target("target", Targets.CreatureWithColor(Color.BLACK))
        effect = Effects.Destroy(t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "83"
        artist = "Nils Hamm"
        flavorText = "\"You're just like me: ruthless, cunning, and ambitious. Obviously you're a threat.\"\n—Basarios the Blade"
        imageUri = "https://cards.scryfall.io/normal/front/5/6/56adf4ea-1b1c-4737-8574-1848ca47d4f3.jpg"
    }
}

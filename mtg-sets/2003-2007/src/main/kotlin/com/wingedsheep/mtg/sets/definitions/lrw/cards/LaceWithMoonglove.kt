package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Lace with Moonglove
 * {2}{G}
 * Instant
 * Target creature gains deathtouch until end of turn.
 * Draw a card.
 */
val LaceWithMoonglove = card("Lace with Moonglove") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Target creature gains deathtouch until end of turn. (Any amount of damage it deals to a " +
        "creature is enough to destroy that creature.)\nDraw a card."

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.Composite(
            Effects.GrantKeyword(Keyword.DEATHTOUCH, t),
            Effects.DrawCards(1)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "225"
        artist = "Rebecca Guay"
        flavorText = "\"Which is more filled with poison: the flower of the moonglove or the minds of elves?\"\n" +
            "—Vessifrus, flamekin demagogue"
        imageUri = "https://cards.scryfall.io/normal/front/3/2/32f209d6-c194-4208-864d-f8a44b88997f.jpg?1783942862"
    }
}

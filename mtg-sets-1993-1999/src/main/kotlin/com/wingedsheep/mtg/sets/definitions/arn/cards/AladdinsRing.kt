package com.wingedsheep.mtg.sets.definitions.arn.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Aladdin's Ring
 * {8}
 * Artifact
 * {8}, {T}: This artifact deals 4 damage to any target.
 */
val AladdinsRing = card("Aladdin's Ring") {
    manaCost = "{8}"
    typeLine = "Artifact"
    oracleText = "{8}, {T}: This artifact deals 4 damage to any target."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{8}"), Costs.Tap)
        val t = target("any target", Targets.Any)
        effect = Effects.DealDamage(4, t)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "57"
        artist = "Dan Frazier"
        flavorText = "\"After these words the magician drew a ring off his finger, and put it on one of Aladdin's, saying: 'It is a talisman against all evil, so long as you obey me.'\" —The Arabian Nights, Junior Classics trans."
        imageUri = "https://cards.scryfall.io/normal/front/b/b/bb2b74a2-cb74-4b54-b9c6-78c63f14cf5b.jpg?1562929937"
    }
}

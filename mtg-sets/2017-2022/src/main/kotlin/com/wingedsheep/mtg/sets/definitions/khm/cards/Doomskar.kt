package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Doomskar
 * {3}{W}{W}
 * Sorcery
 * Destroy all creatures.
 * Foretell {1}{W}{W} (During your turn, you may pay {2} and exile this card from your hand face down. Cast it on a later turn for its foretell cost.)
 *
 * A wrath whose whole point is the foretell line: exiling it face down on turn three hides the
 * board wipe, and [KeywordAbility.foretell] is what lets the engine offer the later {1}{W}{W} cast
 * from exile.
 */
val Doomskar = card("Doomskar") {
    manaCost = "{3}{W}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Destroy all creatures.\n" +
        "Foretell {1}{W}{W} (During your turn, you may pay {2} and exile this card from your hand face down. Cast it on a later turn for its foretell cost.)"

    spell {
        effect = Effects.DestroyAll(GameObjectFilter.Creature)
    }

    keywordAbility(KeywordAbility.foretell("{1}{W}{W}"))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "9"
        artist = "Piotr Dura"
        flavorText = "The realms crashed together, with Bretagard at the center of the calamity."
        imageUri = "https://cards.scryfall.io/normal/front/1/3/130ee895-1e5e-4f82-bb66-e1275bac75dd.jpg"
    }
}

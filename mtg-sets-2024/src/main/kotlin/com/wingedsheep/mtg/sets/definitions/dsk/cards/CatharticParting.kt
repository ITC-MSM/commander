package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Cathartic Parting
 * {1}{G}
 * Sorcery
 * The owner of target artifact or enchantment an opponent controls shuffles it into their library.
 * You may shuffle up to four target cards from your graveyard into your library.
 *
 * Both clauses move cards to their owner's library shuffled — [Effects.ShuffleIntoLibrary] always
 * goes to the owner's library, so the opponent's permanent returns to the opponent's library and
 * your graveyard cards return to yours. The graveyard clause is "you may … up to four target
 * cards", modeled as an optional multi-target requirement (choosing zero is legal). Unchosen
 * indexed targets resolve to nothing and are skipped.
 */
val CatharticParting = card("Cathartic Parting") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "The owner of target artifact or enchantment an opponent controls shuffles it " +
        "into their library. You may shuffle up to four target cards from your graveyard into " +
        "your library."

    spell {
        val permanent = target(
            "permanent",
            TargetObject(filter = TargetFilter(GameObjectFilter.ArtifactOrEnchantment.opponentControls()))
        )
        val (g0, g1, g2, g3) = targets(
            "graveyard card",
            TargetObject(
                count = 4,
                optional = true,
                filter = TargetFilter(GameObjectFilter.Any.ownedByYou(), zone = Zone.GRAVEYARD)
            )
        )
        effect = Effects.ShuffleIntoLibrary(permanent)
            .then(Effects.ShuffleIntoLibrary(g0))
            .then(Effects.ShuffleIntoLibrary(g1))
            .then(Effects.ShuffleIntoLibrary(g2))
            .then(Effects.ShuffleIntoLibrary(g3))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "171"
        artist = "Miranda Meeks"
        flavorText = "\"We got more than most people do. We got the chance to say goodbye.\""
        imageUri = "https://cards.scryfall.io/normal/front/6/a/6afc250a-854d-4555-ba78-db9283fa7c22.jpg?1726286497"
    }
}

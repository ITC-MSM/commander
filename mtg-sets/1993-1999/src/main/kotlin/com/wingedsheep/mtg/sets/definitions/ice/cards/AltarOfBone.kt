package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Altar of Bone
 * {G}{W}
 * Sorcery
 *
 * As an additional cost to cast this spell, sacrifice a creature.
 * Search your library for a creature card, reveal it, put it into your hand, then shuffle.
 *
 * The whole second line is `Patterns.Library.searchLibrary`'s recipe (gather → select → reveal-move
 * → shuffle → `LibrarySearchedEvent`); `reveal = true` is a facade parameter, so restating those
 * steps by hand is how the reveal gets dropped. Eladamri's Call is the same sentence. The first line
 * is the shared sacrifice cost atom, exactly as Natural Order spells it.
 */
val AltarOfBone = card("Altar of Bone") {
    manaCost = "{G}{W}"
    colorIdentity = "GW"
    typeLine = "Sorcery"
    oracleText = "As an additional cost to cast this spell, sacrifice a creature.\n" +
        "Search your library for a creature card, reveal it, put it into your hand, then shuffle."

    additionalCost(Costs.additional.SacrificePermanent(filter = GameObjectFilter.Creature))

    spell {
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.Creature,
            destination = SearchDestination.HAND,
            reveal = true
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "281"
        artist = "Melissa A. Benson"
        imageUri = "https://cards.scryfall.io/normal/front/7/5/75d5b014-8675-4d91-a539-ac5c31d44b35.jpg"
    }
}

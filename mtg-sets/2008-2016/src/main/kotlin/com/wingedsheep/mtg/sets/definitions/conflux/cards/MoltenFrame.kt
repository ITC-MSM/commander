package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Molten Frame
 * {1}{R}
 * Instant
 * Destroy target artifact creature.
 * Cycling {2} ({2}, Discard this card: Draw a card.)
 *
 * "Artifact creature" is one noun phrase and so one filter — the shared
 * [GameObjectFilter.ArtifactCreature] (both predicates required, an `And`), never an `Or` union.
 * [Effects.Destroy] is the `byDestruction` move to the graveyard, so indestructible and
 * regeneration still apply. Cycling is [KeywordAbility.cycling], which carries its own cost.
 */
val MoltenFrame = card("Molten Frame") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Destroy target artifact creature.\n" +
        "Cycling {2} ({2}, Discard this card: Draw a card.)"

    spell {
        val t = target("target", TargetPermanent(filter = TargetFilter(GameObjectFilter.ArtifactCreature)))
        effect = Effects.Destroy(t)
    }

    keywordAbility(KeywordAbility.cycling("{2}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "69"
        artist = "Izzy"
        flavorText = "The metal filigree in his body glowed red-hot, and his flesh soon followed."
        imageUri = "https://cards.scryfall.io/normal/front/5/8/58356504-e28e-456c-b1d3-e6232f4d78a6.jpg"
    }
}

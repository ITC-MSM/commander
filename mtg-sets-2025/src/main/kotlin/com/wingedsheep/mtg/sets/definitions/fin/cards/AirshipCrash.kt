package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Airship Crash
 * {2}{G}
 * Instant
 * Destroy target artifact, enchantment, or creature with flying.
 * Cycling {2} ({2}, Discard this card: Draw a card.)
 *
 * The target filter is a heterogeneous OR — any artifact, any enchantment, OR a
 * creature that has flying. The flying restriction binds only to the creature branch.
 */
val AirshipCrash = card("Airship Crash") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Destroy target artifact, enchantment, or creature with flying.\nCycling {2} ({2}, Discard this card: Draw a card.)"

    spell {
        val t = target(
            "target",
            TargetPermanent(
                filter = TargetFilter(
                    GameObjectFilter.Artifact or
                        GameObjectFilter.Enchantment or
                        GameObjectFilter.Creature.withKeyword(Keyword.FLYING)
                )
            )
        )
        effect = Effects.Move(t, Zone.GRAVEYARD, byDestruction = true)
    }

    keywordAbility(KeywordAbility.cycling("{2}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "171"
        artist = "Enora Mercier"
        flavorText = "\"Just our luck to crash in this forest... it's gonna get interesting...\"\n—Baku, Tantalus leader"
        imageUri = "https://cards.scryfall.io/normal/front/e/c/ec91c4e4-711f-464d-bc83-e6813f4fdcdb.jpg?1750188044"
    }
}

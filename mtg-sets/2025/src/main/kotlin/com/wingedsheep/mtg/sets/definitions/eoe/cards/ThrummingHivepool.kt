package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Thrumming Hivepool
 * {6}
 * Artifact
 *
 * Affinity for Slivers (This spell costs {1} less to cast for each Sliver you control.)
 * Slivers you control have double strike and haste.
 * At the beginning of your upkeep, create two 1/1 colorless Sliver creature tokens.
 */
val ThrummingHivepool = card("Thrumming Hivepool") {
    manaCost = "{6}"
    typeLine = "Artifact"
    oracleText = "Affinity for Slivers (This spell costs {1} less to cast for each Sliver you control.)\n" +
        "Slivers you control have double strike and haste.\n" +
        "At the beginning of your upkeep, create two 1/1 colorless Sliver creature tokens."

    keywordAbility(KeywordAbility.AffinityForSubtype(Subtype.SLIVER))

    val sliversYouControl = GroupFilter(GameObjectFilter.Creature.withSubtype("Sliver").youControl())

    staticAbility {
        ability = GrantKeyword(Keyword.DOUBLE_STRIKE, sliversYouControl)
    }
    staticAbility {
        ability = GrantKeyword(Keyword.HASTE, sliversYouControl)
    }

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = Effects.CreateToken(
            count = 2,
            power = 1,
            toughness = 1,
            creatureTypes = setOf("Sliver"),
            imageUri = "https://cards.scryfall.io/normal/front/b/f/bf7501ee-783d-49c6-b381-1db056360b40.jpg?1752946465"
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "247"
        artist = "Rob Rey"
        flavorText = "\"I'll never escape this rock, but I fear they will.\"\n—Decrypted log 4.11"
        imageUri = "https://cards.scryfall.io/normal/front/8/5/85caf659-7b43-462e-a342-34703d46eb57.jpg?1752947564"
        ruling("2025-07-25", "The mana value of a spell isn't changed by alternative costs, cost increases, or cost reductions. For example, if you cast Thrumming Hivepool (an artifact with affinity for Slivers), its mana value is 6 no matter how many Slivers you controlled when you cast it.")
    }
}

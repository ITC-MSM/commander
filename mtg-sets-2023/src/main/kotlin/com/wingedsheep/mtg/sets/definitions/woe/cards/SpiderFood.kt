package com.wingedsheep.mtg.sets.definitions.woe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Spider Food
 * {2}{G}
 * Sorcery
 * Destroy up to one target artifact, enchantment, or creature with flying. Create a Food token.
 *
 * The target filter is a heterogeneous OR — any artifact, any enchantment, OR a creature that has
 * flying (the flying restriction binds only to the creature branch). The target is optional
 * ("up to one"), so the spell still resolves and creates the Food even with no legal/chosen target.
 */
val SpiderFood = card("Spider Food") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Destroy up to one target artifact, enchantment, or creature with flying. " +
        "Create a Food token. (It's an artifact with \"{2}, {T}, Sacrifice this token: You gain 3 life.\")"

    spell {
        val t = target(
            "target",
            TargetPermanent(
                optional = true,
                filter = TargetFilter(
                    GameObjectFilter.Artifact or
                        GameObjectFilter.Enchantment or
                        GameObjectFilter.Creature.withKeyword(Keyword.FLYING)
                )
            )
        )
        effect = Effects.Move(t, Zone.GRAVEYARD, byDestruction = true) then
            Effects.CreateFood()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "186"
        artist = "Mila Pesic"
        flavorText = "To the giant spiders of Dunbarrow, there's little difference between fly and faerie."
        imageUri = "https://cards.scryfall.io/normal/front/b/9/b9fd720b-e9c2-4e82-917e-bab6c544afb0.jpg?1783915077"
    }
}

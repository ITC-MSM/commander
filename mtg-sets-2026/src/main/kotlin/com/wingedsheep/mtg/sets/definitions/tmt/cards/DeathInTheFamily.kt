package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Death in the Family
 * {1}{B}
 * Instant
 *
 * Exile target creature with mana value 3 or less.
 */
val DeathInTheFamily = card("Death in the Family") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Exile target creature with mana value 3 or less."

    spell {
        val creature = target(
            "creature with mana value 3 or less",
            TargetObject(filter = TargetFilter.Creature.manaValueAtMost(3))
        )
        effect = Effects.Exile(creature)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "61"
        artist = "Justyna Dura"
        flavorText = "Often enemies, always brothers . . . till the end."
        imageUri = "https://cards.scryfall.io/normal/front/2/e/2e4f2148-d398-4f3d-9c59-81c87b6a4588.jpg?1771586845"
    }
}

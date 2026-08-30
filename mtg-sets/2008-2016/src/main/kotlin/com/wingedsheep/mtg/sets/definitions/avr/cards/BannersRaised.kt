package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Banners Raised
 * {R}
 * Instant
 *
 * Creatures you control get +1/+0 until end of turn.
 *
 * A group pump — the Chorus of Woe shape. [Patterns.Group.modifyStatsForAll] iterates the group
 * and applies the bonus to each member ([com.wingedsheep.sdk.scripting.targets.EffectTarget.Self]
 * inside the iteration), so creatures that enter after resolution are correctly untouched.
 */
val BannersRaised = card("Banners Raised") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Creatures you control get +1/+0 until end of turn."

    spell {
        effect = Patterns.Group.modifyStatsForAll(
            1, 0,
            GroupFilter(GameObjectFilter.Creature.youControl())
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "127"
        artist = "Mike Bierek"
        flavorText = "After the destruction of the Helvault, fearful mobs soon became fearless battalions."
        imageUri = "https://cards.scryfall.io/normal/front/a/7/a7792df3-e2ab-4e60-abee-f24b72807107.jpg?1783940690"
    }
}

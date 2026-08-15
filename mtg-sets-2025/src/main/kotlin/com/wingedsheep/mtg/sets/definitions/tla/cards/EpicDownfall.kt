package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Epic Downfall
 * {1}{B}
 * Sorcery
 *
 * Exile target creature with mana value 3 or greater.
 */
val EpicDownfall = card("Epic Downfall") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Exile target creature with mana value 3 or greater."

    spell {
        val creature = target("creature", TargetCreature(filter = TargetFilter.Creature.manaValueAtLeast(3)))
        effect = Effects.Exile(creature)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "96"
        artist = "Hristo D. Chukov"
        flavorText = "With Azula's defeat, all of her nobility, grace, and confidence shattered into harrowing screams from the would-be Fire Lord."
        imageUri = "https://cards.scryfall.io/normal/front/3/b/3b4a6804-04f5-4467-bb1c-9466a47bc55f.jpg?1764120663"
    }
}

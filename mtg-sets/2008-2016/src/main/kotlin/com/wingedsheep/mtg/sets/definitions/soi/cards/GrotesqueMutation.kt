package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Grotesque Mutation
 * {1}{B}
 * Instant
 * Target creature gets +3/+1 and gains lifelink until end of turn. (Damage dealt by the creature also causes its controller to gain that much life.)
 */
val GrotesqueMutation = card("Grotesque Mutation") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Target creature gets +3/+1 and gains lifelink until end of turn. (Damage dealt by the creature also causes its controller to gain that much life.)"

    spell {
        val t = target("target", TargetCreature(filter = TargetFilter.Creature))
        effect = Effects.Composite(
            Effects.ModifyStats(3, 1, t),
            Effects.GrantKeyword(Keyword.LIFELINK, t)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "115"
        artist = "Dan Murayama Scott"
        imageUri = "https://cards.scryfall.io/normal/front/c/e/ce84d54c-ef63-4b90-a2b6-99a4aa21c02d.jpg?1783937773"

        ruling(
            "2016-04-08",
            "Multiple instances of lifelink on the same creature are redundant."
        )
    }
}

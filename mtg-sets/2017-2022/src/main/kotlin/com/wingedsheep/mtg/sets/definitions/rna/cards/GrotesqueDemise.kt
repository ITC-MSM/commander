package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Grotesque Demise — Ravnica Allegiance #75
 * {2}{B} · Instant
 *
 * Exile, not destroy — indestructible and regeneration do not save it. The power restriction
 * lives on the *target*, so it is checked on announcement and again on resolution (CR 608.2b).
 */
val GrotesqueDemise = card("Grotesque Demise") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Exile target creature with power 3 or less."

    spell {
        val small = target("target", TargetCreature(filter = TargetFilter(GameObjectFilter.Creature.powerAtMost(3))))
        effect = Effects.Exile(small)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "75"
        artist = "Ben Wootten"
        flavorText = "\"A debtor's soul has little value, except as a warning to others who might consider defaulting on their loans.\"\n" +
        "—Ubea, Orzhov ministrant"
        imageUri = "https://cards.scryfall.io/normal/front/b/6/b698c5e1-3816-4f35-8e39-65dc68f5c64f.jpg"
    }
}

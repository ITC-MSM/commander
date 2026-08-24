package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Shadowstorm
 * {R}
 * Sorcery
 * Shadowstorm deals 2 damage to each creature with shadow.
 */
val Shadowstorm = card("Shadowstorm") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Shadowstorm deals 2 damage to each creature with shadow."

    spell {
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.withKeyword(Keyword.SHADOW)),
            Effects.DealDamage(2, EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "202"
        artist = "Adam Rex"
        flavorText = "\"You've not seen a storm until you've seen the kind that roars between worlds.\"\n" +
            "—Lyna, Soltari emissary"
        imageUri = "https://cards.scryfall.io/normal/front/3/6/367c4ad6-973d-47ba-9431-312f9f2996f6.jpg"
    }
}

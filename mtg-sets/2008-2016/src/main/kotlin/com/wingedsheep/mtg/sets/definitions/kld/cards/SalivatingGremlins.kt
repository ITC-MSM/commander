package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Salivating Gremlins
 * {2}{R}
 * Creature — Gremlin
 * 2/3
 * Whenever an artifact you control enters, this creature gets +2/+0 and gains trample until end of
 * turn.
 *
 * The Weldfast Wingsmith trigger shape — [Triggers.entersBattlefield] over
 * `Artifact.youControl()` with [TriggerBinding.ANY], so every artifact you control sets it off,
 * not only the source — feeding a self-targeted pump plus keyword grant.
 */
val SalivatingGremlins = card("Salivating Gremlins") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Gremlin"
    oracleText = "Whenever an artifact you control enters, this creature gets +2/+0 and gains trample until end of turn."
    power = 2
    toughness = 3

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Artifact.youControl(),
            binding = TriggerBinding.ANY
        )
        effect = Effects.Composite(
            Effects.ModifyStats(2, 0, EffectTarget.Self),
            Effects.GrantKeyword(Keyword.TRAMPLE, EffectTarget.Self)
        )
        description = "This creature gets +2/+0 and gains trample until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "129"
        artist = "Christopher Burdett"
        flavorText = "If an elf can dream it, a dwarf can build it, and a gremlin can eat it."
        imageUri = "https://cards.scryfall.io/normal/front/b/1/b1e0187a-438f-407c-a0ef-f62517c44994.jpg?1783937188"
    }
}

package com.wingedsheep.mtg.sets.definitions.neo.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Towashi Songshaper — Kamigawa: Neon Dynasty #167 (canonical printing)
 * {1}{R} · Artifact Creature — Human Artificer · 2/2
 *
 * Whenever another artifact you control enters, this creature gets +1/+0 until end of turn.
 *
 * "Another" is the trigger's binding, not a clause in its filter: [TriggerBinding.OTHER] excludes
 * the source, so the Songshaper's own arrival never pumps it.
 */
val TowashiSongshaper = card("Towashi Songshaper") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Artifact Creature — Human Artificer"
    power = 2
    toughness = 2
    oracleText = "Whenever another artifact you control enters, this creature gets +1/+0 until end of turn."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Artifact.youControl(),
            binding = TriggerBinding.OTHER,
        )
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
        description = "Whenever another artifact you control enters, this creature gets +1/+0 until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "167"
        artist = "Fajareka Setiawan"
        flavorText = "Most Uprisers augment their bodies for speed, strength, or protection, but " +
            "a few just want to be one with the music."
        imageUri = "https://cards.scryfall.io/normal/front/f/7/f7ff9be1-765f-4001-a0ac-39c8099924eb.jpg?1783923857"
    }
}

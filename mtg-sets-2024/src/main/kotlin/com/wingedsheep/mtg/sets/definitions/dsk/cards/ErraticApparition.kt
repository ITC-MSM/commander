package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Erratic Apparition
 * {2}{U}
 * Creature — Spirit
 * 1/3
 * Flying, vigilance
 * Eerie — Whenever an enchantment you control enters and whenever you fully unlock a Room,
 * this creature gets +1/+1 until end of turn.
 */
val ErraticApparition = card("Erratic Apparition") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Spirit"
    power = 1
    toughness = 3
    oracleText = "Flying, vigilance\nEerie — Whenever an enchantment you control enters and " +
        "whenever you fully unlock a Room, this creature gets +1/+1 until end of turn."

    keywords(Keyword.FLYING, Keyword.VIGILANCE, Keyword.EERIE)

    // Eerie trigger — part 1: whenever an enchantment you control enters
    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Enchantment.youControl(),
            binding = TriggerBinding.ANY,
        )
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
        description = "Eerie — Whenever an enchantment you control enters, this creature gets +1/+1 until end of turn."
    }

    // Eerie trigger — part 2: whenever you fully unlock a Room
    triggeredAbility {
        trigger = Triggers.RoomFullyUnlocked
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
        description = "Eerie — Whenever you fully unlock a Room, this creature gets +1/+1 until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "54"
        artist = "Miranda Meeks"
        flavorText = "\"What's got you all bent out of shape?\"\n—Joseph, comedian, last words"
        imageUri = "https://cards.scryfall.io/normal/front/a/7/a74fd612-2890-4333-a379-5bf7650fbb87.jpg?1726286058"
    }
}

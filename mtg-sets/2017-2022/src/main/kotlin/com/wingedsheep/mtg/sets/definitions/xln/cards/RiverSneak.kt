package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlocked
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * River Sneak
 * {1}{U}
 * Creature — Merfolk Warrior
 * 1/1
 * This creature can't be blocked.
 * Whenever another Merfolk you control enters, this creature gets +1/+1 until end of turn.
 *
 * The evasion is the source-scoped [CantBeBlocked] static. The bare tribal noun "Merfolk" names
 * every *permanent* with the subtype (not just creatures), so the ETB trigger filters on
 * [GameObjectFilter.Permanent]; [TriggerBinding.OTHER] excludes River Sneak itself.
 */
val RiverSneak = card("River Sneak") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk Warrior"
    power = 1
    toughness = 1
    oracleText = "This creature can't be blocked.\nWhenever another Merfolk you control enters, this creature gets +1/+1 until end of turn."

    staticAbility {
        ability = CantBeBlocked()
    }

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Permanent.withSubtype(Subtype.MERFOLK).youControl(),
            binding = TriggerBinding.OTHER,
        )
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
        description = "Whenever another Merfolk you control enters, this creature gets +1/+1 until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "70"
        artist = "Slawomir Maniak"
        flavorText = "No ripples, no splashes, no warning."
        imageUri = "https://cards.scryfall.io/normal/front/2/b/2be32ffc-dc1d-4bb2-926f-51d110392b06.jpg?1783935775"
    }
}

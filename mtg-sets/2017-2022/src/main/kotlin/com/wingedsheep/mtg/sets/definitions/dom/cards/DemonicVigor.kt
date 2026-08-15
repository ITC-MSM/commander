package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Demonic Vigor
 * {B}
 * Enchantment — Aura
 * Enchant creature
 * Enchanted creature gets +1/+1.
 * When enchanted creature dies, return that card to its owner's hand.
 */
val DemonicVigor = card("Demonic Vigor") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\nEnchanted creature gets +1/+1.\nWhen enchanted creature dies, return that card to its owner's hand."

    auraTarget = Targets.Creature

    staticAbility {
        ability = ModifyStats(1, 1)
    }

    triggeredAbility {
        trigger = Triggers.leavesBattlefield(to = Zone.GRAVEYARD, binding = TriggerBinding.ATTACHED)
        effect = Effects.ReturnToHand(EffectTarget.TriggeringEntity)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "85"
        artist = "Zoltan Boros"
        flavorText = "\"In the Cabal, death is just another mark of devotion.\""
        imageUri = "https://cards.scryfall.io/normal/front/0/9/09950456-09d6-4675-9995-0dc540ddb6e4.jpg?1562731072"
    }
}

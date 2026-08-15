package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget


/**
 * Loporrit Scout
 * {2}{G}
 * Creature — Rabbit Scout
 * 3/2
 * Whenever another creature you control enters, this creature gets +1/+1 until end of turn.
 */
val LoporritScout = card("Loporrit Scout") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Rabbit Scout"
    oracleText = "Whenever another creature you control enters, this creature gets +1/+1 until end of turn."
    power = 3
    toughness = 2
    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Creature.youControl(),
            binding = TriggerBinding.OTHER
        )
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
    }
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "192"
        artist = "Andrea Radeck"
        flavorText = "\"I've come to realize that it's important to put down the book every now and then, and experience some adventures for myself.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/1/a182bc66-bfda-4bf5-bd12-3de5dba60945.jpg"
    }
}

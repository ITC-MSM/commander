package com.wingedsheep.mtg.sets.definitions.m14.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Advocate of the Beast
 * {2}{G}
 * Creature — Elf Shaman
 * 2 / 3
 * At the beginning of your end step, put a +1/+1 counter on target Beast creature you control.
 */
val AdvocateOfTheBeast = card("Advocate of the Beast") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Shaman"
    power = 2
    toughness = 3
    oracleText = "At the beginning of your end step, put a +1/+1 counter on target Beast creature you control."

    triggeredAbility {
        trigger = Triggers.YourEndStep
        val beast = target(
            "target Beast creature you control",
            TargetCreature(
                filter = TargetFilter(GameObjectFilter.Creature.withSubtype(Subtype.BEAST).youControl())
            )
        )
        effect = Effects.AddCounters("+1/+1", 1, beast)
        description = "At the beginning of your end step, put a +1/+1 counter on target Beast creature you control."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "164"
        artist = "Jesper Ejsing"
        flavorText = "\"I am neither their drover nor their leader. To these majestic beasts, I am simply their herdmate and nothing more.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/1/b1320400-5aa8-48d6-be84-197b4559456f.jpg"
    }
}

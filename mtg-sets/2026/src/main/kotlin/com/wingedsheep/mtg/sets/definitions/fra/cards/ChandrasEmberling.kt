package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

val ChandrasEmberling = card("Chandra's Emberling") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Gremlin Elemental"
    oracleText = "Haste\nWhenever you cast a noncreature spell, put a +1/+1 counter on this creature."
    power = 2
    toughness = 2

    keywords(Keyword.HASTE)

    triggeredAbility {
        trigger = Triggers.YouCastNoncreature
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "76"
        artist = "Borja Pindado"
        flavorText = "When Chandra needed to summon a chaotic distraction, only one form came to mind."
        imageUri = "https://cards.scryfall.io/normal/front/3/a/3a64c5f4-9cfc-4d19-bd99-13d619b1aa9d.jpg?1789385708"
        inBooster = false
    }
}

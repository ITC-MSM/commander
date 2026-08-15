package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern.SpellCastEvent
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggerSpec
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Lurking Lizards
 * {1}{G}
 * Creature — Lizard Villain, 1/3
 * Trample
 * Whenever you cast a spell with mana value 4 or greater, put a +1/+1 counter on this creature.
 */
val LurkingLizards = card("Lurking Lizards") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Lizard Villain"
    power = 1
    toughness = 3
    oracleText = "Trample\nWhenever you cast a spell with mana value 4 or greater, put a +1/+1 counter on this creature."

    keywords(Keyword.TRAMPLE)

    triggeredAbility {
        trigger = TriggerSpec(
            event = SpellCastEvent(spellFilter = GameObjectFilter.Any.manaValueAtLeast(4), player = Player.You),
            binding = TriggerBinding.ANY
        )
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "107"
        artist = "Rafater"
        flavorText = "\"The ssserum . . . Connorsss . . . what have you done to usss?!\""
        imageUri = "https://cards.scryfall.io/normal/front/5/8/58b5b49c-ddd6-4d1b-9b61-6e02d8fc55ad.jpg?1758215804"
    }
}

package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

val EdgarAncientBloodlord = card("Edgar, Ancient Bloodlord") {
    manaCost = "{W}{B}"
    colorIdentity = "WB"
    typeLine = "Legendary Creature — Vampire Noble"
    power = 2
    toughness = 3
    oracleText = "Whenever another creature or planeswalker you control dies, you gain 1 life.\n" +
        "{2}, Sacrifice another creature or planeswalker: Put a +1/+1 counter on Edgar. He gains menace until end of turn. (He can't be blocked except by two or more creatures.)"

    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.CreatureOrPlaneswalker.youControl(),
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.OTHER
        )
        effect = Effects.GainLife(1)
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{2}"),
            Costs.SacrificeAnother(GameObjectFilter.CreatureOrPlaneswalker)
        )
        effect = Effects.Composite(
            Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self),
            Effects.GrantKeyword(Keyword.MENACE, EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "270"
        artist = "Joshua Raphael"
        flavorText = "\"Our family was born in blood, and through blood it will endure.\""
        imageUri = "https://cards.scryfall.io/normal/front/7/c/7c619fed-2394-4efc-8cdc-6df5f51c1f57.jpg?1789127743"
        inBooster = false
    }
}

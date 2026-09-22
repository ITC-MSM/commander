package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

val JiangYangguNeverAlone = card("Jiang Yanggu, Never Alone") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Legendary Creature — Human Druid"
    oracleText = "When Jiang Yanggu enters, create Mowu, a legendary 3/3 green Dog creature token.\n" +
        "At the beginning of your end step, untap all tokens you control."
    power = 2
    toughness = 2

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 3,
            toughness = 3,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Dog"),
            name = "Mowu",
            legendary = true,
            imageUri = "https://cards.scryfall.io/normal/front/5/8/5868190d-18f6-43b6-af83-9e0ef585687d.jpg?1789735768"
        )
        description = "When Jiang Yanggu enters, create Mowu, a legendary 3/3 green Dog creature token."
    }

    triggeredAbility {
        trigger = Triggers.YourEndStep
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Token.youControl()),
            Effects.Untap(EffectTarget.Self)
        )
        description = "At the beginning of your end step, untap all tokens you control."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "261"
        artist = "Dmitry Burmak"
        flavorText = "\"I traveled with Mowu as a Planeswalker. Now I do so through Omenpaths. Neither of us are complaining about the longer walk.\""
        imageUri = "https://cards.scryfall.io/normal/front/f/5/f5a0bb3e-8119-4739-8684-e61d1d607dcb.jpg?1789014406"
        inBooster = false
    }
}

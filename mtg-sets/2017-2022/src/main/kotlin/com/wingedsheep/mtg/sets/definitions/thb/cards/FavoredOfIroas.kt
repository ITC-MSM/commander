package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Favored of Iroas
 * {2}{W}
 * Creature — Human Soldier
 * 2/2
 * Constellation — Whenever an enchantment you control enters, this creature gains double strike until end of turn. (It deals both first-strike and regular combat damage.)
 *
 * Constellation is an ability word — it carries no rules meaning of its own, so the card is just a
 * triggered ability on "an enchantment you control enters" (Cult Healer's Eerie shape).
 */
val FavoredOfIroas = card("Favored of Iroas") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    power = 2
    toughness = 2
    oracleText = "Constellation — Whenever an enchantment you control enters, this creature gains double strike until end of turn. (It deals both first-strike and regular combat damage.)"

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Enchantment.youControl(),
            binding = TriggerBinding.ANY,
        )
        effect = Effects.GrantKeyword(Keyword.DOUBLE_STRIKE, EffectTarget.Self)
        description = "Constellation — Whenever an enchantment you control enters, this creature gains double strike until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "15"
        artist = "John Severin Brassell"
        flavorText = "\"The promise of victory fills my heart. There is no room for fear.\""
        imageUri = "https://cards.scryfall.io/normal/front/f/0/f09e7aff-d873-4420-b0d5-1c63d686dd73.jpg?1783931598"
        ruling("2020-01-24", "Multiple instances of double strike on the same creature are redundant.")
        ruling(
            "2020-01-24",
            "A constellation ability triggers whenever an enchantment enters the battlefield under your control for any reason. Enchantments with other card types, such as enchantment creatures, will also cause constellation abilities to trigger."
        )
        ruling(
            "2020-01-24",
            "An Aura spell that has an illegal target when it tries to resolve doesn't resolve and is instead put into its owner's graveyard. It doesn't enter the battlefield, so constellation abilities don't trigger."
        )
    }
}

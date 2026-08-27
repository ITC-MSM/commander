package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding

/**
 * Essence Warden
 * {G}
 * Creature — Elf Shaman
 * 1/1
 * Whenever another creature enters, you gain 1 life.
 *
 * "Another creature", with no "you control" — every player's creatures trigger it, so this is
 * [Triggers.entersBattlefield] with a bare Creature filter rather than
 * [Triggers.OtherCreatureEnters], whose filter is scoped to your side.
 */
val EssenceWarden = card("Essence Warden") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Shaman"
    power = 1
    toughness = 1
    oracleText = "Whenever another creature enters, you gain 1 life."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Creature,
            binding = TriggerBinding.OTHER
        )
        effect = Effects.GainLife(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "145"
        artist = "Terese Nielsen"
        flavorText = "\"The more our numbers grow, the more I gain hope that Volrath and his cursed stronghold will one day fall.\"\n—Eladamri, Lord of Leaves"
        imageUri = "https://cards.scryfall.io/normal/front/d/a/da2a65d6-0887-4fe8-a6e6-909208fddd90.jpg"
    }
}

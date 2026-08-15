package com.wingedsheep.mtg.sets.definitions.otj.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Mine Raider
 * {2}{R}
 * Creature — Human Rogue
 * 3/2
 *
 * Trample
 * When this creature enters, if you control another outlaw, create a Treasure token. (Assassins,
 * Mercenaries, Pirates, Rogues, and Warlocks are outlaws.)
 *
 * Intervening-if ETB (CR 603.4): the Treasure trigger fires only if you control an outlaw other
 * than Mine Raider. Mine Raider is itself a Rogue — and therefore an outlaw — and is on the
 * battlefield as the trigger checks, so "you control another outlaw" is exactly "you control two
 * or more outlaws" ([Conditions.YouControlAtLeast] over the [Subtype.OUTLAW_TYPES] creature
 * filter). The condition is re-checked at resolution, so losing your other outlaws first fizzles it.
 */
val MineRaider = card("Mine Raider") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Rogue"
    power = 3
    toughness = 2
    oracleText = "Trample\nWhen this creature enters, if you control another outlaw, create a " +
        "Treasure token. (Assassins, Mercenaries, Pirates, Rogues, and Warlocks are outlaws. A " +
        "Treasure token is an artifact with \"{T}, Sacrifice this token: Add one mana of any color.\")"

    keywords(Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        triggerCondition = Conditions.YouControlAtLeast(
            2,
            GameObjectFilter.Creature.withAnyOfSubtypes(Subtype.OUTLAW_TYPES),
        )
        effect = Effects.CreateTreasure(1)
        description = "When this creature enters, if you control another outlaw, create a Treasure token."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "135"
        artist = "Warren Mahy"
        imageUri = "https://cards.scryfall.io/normal/front/1/9/19cfacff-e884-4954-aa6b-ed56ca942bf2.jpg?1712355800"

        ruling("2024-04-12", "A card, spell, or permanent is an outlaw if it has the Assassin, Mercenary, Pirate, Rogue, or Warlock creature type. It doesn't matter if it has more than one of those creature types; as long as it has at least one, it's an outlaw.")
    }
}

package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding

/**
 * Prowess of the Fair
 * {1}{B}
 * Kindred Enchantment — Elf
 * Whenever another nontoken Elf is put into your graveyard from the battlefield, you may create a
 * 1/1 green Elf Warrior creature token.
 *
 * The filter is deliberately not creature-scoped: the bare tribal noun "Elf" names every permanent
 * with the subtype, and Lorwyn's Kindred cards carry creature types onto noncreature permanents —
 * Prowess of the Fair is itself an Elf, which is what the "another" is guarding against.
 * "Another" is [TriggerBinding.OTHER], not a filter predicate; "your graveyard" is ownership
 * rather than control, since a permanent always goes to its owner's graveyard (CR 400.3).
 */
val ProwessOfTheFair = card("Prowess of the Fair") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Kindred Enchantment — Elf"
    oracleText = "Whenever another nontoken Elf is put into your graveyard from the battlefield, you may create a 1/1 green Elf Warrior creature token."

    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Permanent.withSubtype("Elf").nontoken().ownedByYou(),
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.OTHER,
        )
        optional = true
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Elf", "Warrior"),
            imageUri = "https://cards.scryfall.io/normal/front/2/7/27b171ac-b2ef-4a80-92d1-6d9e71f3e3ca.jpg?1783942838",
        )
        description = "you may create a 1/1 green Elf Warrior creature token."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "136"
        artist = "Jeremy Jarvis"
        flavorText = "An elvish duel is a thing of beauty: the warriors' grace, the crash of steel, then the artful spray of blood."
        imageUri = "https://cards.scryfall.io/normal/front/0/9/099badba-1c8a-4a74-80e9-133f2bafb94d.jpg?1783942884"
    }
}

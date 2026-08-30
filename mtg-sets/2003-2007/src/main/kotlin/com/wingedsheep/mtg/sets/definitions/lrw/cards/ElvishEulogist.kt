package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Elvish Eulogist
 * {G}
 * Creature — Elf Shaman
 * 1/1
 * Sacrifice this creature: You gain 1 life for each Elf card in your graveyard.
 *
 * The sacrifice is a cost, so the Eulogist is already in the graveyard when the ability resolves
 * and counts itself — the filter matches any Elf *card*, not only Elf creatures, so an Elf Kindred
 * spell in the yard counts too. `Count` rather than `AggregateZone` is the canonical spelling off
 * the battlefield.
 */
val ElvishEulogist = card("Elvish Eulogist") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Shaman"
    power = 1
    toughness = 1
    oracleText = "Sacrifice this creature: You gain 1 life for each Elf card in your graveyard."

    activatedAbility {
        cost = Costs.SacrificeSelf
        effect = Effects.GainLife(
            DynamicAmount.Count(
                player = Player.You,
                zone = Zone.GRAVEYARD,
                filter = GameObjectFilter.Any.withSubtype(Subtype.ELF)
            )
        )
        description = "Sacrifice this creature: You gain 1 life for each Elf card in your graveyard."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "205"
        artist = "Ben Thompson"
        flavorText = "\"No matter how adept our artistic skill, our effigies can never hope to capture the vibrant beauty of a living elf. Perhaps that is truly why we mourn.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/6/564b9d31-3079-45e3-acb3-a74169aa332e.jpg?1783942865"
    }
}

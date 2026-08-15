package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CanOnlyBlockCreaturesWith
import com.wingedsheep.sdk.scripting.CantBeBlockedExceptBy
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.effects.AddManaEffect
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

val RealmOfKoh = card("Realm of Koh") {
    typeLine = "Land"
    colorIdentity = "B"
    oracleText = "This land enters tapped unless you control a basic land.\n" +
            "{T}: Add {B}.\n" +
            "{3}{B}, {T}: Create a 1/1 colorless Spirit creature token with " +
            "\"This token can't block or be blocked by non-Spirit creatures.\""

    replacementEffect(
        EntersTapped(
            unlessCondition = Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.BasicLand)
        )
    )

    activatedAbility {
        cost = Costs.Tap
        effect = AddManaEffect(Color.BLACK)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}{B}"), Costs.Tap)
        effect = CreateTokenEffect(
            count = DynamicAmount.Fixed(1),
            power = 1,
            toughness = 1,
            colors = emptySet(),
            creatureTypes = setOf("Spirit"),
            name = "Spirit",
            imageUri = "https://cards.scryfall.io/normal/front/f/5/f59eba51-458a-40e0-b754-999f91d5d839.jpg?1764117653",
            staticAbilities = listOf(
                CantBeBlockedExceptBy(
                    blockerFilter = GameObjectFilter.Creature.withSubtype("Spirit")
                ),
                CanOnlyBlockCreaturesWith(
                    blockerFilter = GameObjectFilter.Creature.withSubtype("Spirit")
                )
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "276"
        artist = "Andreas Rocha"
        flavorText = "Aang delved deep into the spiraling depths in search of wherever Koh called home."
        imageUri = "https://cards.scryfall.io/normal/front/0/6/061ac694-610c-479f-b038-a4ef5270d5d7.jpg?1764122015"
        ruling("2025-10-02", "If one of these lands enters at the same time as any number of basic lands, those other lands are not counted when determining if this land enters tapped or untapped.")
    }
}

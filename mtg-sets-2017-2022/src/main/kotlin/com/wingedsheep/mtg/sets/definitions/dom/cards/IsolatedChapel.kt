package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.effects.AddManaEffect
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Isolated Chapel
 * Land
 * This land enters tapped unless you control a Plains or a Swamp.
 * {T}: Add {W} or {B}.
 */
val IsolatedChapel = card("Isolated Chapel") {
    typeLine = "Land"
    colorIdentity = "WB"
    oracleText = "This land enters tapped unless you control a Plains or a Swamp.\n{T}: Add {W} or {B}."

    replacementEffect(EntersTapped(
        unlessCondition = Conditions.Any(
            Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Land.withSubtype("Plains")),
            Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Land.withSubtype("Swamp"))
        )
    ))

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.WHITE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.BLACK)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "241"
        artist = "Richard Wright"
        flavorText = "Serra's blessing lies strongest upon Sursi, where her holy chapels are untouched even as the Cabal encroaches."
        imageUri = "https://cards.scryfall.io/normal/front/a/1/a1d95d37-5dbe-4a25-bc80-a4db08f3c63a.jpg?1562740520"
    }
}

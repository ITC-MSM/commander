package com.wingedsheep.mtg.sets.definitions.dft.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.effects.AddManaEffect
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Bleachbone Verge
 * Land
 *
 * {T}: Add {B}.
 * {T}: Add {W}. Activate only if you control a Plains or a Swamp.
 */
val BleachboneVerge = card("Bleachbone Verge") {
    typeLine = "Land"
    colorIdentity = "BW"
    oracleText = "{T}: Add {B}.\n{T}: Add {W}. Activate only if you control a Plains or a Swamp."

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.BLACK)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.WHITE)
        manaAbility = true
        timing = TimingRule.ManaAbility
        restrictions = listOf(
            ActivationRestriction.OnlyIfCondition(
                Conditions.Any(
                    Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Land.withSubtype("Plains")),
                    Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Land.withSubtype("Swamp"))
                )
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "250"
        artist = "Mark Tedin"
        flavorText = "\"Despite being older than nearly every living thing here, Loot is still just a kid. That doesn't change what we need from him.\"\n—Jace"
        imageUri = "https://cards.scryfall.io/normal/front/5/2/52dcdabd-a186-45fe-9fee-6c0f1afeaf16.jpg?1773857337"
    }
}

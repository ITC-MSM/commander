package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Tinker's Tote — The Lost Caverns of Ixalan #40
 * {2}{W} · Artifact · Common
 * Artist: Julia Metzger
 *
 * When this artifact enters, create two 1/1 colorless Gnome artifact creature tokens.
 * {W}, Sacrifice this artifact: You gain 3 life.
 *
 * Ability 1 — [Triggers.EntersBattlefield] fires the ETB. [CreateTokenEffect] with
 *   `count = DynamicAmount.Fixed(2)` makes two 1/1 colorless Gnome artifact creature tokens: no
 *   color set (`colors = emptySet()`),
 *   `artifactToken = true`, `creatureTypes = setOf("Gnome")` — the same token minted by Anim Pakal,
 *   Threefold Thunderhulk, and Adaptive Gemguard. Uses the LCI Gnome token art (Scryfall set
 *   `tlci`, id 6def709a).
 *
 * Ability 2 — [Costs.Composite] of [Costs.Mana] "{W}" and [Costs.SacrificeSelf] with
 *   [Effects.GainLife] (3, default [EffectTarget.Controller]).
 */
val TinkersTote = card("Tinker's Tote") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Artifact"
    oracleText = "When this artifact enters, create two 1/1 colorless Gnome artifact creature tokens.\n" +
        "{W}, Sacrifice this artifact: You gain 3 life."

    // When this artifact enters, create two 1/1 colorless Gnome artifact creature tokens.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = CreateTokenEffect(
            count = DynamicAmount.Fixed(2),
            power = 1,
            toughness = 1,
            colors = emptySet(),
            creatureTypes = setOf("Gnome"),
            artifactToken = true,
            imageUri = "https://cards.scryfall.io/normal/front/6/d/6def709a-53b3-4520-9544-74ab6472d256.jpg?1783913604",
        )
    }

    // {W}, Sacrifice this artifact: You gain 3 life.
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{W}"), Costs.SacrificeSelf)
        effect = Effects.GainLife(3)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "40"
        artist = "Julia Metzger"
        flavorText = "Never leave home without a gnome."
        imageUri = "https://cards.scryfall.io/normal/front/8/3/8321857e-7977-46dd-8357-d732312e5261.jpg?1782694579"
    }
}

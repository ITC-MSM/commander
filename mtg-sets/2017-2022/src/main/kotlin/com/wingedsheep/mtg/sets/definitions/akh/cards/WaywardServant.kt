package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Wayward Servant
 * {W}{B}
 * Creature — Zombie
 * 2/2
 * Whenever another Zombie you control enters, each opponent loses 1 life and you gain 1 life.
 *
 * The printed noun is bare "Zombie", which means any Zombie *permanent* you control — not just
 * creatures — so the trigger filter is [GameObjectFilter.Permanent]. "Another" is
 * [TriggerBinding.OTHER].
 */
val WaywardServant = card("Wayward Servant") {
    manaCost = "{W}{B}"
    colorIdentity = "BW"
    typeLine = "Creature — Zombie"
    oracleText = "Whenever another Zombie you control enters, each opponent loses 1 life and you gain 1 life."
    power = 2
    toughness = 2

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Permanent.withSubtype(Subtype.ZOMBIE).youControl(),
            binding = TriggerBinding.OTHER,
        )
        effect = Effects.Composite(
            Effects.LoseLife(1, EffectTarget.PlayerRef(Player.EachOpponent)),
            Effects.GainLife(1),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "208"
        artist = "Anthony Palumbo"
        flavorText = "If one of the anointed fails to serve with perfect obedience, the desert is always ready to receive it."
        imageUri = "https://cards.scryfall.io/normal/front/e/9/e9e00112-8c6c-4551-ad68-389e315fe148.jpg?1783936460"
    }
}

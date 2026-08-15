package com.wingedsheep.mtg.sets.definitions.otj.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Mourner's Surprise
 * {1}{B}
 * Sorcery
 *
 * Return up to one target creature card from your graveyard to your hand. Create a 1/1 red
 * Mercenary creature token with "{T}: Target creature you control gets +1/+0 until end of turn.
 * Activate only as a sorcery."
 */
val MournersSurprise = card("Mourner's Surprise") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Return up to one target creature card from your graveyard to your hand. " +
        "Create a 1/1 red Mercenary creature token with \"{T}: Target creature you control gets " +
        "+1/+0 until end of turn. Activate only as a sorcery.\""

    spell {
        val creatureCard = target(
            "creature card in your graveyard",
            TargetObject(
                optional = true,
                filter = TargetFilter(GameObjectFilter.Creature.ownedByYou(), zone = Zone.GRAVEYARD)
            )
        )

        effect = Effects.Move(creatureCard, Zone.HAND) then CreateTokenEffect(
            power = 1,
            toughness = 1,
            colors = setOf(Color.RED),
            creatureTypes = setOf("Mercenary"),
            activatedAbilities = listOf(
                ActivatedAbility(
                    cost = AbilityCost.Tap,
                    effect = Effects.ModifyStats(1, 0, EffectTarget.ContextTarget(0)),
                    targetRequirements = listOf(Targets.CreatureYouControl),
                    timing = TimingRule.SorcerySpeed
                )
            ),
            imageUri = "https://cards.scryfall.io/normal/front/5/f/5f04607f-eed2-462e-897f-82e41e5f7049.jpg?1712316319"
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "94"
        artist = "Zuzanna Wużyk"
        flavorText = "\"Y'all should've known better than to buy me a coffin!\""
        imageUri = "https://cards.scryfall.io/normal/front/9/8/980b0b68-7218-49c6-b6bf-022218f3abf4.jpg?1712355617"
    }
}

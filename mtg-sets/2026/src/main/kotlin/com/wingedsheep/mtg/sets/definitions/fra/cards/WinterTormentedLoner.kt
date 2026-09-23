package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantDynamicStatsEffect
import com.wingedsheep.sdk.scripting.effects.ReflexiveTriggerEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * The enters trigger is a reflexive one (CR 603.12): the edict only happens if a creature or
 * planeswalker was actually sacrificed, and Winter herself is a legal sacrifice — she then counts
 * toward her own bonus from the graveyard, though she's no longer around to use it.
 */
val WinterTormentedLoner = card("Winter, Tormented Loner") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Creature — Human Warlock"
    power = 0
    toughness = 3
    oracleText = "When Winter enters, you may sacrifice a creature or planeswalker. When you do, each " +
        "opponent sacrifices a creature of their choice.\n" +
        "Winter gets +1/+0 for each creature and planeswalker card in your graveyard."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = ReflexiveTriggerEffect(
            action = Effects.SacrificeOwn(GameObjectFilter.CreatureOrPlaneswalker),
            optional = true,
            reflexiveEffect = Effects.Sacrifice(
                GameObjectFilter.Creature,
                1,
                EffectTarget.PlayerRef(Player.EachOpponent)
            ),
            descriptionOverride = "You may sacrifice a creature or planeswalker. When you do, each " +
                "opponent sacrifices a creature of their choice."
        )
        description = "When Winter enters, you may sacrifice a creature or planeswalker. When you do, " +
            "each opponent sacrifices a creature of their choice."
    }

    staticAbility {
        ability = GrantDynamicStatsEffect(
            filter = GroupFilter.source(),
            powerBonus = DynamicAmounts.zone(
                Player.You,
                Zone.GRAVEYARD,
                GameObjectFilter.CreatureOrPlaneswalker
            ).count(),
            toughnessBonus = DynamicAmount.Fixed(0)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "240"
        artist = "Aurore Folny"
        flavorText = "He had no one left to betray but himself."
        imageUri = "https://cards.scryfall.io/normal/front/9/6/9670f754-f41f-45ac-8e8b-025ad2c0f66b.jpg?1789127724"
        inBooster = false
    }
}

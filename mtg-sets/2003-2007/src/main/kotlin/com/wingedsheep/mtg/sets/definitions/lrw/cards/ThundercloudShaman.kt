package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Thundercloud Shaman
 * {3}{R}{R}
 * Creature — Giant Shaman
 * 4/4
 * When this creature enters, it deals damage equal to the number of Giants you control to each
 * non-Giant creature.
 *
 * Two different readings of "Giant" in one line, and they are not the same set. The *amount* is the
 * bare tribal noun — every Giant permanent you control, the Shaman itself included, so the sweep is
 * at least 1. The *victims* are "each non-Giant creature", which is creatures only and spans every
 * player's battlefield: the Shaman burns its controller's own non-Giants too.
 *
 * The count is read once as the ability resolves, before any damage is dealt, and state-based
 * actions aren't checked mid-resolution — so a Giant dying to this sweep can't shrink it.
 */
val ThundercloudShaman = card("Thundercloud Shaman") {
    manaCost = "{3}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Giant Shaman"
    power = 4
    toughness = 4
    oracleText = "When this creature enters, it deals damage equal to the number of Giants you " +
        "control to each non-Giant creature."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.notSubtype(Subtype.GIANT)),
            DealDamageEffect(
                DynamicAmounts.battlefield(
                    Player.You,
                    GameObjectFilter.Permanent.withSubtype(Subtype.GIANT)
                ).count(),
                EffectTarget.Self,
            )
        )
        description = "it deals damage equal to the number of Giants you control to each non-Giant creature."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "195"
        artist = "Greg Staples"
        flavorText = "He cares not for the disasters his storm brings as long as his path ahead is clear."
        imageUri = "https://cards.scryfall.io/normal/front/5/8/5864f207-2878-461b-8ff6-76475abc324d.jpg?1783942868"
    }
}

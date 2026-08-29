package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Rage-Scarred Berserker
 * {4}{B}
 * Creature — Minotaur Berserker
 * 5/4
 *
 * When this creature enters, target creature you control gets +1/+0 and gains indestructible until end of turn. (Damage and effects that say "destroy" don't destroy it.)
 *
 * One target, two riders on it: [Effects.ModifyStats] then [Effects.GrantKeyword], in the printed
 * order. Both facades default to `Duration.EndOfTurn`, which is exactly the printed "until end of
 * turn", so neither writes a duration explicitly.
 */
val RageScarredBerserker = card("Rage-Scarred Berserker") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Minotaur Berserker"
    power = 5
    toughness = 4
    oracleText = "When this creature enters, target creature you control gets +1/+0 and gains indestructible until end of turn. (Damage and effects that say \"destroy\" don't destroy it.)"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target("target", Targets.CreatureYouControl)
        effect = Effects.Composite(
            Effects.ModifyStats(1, 0, creature),
            Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, creature),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "113"
        artist = "Antonio José Manzanedo"
        flavorText = "The fury of the slaughter god Mogis burns within him."
        imageUri = "https://cards.scryfall.io/normal/front/1/f/1f76d7c4-a5d6-4144-b5f3-e43b96b695b7.jpg"
    }
}

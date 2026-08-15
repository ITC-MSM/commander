package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.AnyTarget
import com.wingedsheep.sdk.scripting.targets.TargetOther
import com.wingedsheep.sdk.scripting.values.ContextPropertyKey
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Screaming Nemesis
 * {2}{R}
 * Creature — Spirit
 * 3/3
 * Haste
 * Whenever this creature is dealt damage, it deals that much damage to any other target.
 * If a player is dealt damage this way, they can't gain life for the rest of the game.
 */
val ScreamingNemesis = card("Screaming Nemesis") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Spirit"
    power = 3
    toughness = 3
    oracleText = "Haste\n" +
        "Whenever this creature is dealt damage, it deals that much damage to any other target. " +
        "If a player is dealt damage this way, they can't gain life for the rest of the game."

    keywords(Keyword.HASTE)

    // Whenever this creature is dealt damage, it deals that much damage to any other target.
    // If a player is dealt damage this way, they can't gain life for the rest of the game.
    triggeredAbility {
        trigger = Triggers.TakesDamage
        val victim = target("any other target", TargetOther(AnyTarget()))
        effect = Effects.Composite(
            listOf(
                Effects.DealDamage(
                    amount = DynamicAmount.ContextProperty(ContextPropertyKey.TRIGGER_DAMAGE_AMOUNT),
                    target = victim,
                ),
                // No-op when the target isn't a player; locks a struck player for the rest of the game.
                Effects.LockLifeGain(target = victim),
            ),
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "157"
        artist = "Liiga Smilshkalne"
        flavorText = "She sees the faces of those who wronged her in everyone she encounters."
        imageUri = "https://cards.scryfall.io/normal/front/c/e/ce35e6fb-ff54-44c4-a216-7ddd37f46882.jpg?1762773071"
    }
}

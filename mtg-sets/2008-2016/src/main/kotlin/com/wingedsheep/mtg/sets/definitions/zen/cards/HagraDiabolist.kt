package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Hagra Diabolist
 * {4}{B}
 * Creature — Ogre Shaman Ally
 * 3/2
 * Whenever this creature or another Ally you control enters, you may have target player lose life equal to the number of Allies you control.
 *
 * Rally is an ability word, not a keyword: the trigger is an ANY-bound enters trigger over
 * Allies you control, so this creature's own arrival fires it alongside every later Ally.
 * The printed "you may" is the builder's `optional = true`, which lowers to a `Gate.MayDecide`
 * around the counter.
 *
 * "The number of Allies you control" is counted on resolution, and counts permanents — an
 * Ally land or artifact counts as readily as an Ally creature.
 */
val HagraDiabolist = card("Hagra Diabolist") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Ogre Shaman Ally"
    power = 3
    toughness = 2
    oracleText = "Whenever this creature or another Ally you control enters, you may have target player lose life equal to the number of Allies you control."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Permanent.withSubtype("Ally").youControl(),
            binding = TriggerBinding.ANY,
        )
        optional = true
        val player = target("player", Targets.Player)
        effect = Effects.LoseLife(
            DynamicAmounts.battlefield(Player.You, GameObjectFilter.Permanent.withSubtype("Ally")).count(),
            player,
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "95"
        artist = "Karl Kopinski"
        flavorText = "\"When exploring the darkest regions, wickedness can be the best accomplice of all.\""
        imageUri = "https://cards.scryfall.io/normal/front/c/3/c303e7e2-cb22-4dea-889f-d03e2494ed0f.jpg"
    }
}

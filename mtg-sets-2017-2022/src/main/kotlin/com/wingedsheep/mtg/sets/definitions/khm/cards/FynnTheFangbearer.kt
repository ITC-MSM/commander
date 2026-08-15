package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.events.DamageType
import com.wingedsheep.sdk.scripting.events.RecipientFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Fynn, the Fangbearer
 * {1}{G}
 * Legendary Creature — Human Warrior
 * 1/3
 * Deathtouch
 * Whenever a creature you control with deathtouch deals combat damage to a player, that
 * player gets two poison counters.
 *
 * The source filter reads deathtouch off projected state, so creatures that *gain*
 * deathtouch (auras, granted keywords, deathtouch counters) count — Fynn itself included.
 */
val FynnTheFangbearer = card("Fynn, the Fangbearer") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Legendary Creature — Human Warrior"
    power = 1
    toughness = 3
    oracleText = "Deathtouch (Any amount of damage this deals to a creature is enough to destroy it.)\n" +
        "Whenever a creature you control with deathtouch deals combat damage to a player, that player " +
        "gets two poison counters. (A player with ten or more poison counters loses the game.)"

    keywords(Keyword.DEATHTOUCH)

    triggeredAbility {
        trigger = Triggers.dealsDamage(
            damageType = DamageType.Combat,
            recipient = RecipientFilter.AnyPlayer,
            sourceFilter = GameObjectFilter.Creature.youControl().withKeyword(Keyword.DEATHTOUCH),
            binding = TriggerBinding.ANY,
        )
        effect = Effects.AddCounters(
            counterType = Counters.POISON,
            count = 2,
            target = EffectTarget.PlayerRef(Player.TriggeringPlayer),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "170"
        artist = "Lie Setiawan"
        flavorText = "\"Come, Koma, and reclaim what you lost! Or does the great serpent fear a rematch?\""
        imageUri = "https://cards.scryfall.io/normal/front/7/d/7d7a8a90-13c1-4b0c-ab2e-fc8d91ccefd9.jpg?1783928214"
        ruling(
            "2021-02-05",
            "Losing the game because a player (preferably an opponent) has ten or more poison counters " +
                "is a rule of the game. Fynn doesn't have to still be on the battlefield when someone " +
                "(preferably an opponent) gets their tenth poison counter."
        )
    }
}

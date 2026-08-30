package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Kruin Striker
 * {1}{R}
 * Creature — Human Warrior
 * 2 / 1
 *
 * Whenever another creature you control enters, this creature gets +1/+0 and gains trample until
 * end of turn.
 *
 * [Triggers.OtherCreatureEnters] carries both halves of "another creature you control" — the
 * `Creature.youControl()` filter and the OTHER binding that excludes the Striker itself. The two
 * riders are one [Effects.Composite] on [EffectTarget.Self]; both default to
 * [com.wingedsheep.sdk.scripting.Duration.EndOfTurn], which is the printed duration.
 */
val KruinStriker = card("Kruin Striker") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Warrior"
    power = 2
    toughness = 1
    oracleText = "Whenever another creature you control enters, this creature gets +1/+0 and gains trample until " +
        "end of turn. (It can deal excess combat damage to the player or planeswalker it's attacking.)"

    triggeredAbility {
        trigger = Triggers.OtherCreatureEnters
        effect = Effects.Composite(
            Effects.ModifyStats(1, 0, EffectTarget.Self),
            Effects.GrantKeyword(Keyword.TRAMPLE, EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "143"
        artist = "Christopher Moeller"
        flavorText = "Unhappy with the creation of the wolfir, Rorica broke with her order and led a crusade against the \"reformed werewolves.\""
        imageUri = "https://cards.scryfall.io/normal/front/7/3/73e72249-84ea-4e9c-9f64-b67b02ffdf3a.jpg?1783940682"
    }
}

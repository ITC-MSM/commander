package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Battlesong Berserker
 * {3}{R}
 * Creature — Human Berserker
 * 3/4
 *
 * Whenever you attack, target creature you control gets +1/+0 and gains menace
 * until end of turn.
 *
 * "Whenever you attack" is the [Triggers.YouAttack] combat trigger (fires once per
 * declare-attackers, not per attacker). The boost and the menace grant are composed
 * and both expire at end of turn.
 */
val BattlesongBerserker = card("Battlesong Berserker") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Berserker"
    power = 3
    toughness = 4
    oracleText = "Whenever you attack, target creature you control gets +1/+0 and gains menace until end of turn. (It can't be blocked except by two or more creatures.)"

    triggeredAbility {
        trigger = Triggers.YouAttack
        val t = target("target creature you control", Targets.CreatureYouControl)
        effect = Effects.Composite(
            Effects.ModifyStats(1, 0, t),
            Effects.GrantKeyword(Keyword.MENACE, t),
        )
        description = "Whenever you attack, target creature you control gets +1/+0 and gains menace until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "78"
        artist = "Mirko Failoni"
        imageUri = "https://cards.scryfall.io/normal/front/a/1/a1f8b199-5d62-485f-b1c3-b30aa550595b.jpg?1782689197"
    }
}

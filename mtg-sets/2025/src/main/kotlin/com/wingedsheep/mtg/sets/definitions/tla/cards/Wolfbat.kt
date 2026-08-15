package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.AddCountersEffect
import com.wingedsheep.sdk.scripting.effects.MayPayManaEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Wolfbat
 * {2}{B}
 * Creature — Wolf Bat
 * 2/2
 *
 * Flying
 * Whenever you draw your second card each turn, you may pay {B}. If you do, return this card
 * from your graveyard to the battlefield with a finality counter on it. (If a creature with a
 * finality counter on it would die, exile it instead.)
 *
 * Implementation notes:
 *  - The recursion ability fires from the graveyard via [triggerZone] = [Zone.GRAVEYARD], the same
 *    shape as Invasion's Pyre Zombie.
 *  - "your second card each turn" is [Triggers.NthCardDrawn] (n = 2, Player.You).
 *  - "you may pay {B}. If you do, ..." is a [MayPayManaEffect]; the payoff returns this card from
 *    the graveyard to the battlefield ([EffectTarget.Self]) and stamps a finality counter on it
 *    via [AddCountersEffect] / [Counters.FINALITY] (death replacement handled engine-side).
 */
val Wolfbat = card("Wolfbat") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Wolf Bat"
    power = 2
    toughness = 2
    oracleText = "Flying\n" +
        "Whenever you draw your second card each turn, you may pay {B}. If you do, return this card " +
        "from your graveyard to the battlefield with a finality counter on it. (If a creature with a " +
        "finality counter on it would die, exile it instead.)"

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.NthCardDrawn(2)
        triggerZone = Zone.GRAVEYARD
        effect = MayPayManaEffect(
            cost = ManaCost.parse("{B}"),
            effect = Effects.Composite(
                Effects.Move(EffectTarget.Self, Zone.BATTLEFIELD, fromZone = Zone.GRAVEYARD),
                AddCountersEffect(counterType = Counters.FINALITY, count = 1, target = EffectTarget.Self),
            ),
        )
        description = "Whenever you draw your second card each turn, you may pay {B}. If you do, " +
            "return this card from your graveyard to the battlefield with a finality counter on it."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "122"
        artist = "Daniel Romanovsky"
        flavorText = "Wolfbats nest in tunnels carved out by badgermoles and hunt for prey in the dark caverns."
        imageUri = "https://cards.scryfall.io/normal/front/2/a/2ae83b4e-e5a4-4c98-8d16-eef3c71b8ff2.jpg?1764120843"
    }
}

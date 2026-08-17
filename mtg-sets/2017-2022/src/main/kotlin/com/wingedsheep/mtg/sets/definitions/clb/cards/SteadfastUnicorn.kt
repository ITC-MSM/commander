package com.wingedsheep.mtg.sets.definitions.clb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Steadfast Unicorn
 * {W}
 * Creature — Unicorn
 * 1/2
 * {3}{W}: Creatures you control get +1/+1 and gain vigilance until end of turn. Activate only during your turn. (Attacking doesn't cause them to tap.)
 *
 * A team pump written as an [Effects.ForEachInGroup] over the creatures you control, whose body pairs
 * [Effects.ModifyStats] with [Effects.GrantKeyword] on each iterated permanent. "Activate only during
 * your turn" is [ActivationRestriction.OnlyDuringYourTurn] — a restriction on *whose* turn, not the
 * sorcery-speed timing rule, which would also forbid activating in your own combat.
 */
val SteadfastUnicorn = card("Steadfast Unicorn") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Unicorn"
    power = 1
    toughness = 2
    oracleText = "{3}{W}: Creatures you control get +1/+1 and gain vigilance until end of turn. Activate only during your turn. (Attacking doesn't cause them to tap.)"

    activatedAbility {
        cost = Costs.Mana("{3}{W}")
        restrictions = listOf(ActivationRestriction.OnlyDuringYourTurn)
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.youControl()),
            Effects.Composite(
                Effects.ModifyStats(1, 1, EffectTarget.Self),
                Effects.GrantKeyword(Keyword.VIGILANCE, EffectTarget.Self)
            )
        )
        description = "{3}{W}: Creatures you control get +1/+1 and gain vigilance until end of turn. Activate only during your turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "44"
        artist = "John Thacker"
        flavorText = "Only the good-hearted may set foot in a unicorn's domain."
        imageUri = "https://cards.scryfall.io/normal/front/1/3/13762890-6c5e-44e4-9bd8-998bd054db20.jpg?1783922804"
    }
}

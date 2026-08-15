package com.wingedsheep.mtg.sets.definitions.woe.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlockedBy
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Verdant Outrider
 * {2}{G}
 * Creature — Human Knight
 * 4/2
 *
 * {1}{G}: This creature can't be blocked by creatures with power 2 or less this turn.
 *
 * A durational blocking restriction granted to the Outrider itself, so it's the
 * [Effects.GrantStaticAbility] wrapper around a printed-style [CantBeBlockedBy] rather than a new
 * effect type. Power is read from projected state when blockers are declared, so a blocker pumped
 * above 2 after the fact can still block — and, per the 2023-09-01 ruling, activating this *after*
 * blockers are declared doesn't retroactively unblock the Outrider. Both fall out of the
 * restriction being consulted only at declare-blockers time.
 */
val VerdantOutrider = card("Verdant Outrider") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Knight"
    power = 4
    toughness = 2
    oracleText = "{1}{G}: This creature can't be blocked by creatures with power 2 or less this turn."

    activatedAbility {
        cost = Costs.Mana("{1}{G}")
        effect = Effects.GrantStaticAbility(
            ability = CantBeBlockedBy(GameObjectFilter.Creature.powerAtMost(2)),
            target = EffectTarget.Self
        )
        description = "{1}{G}: This creature can't be blocked by creatures with power 2 or less this turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "196"
        artist = "Taras Susak"
        flavorText = "Some knights who survived the invasion have forsaken what remains of their " +
            "courts. The newly formed Verdant Order has sworn to defend the last untouched parts " +
            "of the wilds."
        imageUri = "https://cards.scryfall.io/normal/front/c/3/c34830e4-823a-40fa-ba41-bb2afbf1e499.jpg?1783915074"

        ruling(
            "2023-09-01",
            "Activating Verdant Outrider's ability after it has become blocked by a creature with " +
                "power 2 or less won't cause it to become unblocked."
        )
    }
}

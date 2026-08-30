package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.targets.TargetOther

/**
 * Drooling Groodion
 * {3}{B}{B}{G}
 * Creature — Beast
 * 4/3
 *
 * {2}{B}{G}, Sacrifice a creature: Target creature gets +2/+2 until end of turn. Another target
 * creature gets -2/-2 until end of turn.
 *
 * Two independent target requirements, the second wrapped in [TargetOther] so it cannot be the same
 * creature as the first (CR 601.2c "another"). The sacrifice is a *cost*, paid on activation, so the
 * Groodion may eat itself — and the creature sacrificed can be one of the two targets, which will
 * simply have left the battlefield by the time the ability resolves.
 */
val DroolingGroodion = card("Drooling Groodion") {
    manaCost = "{3}{B}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Creature — Beast"
    power = 4
    toughness = 3
    oracleText = "{2}{B}{G}, Sacrifice a creature: Target creature gets +2/+2 until end of turn. " +
        "Another target creature gets -2/-2 until end of turn."

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{2}{B}{G}"),
            Costs.Sacrifice(GameObjectFilter.Creature)
        )
        val pumped = target("target creature", TargetCreature())
        val weakened = target("another target creature", TargetOther(TargetCreature()))
        effect = Effects.Composite(
            listOf(
                Effects.ModifyStats(2, 2, pumped),
                Effects.ModifyStats(-2, -2, weakened)
            )
        )
        description = "Target creature gets +2/+2 until end of turn. Another target creature gets " +
            "-2/-2 until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "204"
        artist = "Kev Walker"
        flavorText = "\"The Golgari expand, yes, but I refuse to call their tainted creations " +
            "'growth.'\"\n—Veszka, Selesnya evangel"
        imageUri = "https://cards.scryfall.io/normal/front/d/e/de33c222-0d74-4eb5-8794-39f3601eb8f4.jpg?1783943621"
    }
}

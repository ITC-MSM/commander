package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Hunted Troll — Ravnica: City of Guilds #170
 * {2}{G}{G} · Creature — Troll Warrior · 8/4
 *
 * When this creature enters, target opponent creates four 1/1 blue Faerie creature tokens with
 * flying.
 * {G}: Regenerate this creature.
 *
 * The green member of the Hunted cycle. The four flying Faeries are the drawback the regeneration
 * is meant to survive: an 8/4 dies to four 1/1 fliers chump-blocking into it exactly never, but it
 * folds to the removal those Faeries buy time for, and {G} answers that.
 *
 * There is no `Effects.Regenerate` facade — [RegenerateEffect] on [EffectTarget.Self] is the
 * shipped spelling (Cudgel Troll, Woodwraith Strangler). The Faeries enter under the *targeted
 * opponent's* control via [Effects.CreateToken]'s `controller`, as with the rest of the cycle.
 */
val HuntedTroll = card("Hunted Troll") {
    manaCost = "{2}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Troll Warrior"
    oracleText = "When this creature enters, target opponent creates four 1/1 blue Faerie " +
        "creature tokens with flying.\n" +
        "{G}: Regenerate this creature."
    power = 8
    toughness = 4

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val opponent = target("target opponent", Targets.Opponent)
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.BLUE),
            creatureTypes = setOf("Faerie"),
            keywords = setOf(Keyword.FLYING),
            count = 4,
            controller = opponent,
        )
        description = "When this creature enters, target opponent creates four 1/1 blue Faerie " +
            "creature tokens with flying."
    }

    activatedAbility {
        cost = Costs.Mana("{G}")
        effect = RegenerateEffect(EffectTarget.Self)
        description = "{G}: Regenerate this creature."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "170"
        artist = "Greg Staples"
        imageUri = "https://cards.scryfall.io/normal/front/f/d/fd9d891e-ae8c-485a-b999-d1e08fffd164.jpg?1783943635"
    }
}

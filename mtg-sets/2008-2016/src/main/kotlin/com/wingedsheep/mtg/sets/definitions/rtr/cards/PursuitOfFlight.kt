package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Pursuit of Flight
 * {1}{R}
 * Enchantment — Aura
 *
 * Enchant creature
 * Enchanted creature gets +2/+2 and has "{U}: This creature gains flying until end of turn."
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * Deviant Glee's shape in the other guild's colours — see that card for why the granted ability's
 * [EffectTarget.Self] is the enchanted creature and not the Aura.
 */
val PursuitOfFlight = card("Pursuit of Flight") {
    manaCost = "{1}{R}"
    colorIdentity = "RU"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature gets +2/+2 and has \"{U}: This creature gains flying until end of turn.\""

    auraTarget = Targets.Creature

    staticAbility {
        ability = ModifyStats(2, 2)
    }

    staticAbility {
        ability = GrantActivatedAbility(
            ability = ActivatedAbility(
                id = AbilityId.generate(),
                cost = Costs.Mana("{U}"),
                effect = Effects.GrantKeyword(Keyword.FLYING, EffectTarget.Self),
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "102"
        artist = "Christopher Moeller"
        flavorText = "\"Watch the voltage. We don't need another charred, crashing viashino.\"\n" +
            "—Bori Andon, Izzet blastseeker"
        imageUri = "https://cards.scryfall.io/normal/front/3/7/37a6290c-a0a8-4032-972b-84a7eef04dae.jpg?1783940354"
    }
}

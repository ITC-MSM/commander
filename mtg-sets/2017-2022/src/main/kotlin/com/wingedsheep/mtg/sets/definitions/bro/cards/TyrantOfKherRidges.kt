package com.wingedsheep.mtg.sets.definitions.bro.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Tyrant of Kher Ridges
 * {4}{R}{R}
 * Creature — Dragon
 * 4/5
 * Flying
 * When this creature enters, it deals 4 damage to any target.
 * {R}: This creature gets +1/+0 until end of turn.
 *
 * "It deals" is the trigger's own source, which is [Effects.DealDamage]'s default — no explicit
 * `damageSource` needed.
 */
val TyrantOfKherRidges = card("Tyrant of Kher Ridges") {
    manaCost = "{4}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Dragon"
    power = 4
    toughness = 5
    oracleText = "Flying\n" +
        "When this creature enters, it deals 4 damage to any target.\n" +
        "{R}: This creature gets +1/+0 until end of turn."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val anyTarget = target("any target", Targets.Any)
        effect = Effects.DealDamage(4, anyTarget)
    }

    activatedAbility {
        cost = Costs.Mana("{R}")
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "154"
        artist = "Karl Kopinski"
        flavorText = "In war, dragons don't see sides, just side dishes."
        imageUri = "https://cards.scryfall.io/normal/front/7/2/72a271b1-b757-4373-8e6d-e9698ae96bed.jpg?1783920058"
    }
}

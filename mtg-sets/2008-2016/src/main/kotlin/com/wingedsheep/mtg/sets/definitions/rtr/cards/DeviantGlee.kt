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
 * Deviant Glee
 * {B}
 * Enchantment — Aura
 *
 * Enchant creature
 * Enchanted creature gets +2/+1 and has "{R}: This creature gains trample until end of turn."
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * Trollhide's shape: the stat bonus is a static [ModifyStats] on the attached creature, and the
 * quoted ability is a [GrantActivatedAbility] whose [EffectTarget.Self] resolves to the host
 * (CR 113.7), so "this creature" inside the quotes means the enchanted creature and not the Aura.
 */
val DeviantGlee = card("Deviant Glee") {
    manaCost = "{B}"
    colorIdentity = "BR"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature gets +2/+1 and has \"{R}: This creature gains trample until end of turn.\""

    auraTarget = Targets.Creature

    staticAbility {
        ability = ModifyStats(2, 1)
    }

    staticAbility {
        ability = GrantActivatedAbility(
            ability = ActivatedAbility(
                id = AbilityId.generate(),
                cost = Costs.Mana("{R}"),
                effect = Effects.GrantKeyword(Keyword.TRAMPLE, EffectTarget.Self),
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "65"
        artist = "Michael C. Hayes"
        flavorText = "\"You just need the right incentive to fulfill my dreams.\""
        imageUri = "https://cards.scryfall.io/normal/front/e/1/e150896e-8745-42ac-894b-8f42a92bd7a7.jpg?1783940363"
    }
}

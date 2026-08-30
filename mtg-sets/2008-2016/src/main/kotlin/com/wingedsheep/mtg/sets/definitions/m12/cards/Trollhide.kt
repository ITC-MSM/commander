package com.wingedsheep.mtg.sets.definitions.m12.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Trollhide
 * {2}{G}
 * Enchantment — Aura
 * Enchant creature
 * Enchanted creature gets +2 / +2 and has "{1}{G}: Regenerate this creature." (The next time the
 * creature would be destroyed this turn, instead tap it, remove it from combat, and heal all damage
 * on it.)
 *
 * Canonical printing: Magic 2012, the card's earliest printing. Reprinted in M14 as a `Printing`
 * row.
 *
 * The +2 / +2 is a static [ModifyStats] on the attached creature; the quoted ability is a
 * [GrantActivatedAbility] whose [EffectTarget.Self] resolves to the host creature (CR 113.7), so
 * "{1}{G}: Regenerate this creature" regenerates the enchanted creature (Lunarch Mantle). There is
 * no `Effects.Regenerate` facade — [RegenerateEffect] is the shipped spelling (Cudgel Troll).
 */
val Trollhide = card("Trollhide") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
            "Enchanted creature gets +2/+2 and has \"{1}{G}: Regenerate this creature.\" (The next time the creature would be destroyed this turn, instead tap it, remove it from combat, and heal all damage on it.)"

    auraTarget = Targets.Creature

    staticAbility {
        ability = ModifyStats(2, 2)
    }

    staticAbility {
        ability = GrantActivatedAbility(
            ability = ActivatedAbility(
                id = AbilityId.generate(),
                cost = Costs.Mana("{1}{G}"),
                effect = RegenerateEffect(EffectTarget.Self)
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "199"
        artist = "Steven Belledin"
        imageUri = "https://cards.scryfall.io/normal/front/3/2/32c8d6ed-4764-433b-9617-363e46e5b250.jpg"
    }
}

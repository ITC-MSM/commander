package com.wingedsheep.mtg.sets.definitions.emn.cards

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
 * Lunarch Mantle
 * {1}{W}
 * Enchantment — Aura
 * Enchant creature
 * Enchanted creature gets +2/+2 and has "{1}, Sacrifice a permanent: This creature gains flying
 * until end of turn."
 *
 * The +2/+2 is a static [ModifyStats] on the attached creature. The granted activated ability is a
 * [GrantActivatedAbility] static whose [EffectTarget.Self] resolves to the host creature (CR 113.7),
 * so "{1}, Sacrifice a permanent" grants flying until end of turn to the enchanted creature itself.
 */
val LunarchMantle = card("Lunarch Mantle") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature gets +2/+2 and has \"{1}, Sacrifice a permanent: This creature gains " +
        "flying until end of turn.\""

    auraTarget = Targets.Creature

    staticAbility {
        ability = ModifyStats(2, 2)
    }

    staticAbility {
        ability = GrantActivatedAbility(
            ability = ActivatedAbility(
                id = AbilityId.generate(),
                cost = Costs.Composite(Costs.Mana("{1}"), Costs.Sacrifice()),
                effect = Effects.GrantKeyword(Keyword.FLYING, EffectTarget.Self)
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "35"
        artist = "Anastasia Ovchinnikova"
        flavorText = "\"A boon from the angels should never be cast aside.\"\n—Manfried Ulmach, Chief Inquisitor"
        imageUri = "https://cards.scryfall.io/normal/front/3/6/360f4759-c178-4ac2-b561-c81e13f67740.jpg?1782711925"
    }
}

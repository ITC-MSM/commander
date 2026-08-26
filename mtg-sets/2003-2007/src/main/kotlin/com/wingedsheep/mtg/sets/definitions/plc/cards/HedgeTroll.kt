package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Hedge Troll
 * {2}{G}
 * Creature — Troll Cleric
 * 2/2
 * This creature gets +1/+1 as long as you control a Plains.
 * {W}: Regenerate this creature.
 *
 * "A Plains" is a *land* with the subtype, not a permanent with it — a creature that has been
 * turned into a Plains land type qualifies, an enchantment named Plains would not.
 */
val HedgeTroll = card("Hedge Troll") {
    manaCost = "{2}{G}"
    colorIdentity = "GW"
    typeLine = "Creature — Troll Cleric"
    power = 2
    toughness = 2
    oracleText = "This creature gets +1/+1 as long as you control a Plains.\n" +
        "{W}: Regenerate this creature."

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(
                powerBonus = 1,
                toughnessBonus = 1,
                filter = GroupFilter.source()
            ),
            condition = Exists(
                Player.You,
                Zone.BATTLEFIELD,
                GameObjectFilter.Land.withSubtype(Subtype.PLAINS)
            )
        )
    }

    activatedAbility {
        cost = Costs.Mana("{W}")
        effect = RegenerateEffect(EffectTarget.Self)
        description = "{W}: Regenerate this creature."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "151"
        artist = "Paolo Parente"
        flavorText = "His abode was clean and bare, not a morsel in sight. I asked him what he ate, fearing the answer. He smiled and said his faith alone sustained him."
        imageUri = "https://cards.scryfall.io/normal/front/2/b/2b2e6027-598d-4ba0-93ae-f76d031de8af.jpg"
    }
}

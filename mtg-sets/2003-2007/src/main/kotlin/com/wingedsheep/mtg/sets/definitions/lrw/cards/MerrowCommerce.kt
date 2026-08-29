package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Merrow Commerce
 * {1}{U}
 * Kindred Enchantment — Merfolk
 * At the beginning of your end step, untap all Merfolk you control.
 *
 * A group untap, not a targeted one: [Effects.ForEachInGroup] rebinds [EffectTarget.Self] to each
 * iterated permanent, so nothing is targeted. The group is *Merfolk permanents*, not Merfolk
 * creatures — Merrow Commerce is itself a Merfolk (a Kindred enchantment), and so is any other
 * non-creature Merfolk permanent on the battlefield.
 */
val MerrowCommerce = card("Merrow Commerce") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Kindred Enchantment — Merfolk"
    oracleText = "At the beginning of your end step, untap all Merfolk you control."

    triggeredAbility {
        trigger = Triggers.YourEndStep
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Permanent.withSubtype(Subtype.MERFOLK)).youControl(),
            Effects.Untap(EffectTarget.Self)
        )
        description = "At the beginning of your end step, untap all Merfolk you control."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "72"
        artist = "Steve Ellis"
        flavorText = "Schools meet and mingle on Lorwyn's riverways. In the bustling interplay, the merrow renew their sense of community as they sharpen their wits and hone their trading skills."
        imageUri = "https://cards.scryfall.io/normal/front/3/a/3ad0c3b4-fd24-44ad-8b30-4176f07be3d6.jpg?1783942900"
    }
}

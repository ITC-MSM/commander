package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBlock
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Olivia's Bloodsworn (Shadows over Innistrad #127)
 * {1}{B}
 * Creature — Vampire Soldier
 * 2 / 1
 *
 * Flying
 * This creature can't block.
 * {R}: Target Vampire gains haste until end of turn.
 *
 * "Target Vampire" is a bare tribal noun, so it is any Vampire *permanent* — not just a Vampire
 * creature — hence [TargetFilter.Permanent] narrowed by the subtype rather than
 * [TargetFilter.Creature].
 */
val OliviasBloodsworn = card("Olivia's Bloodsworn") {
    manaCost = "{1}{B}"
    colorIdentity = "BR"
    typeLine = "Creature — Vampire Soldier"
    power = 2
    toughness = 1
    oracleText = "Flying\n" +
        "This creature can't block.\n" +
        "{R}: Target Vampire gains haste until end of turn."

    keywords(Keyword.FLYING)

    staticAbility {
        ability = CantBlock()
    }

    activatedAbility {
        cost = Costs.Mana("{R}")
        val t = target("target", TargetPermanent(filter = TargetFilter.Permanent.withSubtype(Subtype.VAMPIRE)))
        effect = Effects.GrantKeyword(Keyword.HASTE, t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "127"
        artist = "Daarken"
        flavorText = "\"As we muster for war, I have little patience for lethargy or recalcitrance.\""
        imageUri = "https://cards.scryfall.io/normal/front/f/d/fdda34e9-f28b-4606-8298-b2d0c15033e6.jpg?1783937768"
    }
}

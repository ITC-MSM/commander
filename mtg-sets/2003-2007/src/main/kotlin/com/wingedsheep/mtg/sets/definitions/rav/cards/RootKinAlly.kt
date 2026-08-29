package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Root-Kin Ally
 * {4}{G}{G}
 * Creature — Elemental Warrior
 * 3/3
 * Convoke (Your creatures can help cast this spell. Each creature you tap while casting this spell pays for {1} or one mana of that creature's color.)
 * Tap two untapped creatures you control: This creature gets +2/+2 until end of turn.
 *
 * [Costs.TapPermanents] already means "untapped ... you control", so the filter is the bare
 * [GameObjectFilter.Creature]. Root-Kin Ally is a legal choice for its own cost — nothing in the
 * printed line excludes it — so `excludeSelf` stays at its default.
 */
val RootKinAlly = card("Root-Kin Ally") {
    manaCost = "{4}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elemental Warrior"
    oracleText = "Convoke (Your creatures can help cast this spell. Each creature you tap while casting this spell pays for {1} or one mana of that creature's color.)\n" +
        "Tap two untapped creatures you control: This creature gets +2/+2 until end of turn."
    power = 3
    toughness = 3

    keywords(Keyword.CONVOKE)

    activatedAbility {
        cost = Costs.TapPermanents(count = 2, filter = GameObjectFilter.Creature)
        effect = Effects.ModifyStats(2, 2, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "180"
        artist = "Arnie Swekel"
        imageUri = "https://cards.scryfall.io/normal/front/e/4/e469162b-c8c9-4746-80f3-d2c2acd89e0f.jpg"
    }
}

package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Viscerid Deepwalker
 * {4}{U}
 * Creature — Homarid Warrior
 * 2 / 3
 * {U}: This creature gets +1/+0 until end of turn.
 * Suspend 4—{U} (Rather than cast this card from your hand, you may pay {U} and exile it with four time counters on it. At the beginning of your upkeep, remove a time counter. When the last is removed, you may cast it without paying its mana cost. It has haste.)
 *
 * A firebreathing-shaped pump on itself: an untargeted [Effects.ModifyStats] on
 * [EffectTarget.Self] for the default end-of-turn duration.
 */
val VisceridDeepwalker = card("Viscerid Deepwalker") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Homarid Warrior"
    power = 2
    toughness = 3
    oracleText = "{U}: This creature gets +1/+0 until end of turn.\n" +
        "Suspend 4—{U} (Rather than cast this card from your hand, you may pay {U} and exile it with four time counters on it. At the beginning of your upkeep, remove a time counter. When the last is removed, you may cast it without paying its mana cost. It has haste.)"

    activatedAbility {
        cost = Costs.Mana("{U}")
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
    }

    keywordAbility(KeywordAbility.suspend("{U}", 4))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "91"
        artist = "Heather Hudson"
        imageUri = "https://cards.scryfall.io/normal/front/c/d/cdc9f54e-7fba-4f03-83ca-6d293dffc07a.jpg"
    }
}

package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.values.ContextPropertyKey
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Manaform Hellkite — Innistrad: Crimson Vow #170
 * {2}{R}{R} · Creature — Dragon · Mythic · 4/4
 * Artist: Andrew Mar
 *
 * Flying
 * Whenever you cast a noncreature spell, create an X/X red Dragon Illusion creature token with
 * flying and haste, where X is the amount of mana spent to cast that spell. Exile that token at
 * the beginning of the next end step.
 *
 * X is [ContextPropertyKey.MANA_SPENT_ON_TRIGGERING_SPELL] — the mana *actually paid* for the
 * triggering spell, read off `SpellCastEvent.totalManaSpent`, not that spell's printed mana value.
 * A spell cast without paying its mana cost makes a 0/0 token that dies immediately (Scryfall
 * ruling, below), which is why the amount must be the spend and not [DynamicAmount.TotalManaSpent]
 * (the *resolving* object's own cast — here, the Hellkite's).
 *
 * The end-step exile is [CreateTokenEffect.exileAtStep], the same delayed-cleanup rider Valduk,
 * Keeper of the Flame uses; it fires at the beginning of the next end step whether or not the
 * Hellkite is still on the battlefield.
 */
val ManaformHellkite = card("Manaform Hellkite") {
    manaCost = "{2}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Dragon"
    power = 4
    toughness = 4
    oracleText = "Flying\n" +
        "Whenever you cast a noncreature spell, create an X/X red Dragon Illusion creature token " +
        "with flying and haste, where X is the amount of mana spent to cast that spell. Exile " +
        "that token at the beginning of the next end step."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.YouCastNoncreature
        effect = CreateTokenEffect(
            count = DynamicAmount.Fixed(1),
            power = 0,
            toughness = 0,
            colors = setOf(Color.RED),
            creatureTypes = setOf("Dragon", "Illusion"),
            keywords = setOf(Keyword.FLYING, Keyword.HASTE),
            dynamicPower = DynamicAmount.ContextProperty(ContextPropertyKey.MANA_SPENT_ON_TRIGGERING_SPELL),
            dynamicToughness = DynamicAmount.ContextProperty(ContextPropertyKey.MANA_SPENT_ON_TRIGGERING_SPELL),
            exileAtStep = Step.END,
            imageUri = "https://cards.scryfall.io/normal/front/e/0/e04ca01e-d2ff-45ce-bf6f-9f756808c8fb.jpg?1783924696"
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "170"
        artist = "Andrew Mar"
        imageUri = "https://cards.scryfall.io/normal/front/5/2/52e59170-aa80-45e1-ad0d-ce4818e78d2a.jpg?1783924829"
        ruling(
            "2021-11-19",
            "Manaform Hellkite's triggered ability looks at the mana that was actually spent to " +
                "cast the spell, which may be different than the mana cost of that spell. For " +
                "example, if you cast a noncreature spell without paying its mana cost, this " +
                "ability creates a 0/0 Dragon Illusion token. In most cases, such a creature " +
                "would immediately be put into your graveyard."
        )
    }
}

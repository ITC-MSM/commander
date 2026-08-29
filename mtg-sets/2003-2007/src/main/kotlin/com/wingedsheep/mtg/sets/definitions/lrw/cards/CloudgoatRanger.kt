package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Cloudgoat Ranger
 * {3}{W}{W}
 * Creature — Giant Warrior Ranger
 * 3/3
 * When this creature enters, create three 1/1 white Kithkin Soldier creature tokens.
 * Tap three untapped Kithkin you control: This creature gets +2/+0 and gains flying until end of turn.
 *
 * The pump's cost is [Costs.TapPermanents], not `{T}`: "untapped" and "you control" are carried by
 * the atom, and because it isn't the tap symbol, summoning-sick Kithkin (the freshly made tokens
 * included) can pay it (CR 302.6).
 */
val CloudgoatRanger = card("Cloudgoat Ranger") {
    manaCost = "{3}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Giant Warrior Ranger"
    power = 3
    toughness = 3
    oracleText = "When this creature enters, create three 1/1 white Kithkin Soldier creature tokens.\n" +
        "Tap three untapped Kithkin you control: This creature gets +2/+0 and gains flying until end of turn."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Kithkin", "Soldier"),
            count = 3,
            imageUri = "https://cards.scryfall.io/normal/front/a/d/ad29eb21-7ee3-4a67-9601-a62ea0cbe4c0.jpg?1783942839",
        )
        description = "create three 1/1 white Kithkin Soldier creature tokens."
    }

    activatedAbility {
        cost = Costs.TapPermanents(
            count = 3,
            filter = GameObjectFilter.Permanent.withSubtype(Subtype.KITHKIN)
        )
        effect = Effects.Composite(
            Effects.ModifyStats(2, 0, EffectTarget.Self),
            Effects.GrantKeyword(Keyword.FLYING, EffectTarget.Self)
        )
        description = "Tap three untapped Kithkin you control: This creature gets +2/+0 and gains flying until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "10"
        artist = "Adam Rex"
        imageUri = "https://cards.scryfall.io/normal/front/9/e/9e344859-7248-4498-8303-252f196a007b.jpg?1783942916"
    }
}

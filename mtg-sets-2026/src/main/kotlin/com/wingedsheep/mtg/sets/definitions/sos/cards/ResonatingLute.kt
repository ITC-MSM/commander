package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.ManaRestriction
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Resonating Lute
 * {2}{U}{R}
 * Artifact
 * Lands you control have "{T}: Add two mana of any one color. Spend this mana only to cast instant
 * and sorcery spells."
 * {T}: Draw a card. Activate only if you have seven or more cards in your hand.
 *
 * The static ability grants every land you control a [ManaRestriction.InstantOrSorceryOnly] mana
 * ability that adds two mana of one chosen colour (Add **any one** colour — [Effects.AddAnyColorMana]
 * with `amount = 2`, not a free combination). The card's own `{T}: Draw a card` is gated by
 * [ActivationRestriction.OnlyIfCondition] on [Conditions.CardsInHandAtLeast] (7) — a "loot when
 * flooded" payoff that turns excess cards into more draws.
 */
val ResonatingLute = card("Resonating Lute") {
    manaCost = "{2}{U}{R}"
    colorIdentity = "UR"
    typeLine = "Artifact"
    oracleText = "Lands you control have \"{T}: Add two mana of any one color. Spend this mana only " +
        "to cast instant and sorcery spells.\"\n" +
        "{T}: Draw a card. Activate only if you have seven or more cards in your hand."

    staticAbility {
        ability = GrantActivatedAbility(
            ability = ActivatedAbility(
                id = AbilityId.generate(),
                cost = Costs.Tap,
                effect = Effects.AddAnyColorMana(
                    amount = 2,
                    restriction = ManaRestriction.InstantOrSorceryOnly,
                ),
                isManaAbility = true,
                timing = TimingRule.ManaAbility,
            ),
            filter = GroupFilter(GameObjectFilter.Land.youControl()),
        )
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.DrawCards(1)
        restrictions = listOf(
            ActivationRestriction.OnlyIfCondition(Conditions.CardsInHandAtLeast(7)),
        )
        description = "{T}: Draw a card. Activate only if you have seven or more cards in your hand."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "221"
        artist = "Edgar Sánchez Hidalgo"
        flavorText = "School styluses are just tools. Passion is a Prismari's true instrument."
        imageUri = "https://cards.scryfall.io/normal/front/6/e/6ef168d6-28f2-4c24-9bfa-82c35663b729.jpg?1775938538"
    }
}

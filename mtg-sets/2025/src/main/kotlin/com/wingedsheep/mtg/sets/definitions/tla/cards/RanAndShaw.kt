package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.firebending
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ModifyStatsEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Ran and Shaw
 * {3}{R}{R}
 * Legendary Creature — Dragon
 * 4/4
 *
 * Flying, firebending 2
 * When Ran and Shaw enter, if you cast them and there are three or more Dragon and/or Lesson cards
 *   in your graveyard, create a token that's a copy of Ran and Shaw, except it's not legendary.
 * {3}{R}: Dragons you control get +2/+0 until end of turn.
 *
 * The ETB is an intervening-if (CR 603.4): `triggerCondition` gates on both "if you cast them"
 * ([Conditions.WasCast]) and the graveyard count, checked when the ability would trigger and again
 * on resolution. The copy uses the new `removeLegendary` clause on [Effects.CreateTokenCopyOfSelf]
 * ("except it's not legendary"). "Dragon and/or Lesson cards" is a single subtype-OR filter, so a
 * card that is both is still counted once.
 */
val RanAndShaw = card("Ran and Shaw") {
    manaCost = "{3}{R}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — Dragon"
    power = 4
    toughness = 4
    oracleText = "Flying, firebending 2\n" +
        "When Ran and Shaw enter, if you cast them and there are three or more Dragon and/or Lesson " +
        "cards in your graveyard, create a token that's a copy of Ran and Shaw, except it's not legendary.\n" +
        "{3}{R}: Dragons you control get +2/+0 until end of turn."

    keywords(Keyword.FLYING)
    firebending(2)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        triggerCondition = Conditions.All(
            Conditions.WasCast,
            Conditions.CardsInGraveyardMatchingAtLeast(3, GameObjectFilter.Any.withAnySubtype("Dragon", "Lesson")),
        )
        effect = Effects.CreateTokenCopyOfSelf(removeLegendary = true)
        description = "When Ran and Shaw enter, if you cast them and there are three or more Dragon " +
            "and/or Lesson cards in your graveyard, create a token that's a copy of Ran and Shaw, " +
            "except it's not legendary."
    }

    activatedAbility {
        cost = Costs.Mana("{3}{R}")
        effect = Effects.ForEachInGroup(
            filter = GroupFilter(GameObjectFilter.Creature.withSubtype("Dragon").youControl()),
            effect = ModifyStatsEffect(2, 0, EffectTarget.Self),
        )
        description = "{3}{R}: Dragons you control get +2/+0 until end of turn."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "150"
        artist = "Miho Midorikawa"
        imageUri = "https://cards.scryfall.io/normal/front/6/4/6436e2d0-989f-47e3-90bc-9cde82a2ddb4.jpg?1764121032"
    }
}

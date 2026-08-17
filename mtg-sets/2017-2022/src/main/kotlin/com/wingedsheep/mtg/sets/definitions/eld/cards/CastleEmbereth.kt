package com.wingedsheep.mtg.sets.definitions.eld.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.predicates.CardPredicate
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/** A Mountain — the land type, not the card name, so a dual land with the type counts. */
private val AMountainYouControl = GameObjectFilter(
    cardPredicates = listOf(CardPredicate.IsLand, CardPredicate.HasSubtype(Subtype.MOUNTAIN))
)

/**
 * Castle Embereth
 * Land
 *
 * This land enters tapped unless you control a Mountain.
 * {T}: Add {R}.
 * {1}{R}{R}, {T}: Creatures you control get +1/+0 until end of turn.
 *
 * The pump is a one-shot over the creatures you control *as the ability resolves*
 * ([Effects.ForEachInGroup]) — creatures that arrive later in the turn are not affected.
 */
val CastleEmbereth = card("Castle Embereth") {
    manaCost = ""
    colorIdentity = "R"
    typeLine = "Land"
    oracleText = "This land enters tapped unless you control a Mountain.\n" +
        "{T}: Add {R}.\n" +
        "{1}{R}{R}, {T}: Creatures you control get +1/+0 until end of turn."

    replacementEffect(
        EntersTapped(
            unlessCondition = Exists(
                player = Player.You,
                zone = Zone.BATTLEFIELD,
                filter = AMountainYouControl
            )
        )
    )

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.RED)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{R}{R}"), Costs.Tap)
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.youControl()),
            Effects.ModifyStats(1, 0, EffectTarget.Self)
        )
        description = "Creatures you control get +1/+0 until end of turn."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "239"
        artist = "Jaime Jones"
        flavorText = "Without Embereth's courage, the realm would falter and fall."
        imageUri = "https://cards.scryfall.io/normal/front/8/b/8bb8512e-6913-4be6-8828-24cfcbec042e.jpg"
    }
}

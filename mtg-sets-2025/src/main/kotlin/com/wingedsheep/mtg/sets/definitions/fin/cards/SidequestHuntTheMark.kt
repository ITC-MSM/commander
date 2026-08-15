package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.values.TurnTracker
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.TransformEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Sidequest: Hunt the Mark // Yiazmat, Ultimate Mark — Final Fantasy #119
 * {3}{B}{B} · Enchantment // Legendary Creature — Dragon · 5/6
 *
 * Front — Sidequest: Hunt the Mark:
 *   When this enchantment enters, destroy up to one target creature.
 *   At the beginning of your end step, if a creature died under an opponent's control this
 *   turn, create a Treasure token. Then if you control three or more Treasures, transform
 *   this enchantment.
 *
 * Back — Yiazmat, Ultimate Mark:
 *   {1}{B}, Sacrifice another creature or artifact: Yiazmat gains indestructible until end
 *   of turn. Tap it.
 *
 * The end-step trigger's intervening-"if" reads the opponents' creatures-died-this-turn
 * tracker ([DynamicAmount.TurnTracking] over [Player.EachOpponent] sums across opponents);
 * the "Then if" Treasure-count check is a resolution-time [ConditionalEffect], evaluated
 * after the Treasure is created.
 */
private val YiazmatUltimateMark = card("Yiazmat, Ultimate Mark") {
    manaCost = ""
    colorIdentity = "B"
    typeLine = "Legendary Creature — Dragon"
    oracleText = "{1}{B}, Sacrifice another creature or artifact: Yiazmat gains indestructible " +
        "until end of turn. Tap it."
    power = 5
    toughness = 6

    // {1}{B}, Sacrifice another creature or artifact: Yiazmat gains indestructible until end
    // of turn. Tap it.
    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{1}{B}"),
            Costs.SacrificeAnother(GameObjectFilter.Creature or GameObjectFilter.Artifact),
        )
        effect = Effects.Composite(
            Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, EffectTarget.Self),
            Effects.Tap(EffectTarget.Self),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "119"
        artist = "Joshua Raphael"
        flavorText = "Legend says it is an Anima, guardian to a sacred blade. Though most " +
            "sacred amongst its kind, its great power drove it to madness, and in the end, it " +
            "became a threat to its own creator."
        imageUri = "https://cards.scryfall.io/normal/back/c/3/c3eb2ae5-10de-4c3d-91c8-8734befc80b2.jpg?1782686509"
    }
}

private val SidequestHuntTheMarkFront = card("Sidequest: Hunt the Mark") {
    manaCost = "{3}{B}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment"
    oracleText = "When this enchantment enters, destroy up to one target creature.\n" +
        "At the beginning of your end step, if a creature died under an opponent's control " +
        "this turn, create a Treasure token. Then if you control three or more Treasures, " +
        "transform this enchantment."

    // When this enchantment enters, destroy up to one target creature.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target(
            "creature",
            TargetObject(optional = true, filter = TargetFilter.Creature),
        )
        effect = Effects.Destroy(t)
    }

    // At the beginning of your end step, if a creature died under an opponent's control this
    // turn, create a Treasure token. Then if you control three or more Treasures, transform
    // this enchantment.
    triggeredAbility {
        trigger = Triggers.YourEndStep
        triggerCondition = Conditions.CompareAmounts(
            DynamicAmount.TurnTracking(Player.EachOpponent, TurnTracker.CREATURES_DIED),
            ComparisonOperator.GTE,
            DynamicAmount.Fixed(1),
        )
        effect = Effects.Composite(
            Effects.CreateTreasure(1),
            ConditionalEffect(
                condition = Conditions.YouControlAtLeast(3, GameObjectFilter.Artifact.withSubtype("Treasure")),
                effect = TransformEffect(EffectTarget.Self),
            ),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "119"
        artist = "Nino Is"
        imageUri = "https://cards.scryfall.io/normal/front/c/3/c3eb2ae5-10de-4c3d-91c8-8734befc80b2.jpg?1782686509"

        ruling(
            "2025-06-06",
            "Sidequest: Hunt the Mark's last ability checks at the moment it would trigger to " +
                "see if a creature died under an opponent's control this turn. If none did, the " +
                "ability won't trigger at all. Once your end step begins, it's too late to cause " +
                "creatures your opponents control to die in order to cause the ability to trigger."
        )
    }
}

val SidequestHuntTheMark: CardDefinition = CardDefinition.doubleFacedPermanent(
    frontFace = SidequestHuntTheMarkFront,
    backFace = YiazmatUltimateMark,
)

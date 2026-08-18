package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.effects.BecomeArtifactEffect
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Tenth District Hero
 * {1}{W}
 * Creature — Human
 * 2/3
 *
 * {1}{W}, Collect evidence 2: This creature becomes a Human Detective with base power and
 * toughness 4/4 and gains vigilance.
 * {2}{W}, Collect evidence 4: If this creature is a Detective, it becomes a legendary creature
 * named Mileva, the Stalwart, it has base power and toughness 5/5, and it gains "Other creatures
 * you control have indestructible."
 *
 * Both abilities are **durationless** (`Duration.Permanent`) — per the card's rulings the effects
 * last until the game ends, the permanent leaves the battlefield, or a later effect overwrites
 * them. Neither is a transform: they are ordinary continuous effects stacked by timestamp, so
 * re-activating the first ability after the second re-sets base P/T to 4/4 while leaving the
 * name, supertype and granted static from the second in place (the second ruling's case).
 *
 * The second ability's "if this creature is a Detective" is a **resolution-time** check on the
 * source (third ruling: the ability may be activated regardless of type and simply does nothing
 * if the check fails), so it is a `ConditionalEffect` inside the effect rather than an
 * `ActivationRestriction`. It reads projected state via `Conditions.SourceMatches`, so any route
 * to Detective-hood counts, not just the first ability (fourth ruling).
 *
 * "becomes a legendary creature" is the supertype, granted with `AddCardType("LEGENDARY")` —
 * the engine projects supertypes into the same type set, so there is no separate supertype effect.
 *
 * The rename and the granted static ride `BecomeArtifactEffect`, the SDK's general permanent
 * transform, with every artifact-flavoured knob turned off: `cardTypes = null` and the new
 * `subtypes = null` keep the Hero exactly the Human Detective it already is, and
 * `loseAllAbilities = false` keeps its own two activated abilities. That effect is the only route
 * to a *projected* rename (Layer 3) and a *projected* static grant — `Effects.GrantStaticAbility`
 * writes to the point-of-use `grantedStaticAbilities` store instead, which the layer projector
 * never reads, so a `SetName` or `GrantKeyword` handed over that way is silently inert. Its
 * grants also last exactly as long as the permanent stays on the battlefield, which is the
 * durationless lifetime this ability wants.
 */
val TenthDistrictHero = card("Tenth District Hero") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human"
    power = 2
    toughness = 3
    oracleText = "{1}{W}, Collect evidence 2: This creature becomes a Human Detective with base " +
        "power and toughness 4/4 and gains vigilance. (To collect evidence 2, exile cards with " +
        "total mana value 2 or greater from your graveyard.)\n" +
        "{2}{W}, Collect evidence 4: If this creature is a Detective, it becomes a legendary " +
        "creature named Mileva, the Stalwart, it has base power and toughness 5/5, and it gains " +
        "\"Other creatures you control have indestructible.\""

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{W}"), Costs.CollectEvidence(2))
        effect = Effects.BecomeCreature(
            target = EffectTarget.Self,
            power = 4,
            toughness = 4,
            keywords = setOf(Keyword.VIGILANCE),
            creatureTypes = setOf("Human", "Detective"),
            duration = Duration.Permanent,
        )
        description = "This creature becomes a Human Detective with base power and toughness 4/4 " +
            "and gains vigilance."
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}{W}"), Costs.CollectEvidence(4))
        effect = ConditionalEffect(
            condition = Conditions.SourceMatches(
                GameObjectFilter.Creature.withSubtype(Subtype.DETECTIVE)
            ),
            effect = Effects.Composite(
                Effects.AddCardType("LEGENDARY", EffectTarget.Self, Duration.Permanent),
                BecomeArtifactEffect(
                    target = EffectTarget.Self,
                    cardTypes = null,
                    subtypes = null,
                    colors = null,
                    loseAllAbilities = false,
                    name = "Mileva, the Stalwart",
                    grantedStaticAbilities = listOf(
                        GrantKeyword(
                            keyword = Keyword.INDESTRUCTIBLE,
                            filter = GroupFilter.OtherCreaturesYouControl,
                        ),
                    ),
                    duration = Duration.Permanent,
                ),
                Effects.SetBasePowerAndToughness(
                    power = 5,
                    toughness = 5,
                    target = EffectTarget.Self,
                    duration = Duration.Permanent,
                ),
            ),
        )
        description = "If this creature is a Detective, it becomes a legendary creature named " +
            "Mileva, the Stalwart, it has base power and toughness 5/5, and it gains \"Other " +
            "creatures you control have indestructible.\""
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "34"
        artist = "Kai Carpenter"
        imageUri = "https://cards.scryfall.io/normal/front/7/c/7c65a79e-f28a-4f30-95a4-1ea55fd84564.jpg?1783912919"

        ruling(
            "2024-02-02",
            "Neither of these abilities have durations. If one of them resolves, it will remain " +
                "in effect until the game ends, Tenth District Hero leaves the battlefield, or " +
                "some subsequent effect changes its characteristics, whichever comes first.",
        )
        ruling(
            "2024-02-02",
            "You can activate Tenth District Hero's second ability regardless of what creature " +
                "types it is. The ability checks Tenth District Hero's creature types when it " +
                "resolves. If Tenth District Hero isn't a Detective at that time, the ability " +
                "does nothing.",
        )
        ruling(
            "2024-02-02",
            "If you can't exile enough cards to meet or exceed the required mana value, you " +
                "can't choose to collect evidence at all.",
        )
    }
}

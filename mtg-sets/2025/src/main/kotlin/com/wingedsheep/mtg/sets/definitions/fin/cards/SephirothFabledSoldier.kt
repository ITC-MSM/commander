package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.IncrementAbilityResolutionCountEffect
import com.wingedsheep.sdk.scripting.effects.OptionalCostEffect
import com.wingedsheep.sdk.scripting.effects.SacrificeEffect
import com.wingedsheep.sdk.scripting.effects.TransformEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Sephiroth, Fabled SOLDIER // Sephiroth, One-Winged Angel
 * {2}{B} — Legendary Creature — Human Avatar Soldier 3/3
 *   // Legendary Creature — Angel Nightmare Avatar 5/5
 *
 * Front — Sephiroth, Fabled SOLDIER:
 *   Whenever Sephiroth enters or attacks, you may sacrifice another creature. If you do, draw a card.
 *   Whenever another creature dies, target opponent loses 1 life and you gain 1 life. If this is the
 *   fourth time this ability has resolved this turn, transform Sephiroth.
 *
 * Back — Sephiroth, One-Winged Angel:
 *   Flying
 *   Super Nova — As this creature transforms into Sephiroth, One-Winged Angel, you get an emblem with
 *   "Whenever a creature dies, target opponent loses 1 life and you gain 1 life."
 *   Whenever Sephiroth attacks, you may sacrifice any number of other creatures. If you do, draw that
 *   many cards.
 *
 * Modeling notes:
 *  - "enters or attacks" has no combined trigger in the engine — it is two sibling triggered
 *    abilities sharing one body (the Gilgamesh / Frodo shape).
 *  - "another creature dies" is a leave-to-graveyard trigger with OTHER binding (excludes Sephiroth
 *    itself), so if Sephiroth dies alongside other creatures the ability still triggers for each of
 *    them but not for itself (CR 603.2). The fourth-resolution transform targets Self, so a dead
 *    Sephiroth simply no-ops that clause — matching the ruling that it "won't transform" in that case.
 *  - The resolution-count clause increments only when the ability actually resolves (an illegal /
 *    countered trigger never runs the increment), so a fizzled trigger doesn't advance the count.
 *  - The Super Nova emblem is a permanent global triggered ability (CreateGlobalTriggeredAbility)
 *    created by the transform-to-back trigger, so it survives Sephiroth leaving and is only granted
 *    by actually transforming (not by entering back-face-up).
 */
private val SephirothOneWingedAngel = card("Sephiroth, One-Winged Angel") {
    manaCost = ""
    colorIdentity = "B"
    typeLine = "Legendary Creature — Angel Nightmare Avatar"
    oracleText = "Flying\n" +
        "Super Nova — As this creature transforms into Sephiroth, One-Winged Angel, you get an " +
        "emblem with \"Whenever a creature dies, target opponent loses 1 life and you gain 1 life.\"\n" +
        "Whenever Sephiroth attacks, you may sacrifice any number of other creatures. If you do, " +
        "draw that many cards."
    power = 5
    toughness = 5

    keywords(Keyword.FLYING)

    // Super Nova — As this creature transforms into Sephiroth, One-Winged Angel, you get an emblem.
    triggeredAbility {
        trigger = Triggers.TransformsToBack
        effect = Effects.CreateGlobalTriggeredAbility(
            duration = Duration.Permanent,
            ability = TriggeredAbility.create(
                trigger = Triggers.AnyCreatureDies.event,
                binding = Triggers.AnyCreatureDies.binding,
                effect = Effects.Composite(
                    listOf(
                        Effects.LoseLife(1, EffectTarget.ContextTarget(0)),
                        Effects.GainLife(1),
                    )
                ),
                targetRequirement = Targets.Opponent,
                descriptionOverride = "Whenever a creature dies, target opponent loses 1 life and " +
                    "you gain 1 life.",
            ),
            descriptionOverride = "Whenever a creature dies, target opponent loses 1 life and you " +
                "gain 1 life.",
        )
        description = "Super Nova — As this creature transforms into Sephiroth, One-Winged Angel, " +
            "you get an emblem with \"Whenever a creature dies, target opponent loses 1 life and " +
            "you gain 1 life.\""
    }

    // Whenever Sephiroth attacks, you may sacrifice any number of other creatures. If you do, draw
    // that many cards.
    triggeredAbility {
        trigger = Triggers.Attacks
        effect = SacrificeEffect(
            filter = GameObjectFilter.Creature,
            any = true,
            excludeSource = true,
        ).then(Effects.DrawCards(DynamicAmounts.permanentsSacrificedThisWay()))
        description = "Whenever Sephiroth attacks, you may sacrifice any number of other creatures. " +
            "If you do, draw that many cards."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "115"
        artist = "Wisnu Tan"
        imageUri = "https://cards.scryfall.io/normal/back/8/5/85eaf5e7-77dc-4842-a70c-ce4ac7f724df.jpg?1782686512"
    }
}

private val SephirothFabledSoldierFrontFace = card("Sephiroth, Fabled SOLDIER") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Creature — Human Avatar Soldier"
    oracleText = "Whenever Sephiroth enters or attacks, you may sacrifice another creature. If you " +
        "do, draw a card.\n" +
        "Whenever another creature dies, target opponent loses 1 life and you gain 1 life. If this " +
        "is the fourth time this ability has resolved this turn, transform Sephiroth."
    power = 3
    toughness = 3

    // Whenever Sephiroth enters or attacks, you may sacrifice another creature. If you do, draw a
    // card. — modeled as two sibling triggers sharing one body.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = OptionalCostEffect(
            cost = SacrificeEffect(
                filter = GameObjectFilter.Creature,
                count = 1,
                excludeSource = true,
            ),
            ifPaid = Effects.DrawCards(1),
        )
        description = "Whenever Sephiroth enters, you may sacrifice another creature. If you do, " +
            "draw a card."
    }
    triggeredAbility {
        trigger = Triggers.Attacks
        effect = OptionalCostEffect(
            cost = SacrificeEffect(
                filter = GameObjectFilter.Creature,
                count = 1,
                excludeSource = true,
            ),
            ifPaid = Effects.DrawCards(1),
        )
        description = "Whenever Sephiroth attacks, you may sacrifice another creature. If you do, " +
            "draw a card."
    }

    // Whenever another creature dies, target opponent loses 1 life and you gain 1 life. If this is
    // the fourth time this ability has resolved this turn, transform Sephiroth.
    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Creature,
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.OTHER,
        )
        val opponent = target("target opponent", Targets.Opponent)
        effect = Effects.Composite(
            listOf(
                Effects.LoseLife(1, opponent),
                Effects.GainLife(1),
            )
        ).then(IncrementAbilityResolutionCountEffect)
            .then(
                ConditionalEffect(
                    condition = Conditions.SourceAbilityResolvedNTimes(4),
                    effect = TransformEffect(EffectTarget.Self),
                )
            )
        description = "Whenever another creature dies, target opponent loses 1 life and you gain 1 " +
            "life. If this is the fourth time this ability has resolved this turn, transform Sephiroth."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "115"
        artist = "Wisnu Tan"
        imageUri = "https://cards.scryfall.io/normal/front/8/5/85eaf5e7-77dc-4842-a70c-ce4ac7f724df.jpg?1782686512"
        ruling(
            "2025-06-06",
            "If Sephiroth, Fabled SOLDIER and one or more other creatures die at the same time, " +
                "its last ability will trigger for each of those other creatures. (It won't " +
                "transform, though.)"
        )
        ruling(
            "2025-06-06",
            "If this card somehow enters the battlefield with its back face up, it didn't " +
                "transform, so you won't get an emblem."
        )
    }
}

val SephirothFabledSoldier: CardDefinition = CardDefinition.doubleFacedCreature(
    frontFace = SephirothFabledSoldierFrontFace,
    backFace = SephirothOneWingedAngel,
)

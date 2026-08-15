package com.wingedsheep.mtg.sets.definitions.woe.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Imodane's Recruiter // Train Troops
 * {2}{R}
 * Creature — Human Knight
 * 2/2
 *
 * When this creature enters, creatures you control get +1/+0 and gain haste until end of turn.
 *
 * Adventure: Train Troops — {4}{W}, Sorcery — Adventure
 * Create two 2/2 white Knight creature tokens with vigilance.
 *
 * (CR 715: Adventure cards. Casting the Adventure exiles the card on resolution and lets the caster
 * cast it as the creature spell while it remains in exile.)
 *
 * The ETB pump is a `ForEachInGroup` over creatures you control, enumerated once at resolution —
 * the Recruiter itself is included, and creatures that arrive later in the turn are not (WOE
 * ruling). Both halves of the grant are `EndOfTurn`-duration and target the iteration entity.
 */
val ImodanesRecruiter = card("Imodane's Recruiter") {
    manaCost = "{2}{R}"
    colorIdentity = "RW"
    typeLine = "Creature — Human Knight"
    power = 2
    toughness = 2
    oracleText = "When this creature enters, creatures you control get +1/+0 and gain haste until end of turn."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.youControl()),
            Effects.Composite(
                Effects.ModifyStats(1, 0, EffectTarget.Self),
                Effects.GrantKeyword(Keyword.HASTE, EffectTarget.Self)
            )
        )
    }

    adventure("Train Troops") {
        manaCost = "{4}{W}"
        typeLine = "Sorcery — Adventure"
        oracleText = "Create two 2/2 white Knight creature tokens with vigilance. " +
            "(Then exile this card. You may cast the creature later from exile.)"
        spell {
            effect = Effects.CreateToken(
                power = 2,
                toughness = 2,
                colors = setOf(Color.WHITE),
                creatureTypes = setOf("Knight"),
                keywords = setOf(Keyword.VIGILANCE),
                count = 2,
                imageUri = "https://cards.scryfall.io/normal/front/f/4/f4035134-a162-4651-86c5-ae006b6e0e20.jpg?1783914992"
            )
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "229"
        artist = "Néstor Ossandón Leal"
        imageUri = "https://cards.scryfall.io/normal/front/4/d/4dbaa855-3f8e-42e6-8ec8-5ffbc5c8acf0.jpg?1783915064"

        ruling(
            "2023-09-01",
            "Imodane's Recruiter's triggered ability affects only creatures you control at the time it " +
                "resolves. Creatures you begin to control later in the turn won't get +1/+0 or gain haste."
        )
        ruling(
            "2023-09-01",
            "If a spell is cast as an Adventure, its controller exiles it instead of putting it into its " +
                "owner's graveyard as it resolves. For as long as it remains exiled, that player may cast it " +
                "as a permanent spell."
        )
    }
}

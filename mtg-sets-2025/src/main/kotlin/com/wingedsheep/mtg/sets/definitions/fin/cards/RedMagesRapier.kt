package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.jobSelect
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GrantSubtype
import com.wingedsheep.sdk.scripting.GrantTriggeredAbility
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Red Mage's Rapier
 * {1}{R}
 * Artifact — Equipment
 * Job select (When this Equipment enters, create a 1/1 colorless Hero creature token,
 *   then attach this to it.)
 * Equipped creature has "Whenever you cast a noncreature spell, this creature gets +2/+0
 *   until end of turn" and is a Wizard in addition to its other types.
 * Equip {3}
 *
 * The granted "Whenever you cast a noncreature spell, this creature gets +2/+0 until end of
 * turn" ability lives on the equipped creature (GrantTriggeredAbility over the attached-
 * creature filter). "You" resolves to the creature's controller, and "this creature" is the
 * pumped permanent (EffectTarget.Self relative to the bearer).
 */
val RedMagesRapier = card("Red Mage's Rapier") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Artifact — Equipment"
    oracleText = "Job select (When this Equipment enters, create a 1/1 colorless Hero creature token, then attach this to it.)\n" +
        "Equipped creature has \"Whenever you cast a noncreature spell, this creature gets +2/+0 until end of turn\" and is a Wizard in addition to its other types.\n" +
        "Equip {3} ({3}: Attach to target creature you control. Equip only as a sorcery.)"

    jobSelect()

    staticAbility {
        ability = GrantTriggeredAbility(
            ability = TriggeredAbility.create(
                trigger = Triggers.YouCastNoncreature.event,
                binding = Triggers.YouCastNoncreature.binding,
                effect = Effects.ModifyStats(2, 0, EffectTarget.Self, Duration.EndOfTurn)
            ),
            filter = Filters.EquippedCreature
        )
    }
    staticAbility {
        ability = GrantSubtype("Wizard", Filters.EquippedCreature)
    }

    equipAbility("{3}")

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "152"
        artist = "Alexandre Honoré"
        imageUri = "https://cards.scryfall.io/normal/front/e/0/e0579955-75f9-47a9-8b03-e287d120826a.jpg?1748706327"
    }
}

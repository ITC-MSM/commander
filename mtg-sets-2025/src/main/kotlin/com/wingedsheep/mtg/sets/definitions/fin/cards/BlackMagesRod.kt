package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.jobSelect
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantSubtype
import com.wingedsheep.sdk.scripting.GrantTriggeredAbility
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Black Mage's Rod
 * {1}{B}
 * Artifact — Equipment
 * Job select (When this Equipment enters, create a 1/1 colorless Hero creature token,
 *   then attach this to it.)
 * Equipped creature gets +1/+0, has "Whenever you cast a noncreature spell, this creature
 *   deals 1 damage to each opponent," and is a Wizard in addition to its other types.
 * Equip {3}
 *
 * The granted "Whenever you cast a noncreature spell, this creature deals 1 damage to each
 * opponent" ability lives on the equipped creature (GrantTriggeredAbility over the attached-
 * creature filter). "You" resolves to the creature's controller, and "this creature" is the
 * damage source (EffectTarget.Self relative to the bearer).
 */
val BlackMagesRod = card("Black Mage's Rod") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Artifact — Equipment"
    oracleText = "Job select (When this Equipment enters, create a 1/1 colorless Hero creature token, then attach this to it.)\n" +
        "Equipped creature gets +1/+0, has \"Whenever you cast a noncreature spell, this creature deals 1 damage to each opponent,\" and is a Wizard in addition to its other types.\n" +
        "Equip {3} ({3}: Attach to target creature you control. Equip only as a sorcery.)"

    jobSelect()

    staticAbility {
        ability = ModifyStats(1, 0, Filters.EquippedCreature)
    }
    staticAbility {
        ability = GrantTriggeredAbility(
            ability = TriggeredAbility.create(
                trigger = Triggers.YouCastNoncreature.event,
                binding = Triggers.YouCastNoncreature.binding,
                effect = Effects.DealDamage(
                    1,
                    EffectTarget.PlayerRef(Player.EachOpponent),
                    damageSource = EffectTarget.Self
                )
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
        collectorNumber = "90"
        artist = "Nino Is"
        imageUri = "https://cards.scryfall.io/normal/front/3/5/35e8f140-055f-4fc7-a765-fb030d828214.jpg?1748706099"
    }
}

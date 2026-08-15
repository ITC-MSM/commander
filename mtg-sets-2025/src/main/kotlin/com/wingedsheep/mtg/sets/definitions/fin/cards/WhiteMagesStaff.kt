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

/**
 * White Mage's Staff
 * {1}{W}
 * Artifact — Equipment
 * Job select (When this Equipment enters, create a 1/1 colorless Hero creature token,
 *   then attach this to it.)
 * Equipped creature gets +1/+1, has "Whenever this creature attacks, you gain 1 life,"
 *   and is a Cleric in addition to its other types.
 * Equip {3}
 *
 * The granted "Whenever this creature attacks, you gain 1 life" ability lives on the
 * equipped creature (GrantTriggeredAbility over the attached-creature filter), so it
 * fires on the creature's own attacks (SELF binding) and benefits the creature's
 * controller.
 */
val WhiteMagesStaff = card("White Mage's Staff") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Artifact — Equipment"
    oracleText = "Job select (When this Equipment enters, create a 1/1 colorless Hero creature token, then attach this to it.)\n" +
        "Equipped creature gets +1/+1, has \"Whenever this creature attacks, you gain 1 life,\" and is a Cleric in addition to its other types.\n" +
        "Equip {3} ({3}: Attach to target creature you control. Equip only as a sorcery.)"

    jobSelect()

    staticAbility {
        ability = ModifyStats(1, 1, Filters.EquippedCreature)
    }
    staticAbility {
        ability = GrantTriggeredAbility(
            ability = TriggeredAbility.create(
                trigger = Triggers.attacks().event,
                binding = Triggers.attacks().binding,
                effect = Effects.GainLife(1)
            ),
            filter = Filters.EquippedCreature
        )
    }
    staticAbility {
        ability = GrantSubtype("Cleric", Filters.EquippedCreature)
    }

    equipAbility("{3}")

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "42"
        artist = "Kim Dingwall"
        imageUri = "https://cards.scryfall.io/normal/front/3/0/30db372e-0b4c-4e16-9667-bf3fda666f72.jpg?1748705912"
    }
}

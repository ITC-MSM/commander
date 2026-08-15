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
 * Thief's Knife
 * {2}{U}
 * Artifact — Equipment
 * Job select (When this Equipment enters, create a 1/1 colorless Hero creature token,
 *   then attach this to it.)
 * Equipped creature gets +1/+1, has "Whenever this creature deals combat damage to a
 *   player, draw a card," and is a Rogue in addition to its other types.
 * Equip {4}
 *
 * The granted "deals combat damage to a player → draw a card" ability lives on the equipped
 * creature (GrantTriggeredAbility over the attached-creature filter, SELF binding), so it fires
 * on the equipped creature's own combat damage and draws for the creature's controller.
 */
val ThiefsKnife = card("Thief's Knife") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Artifact — Equipment"
    oracleText = "Job select (When this Equipment enters, create a 1/1 colorless Hero creature token, then attach this to it.)\n" +
        "Equipped creature gets +1/+1, has \"Whenever this creature deals combat damage to a player, draw a card,\" and is a Rogue in addition to its other types.\n" +
        "Equip {4} ({4}: Attach to target creature you control. Equip only as a sorcery.)"

    jobSelect()

    staticAbility {
        ability = ModifyStats(1, 1, Filters.EquippedCreature)
    }
    staticAbility {
        ability = GrantTriggeredAbility(
            ability = TriggeredAbility.create(
                trigger = Triggers.DealsCombatDamageToPlayer.event,
                binding = Triggers.DealsCombatDamageToPlayer.binding,
                effect = Effects.DrawCards(1)
            ),
            filter = Filters.EquippedCreature
        )
    }
    staticAbility {
        ability = GrantSubtype("Rogue", Filters.EquippedCreature)
    }

    equipAbility("{4}")

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "81"
        artist = "Domenico Cava"
        imageUri = "https://cards.scryfall.io/normal/front/c/2/c2dcfd0a-3f52-4616-a09a-fd2db8b6b93e.jpg?1748706064"
    }
}

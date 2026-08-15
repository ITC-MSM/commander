package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.jobSelect
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantSubtype
import com.wingedsheep.sdk.scripting.GrantTriggeredAbility
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Astrologian's Planisphere
 * {1}{U}
 * Artifact — Equipment
 * Job select (When this Equipment enters, create a 1/1 colorless Hero creature token,
 *   then attach this to it.)
 * Equipped creature is a Wizard in addition to its other types and has "Whenever you cast a
 *   noncreature spell and whenever you draw your third card each turn, put a +1/+1 counter on
 *   this creature."
 * Diana — Equip {2}
 *
 * The granted ability lives on the equipped creature ([GrantTriggeredAbility] over the
 * attached-creature filter), so "you" resolves to the creature's controller and "this creature"
 * ([EffectTarget.Self]) is the bearer. The single printed ability has two trigger conditions, so
 * it is modeled as two granted triggered abilities: [Triggers.YouCastNoncreature] and
 * [Triggers.NthCardDrawn]`(3)` (CR 121.2 — "draw your third card each turn"), each adding a
 * +1/+1 counter to the equipped creature.
 */
val AstrologiansPlanisphere = card("Astrologian's Planisphere") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Artifact — Equipment"
    oracleText = "Job select (When this Equipment enters, create a 1/1 colorless Hero creature token, then attach this to it.)\n" +
        "Equipped creature is a Wizard in addition to its other types and has \"Whenever you cast a noncreature spell and whenever you draw your third card each turn, put a +1/+1 counter on this creature.\"\n" +
        "Diana — Equip {2} ({2}: Attach to target creature you control. Equip only as a sorcery.)"

    jobSelect()

    staticAbility {
        ability = GrantSubtype("Wizard", Filters.EquippedCreature)
    }
    staticAbility {
        ability = GrantTriggeredAbility(
            ability = TriggeredAbility.create(
                trigger = Triggers.YouCastNoncreature.event,
                binding = Triggers.YouCastNoncreature.binding,
                effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
            ),
            filter = Filters.EquippedCreature
        )
    }
    staticAbility {
        ability = GrantTriggeredAbility(
            ability = TriggeredAbility.create(
                trigger = Triggers.NthCardDrawn(3).event,
                binding = Triggers.NthCardDrawn(3).binding,
                effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
            ),
            filter = Filters.EquippedCreature
        )
    }

    equipAbility("{2}")

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "46"
        artist = "Josephine Chang"
        imageUri = "https://cards.scryfall.io/normal/front/b/f/bfa4e927-1d6f-4a64-9801-7d168a5ef3f6.jpg?1748705924"
    }
}

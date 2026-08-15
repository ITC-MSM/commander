package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect

/**
 * Stormbeacon Blade — Tarkir: Dragonstorm #27
 * {1}{W} · Artifact — Equipment
 *
 * Equipped creature gets +3/+0.
 * Whenever equipped creature attacks, draw a card if you control three or more attacking creatures.
 * Equip {2}
 *
 * Standard Equipment shell: the +3/+0 is a [ModifyStats] static over
 * [Filters.EquippedCreature]; equip via [card.equipAbility]. The attack trigger uses
 * `Triggers.attacks(binding = ATTACHED)` ("equipped creature attacks") and gates the draw
 * with an intervening-if condition — [Conditions.YouControlAtLeast] over attacking creatures
 * — so the draw only happens when three or more of your creatures are attacking (evaluated as
 * the triggered ability resolves, no `elseEffect`).
 */
val StormbeaconBlade = card("Stormbeacon Blade") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature gets +3/+0.\n" +
        "Whenever equipped creature attacks, draw a card if you control three or more attacking creatures.\n" +
        "Equip {2}"

    staticAbility {
        ability = ModifyStats(+3, 0, Filters.EquippedCreature)
    }

    triggeredAbility {
        trigger = Triggers.attacks(binding = TriggerBinding.ATTACHED)
        effect = ConditionalEffect(
            condition = Conditions.YouControlAtLeast(3, GameObjectFilter.Creature.attacking()),
            effect = Effects.DrawCards(1)
        )
    }

    equipAbility("{2}")

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "27"
        artist = "Jorge Jacinto"
        imageUri = "https://cards.scryfall.io/normal/front/f/2/f2f12684-c80a-422b-9c3f-ed4f31742b9d.jpg?1743204061"
    }
}

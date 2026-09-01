package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Maja, Bretagard Protector
 * {2}{G}{W}{W}
 * Legendary Creature — Human Warrior
 * 2/3
 * Other creatures you control get +1/+1.
 * Landfall — Whenever a land you control enters, create a 1/1 white Human Warrior creature token.
 *
 * An anthem plus a landfall token-maker, which compound: every land drop adds a Human Warrior that
 * the anthem immediately pumps. `excludeSelf = true` on the anthem carries the printed "Other".
 *
 * The landfall trigger binds [TriggerBinding.ANY], not the default `SELF`: the permanent that enters
 * is the land, not Maja, so a source-bound trigger would never fire.
 */
val MajaBretagardProtector = card("Maja, Bretagard Protector") {
    manaCost = "{2}{G}{W}{W}"
    colorIdentity = "GW"
    typeLine = "Legendary Creature — Human Warrior"
    oracleText = "Other creatures you control get +1/+1.\n" +
        "Landfall — Whenever a land you control enters, create a 1/1 white Human Warrior creature token."
    power = 2
    toughness = 3

    // Other creatures you control get +1/+1.
    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(GameObjectFilter.Creature.youControl(), excludeSelf = true)
        )
    }

    // Landfall — Whenever a land you control enters, create a 1/1 white Human Warrior token.
    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Land.youControl(),
            binding = TriggerBinding.ANY
        )
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Human", "Warrior")
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "222"
        artist = "Lie Setiawan"
        flavorText = "\"Our enemies have breached our realm. To survive, the clans must fight as one!\""
        imageUri = "https://cards.scryfall.io/normal/front/c/c/cc3707f1-ed9d-412e-a7be-b6d8b554bd6c.jpg"
    }
}

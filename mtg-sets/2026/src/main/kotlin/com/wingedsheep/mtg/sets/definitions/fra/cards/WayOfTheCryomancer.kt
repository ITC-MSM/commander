package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.grantedLoyaltyAbility
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Way of the Cryomancer (Reality Fracture #223) — {2}{U} Legendary Enchantment.
 *
 * Empower Jace 5, plus a granted −3 whose effect is Howl of the Horde's delayed copy:
 * [Effects.CopyNextSpellCast] watches for the controller's next instant or sorcery this turn and
 * copies it once, letting them choose new targets for the copy.
 */
val WayOfTheCryomancer = card("Way of the Cryomancer") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Enchantment"
    oracleText = "When Way of the Cryomancer enters, empower Jace 5. (Put five loyalty counters on a Jace token you control. If you don't control one, first create a blue Jace planeswalker token with \"[−1]: Surveil 1\" and \"[−3]: Draw a card.\")\n" +
        "Planeswalkers you control have \"[−3]: When you next cast an instant or sorcery spell this turn, copy that spell. You may choose new targets for the copy.\""

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Mechanic.empowerJace(5)
    }

    staticAbility {
        ability = GrantActivatedAbility(
            ability = grantedLoyaltyAbility(-3) {
                effect = Effects.CopyNextSpellCast()
                description = "When you next cast an instant or sorcery spell this turn, copy that spell. " +
                    "You may choose new targets for the copy."
            },
            filter = GroupFilter(GameObjectFilter.Planeswalker.youControl())
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "223"
        artist = "Andrey Kuzinskiy"
        imageUri = "https://cards.scryfall.io/normal/front/8/3/838b0efb-7398-4df9-8fdf-b8af43b47938.jpg?1789014637"
        inBooster = false
    }
}

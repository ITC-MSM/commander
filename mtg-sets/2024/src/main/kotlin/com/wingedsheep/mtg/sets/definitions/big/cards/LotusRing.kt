package com.wingedsheep.mtg.sets.definitions.big.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Lotus Ring
 * {3}
 * Artifact — Equipment
 *
 * Indestructible
 * Equipped creature gets +3/+3 and has vigilance and "{T}, Sacrifice this creature:
 * Add three mana of any one color."
 * Equip {3}
 *
 * The granted "Add three mana of any one color" ability is a mana ability (CR 605.1a):
 * it has no target, isn't a loyalty ability, and could add mana — the {T} + sacrifice
 * cost doesn't disqualify it. [Effects.AddAnyColorMana] picks a single color and produces
 * three of it (one chosen color, not three independently-colored mana).
 */
val LotusRing = card("Lotus Ring") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact — Equipment"
    oracleText = "Indestructible\nEquipped creature gets +3/+3 and has vigilance and " +
        "\"{T}, Sacrifice this creature: Add three mana of any one color.\"\nEquip {3}"

    // Indestructible on the Equipment itself.
    keywords(Keyword.INDESTRUCTIBLE)

    // Equipped creature gets +3/+3.
    staticAbility {
        ability = ModifyStats(+3, +3, Filters.EquippedCreature)
    }

    // Equipped creature has vigilance.
    staticAbility {
        ability = GrantKeyword(Keyword.VIGILANCE, Filters.EquippedCreature)
    }

    // Equipped creature has "{T}, Sacrifice this creature: Add three mana of any one color."
    staticAbility {
        ability = GrantActivatedAbility(
            ability = ActivatedAbility(
                id = AbilityId.generate(),
                cost = Costs.Composite(Costs.Tap, Costs.SacrificeSelf),
                effect = Effects.AddAnyColorMana(3),
                isManaAbility = true,
                descriptionOverride = "{T}, Sacrifice this creature: Add three mana of any one color."
            ),
            filter = Filters.EquippedCreature
        )
    }

    equipAbility("{3}")

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "24"
        artist = "Alayna Danner"
        imageUri = "https://cards.scryfall.io/normal/front/0/2/02267717-66e0-41f7-8009-75586a4aa4be.jpg?1739804230"
    }
}

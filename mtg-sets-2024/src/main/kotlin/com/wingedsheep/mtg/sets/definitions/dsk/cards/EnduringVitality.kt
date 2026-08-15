package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.enduring
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Enduring Vitality
 * {1}{G}{G}
 * Enchantment Creature — Elk Glimmer
 * 3/3
 * Vigilance
 * Creatures you control have "{T}: Add one mana of any color."
 * When Enduring Vitality dies, if it was a creature, return it to the battlefield under its
 *   owner's control. It's an enchantment. (It's not a creature.)
 *
 * The mana ability is granted to every creature you control via [GrantActivatedAbility] (the
 * Citanul Hierophants / Cryptolith Rite lord shape); each grantee taps for one mana of any
 * color. The death clause is the Duskmourn "Enduring" mechanic — see [enduring].
 */
val EnduringVitality = card("Enduring Vitality") {
    manaCost = "{1}{G}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment Creature — Elk Glimmer"
    oracleText = "Vigilance\n" +
        "Creatures you control have \"{T}: Add one mana of any color.\"\n" +
        "When Enduring Vitality dies, if it was a creature, return it to the battlefield under " +
        "its owner's control. It's an enchantment. (It's not a creature.)"
    power = 3
    toughness = 3

    keywords(Keyword.VIGILANCE)
    enduring()

    staticAbility {
        ability = GrantActivatedAbility(
            ability = ActivatedAbility(
                id = AbilityId.generate(),
                cost = Costs.Tap,
                effect = Effects.AddAnyColorMana(1)
            ),
            filter = GroupFilter(GameObjectFilter.Creature.youControl())
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "176"
        artist = "Valera Lutfullina"
        imageUri = "https://cards.scryfall.io/normal/front/9/d/9d76a30c-0431-4334-892a-9822dda9671a.jpg?1726286517"
    }
}

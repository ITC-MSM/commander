package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.IncreaseActivatedAbilityCost
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Suppression Field
 * {1}{W}
 * Enchantment
 *
 * Activated abilities cost {2} more to activate unless they're mana abilities.
 *
 * The board-wide form of Skyseer's Chariot's tax: the same [IncreaseActivatedAbilityCost], with
 * `GameObjectFilter.Any` in place of the chosen-name predicate so it reaches every source, its
 * controller's included. `excludeManaAbilities` is the "unless they're mana abilities" half — the
 * engine reads the ability's own `isManaAbility` flag (CR 605.1a), so a land's `{T}: Add …` and a
 * Signet's `{1}, {T}: Add …` stay at their printed cost while everything else pays {2} more.
 *
 * A tax, unlike a reduction, applies even to an ability with no mana in its cost: an equip, a
 * loyalty ability, or a bare `{T}:` becomes `{2}, {T}:`. Static and triggered abilities are
 * untouched — only abilities written "[cost]: [effect]" are activated abilities at all.
 */
val SuppressionField = card("Suppression Field") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment"
    oracleText = "Activated abilities cost {2} more to activate unless they're mana abilities."

    staticAbility {
        ability = IncreaseActivatedAbilityCost(
            filter = GroupFilter(GameObjectFilter.Any),
            amount = DynamicAmount.Fixed(2),
            excludeManaAbilities = true,
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "31"
        artist = "John Avon"
        flavorText = "The most feared of Azorius punishments is to be freed—sent back out into " +
            "the world, stripped of all magical defenses."
        imageUri = "https://cards.scryfall.io/normal/front/0/d/0df7883b-f744-4410-a682-7391bd697afb.jpg?1783943694"
        ruling(
            "2016-06-08",
            "Activated abilities contain a colon. They're generally written \"[Cost]: [Effect].\" " +
                "Some keywords are activated abilities and will have colons in their reminder text."
        )
        ruling(
            "2005-10-01",
            "Suppression Field's ability doesn't affect static abilities, triggered abilities, or " +
                "mana abilities. (A \"mana ability\" is an ability that produces mana, not an " +
                "ability that costs mana.)"
        )
    }
}

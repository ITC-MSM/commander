package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeAttackedBy
import com.wingedsheep.sdk.scripting.ChoiceType
import com.wingedsheep.sdk.scripting.EntersWithChoice
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Teferi's Moat
 * {3}{W}{U}
 * Enchantment
 * As this enchantment enters, choose a color.
 * Creatures of the chosen color without flying can't attack you.
 *
 * The chosen color is stored at ETB via [EntersWithChoice] (ChoiceType.COLOR →
 * CastChoicesComponent). The attack restriction is [CantBeAttackedBy] over the whole clause —
 * creatures sharing the chosen color (resolved with this enchantment as the predicate source)
 * that lack flying.
 */
val TeferisMoat = card("Teferi's Moat") {
    manaCost = "{3}{W}{U}"
    colorIdentity = "WU"
    typeLine = "Enchantment"
    oracleText = "As this enchantment enters, choose a color.\n" +
        "Creatures of the chosen color without flying can't attack you."

    replacementEffect(EntersWithChoice(ChoiceType.COLOR))

    staticAbility {
        ability = CantBeAttackedBy(
            GameObjectFilter.Creature.sharingChosenColorWithSource().withoutKeyword(Keyword.FLYING)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "279"
        artist = "rk post"
        imageUri = "https://cards.scryfall.io/normal/front/9/e/9ed5845c-ef6d-4a7b-b725-b09d3e9bbc17.jpg?1591725011"
    }
}

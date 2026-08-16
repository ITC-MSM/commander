package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Starnheim Aspirant
 * {2}{W}
 * Creature — Human Cleric
 * 2/2
 * Angel spells you cast cost {2} less to cast.
 *
 * Dragonspeaker Shaman's shape: a static [ModifySpellCost] over [SpellCostTarget.YouCast] with an
 * Angel-subtype filter, reducing only the generic portion ([CostModification.ReduceGeneric]).
 */
val StarnheimAspirant = card("Starnheim Aspirant") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Cleric"
    power = 2
    toughness = 2
    oracleText = "Angel spells you cast cost {2} less to cast."

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.YouCast(GameObjectFilter.Any.withSubtype("Angel")),
            modification = CostModification.ReduceGeneric(2),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "380"
        artist = "Kieran Yanner"
        flavorText = "\"I do not fear death. I know my soul will rise to Starnheim, where I will feast forever among valkyries and heroes. Can you say the same?\""
        imageUri = "https://cards.scryfall.io/normal/front/e/a/ea007a18-b31a-4881-92c4-86120dc5729b.jpg?1783928123"
        ruling("2021-02-05", "To determine the total cost of a spell, start with the mana cost or alternative cost you're paying, add any cost increases, then apply any cost reductions (such as that of Starnheim Aspirant). The mana value of the spell is determined only by its mana cost, no matter what the total cost to cast the spell was.")
        ruling("2021-02-05", "The cost reduction applies only to generic mana in the costs of Angel spells you cast. It can't reduce requirements of specific colors of mana.")
        ruling("2021-02-05", "An Angel spell is a creature spell with the creature type Angel. Instant, sorcery, and enchantment cards that may create Angel tokens are not Angel spells.")
    }
}

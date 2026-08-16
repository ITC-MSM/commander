package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.effects.ManaRestriction

/**
 * Freya Crescent
 * {R}
 * Legendary Creature — Rat Knight
 * 1/1
 *
 * Jump — During your turn, Freya Crescent has flying.
 * {T}: Add {R}. Spend this mana only to cast an Equipment spell or activate an equip ability.
 *
 * "Jump" is an ability word (no rules meaning); it's modeled as a conditional static grant of
 * flying to this creature gated on [Conditions.IsYourTurn] — the same shape as Dragoon's Lance.
 *
 * The spend restriction is the disjunction of the two spend contexts the card names, one atom each:
 * [ManaRestriction.SubtypeSpellsOnly] for "cast an Equipment spell" and
 * [ManaRestriction.EquipAbilityActivationOnly] for "activate an equip ability" (CR 702.6). Same
 * shape as Ronin, Shadow Stalker's identical clause.
 *
 * It used to be the one-liner [ManaRestriction.SubtypeSpellsOrAbilitiesOnly] for the Equipment
 * subtype, which is over-broad: that admits *every* activated ability of an Equipment source, so it
 * would also pay for Iron Man Armor's "{2}: … it becomes a 0/0 Construct Hero artifact creature" or
 * Batterskull's "{3}: Return this Equipment to its owner's hand". Only the equip ability itself
 * qualifies, and a card type can't separate them — the equip ability and the others share one
 * source. `EquipAbilityActivationOnly` tests the fact directly
 * (`ActivatedAbility.isEquipAbility`, reached through `SpellPaymentContext.isEquipAbilityActivation`).
 */
val FreyaCrescent = card("Freya Crescent") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — Rat Knight"
    oracleText = "Jump — During your turn, Freya Crescent has flying.\n" +
        "{T}: Add {R}. Spend this mana only to cast an Equipment spell or activate an equip ability."
    power = 1
    toughness = 1

    // Jump — During your turn, Freya Crescent has flying.
    staticAbility {
        condition = Conditions.IsYourTurn
        ability = GrantKeyword(Keyword.FLYING, Filters.Self)
    }

    // {T}: Add {R}. Spend this mana only to cast an Equipment spell or activate an equip ability.
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(
            color = Color.RED,
            amount = 1,
            restriction = ManaRestriction.AnyOf(
                listOf(
                    ManaRestriction.SubtypeSpellsOnly(setOf("Equipment")),
                    ManaRestriction.EquipAbilityActivationOnly,
                )
            )
        )
        manaAbility = true
        description = "{T}: Add {R}. Spend this mana only to cast an Equipment spell or activate an equip ability."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "138"
        artist = "Nereida"
        flavorText = "\"We live not to forget our past, but to learn from it!\""
        imageUri = "https://cards.scryfall.io/normal/front/9/9/9921f646-e893-44db-ac89-0633c1009788.jpg?1748706279"
    }
}

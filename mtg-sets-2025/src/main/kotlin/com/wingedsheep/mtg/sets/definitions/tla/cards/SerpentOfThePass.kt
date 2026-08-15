package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.CostReductionSource
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget
import com.wingedsheep.sdk.scripting.conditions.Compare
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Serpent of the Pass — Avatar: The Last Airbender #70
 * {5}{U}{U} · Creature — Serpent · Uncommon
 * 6/5
 *
 * If there are three or more Lesson cards in your graveyard, you may cast this spell as though it
 * had flash.
 * This spell costs {1} less to cast for each noncreature, nonland card in your graveyard.
 *
 * Two cast-time statics, both modelled with existing primitives:
 *   - the conditional flash clause is the card-level [conditionalFlash], a [Compare] that the spell
 *     may be cast at instant speed while three or more Lesson-subtype cards sit in your graveyard,
 *   - the cost reduction is a [ModifySpellCost] on the self-cast that reduces generic mana by the
 *     count of noncreature, nonland cards in your graveyard ([CostReductionSource.CardsInGraveyardMatchingFilter]).
 */
val SerpentOfThePass = card("Serpent of the Pass") {
    manaCost = "{5}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Serpent"
    power = 6
    toughness = 5
    oracleText = "If there are three or more Lesson cards in your graveyard, you may cast this " +
        "spell as though it had flash.\n" +
        "This spell costs {1} less to cast for each noncreature, nonland card in your graveyard."

    conditionalFlash = Compare(
        DynamicAmount.Count(Player.You, Zone.GRAVEYARD, GameObjectFilter.Any.withSubtype(Subtype.LESSON)),
        ComparisonOperator.GTE,
        DynamicAmount.Fixed(3),
    )

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.ReduceGenericBy(
                CostReductionSource.CardsInGraveyardMatchingFilter(
                    filter = GameObjectFilter.Noncreature and GameObjectFilter.Nonland,
                ),
            ),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "70"
        artist = "Eiji Kaneda"
        flavorText = "Those who walk the Serpent's Pass quickly discover the origin of its name."
        imageUri = "https://cards.scryfall.io/normal/front/8/7/87595843-15bc-48bb-8a81-3e6ad924ed44.jpg?1764120436"
    }
}

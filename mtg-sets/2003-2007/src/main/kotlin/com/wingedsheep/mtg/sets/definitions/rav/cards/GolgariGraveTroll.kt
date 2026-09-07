package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithDynamicCounters
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Golgari Grave-Troll
 * {4}{G}
 * Creature — Troll Skeleton
 * 0/0
 * This creature enters with a +1/+1 counter on it for each creature card in your graveyard.
 * {1}, Remove a +1/+1 counter from this creature: Regenerate this creature.
 * Dredge 6
 *
 * A 0/0 whose whole body is the graveyard it came out of. The three lines compose without new
 * vocabulary: [EntersWithDynamicCounters] over a `Count(You, GRAVEYARD, Creature)` for the entry,
 * the existing `RemoveCounterFromSelf` cost atom plus `Effects.Regenerate` for the shield, and
 * [KeywordAbility.dredge] for the recursion.
 *
 * The counter count is taken as the Troll enters, so a Grave-Troll returned *from* the graveyard
 * straight to the battlefield still counts itself — it hasn't left the graveyard when the
 * as-enters replacement measures (per the 2018 ruling below).
 *
 * The regeneration shield is bought with the Troll's own body: each activation shrinks it by one,
 * so damage already marked on it can turn lethal the moment a counter comes off, killing it before
 * the activated ability it paid for ever resolves.
 */
val GolgariGraveTroll = card("Golgari Grave-Troll") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Troll Skeleton"
    power = 0
    toughness = 0
    oracleText = "This creature enters with a +1/+1 counter on it for each creature card in your graveyard.\n" +
        "{1}, Remove a +1/+1 counter from this creature: Regenerate this creature.\n" +
        "Dredge 6 (If you would draw a card, you may mill six cards instead. If you do, return this card from your graveyard to your hand.)"

    // "This creature enters with a +1/+1 counter on it for each creature card in your graveyard."
    replacementEffect(
        EntersWithDynamicCounters(
            count = DynamicAmount.Count(Player.You, Zone.GRAVEYARD, GameObjectFilter.Creature)
        )
    )

    // "{1}, Remove a +1/+1 counter from this creature: Regenerate this creature."
    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{1}"),
            Costs.RemoveCounterFromSelf(Counters.PLUS_ONE_PLUS_ONE, 1)
        )
        effect = Effects.Regenerate(EffectTarget.Self)
        description = "{1}, Remove a +1/+1 counter from this creature: Regenerate this creature."
    }

    keywordAbility(KeywordAbility.dredge(6))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "167"
        artist = "Greg Hildebrandt"
        imageUri = "https://cards.scryfall.io/normal/front/f/6/f61b50e6-2166-435d-bf48-c4a0cff9999c.jpg?1783943637"
        ruling(
            "2018-12-07",
            "Because damage remains marked on a creature until it's removed as the turn ends, " +
                "nonlethal damage dealt to Golgari Grave-Troll may become lethal if you remove +1/+1 " +
                "counters from it during that turn. In this case, it dies before you can resolve the " +
                "activated ability that will regenerate it."
        )
        ruling(
            "2018-12-07",
            "If you return Golgari Grave-Troll from your graveyard directly to the battlefield, " +
                "its first ability counts itself."
        )
        ruling(
            "2024-01-12",
            "You can't attempt to use a dredge ability if you don't have enough cards in your library."
        )
        ruling(
            "2024-01-12",
            "Dredge can replace any card draw, not only the one during your draw step."
        )
    }
}

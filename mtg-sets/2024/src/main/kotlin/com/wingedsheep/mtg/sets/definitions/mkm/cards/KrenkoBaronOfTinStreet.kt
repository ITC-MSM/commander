package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.CREATED_TOKENS
import com.wingedsheep.sdk.scripting.effects.MayPayManaEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Krenko, Baron of Tin Street — Murders at Karlov Manor #135
 * {2}{R} · Legendary Creature — Goblin · 3/3
 *
 * The activated ability snapshots all Goblins its controller currently controls and puts one
 * counter on each. Sacrificing an artifact is part of the cost, so that zone change can also
 * trigger Krenko's second ability before the activated ability resolves.
 *
 * The graveyard trigger is ANY-bound and intentionally has no controller restriction: artifacts
 * belonging to any player count. The optional payment is made on resolution. The created Goblin's
 * haste is a temporary grant through the created-token pipeline rather than a printed token
 * keyword, so a surviving token correctly loses haste at cleanup.
 */
val KrenkoBaronOfTinStreet = card("Krenko, Baron of Tin Street") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — Goblin"
    oracleText = "Haste\n" +
        "{T}, Sacrifice an artifact: Put a +1/+1 counter on each Goblin you control.\n" +
        "Whenever an artifact is put into a graveyard from the battlefield, you may pay {R}. " +
        "If you do, create a 1/1 red Goblin creature token. It gains haste until end of turn."
    power = 3
    toughness = 3

    keywords(Keyword.HASTE)

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.Sacrifice(GameObjectFilter.Artifact))
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.withSubtype("Goblin").youControl()),
            Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self),
        )
        description = "{T}, Sacrifice an artifact: Put a +1/+1 counter on each Goblin you control."
    }

    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Artifact,
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.ANY,
        )
        effect = MayPayManaEffect(
            cost = ManaCost.parse("{R}"),
            effect = Effects.Composite(
                Effects.CreateToken(
                    power = 1,
                    toughness = 1,
                    colors = setOf(Color.RED),
                    creatureTypes = setOf("Goblin"),
                    imageUri = "https://cards.scryfall.io/normal/front/c/d/" +
                        "cd6cd0d3-7973-49e6-9c1c-6f516a5d5fe5.jpg?1783912608",
                ),
                Effects.GrantKeyword(
                    Keyword.HASTE,
                    EffectTarget.PipelineTarget(CREATED_TOKENS, 0),
                ),
            ),
        )
        description = "Whenever an artifact is put into a graveyard from the battlefield, you may " +
            "pay {R}. If you do, create a 1/1 red Goblin creature token. It gains haste until end " +
            "of turn."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "135"
        artist = "Brian Valeza"
        imageUri = "https://cards.scryfall.io/normal/front/5/5/" +
            "5524b712-c67d-4d2e-9344-9e85a6ce3227.jpg?1783912878"
        ruling(
            "2024-02-02",
            "If Krenko, Baron of Tin Street dies at the same time as one or more artifacts are put " +
                "into a graveyard from the battlefield, its last ability will still trigger for " +
                "each of those artifacts.",
        )
    }
}

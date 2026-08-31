package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithCounters
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.SearchDestination
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPermanent
import com.wingedsheep.sdk.scripting.values.ContextPropertyKey
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Scrapyard Recombiner
 * {3}
 * Artifact Creature — Construct
 * 0/0
 * Modular 2 (This creature enters with two +1/+1 counters on it. When it dies, you may put its +1/+1 counters on target artifact creature.)
 * {T}, Sacrifice an artifact: Search your library for a Construct card, reveal it, put it into your hand, then shuffle.
 *
 * **Modular is lowered here, not handled by the engine** — the same two-declaration shape
 * `mh2/cards/ArcboundMouser.kt` uses. [KeywordAbility.modular] is display-only vocabulary (nothing
 * in the rules engine reads `Keyword.MODULAR`), so the two halves the reminder text spells out are
 * wired explicitly:
 *
 *  - the ETB half is an [EntersWithCounters] replacement (`selfOnly`, two +1/+1 counters). That is
 *    why the printed box is 0/0: a Recombiner on the battlefield is a 2/2 made entirely of counters.
 *  - the death half is an *optional* dies trigger reading
 *    [ContextPropertyKey.LAST_KNOWN_PLUS_ONE_COUNTER_COUNT] rather than the live entity — the
 *    counters cease to exist the moment the Recombiner changes zones, so the count must come from
 *    last-known information (CR 603.10 / 608.2h).
 *
 * That last point is also why this uses [Effects.AddDynamicCounters] with an explicit +1/+1 count
 * rather than the neighbouring `Effects.MoveAllLastKnownCounters` shape (Servant of the Scale):
 * moving *all* last-known counters would hand the target any leftover -1/-1 counters too, and
 * modular only ever moves the +1/+1 ones.
 *
 * The tutor is the plain [Patterns.Library.searchLibrary] recipe — a bare "Construct card" is any
 * permanent card with the subtype, not a creature card, so the filter is
 * [GameObjectFilter.Permanent] narrowed by subtype.
 */
val ScrapyardRecombiner = card("Scrapyard Recombiner") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Construct"
    power = 0
    toughness = 0
    oracleText = "Modular 2 (This creature enters with two +1/+1 counters on it. When it dies, you may put its +1/+1 counters on target artifact creature.)\n" +
        "{T}, Sacrifice an artifact: Search your library for a Construct card, reveal it, put it into your hand, then shuffle."

    keywordAbility(KeywordAbility.modular(2))

    // Modular, half one: "This creature enters with two +1/+1 counters on it."
    replacementEffect(
        EntersWithCounters(
            counterType = CounterTypeFilter.PlusOnePlusOne,
            count = 2,
            selfOnly = true
        )
    )

    // Modular, half two: "When it dies, you may put its +1/+1 counters on target artifact creature."
    triggeredAbility {
        trigger = Triggers.Dies
        target = TargetPermanent(filter = TargetFilter(GameObjectFilter.ArtifactCreature))
        optional = true
        effect = Effects.AddDynamicCounters(
            Counters.PLUS_ONE_PLUS_ONE,
            DynamicAmount.ContextProperty(ContextPropertyKey.LAST_KNOWN_PLUS_ONE_COUNTER_COUNT),
            EffectTarget.ContextTarget(0)
        )
        description = "When this creature dies, you may put its +1/+1 counters on target artifact creature."
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.Sacrifice(GameObjectFilter.Artifact))
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.Any.withSubtype(Subtype.CONSTRUCT),
            destination = SearchDestination.HAND,
            shuffleAfter = true,
            reveal = true
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "227"
        artist = "Simon Dominic"
        imageUri = "https://cards.scryfall.io/normal/front/8/9/8945f5e0-c143-47ca-910e-32ad9ac34487.jpg?1783933073"
    }
}

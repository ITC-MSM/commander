package com.wingedsheep.mtg.sets.definitions.mh3.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithCounters
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPermanent
import com.wingedsheep.sdk.scripting.values.ContextPropertyKey
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Arcbound Condor — Modern Horizons 3 #81
 * {2}{B}{B} · Artifact Creature — Bird · 0/0
 *
 * Flying
 * Modular 3 (This creature enters with three +1/+1 counters on it. When it dies, you may put its
 * +1/+1 counters on target artifact creature.)
 * Whenever another artifact you control enters, target creature an opponent controls gets -1/-1
 * until end of turn.
 *
 * **Modular is lowered here, not handled by the engine.** [KeywordAbility.modular] is display-only
 * vocabulary — nothing in the rules engine reads `Keyword.MODULAR` — so the two halves the reminder
 * text spells out are wired explicitly, exactly as the SDK lowers rampage/bushido/training:
 *
 *  - the ETB half is an [EntersWithCounters] replacement (`selfOnly`, three +1/+1 counters), which
 *    is why the printed box is 0/0: a Condor on the battlefield is a 3/3 made of counters.
 *  - the death half is an *optional* dies trigger. It reads
 *    [ContextPropertyKey.LAST_KNOWN_PLUS_ONE_COUNTER_COUNT] rather than the live entity (CR 603.10 /
 *    608.2g — counters cease to exist on the zone change, so the count must come from last-known
 *    information), which is also what the 2024-06-07 ruling describes: a Condor killed by -1/-1
 *    counters still moves however many +1/+1 counters survived the CR 704.5q annihilation.
 *
 * That last point is why this uses [Effects.AddDynamicCounters] with an explicitly +1/+1 count
 * rather than the neighbouring `Effects.MoveAllLastKnownCounters` shape (Servant of the Scale,
 * Essence Channeler): moving *all* last-known counters would hand the target the Condor's leftover
 * -1/-1 counters too, and modular only ever moves the +1/+1 ones.
 *
 * The enters trigger uses [TriggerBinding.OTHER] so the Condor's own entry doesn't fire it, and the
 * artifact filter is `youControl()` — an opponent's artifact entering is not "another artifact you
 * control". It is per-permanent: several artifacts entering at once each fire it separately.
 */
val ArcboundCondor = card("Arcbound Condor") {
    manaCost = "{2}{B}{B}"
    colorIdentity = "B"
    typeLine = "Artifact Creature — Bird"
    power = 0
    toughness = 0
    oracleText = "Flying\n" +
        "Modular 3 (This creature enters with three +1/+1 counters on it. When it dies, you may " +
        "put its +1/+1 counters on target artifact creature.)\n" +
        "Whenever another artifact you control enters, target creature an opponent controls gets " +
        "-1/-1 until end of turn."

    keywords(Keyword.FLYING)
    keywordAbility(KeywordAbility.modular(3))

    // Modular, half one: "This creature enters with three +1/+1 counters on it."
    replacementEffect(
        EntersWithCounters(
            counterType = CounterTypeFilter.PlusOnePlusOne,
            count = 3,
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

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Artifact.youControl(),
            binding = TriggerBinding.OTHER
        )
        target = Targets.CreatureOpponentControls
        effect = Effects.ModifyStats(-1, -1, EffectTarget.ContextTarget(0))
        description = "Whenever another artifact you control enters, target creature an opponent " +
            "controls gets -1/-1 until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "81"
        artist = "Michele Giorgi"
        imageUri = "https://cards.scryfall.io/normal/front/e/7/e7995b44-7932-4d28-9a4c-a32ba35c60d7.jpg?1783911284"

        ruling(
            "2024-06-07",
            "If this creature gets enough -1/-1 counters put on it to cause it to go to the " +
                "graveyard, modular will put a number of +1/+1 counters on the target artifact " +
                "creature equal to the number of +1/+1 counters on this creature before it left " +
                "the battlefield."
        )
    }
}

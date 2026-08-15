package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.decayed
import com.wingedsheep.sdk.dsl.renew
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.AddCountersEffect
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Rot-Curse Rakshasa — Tarkir: Dragonstorm #87
 * {1}{B} · Creature — Demon · 5/5
 *
 * Trample
 * Decayed (This creature can't block. When it attacks, sacrifice it at end of combat.)
 * Renew — {X}{B}{B}, Exile this card from your graveyard: Put a decayed counter on each of
 * X target creatures. Activate only as a sorcery.
 *
 * Trample and Decayed are keyword abilities — the printed Decayed half is composed by the
 * [com.wingedsheep.sdk.dsl.CardBuilder.decayed] helper (a "can't block" static + an
 * attack-triggered end-of-combat sacrifice). The Renew ability is a graveyard-activated,
 * sorcery-speed ability ([com.wingedsheep.sdk.dsl.CardBuilder.renew]) whose {X} clamps the
 * number of targets ([TargetCreature.dynamicMaxCount] = [DynamicAmount.XValue], the
 * Builder's Bane / Icy Blast pattern). [ForEachTargetEffect] puts one decayed counter
 * ([Counters.DECAYED]) on each chosen creature — the counter grants Decayed to *any* creature
 * (CR 702.147a), realized by the engine off the counter.
 */
val RotCurseRakshasa = card("Rot-Curse Rakshasa") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Demon"
    power = 5
    toughness = 5
    oracleText = "Trample\n" +
        "Decayed (This creature can't block. When it attacks, sacrifice it at end of combat.)\n" +
        "Renew — {X}{B}{B}, Exile this card from your graveyard: Put a decayed counter on each of " +
        "X target creatures. Activate only as a sorcery."

    keywords(Keyword.TRAMPLE)
    decayed()

    renew("{X}{B}{B}") {
        target("creatures", TargetCreature(dynamicMaxCount = DynamicAmount.XValue))
        effect = ForEachTargetEffect(
            listOf(
                AddCountersEffect(Counters.DECAYED, 1, EffectTarget.ContextTarget(0))
            )
        )
        description = "Put a decayed counter on each of X target creatures."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "87"
        artist = "Chris Rahn"
        imageUri = "https://cards.scryfall.io/normal/front/3/1/31276460-fa9d-47da-85c5-c4baa8074d0d.jpg?1743204311"
    }
}

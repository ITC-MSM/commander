package com.wingedsheep.mtg.sets.definitions.neo.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Patchwork Automaton — Kamigawa: Neon Dynasty #254 (canonical printing)
 * {2} · Artifact Creature — Construct · 1/1
 *
 * Ward {2}
 * Whenever you cast an artifact spell, put a +1/+1 counter on this creature.
 *
 * The counters are permanent, so ward protects an investment that grows: an opponent must pay {2}
 * every time they try to answer it.
 */
val PatchworkAutomaton = card("Patchwork Automaton") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Construct"
    power = 1
    toughness = 1
    oracleText = "Ward {2} (Whenever this creature becomes the target of a spell or ability an " +
        "opponent controls, counter it unless that player pays {2}.)\n" +
        "Whenever you cast an artifact spell, put a +1/+1 counter on this creature."

    keywordAbility(KeywordAbility.ward("{2}"))

    triggeredAbility {
        trigger = Triggers.youCastSpell(GameObjectFilter.Artifact)
        effect = Effects.AddCounters("+1/+1", 1, EffectTarget.Self)
        description = "Whenever you cast an artifact spell, put a +1/+1 counter on this creature."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "254"
        artist = "Donato Giancola"
        flavorText = "It dwells happily near the ever-burning forges of Sokenzan City, crafting " +
            "small things of beauty from discarded scraps."
        imageUri = "https://cards.scryfall.io/normal/front/7/a/7a4e1580-dd26-4f4b-ac98-3e6fa7b879d5.jpg?1783923821"
    }
}

package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

val TraxosScourgeEternal = card("Traxos, Scourge Eternal") {
    manaCost = "{4}"
    colorIdentity = ""
    typeLine = "Legendary Artifact Creature — Dragon Construct"
    oracleText = "Trample\nTraxos doesn't untap during your untap step.\n" +
        "Whenever you cast an artifact or creature spell, untap Traxos."
    power = 5
    toughness = 4

    keywords(Keyword.TRAMPLE)
    flags(AbilityFlag.DOESNT_UNTAP)

    triggeredAbility {
        trigger = Triggers.youCastSpell(spellFilter = GameObjectFilter.Artifact or GameObjectFilter.Creature)
        effect = Effects.Untap(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "280"
        artist = "Aaron Miller"
        flavorText = "Its orders, ancient. Its advance, ponderous. Its threat, undeniable."
        imageUri = "https://cards.scryfall.io/normal/front/0/5/05102c46-96f8-44a0-a1e6-e388fa5e0841.jpg?1789557046"
        inBooster = false
    }
}

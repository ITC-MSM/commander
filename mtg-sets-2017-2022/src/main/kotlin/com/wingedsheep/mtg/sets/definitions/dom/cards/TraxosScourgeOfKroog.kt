package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Traxos, Scourge of Kroog
 * {4}
 * Legendary Artifact Creature — Construct
 * 7/7
 * Trample
 * Traxos, Scourge of Kroog enters tapped and doesn't untap during your untap step.
 * Whenever you cast a historic spell, untap Traxos.
 */
val TraxosScourgeOfKroog = card("Traxos, Scourge of Kroog") {
    manaCost = "{4}"
    colorIdentity = ""
    typeLine = "Legendary Artifact Creature — Construct"
    power = 7
    toughness = 7
    oracleText = "Trample\nTraxos, Scourge of Kroog enters tapped and doesn't untap during your untap step.\nWhenever you cast a historic spell, untap Traxos."

    keywords(Keyword.TRAMPLE)

    replacementEffect(EntersTapped())
    flags(AbilityFlag.DOESNT_UNTAP)

    triggeredAbility {
        trigger = Triggers.YouCastHistoric
        effect = Effects.Untap(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "234"
        artist = "Lius Lasahido"
        imageUri = "https://cards.scryfall.io/normal/front/d/a/dab80216-3df7-4e4f-8732-16dd6cac6bcf.jpg?1562743954"
        ruling("2018-04-27", "A card, spell, or permanent is historic if it has the legendary supertype, the artifact card type, or the Saga subtype.")
        ruling("2018-04-27", "Abilities that trigger \"whenever you cast a historic spell\" resolve before the spell that caused them to trigger.")
    }
}

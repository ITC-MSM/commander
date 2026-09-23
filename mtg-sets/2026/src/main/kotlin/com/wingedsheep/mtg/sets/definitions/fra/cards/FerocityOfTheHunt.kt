package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Ferocity of the Hunt — Reality Fracture #134
 * {1}{B/G} · Enchantment — Aura
 *
 * Flash
 * Enchant creature
 * Enchanted creature gets +1/+0 and has deathtouch.
 * When enchanted creature dies, return that card to the battlefield tapped under its owner's control.
 *
 * Fungal Fortitude's shape (an ATTACHED-bound dies trigger), with the graveyard guard on the
 * return: "that card" is the card that went to the graveyard, so if it has left the graveyard
 * by the time the trigger resolves (exiled in response), nothing returns. A plain move to the
 * battlefield with no controller override enters under the card's owner's control.
 */
val FerocityOfTheHunt = card("Ferocity of the Hunt") {
    manaCost = "{1}{B/G}"
    colorIdentity = "BG"
    typeLine = "Enchantment — Aura"
    oracleText = "Flash\nEnchant creature\nEnchanted creature gets +1/+0 and has deathtouch.\n" +
        "When enchanted creature dies, return that card to the battlefield tapped under its owner's control."

    keywords(Keyword.FLASH)

    auraTarget = Targets.Creature

    staticAbility {
        ability = ModifyStats(1, 0)
    }
    staticAbility {
        ability = GrantKeyword(Keyword.DEATHTOUCH)
    }

    triggeredAbility {
        trigger = Triggers.leavesBattlefield(to = Zone.GRAVEYARD, binding = TriggerBinding.ATTACHED)
        effect = Effects.PutOntoBattlefieldFromGraveyard(EffectTarget.TriggeringEntity, tapped = true)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "134"
        artist = "Elliot Lang"
        imageUri = "https://cards.scryfall.io/normal/front/a/9/a9793ce9-5a0b-41fe-b9ad-02f6f7da2481.jpg?1789644856"
        inBooster = false
    }
}

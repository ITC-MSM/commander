package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ReplaceTokenCreationWithAttachedCopy
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Moonlit Meditation
 * {2}{U}
 * Enchantment — Aura
 * Enchant artifact or creature you control
 * The first time you would create one or more tokens each turn, you may instead
 * create that many tokens that are copies of enchanted permanent.
 */
val MoonlitMeditation = card("Moonlit Meditation") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant artifact or creature you control\n" +
        "The first time you would create one or more tokens each turn, you may instead " +
        "create that many tokens that are copies of enchanted permanent."

    auraTarget = TargetPermanent(filter = TargetFilter.CreatureOrArtifact.youControl())

    replacementEffect(
        ReplaceTokenCreationWithAttachedCopy(
            optional = true,
            oncePerTurn = true,
            attachmentVerb = "enchanted"
        )
    )

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "69"
        artist = "Liiga Smilshkalne"
        flavorText = "\"All is the hand that weaves the Fabric of Being. The Fabric of Being is All.\"\n" +
            "—*Drix Codex*, line 1"
        imageUri = "https://cards.scryfall.io/normal/front/f/2/f2a56007-5bca-4edf-9cc4-5f77a273636c.jpg?1752946830"
        ruling("2025-07-25", "The effect of Moonlit Meditation's last ability applies before anything that modifies how those tokens enter the battlefield.")
        ruling("2025-07-25", "The effect of Moonlit Meditation's last ability can apply to any token, not just artifact or creature tokens. For example, you could replace creating a Shard token (a predefined enchantment token) with creating a copy of the enchanted permanent.")
        ruling("2025-07-25", "If the enchanted permanent is legendary, the copies will also be legendary. If this results in you controlling more than one legendary permanent with the same name, you'll put all but one of them into their owner's graveyard.")
        ruling("2025-07-25", "If you choose not to apply the replacement effect, you will not get the choice to apply it again until the next turn.")
        ruling("2025-07-25", "If you create one or more tokens, and then Moonlit Meditation comes under your control that same turn, the replacement effect won't apply to any tokens you create for the rest of the turn.")
    }
}

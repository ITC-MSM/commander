package com.wingedsheep.mtg.sets.definitions.dka.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.MustBeBlockedEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Deadly Allure
 * {B}
 * Sorcery
 * Target creature gains deathtouch until end of turn and must be blocked this turn if able.
 * Flashback {G}
 */
val DeadlyAllure = card("Deadly Allure") {
    manaCost = "{B}"
    colorIdentity = "BG"
    typeLine = "Sorcery"
    oracleText = "Target creature gains deathtouch until end of turn and must be blocked this turn if able.\n" +
        "Flashback {G} (You may cast this card from your graveyard for its flashback cost. Then exile it.)"
    spell {
        val t = target("target", TargetCreature(filter = TargetFilter.Creature))
        effect = Effects.Composite(
            Effects.GrantKeyword(Keyword.DEATHTOUCH, t),
            MustBeBlockedEffect(t, allCreatures = false)
        )
    }
    keywordAbility(KeywordAbility.flashback("{G}"))
    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "58"
        artist = "Steve Argyle"
        imageUri = "https://cards.scryfall.io/normal/front/2/7/271bc29e-d33f-4c21-a3ee-b2c6b5c9015e.jpg?1782714616"
    }
}

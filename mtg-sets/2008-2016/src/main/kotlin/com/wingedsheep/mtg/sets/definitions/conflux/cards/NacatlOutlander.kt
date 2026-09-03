package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.ProtectionScope

/**
 * Nacatl Outlander — Conflux #119
 * {R}{G} · Creature — Cat Scout · 2/2
 *
 * Protection from blue
 *
 * The Naya member of the "Outlander" cycle — see [GoblinOutlander]. Its only printed line is the
 * protection keyword, spelled as [KeywordAbility.Protection] over a [ProtectionScope.Color] scope;
 * the engine projects that keyword and every protection rule (targeting, blocking, damage, aura
 * fall-off) reads it directly.
 */
val NacatlOutlander = card("Nacatl Outlander") {
    manaCost = "{R}{G}"
    colorIdentity = "GR"
    typeLine = "Creature — Cat Scout"
    power = 2
    toughness = 2
    oracleText = "Protection from blue"

    keywordAbility(KeywordAbility.Protection(ProtectionScope.Color(Color.BLUE)))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "119"
        artist = "Zoltan Boros & Gabor Szikszai"
        flavorText = "Survival in the wilds of Naya left Tiyan well equipped to win the civilized battles of Bant."
        imageUri = "https://cards.scryfall.io/normal/front/1/2/12b5b694-46c8-4cb0-ab6b-4bc67c04cc7f.jpg"
    }
}

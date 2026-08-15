package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Ghitu Fire
 * {X}{R}
 * Sorcery
 * You may cast this spell as though it had flash if you pay {2} more to cast it.
 * Ghitu Fire deals X damage to any target.
 */
val GhituFire = card("Ghitu Fire") {
    manaCost = "{X}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "You may cast this spell as though it had flash if you pay {2} more to cast it. " +
        "(You may cast it any time you could cast an instant.)\n" +
        "Ghitu Fire deals X damage to any target."

    keywordAbility(KeywordAbility.flashKicker("{2}"))

    spell {
        target = Targets.Any
        effect = Effects.DealXDamage(EffectTarget.ContextTarget(0))
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "144"
        artist = "Glen Angus"
        imageUri = "https://cards.scryfall.io/normal/front/7/8/78827acd-a526-411b-bd22-ab9b538c75dd.jpg?1562919168"
        ruling("2004-10-04", "The extra 2 mana to cast it as an instant does not count as part of the mana cost.")
    }
}

package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Rift Bolt
 * {2}{R}
 * Sorcery
 * Rift Bolt deals 3 damage to any target.
 * Suspend 1—{R} (Rather than cast this card from your hand, you may pay {R} and exile it with a time counter on it. At the beginning of your upkeep, remove a time counter. When the last is removed, you may cast it without paying its mana cost.)
 *
 * A Lightning Bolt body on a one-counter suspend: [Targets.Any] is "any target" (creature,
 * player, planeswalker or battle) and the damage source defaults to the spell itself.
 */
val RiftBolt = card("Rift Bolt") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Rift Bolt deals 3 damage to any target.\n" +
        "Suspend 1—{R} (Rather than cast this card from your hand, you may pay {R} and exile it with a time counter on it. At the beginning of your upkeep, remove a time counter. When the last is removed, you may cast it without paying its mana cost.)"

    spell {
        val t = target("target", Targets.Any)
        effect = Effects.DealDamage(3, t)
    }

    keywordAbility(KeywordAbility.suspend("{R}", 1))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "176"
        artist = "Michael Sutfin"
        imageUri = "https://cards.scryfall.io/normal/front/8/8/88dde96e-6824-4d26-9fb5-86b9f3c50959.jpg"
    }
}

package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Fiery Inscription
 * {2}{R}
 * Enchantment
 *
 * When this enchantment enters, the Ring tempts you.
 * Whenever you cast an instant or sorcery spell, this enchantment deals 2 damage to each opponent.
 */
val FieryInscription = card("Fiery Inscription") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment"
    oracleText = "When this enchantment enters, the Ring tempts you.\n" +
        "Whenever you cast an instant or sorcery spell, this enchantment deals 2 damage to each opponent."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.TheRingTemptsYou()
    }

    triggeredAbility {
        trigger = Triggers.YouCastInstantOrSorcery
        effect = Effects.DealDamage(2, EffectTarget.PlayerRef(Player.EachOpponent))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "126"
        artist = "John Di Giovanni"
        flavorText = "Ash nazg durbatulûk, ash nazg gimbatul, ash nazg thrakatulûk agh burzum-ishi krimpatul."
        imageUri = "https://cards.scryfall.io/normal/front/8/c/8c321159-43e5-40fc-9f0c-4ecc4f6cfd20.jpg?1686968925"
    }
}

package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlockedByFewerThan
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Pathrazer of Ulamog
 * {11}
 * Creature — Eldrazi
 * 9 / 9
 *
 * Annihilator 3 (Whenever this creature attacks, defending player sacrifices three permanents of
 * their choice.)
 * This creature can't be blocked except by three or more creatures.
 *
 * Modeling notes:
 *  - Annihilator is a **display-only** [KeywordAbility.Numeric] in this SDK — nothing in
 *    `rules-engine` reads it — so the card declares it for the printed line *and* lowers the
 *    behaviour by hand, exactly as Artisan of Kozilek does for annihilator 2: a [Triggers.Attacks]
 *    triggered ability whose effect is the edict form of [Effects.Sacrifice], where the *defending
 *    player* chooses. Declaring the keyword ability alone would render the reminder text and do
 *    nothing.
 *  - The filter is [GameObjectFilter.Permanent], not `Creature`: annihilator eats any permanent of
 *    the defending player's choice, lands and enchantments included.
 *  - The blocking clause is the generalized-menace static [CantBeBlockedByFewerThan] with
 *    `minBlockers = 3` (menace is the N = 2 case). It may still be left unblocked entirely — the
 *    restriction only bites once at least one creature blocks — which is what "can't be blocked
 *    except by three or more creatures" means.
 */
val PathrazerOfUlamog = card("Pathrazer of Ulamog") {
    manaCost = "{11}"
    colorIdentity = ""
    typeLine = "Creature — Eldrazi"
    power = 9
    toughness = 9
    oracleText = "Annihilator 3 (Whenever this creature attacks, defending player sacrifices three permanents of their choice.)\n" +
        "This creature can't be blocked except by three or more creatures."

    keywordAbility(KeywordAbility.annihilator(3))

    // Annihilator 3 — the lowering of the display-only keyword ability above.
    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Effects.Sacrifice(
            GameObjectFilter.Permanent,
            3,
            EffectTarget.PlayerRef(Player.DefendingPlayer)
        )
        description = "Annihilator 3"
    }

    staticAbility {
        ability = CantBeBlockedByFewerThan(3)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "9"
        artist = "Austin Hsu"
        flavorText = "No thought but hunger. No strategy but destruction."
        imageUri = "https://cards.scryfall.io/normal/front/3/f/3fdd84b5-fd93-483e-a131-028d04d9dea7.jpg?1783942012"
    }
}

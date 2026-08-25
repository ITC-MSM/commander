package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Thornado — Modern Horizons #184
 * {2}{G} · Instant
 *
 * Destroy target creature with flying.
 * Cycling {1}{G}
 *
 * "With flying" is a restriction on the *target*, not a condition on the effect, so it lives in
 * the target requirement — [Targets.CreatureWithKeyword] — and is rechecked at resolution
 * (CR 608.2b): a creature that loses flying in response is no longer a legal target and the spell
 * fizzles rather than destroying it.
 *
 * The keyword check must read projected characteristics, which is what
 * `GameObjectFilter.Creature.withKeyword` gives it — a creature granted flying by an Aura or a
 * lord is a legal target, and one whose flying was removed is not.
 *
 * Green removal with a mode-like escape hatch: cycling costs less than the spell, so an opponent
 * with no fliers doesn't leave you holding a dead card. Cycling contributes no simple keyword to
 * `keywords` — it is purely the parameterized ability.
 */
val Thornado = card("Thornado") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Destroy target creature with flying.\n" +
        "Cycling {1}{G} ({1}{G}, Discard this card: Draw a card.)"

    keywordAbility(KeywordAbility.cycling("{1}{G}"))

    spell {
        val flier = target("target creature with flying", Targets.CreatureWithKeyword(Keyword.FLYING))
        effect = Effects.Destroy(flier)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "184"
        artist = "Volkan Baǵa"
        flavorText = "Every bough, branch, and bramble lashed out in defense of the forest."
        imageUri = "https://cards.scryfall.io/normal/front/e/a/eadffd6b-d707-4fc5-a600-44eb9124b195.jpg?1783933090"
    }
}

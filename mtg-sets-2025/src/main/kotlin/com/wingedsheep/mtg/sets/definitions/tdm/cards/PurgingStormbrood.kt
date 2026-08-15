package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Purging Stormbrood // Absorb Essence — Tarkir: Dragonstorm #213
 * {4}{B} · Creature — Dragon · 4/4
 *
 * Flying
 * Ward—Pay 2 life.
 * When this creature enters, remove all counters from up to one target creature.
 *
 * Omen: Absorb Essence — {1}{W}, Instant — Omen
 * Target creature gets +2/+2 and gains lifelink and hexproof until end of turn.
 *
 * (Omen, Tarkir: Dragonstorm: casting the Omen face shuffles this card into its owner's
 * library on resolution instead of putting it in the graveyard. From every zone other than
 * the stack the card is just the Dragon — see [com.wingedsheep.sdk.model.CardLayout.OMEN].)
 */
val PurgingStormbrood = card("Purging Stormbrood") {
    manaCost = "{4}{B}"
    colorIdentity = "BW"
    typeLine = "Creature — Dragon"
    power = 4
    toughness = 4
    oracleText = "Flying\nWard—Pay 2 life. (Whenever this creature becomes the target of a spell " +
        "or ability an opponent controls, counter it unless that player pays 2 life.)\n" +
        "When this creature enters, remove all counters from up to one target creature."

    keywords(Keyword.FLYING)
    keywordAbility(KeywordAbility.wardLife(2))

    // ETB: remove all counters from up to one target creature.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        target("up to one target creature", Targets.UpToCreatures(1))
        effect = Effects.RemoveAllCounters(EffectTarget.ContextTarget(0))
    }

    // Omen: Absorb Essence — Instant. Target creature gets +2/+2 and gains lifelink and hexproof.
    omen("Absorb Essence") {
        manaCost = "{1}{W}"
        typeLine = "Instant — Omen"
        oracleText = "Target creature gets +2/+2 and gains lifelink and hexproof until end of turn. " +
            "(Then shuffle this card into its owner's library.)"
        spell {
            val creature = target("creature", Targets.Creature)
            effect = Effects.Composite(
                Effects.ModifyStats(2, 2, creature),
                Effects.GrantKeyword(Keyword.LIFELINK, creature),
                Effects.GrantHexproof(creature),
            )
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "213"
        artist = "David Astruga"
        imageUri = "https://cards.scryfall.io/normal/front/3/9/3988dc76-072c-4f43-849d-2e73c6f6ff58.jpg?1743204843"
    }
}

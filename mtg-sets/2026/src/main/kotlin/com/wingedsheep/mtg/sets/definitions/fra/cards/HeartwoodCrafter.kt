package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.ManaRestriction

/**
 * Heartwood Crafter // Soul Tether — Reality Fracture #105
 * {G} · Creature — Elf Artificer · 1/1 · Uncommon
 *
 * Enters prepared. "{T}: Add {C}. This mana can't be spent to cast spells from your hand."
 *
 * The mana restriction is the negative [ManaRestriction.CannotCastSpellsFromHand]: the {C} can pay
 * for Soul Tether's prepare-spell copy (cast from exile), for a spell cast from the graveyard, or
 * for any activated ability — only a cast from hand is off limits.
 */
val HeartwoodCrafter = card("Heartwood Crafter") {
    manaCost = "{G}"
    colorIdentity = "GR"
    typeLine = "Creature — Elf Artificer"
    power = 1
    toughness = 1
    oracleText = "This creature enters prepared. (While it's prepared, you may cast a copy of its spell. Doing so unprepares it.)\n" +
        "{T}: Add {C}. This mana can't be spent to cast spells from your hand."

    keywords(Keyword.PREPARED)

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1, restriction = ManaRestriction.CannotCastSpellsFromHand)
        manaAbility = true
        timing = TimingRule.ManaAbility
        description = "{T}: Add {C}. This mana can't be spent to cast spells from your hand."
    }

    prepare("Soul Tether") {
        manaCost = "{2}{R/G}"
        typeLine = "Sorcery"
        oracleText = "Create a Heartwood token. (It's a red and green artifact with \"{T}: Add {R} or {G}.\")"
        spell {
            effect = Effects.CreateHeartwood()
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "105"
        artist = "Anastasia Ovchinnikova"
        imageUri = "https://cards.scryfall.io/normal/front/9/1/910a1f41-17fd-4ab0-9597-7151e79dc760.jpg?1789127630"
        inBooster = false
    }
}

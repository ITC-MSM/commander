package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.ManaRestriction

/**
 * Ixalli's Lorekeeper
 * {G}
 * Creature — Human Druid
 * 1/1
 * Uncommon — LCI #194
 *
 * {T}: Add one mana of any color. Spend this mana only to cast a Dinosaur spell or
 *      activate an ability of a Dinosaur source.
 *
 * The single activated ability is a mana ability that produces one mana of any color
 * with [ManaRestriction.SubtypeSpellsOrAbilitiesOnly] baked to "Dinosaur" and
 * `creatureOnly = false` (the default), so the mana is usable for both Dinosaur spells
 * and activated abilities of Dinosaur sources — matching the printed oracle exactly.
 */
val IxallisLorekeeper = card("Ixalli's Lorekeeper") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Druid"
    power = 1
    toughness = 1
    oracleText = "{T}: Add one mana of any color. Spend this mana only to cast a Dinosaur spell or activate an ability of a Dinosaur source."

    // {T}: Add one mana of any color. Spend this mana only to cast a Dinosaur spell or
    //      activate an ability of a Dinosaur source.
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddAnyColorMana(
            amount = 1,
            restriction = ManaRestriction.SubtypeSpellsOrAbilitiesOnly("Dinosaur")
        )
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "194"
        artist = "Ernanda Souza"
        flavorText = "\"Did we follow the dinosaurs out of the caverns, or did they follow us? These glyphs could change our entire understanding of our history.\""
        imageUri = "https://cards.scryfall.io/normal/front/4/b/4bc94ded-458d-4458-9c0b-136d825b885d.jpg?1782694453"
    }
}

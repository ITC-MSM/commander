package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Merciless Enforcers
 * {1}{B}
 * Creature — Human Mercenary Villain
 * 2/1
 * Lifelink
 * {3}{B}: This creature deals 1 damage to each opponent.
 */
val MercilessEnforcers = card("Merciless Enforcers") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Mercenary Villain"
    oracleText = "Lifelink\n{3}{B}: This creature deals 1 damage to each opponent."
    power = 2
    toughness = 1
    keywords(Keyword.LIFELINK)
    activatedAbility {
        cost = Costs.Mana("{3}{B}")
        effect = DealDamageEffect(1, EffectTarget.PlayerRef(Player.EachOpponent))
    }
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "58"
        artist = "Alex Horley-Orlandelli"
        flavorText = "\"Montana, Fancy Dan, Ox... you guys are *so* brave to keep dressing like that. And I'm the one wearing spandex!\"\n—Spider-Man"
        imageUri = "https://cards.scryfall.io/normal/front/f/b/fba9c76c-1432-4554-88bd-3f5e8709a963.jpg?1758215825"
    }
}

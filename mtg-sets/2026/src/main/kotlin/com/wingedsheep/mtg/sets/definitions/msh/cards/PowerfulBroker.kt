package com.wingedsheep.mtg.sets.definitions.msh.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Powerful Broker — Marvel Super Heroes #179 (common)
 *
 * {2}{G} · Creature — Human Villain · 3/3
 *   {T}: For each kind of counter on target permanent or player, give that permanent or player
 *   another counter of that kind. Activate only as a sorcery.
 *
 * This is **proliferate aimed at exactly one object** — the same placement rule as CR 701.34a
 * ("give each another counter of each kind already there"), but with the recipient chosen as a
 * *target* instead of at resolution. That distinction is why it is
 * `Effects.Proliferate(target = …)` and not a plain `Effects.Proliferate()`: the recipient is
 * announced with the ability (CR 601.2c), so opponents can respond to it, it must be a legal
 * target (hexproof/shroud/protection all apply), and the ability is countered on resolution if
 * that one target has become illegal (CR 608.2b). Untargeted proliferate has none of those
 * properties and would also let the controller hit any number of other permanents.
 *
 * The recipient is `Targets.PermanentOrPlayer` — the printed wording is "permanent or player", not
 * "creature or player", so a land, an artifact with charge counters, or a player with poison
 * counters are all legal choices. A target with no counters at all is still legal; the ability
 * simply does nothing to it.
 *
 * "Activate only as a sorcery" is [TimingRule.SorcerySpeed].
 */
val PowerfulBroker = card("Powerful Broker") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Villain"
    power = 3
    toughness = 3
    oracleText = "{T}: For each kind of counter on target permanent or player, give that permanent " +
        "or player another counter of that kind. Activate only as a sorcery."

    activatedAbility {
        cost = Costs.Tap
        val recipient = target("target permanent or player", Targets.PermanentOrPlayer)
        effect = Effects.Proliferate(recipient)
        timing = TimingRule.SorcerySpeed
        description = "{T}: For each kind of counter on target permanent or player, give that " +
            "permanent or player another counter of that kind. Activate only as a sorcery."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "179"
        artist = "JB Casacop"
        flavorText = "\"Our new product makes disobedience literally unthinkable!\""
        imageUri = "https://cards.scryfall.io/normal/front/8/0/801f0417-b663-4e28-9a61-9570061654d7.jpg?1783902917"
    }
}

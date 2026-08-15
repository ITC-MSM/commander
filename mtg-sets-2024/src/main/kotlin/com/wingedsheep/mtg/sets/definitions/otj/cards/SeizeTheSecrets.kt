package com.wingedsheep.mtg.sets.definitions.otj.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostGating
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Seize the Secrets
 * {2}{U}
 * Sorcery
 *
 * This spell costs {1} less to cast if you've committed a crime this turn. (Targeting opponents,
 * anything they control, and/or cards in their graveyards is a crime.)
 * Draw two cards.
 *
 * The cost reduction reads the turn-scoped crime tracker (CR Outlaws of Thunder Junction). Seize
 * the Secrets itself doesn't target an opponent, so it never sets the flag — the crime must have
 * been committed by an earlier spell or ability this turn.
 */
val SeizeTheSecrets = card("Seize the Secrets") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "This spell costs {1} less to cast if you've committed a crime this turn. " +
        "(Targeting opponents, anything they control, and/or cards in their graveyards is a crime.)\n" +
        "Draw two cards."

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.ReduceGeneric(1),
            gating = CostGating.OnlyIf(Conditions.YouCommittedCrimeThisTurn),
        )
    }

    spell {
        effect = Effects.DrawCards(2)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "64"
        artist = "Miranda Meeks"
        flavorText = "Nolan refused to give up the vault's location. The strange figure simply " +
            "smiled and reached into his mind."
        imageUri = "https://cards.scryfall.io/normal/front/1/b/1bdbdfa8-aa28-4b3d-95e7-3d0e7e37f982.jpg?1712355485"

        ruling("2024-04-12", "A player commits a crime as they cast a spell, activate an ability, or put a triggered ability on the stack that targets at least one opponent, at least one permanent, spell, or ability an opponent controls, and/or at least one card in an opponent's graveyard.")
        ruling("2024-04-12", "Seize the Secrets checks whether you've committed a crime this turn as you cast it, not as it resolves. Targeting something for Seize the Secrets won't help, as it has no targets.")
        ruling("2024-04-12", "Once you've committed a crime, you've committed a crime for the rest of the turn. It doesn't matter if the spell or ability that committed the crime is later countered or otherwise leaves the stack.")
    }
}

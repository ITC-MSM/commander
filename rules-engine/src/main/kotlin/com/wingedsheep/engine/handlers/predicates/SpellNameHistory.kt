package com.wingedsheep.engine.handlers.predicates

import com.wingedsheep.engine.state.GameState

/** Cast-time names survive resolution, countering, and later zone changes. Nameless spells never match. */
internal fun sharesNameWithSpellCastThisTurn(state: GameState, name: String?): Boolean {
    if (name.isNullOrBlank()) return false
    return state.spellsCastThisTurnByPlayer.values.any { records ->
        records.any { record ->
            !record.isFaceDown && !record.name.isNullOrBlank() &&
                (record.name == name || (
                    (" // " in name || " // " in record.name) &&
                        record.name.split(" // ").any { it in name.split(" // ") }
                ))
        }
    }
}
